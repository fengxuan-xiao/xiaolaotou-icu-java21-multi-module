package com.example.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "权限信息DTO")
public class PermissionDTO {

    @Schema(description = "角色编码列表")
    private List<String> roles;

    @Schema(description = "按钮级权限标识列表")
    private List<String> permissions;
}
