package com.example.blo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.api.dto.AssignRoleDTO;
import com.example.api.dto.PermissionDTO;
import com.example.api.dto.RoleDTO;
import com.example.api.dto.UserDTO;

import java.util.List;

public interface IPermissionBLO {

    List<RoleDTO> getRoleList();

    IPage<UserDTO> getUserList(Integer pageNum, Integer pageSize);

    void assignRoleToUser(AssignRoleDTO assignRoleDTO);

    PermissionDTO getCurrentUserPermissions();
}
