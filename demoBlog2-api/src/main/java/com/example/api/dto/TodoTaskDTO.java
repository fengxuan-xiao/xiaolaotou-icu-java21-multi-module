package com.example.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "待办任务DTO")
public class TodoTaskDTO {

    @Schema(description = "文章ID")
    private Long id;

    @Schema(description = "文章标题")
    private String articleTitle;

    @Schema(description = "作者名称")
    private String authorName;

    @Schema(description = "流程实例ID")
    private String processInstanceId;

    @Schema(description = "任务ID")
    private String taskId;

    @Schema(description = "审核状态：1-待初审，3-待复核")
    private Integer auditStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
