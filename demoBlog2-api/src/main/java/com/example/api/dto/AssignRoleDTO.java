package com.example.api.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema(description = "用户角色分配DTO")
public class AssignRoleDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", required = true)
    private Long userId;

    @NotNull(message = "角色ID列表不能为空")
    @Schema(description = "角色ID列表", required = true)
    private List<Long> roleIds;
}
