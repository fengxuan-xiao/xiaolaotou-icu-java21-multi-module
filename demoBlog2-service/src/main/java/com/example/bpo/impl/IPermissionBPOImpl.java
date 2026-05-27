package com.example.bpo.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.api.bpo.IPermissionBPO;
import com.example.api.dto.AssignRoleDTO;
import com.example.api.dto.PermissionDTO;
import com.example.api.dto.RoleDTO;
import com.example.api.dto.UserDTO;
import com.example.api.dto.common.PageResult;
import com.example.api.dto.common.Result;
import com.example.blo.IPermissionBLO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permission")
@Slf4j
@Tag(name = "权限管理", description = "角色分配与权限查询接口")
public class IPermissionBPOImpl implements IPermissionBPO {

    @Autowired
    private IPermissionBLO permissionBLO;

    @Override
    @Operation(summary = "获取所有角色列表", description = "获取系统中所有可用的角色")
    @GetMapping("/role/list")
    public Result<List<RoleDTO>> getRoleList() {
        try {
            List<RoleDTO> roleList = permissionBLO.getRoleList();
            return Result.success(roleList);
        } catch (Exception e) {
            log.error("获取角色列表失败", e);
            return Result.error("获取角色列表失败");
        }
    }

    @Override
    @Operation(summary = "获取用户列表", description = "分页获取用户列表及其角色信息")
    @GetMapping("/user/list")
    public Result<PageResult<UserDTO>> getUserList(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        try {
            if (pageNum == null) pageNum = 1;
            if (pageSize == null) pageSize = 10;

            IPage<UserDTO> userPage = permissionBLO.getUserList(pageNum, pageSize);

            PageResult<UserDTO> pageResult = PageResult.of(
                    userPage.getRecords(),
                    userPage.getTotal(),
                    (int) userPage.getCurrent(),
                    (int) userPage.getSize()
            );

            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return Result.error("获取用户列表失败");
        }
    }

    @Override
    @Operation(summary = "为用户分配角色", description = "为指定用户分配一个或多个角色")
    @PostMapping("/user/assign-role")
    public Result<Void> assignRoleToUser(@RequestBody AssignRoleDTO assignRoleDTO) {
        try {
//            if (!StpUtil.isLogin()) {
//                return Result.error("未登录");
//            }

            permissionBLO.assignRoleToUser(assignRoleDTO);
            return Result.success(null, "分配成功");
        } catch (IllegalArgumentException e) {
            log.warn("分配角色失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("分配角色异常", e);
            return Result.error("分配角色失败，请稍后重试");
        }
    }

    @Override
    @Operation(summary = "获取当前用户权限", description = "获取当前登录用户的角色和权限标识")
    @GetMapping("/auth/permissions")
    public Result<PermissionDTO> getCurrentUserPermissions() {
        try {
            if (!StpUtil.isLogin()) {
                return Result.error("未登录");
            }

            PermissionDTO permissions = permissionBLO.getCurrentUserPermissions();
            return Result.success(permissions);
        } catch (Exception e) {
            log.error("获取用户权限失败", e);
            return Result.error("获取用户权限失败");
        }
    }
}
