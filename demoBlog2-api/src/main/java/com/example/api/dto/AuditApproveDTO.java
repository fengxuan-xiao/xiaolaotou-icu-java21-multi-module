package com.example.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "审批请求DTO")
public class AuditApproveDTO {

    @NotBlank(message = "任务ID不能为空")
    @Schema(description = "Flowable任务ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String taskId;

    @NotBlank(message = "流程实例ID不能为空")
    @Schema(description = "流程实例ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String processInstanceId;

    @NotNull(message = "审批结果不能为空")
    @Schema(description = "是否通过：true-通过，false-驳回", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean approved;

    @Schema(description = "审批意见")
    private String comment;
}
