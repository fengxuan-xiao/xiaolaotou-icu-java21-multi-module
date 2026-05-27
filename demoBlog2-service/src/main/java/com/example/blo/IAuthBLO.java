package com.example.blo;

import com.example.api.dto.LoginResponseDTO;
import com.example.api.dto.PermissionDTO;
import com.example.entity.User;

public interface IAuthBLO {

    LoginResponseDTO login(User user);

    PermissionDTO getCurrentUserPermissions();

    boolean checkPermission(String permission);

    boolean hasRole(String roleCode);
}
