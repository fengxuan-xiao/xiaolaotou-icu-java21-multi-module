package com.example.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "税务消息处理请求")
public class TaxProcessDTO {

    @Schema(description = "处理结果：0-成功，1-失败，2-重复数据")
    private Integer result;

    @Schema(description = "处理说明")
    private String msg;
}
