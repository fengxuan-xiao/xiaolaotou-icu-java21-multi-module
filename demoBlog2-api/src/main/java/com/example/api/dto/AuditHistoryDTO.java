package com.example.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "审核历史DTO")
public class AuditHistoryDTO {

    @Schema(description = "操作人姓名")
    private String operatorName;

    @Schema(description = "操作动作")
    private String action;

    @Schema(description = "审批意见")
    private String comment;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
