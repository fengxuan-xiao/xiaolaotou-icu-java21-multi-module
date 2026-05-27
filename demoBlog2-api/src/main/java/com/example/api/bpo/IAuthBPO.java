package com.example.api.bpo;

import com.example.api.dto.LoginResponseDTO;
import com.example.api.dto.PermissionDTO;
import com.example.api.dto.UserDTO;
import com.example.api.dto.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "authBPO", value = "xgh-demoBlog2-service", path = "/auth")
public interface IAuthBPO {

    @PostMapping("/login")
    Result<LoginResponseDTO> login(@RequestBody UserDTO userDTO);

    @GetMapping("/permissions")
    Result<PermissionDTO> getPermissions();
}
