package com.example.api.dto;

//import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
////@Schema(description = "导入记录")
public class ImportRecordVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "文章内容")
    private String content;

    @Schema(description = "作者名称")
    private String authorName;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "导入状态: SUCCESS-成功, FAIL-失败")
    private String status;

    @Schema(description = "失败原因")
    private String failReason;

    @Schema(description = "Excel行号")
    private Integer rowIndex;

    @Schema(description = "导入时间")
    private LocalDateTime importTime;

    @Schema(description = "操作人")
    private String operator;
}

