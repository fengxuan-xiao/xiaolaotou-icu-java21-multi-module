package com.example.api.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
public class UserRoleDTO {
    private Long id;
    private Long userId;
    private Long roleId;
    private LocalDateTime createTime;
}
