package com.example.bpo.impl;

import com.example.api.bpo.ILoginBPO;
import com.example.api.dto.UserDTO;
import com.example.api.dto.common.Result;
import com.example.blo.ILoginBLO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
@Tag(name = "用户注册", description = "用户注册接口")
public class ILoginBPOImpl implements ILoginBPO {

    @Autowired
    private ILoginBLO iLoginBLO;
    @Override
    @Operation(summary = "用户注册", description = "用户注册接口")
    @PostMapping(value = "/register")
    public Result<UserDTO> register(@RequestBody UserDTO userDTO) {


        Result<String> result = iLoginBLO.register(userDTO);

        Result<UserDTO> response = new Result<>();
        response.setCode(result.getCode());
        response.setMsg(result.getMsg());
        response.setData(userDTO);

        return response;
    }

    @Override
    @Operation(summary = "用户登录", description = "用户登录接口")
    @PostMapping(value = "/login")
    public Result<String> login(@RequestBody UserDTO userDTO) {


        Result<String> result = iLoginBLO.login(userDTO);
        result.setData(result.getMsg());

        return result;
    }
}
