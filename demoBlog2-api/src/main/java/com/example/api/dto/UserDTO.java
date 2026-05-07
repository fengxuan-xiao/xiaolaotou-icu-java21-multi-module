package com.example.api.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private Byte status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
