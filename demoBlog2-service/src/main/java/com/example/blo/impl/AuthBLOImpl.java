package com.example.blo.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.api.dto.LoginResponseDTO;
import com.example.api.dto.PermissionDTO;
import com.example.blo.IAuthBLO;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.entity.UserRole;
import com.example.mapper.RoleMapper;
import com.example.mapper.UserMapper;
import com.example.mapper.UserRoleMapper;
import com.example.utils.JwtUtil;
import com.example.utils.RedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthBLOImpl implements IAuthBLO {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Override
    public LoginResponseDTO login(User loginUser) {
        log.info("用户登录请求，用户名: {}", loginUser.getUsername());

        if (loginUser.getUsername() == null || loginUser.getUsername().trim().isEmpty()) {
            log.warn("登录失败：用户名为空");
            throw new IllegalArgumentException("用户名不能为空");
        }

        if (loginUser.getPassword() == null || loginUser.getPassword().trim().isEmpty()) {
            log.warn("登录失败：密码为空");
            throw new IllegalArgumentException("密码不能为空");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginUser.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            log.warn("登录失败：用户不存在，用户名: {}", loginUser.getUsername());
            throw new IllegalArgumentException("用户名或密码错误");
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            log.warn("登录失败：账号已被禁用，用户名: {}", loginUser.getUsername());
            throw new IllegalArgumentException("账号已被禁用");
        }

        String encryptedPassword = DigestUtils.md5DigestAsHex(loginUser.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!encryptedPassword.equals(user.getPassword())) {
            log.warn("登录失败：密码错误，用户名: {}", loginUser.getUsername());
            throw new IllegalArgumentException("用户名或密码错误");
        }

        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());

        redisCacheUtil.evictUserPermissionsCache(user.getId());

        log.info("用户登录成功，userId: {}, username: {}", user.getId(), user.getUsername());
        return response;
    }

    @Override
    public PermissionDTO getCurrentUserPermissions() {
        Long userId = StpUtil.getLoginIdAsLong();
        log.debug("获取用户权限，userId: {}", userId);

        Object cachedPermissions = redisCacheUtil.getCachedUserPermissions(userId);
        if (cachedPermissions != null) {
            log.info("权限缓存命中，userId: {}", userId);
            return (PermissionDTO) cachedPermissions;
        }

        LambdaQueryWrapper<UserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(urWrapper);

        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        List<String> roleCodes = roleIds.stream()
                .map(roleId -> {
                    Role role = roleMapper.selectById(roleId);
                    return role != null ? role.getRoleCode() : null;
                })
                .filter(code -> code != null)
                .collect(Collectors.toList());

        List<String> permissions = buildPermissionsByRoles(roleCodes);

        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setRoles(roleCodes);
        permissionDTO.setPermissions(permissions);

        redisCacheUtil.cacheUserPermissions(userId, permissionDTO, 30, TimeUnit.MINUTES);

        log.info("获取用户权限成功，userId: {}, roles: {}", userId, roleCodes);
        return permissionDTO;
    }

    @Override
    public boolean checkPermission(String permission) {
        if (!StpUtil.isLogin()) {
            log.debug("用户未登录，权限检查失败");
            return false;
        }

        PermissionDTO permissions = getCurrentUserPermissions();
        boolean hasPermission = permissions.getPermissions().contains(permission);
        log.debug("权限检查，userId: {}, permission: {}, result: {}",
                StpUtil.getLoginIdAsLong(), permission, hasPermission);
        return hasPermission;
    }

    @Override
    public boolean hasRole(String roleCode) {
        if (!StpUtil.isLogin()) {
            log.debug("用户未登录，角色检查失败");
            return false;
        }

        PermissionDTO permissions = getCurrentUserPermissions();
        boolean hasRole = permissions.getRoles().contains(roleCode);
        log.debug("角色检查，userId: {}, roleCode: {}, result: {}",
                StpUtil.getLoginIdAsLong(), roleCode, hasRole);
        return hasRole;
    }

    private List<String> buildPermissionsByRoles(List<String> roleCodes) {
        List<String> permissions = Arrays.asList(
                "article:view",
                "article:add",
                "article:edit",
                "article:delete",
                "article:submit_audit",
                "audit:first_check",
                "audit:recheck",
                "audit:approve",
                "audit:reject"
        );

        return permissions.stream()
                .filter(permission -> matchPermissionWithRole(permission, roleCodes))
                .collect(Collectors.toList());
    }

    private boolean matchPermissionWithRole(String permission, List<String> roleCodes) {
        if (roleCodes.contains("admin")) {
            return true;
        }

        if (permission.startsWith("article:") && roleCodes.contains("author")) {
            return true;
        }

        if ((permission.equals("audit:first_check") || permission.equals("audit:approve") || permission.equals("audit:reject"))
                && roleCodes.contains("first_check")) {
            return true;
        }

        if ((permission.equals("audit:recheck") || permission.equals("audit:approve") || permission.equals("audit:reject"))
                && roleCodes.contains("recheck")) {
            return true;
        }

        return false;
    }
}
