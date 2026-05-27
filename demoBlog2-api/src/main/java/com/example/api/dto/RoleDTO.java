package com.example.api.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
public class RoleDTO {
    private Long id;
    private String roleCode;
    private String roleName;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
