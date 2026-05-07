package com.example.api.bpo;

import com.example.api.dto.UserDTO;
import com.example.api.dto.common.Result;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "loginBPO",value = "xgh-demoBlog2-service", path = "/user")
public interface ILoginBPO {
    @PostMapping("/register")
    public Result<UserDTO> register(@RequestBody UserDTO userDTO);

    @PostMapping("/login")
    public Result<String> login(@RequestBody UserDTO userDTO);

}
