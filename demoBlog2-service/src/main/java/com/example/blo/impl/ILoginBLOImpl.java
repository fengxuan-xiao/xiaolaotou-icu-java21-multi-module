package com.example.blo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.api.dto.UserDTO;
import com.example.api.dto.common.Result;
import com.example.blo.ILoginBLO;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.utils.JwtUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ILoginBLOImpl implements ILoginBLO {

    @Resource
    private UserMapper userMapper;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    @Override
    public Result<String> register(UserDTO userDTO) {

        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, user.getUsername())
        );
        if (count > 0) {
            return Result.error("账号已存在");
        }

        user.setPassword(encoder.encode(user.getPassword()));

        user.setStatus((byte) 1);

        userMapper.insert(user);
        return Result.success("注册成功");
    }



    @Override
    public Result<String> login(UserDTO userDTO) {



        User dbUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, userDTO.getUsername())
        );

        if (dbUser == null) {
            return Result.error("账号不存在");
        }

        // 校验密码
        if (!encoder.matches(userDTO.getPassword(), dbUser.getPassword())) {
            return Result.error("密码错误");
        }

        // 账号是否被禁用
        if (dbUser.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }



        // 2. 生成 Token
        // 通常存入用户ID或用户名，建议存唯一标识如 ID
        String token = JwtUtil.generateToken(String.valueOf(dbUser.getId()));

        log.info("用户登录成功，用户ID: {}, 用户名: {}", dbUser.getId(), dbUser.getUsername());


        return Result.success(token);
    }
}
