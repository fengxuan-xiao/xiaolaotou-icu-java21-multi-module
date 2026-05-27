package com.example.bpo.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.example.api.bpo.IAuthBPO;
import com.example.api.dto.LoginResponseDTO;
import com.example.api.dto.PermissionDTO;
import com.example.api.dto.UserDTO;
import com.example.api.dto.common.Result;
import com.example.blo.IAuthBLO;
import com.example.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@Tag(name = "认证授权", description = "用户登录和权限管理接口")
public class IAuthBPOImpl implements IAuthBPO {

    @Autowired
    private IAuthBLO authBLO;

    @Override
    @Operation(summary = "用户登录", description = "用户登录并返回Token")
    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@RequestBody UserDTO userDTO) {
        try {
            User user = new User();
            BeanUtils.copyProperties(userDTO, user);

            LoginResponseDTO response = authBLO.login(user);
            return Result.success(response, "登录成功");
        } catch (IllegalArgumentException e) {
            log.warn("登录失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("登录异常", e);
            return Result.error("登录失败，请稍后重试");
        }
    }

    @Override
    @Operation(summary = "获取当前用户权限", description = "获取当前登录用户的角色和权限")
    @GetMapping("/permissions")
    public Result<PermissionDTO> getPermissions() {
        try {
            if (!StpUtil.isLogin()) {
                return Result.error("未登录");
            }

            PermissionDTO permissions = authBLO.getCurrentUserPermissions();
            return Result.success(permissions);
        } catch (Exception e) {
            log.error("获取权限失败", e);
            return Result.error("获取权限失败");
        }
    }
}
