package com.example.blo.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.dto.AssignRoleDTO;
import com.example.api.dto.PermissionDTO;
import com.example.api.dto.RoleDTO;
import com.example.api.dto.UserDTO;
import com.example.blo.IPermissionBLO;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.entity.UserRole;
import com.example.mapper.RoleMapper;
import com.example.mapper.UserMapper;
import com.example.mapper.UserRoleMapper;
import com.example.utils.RedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IPermissionBLOImpl implements IPermissionBLO {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Override
    public List<RoleDTO> getRoleList() {
        log.info("获取所有角色列表");

        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Role::getId);

        List<Role> roles = roleMapper.selectList(wrapper);

        return roles.stream().map(role -> {
            RoleDTO dto = new RoleDTO();
            BeanUtils.copyProperties(role, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public IPage<UserDTO> getUserList(Integer pageNum, Integer pageSize) {
        log.info("获取用户列表，pageNum: {}, pageSize: {}", pageNum, pageSize);

        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(User::getCreateTime);

        IPage<User> userPage = userMapper.selectPage(page, wrapper);

        Page<UserDTO> dtoPage = new Page<>();
        dtoPage.setCurrent(userPage.getCurrent());
        dtoPage.setSize(userPage.getSize());
        dtoPage.setTotal(userPage.getTotal());
        dtoPage.setPages(userPage.getPages());

        List<UserDTO> dtoList = userPage.getRecords().stream().map(user -> {
            UserDTO dto = new UserDTO();
            BeanUtils.copyProperties(user, dto);

            LambdaQueryWrapper<UserRole> urWrapper = new LambdaQueryWrapper<>();
            urWrapper.eq(UserRole::getUserId, user.getId());
            List<UserRole> userRoles = userRoleMapper.selectList(urWrapper);

            List<Long> roleIds = userRoles.stream()
                    .map(UserRole::getRoleId)
                    .collect(Collectors.toList());

            dto.setRoleIds(roleIds);

            return dto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoleToUser(AssignRoleDTO assignRoleDTO) {
        Long userId = assignRoleDTO.getUserId();
        List<Long> roleIds = assignRoleDTO.getRoleIds();

        log.info("为用户分配角色，userId: {}, roleIds: {}", userId, roleIds);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        for (Long roleId : roleIds) {
            Role role = roleMapper.selectById(roleId);
            if (role == null) {
                throw new IllegalArgumentException("角色不存在，roleId: " + roleId);
            }
        }

        LambdaQueryWrapper<UserRole> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(UserRole::getUserId, userId);
        userRoleMapper.delete(deleteWrapper);

        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRole> userRoles = new ArrayList<>();
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoles.add(userRole);
            }

            for (UserRole userRole : userRoles) {
                userRoleMapper.insert(userRole);
            }
        }

        redisCacheUtil.evictUserPermissions(userId);

        log.info("用户角色分配成功，userId: {}", userId);
    }

    @Override
    public PermissionDTO getCurrentUserPermissions() {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("获取用户权限，userId: {}", userId);

        PermissionDTO cached = (PermissionDTO) redisCacheUtil.getCachedUserPermissions(userId);
        if (cached != null) {
            log.debug("从缓存获取用户权限，userId: {}", userId);
            return cached;
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

    private List<String> buildPermissionsByRoles(List<String> roleCodes) {
        List<String> permissions = new ArrayList<>();

        if (roleCodes.contains("admin")) {
            permissions.addAll(List.of(
                    "article:view",
                    "article:add",
                    "article:edit",
                    "article:delete",
                    "article:submit_audit",
                    "audit:first_check",
                    "audit:recheck",
                    "audit:approve",
                    "audit:reject"
            ));
        } else {
            if (roleCodes.contains("author")) {
                permissions.addAll(List.of(
                        "article:view",
                        "article:add",
                        "article:edit",
                        "article:submit_audit"
                ));
            }

            if (roleCodes.contains("first_check")) {
                permissions.addAll(List.of(
                        "article:view",
                        "audit:first_check",
                        "audit:approve",
                        "audit:reject"
                ));
            }

            if (roleCodes.contains("recheck")) {
                permissions.addAll(List.of(
                        "article:view",
                        "audit:recheck",
                        "audit:approve",
                        "audit:reject"
                ));
            }
        }

        return permissions.stream().distinct().collect(Collectors.toList());
    }
}
