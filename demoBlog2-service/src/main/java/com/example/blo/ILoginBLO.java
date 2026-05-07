package com.example.blo;

import com.example.api.dto.UserDTO;
import com.example.api.dto.common.Result;

public interface ILoginBLO {


    Result<String> register(UserDTO userDTO);
    Result<String> login(UserDTO userDTO);
}
