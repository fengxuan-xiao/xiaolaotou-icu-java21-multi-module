package com.example.api.bpo;

import com.example.api.dto.AssignRoleDTO;
import com.example.api.dto.PermissionDTO;
import com.example.api.dto.RoleDTO;
import com.example.api.dto.UserDTO;
import com.example.api.dto.common.PageResult;
import com.example.api.dto.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(contextId = "permissionBPO", value = "xgh-demoBlog2-service", path = "/permission")
public interface IPermissionBPO {

    @GetMapping("/role/list")
    Result<List<RoleDTO>> getRoleList();

    @GetMapping("/user/list")
    Result<PageResult<UserDTO>> getUserList(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize
    );

    @PostMapping("/user/assign-role")
    Result<Void> assignRoleToUser(@RequestBody AssignRoleDTO assignRoleDTO);

    @GetMapping("/auth/permissions")
    Result<PermissionDTO> getCurrentUserPermissions();
}
