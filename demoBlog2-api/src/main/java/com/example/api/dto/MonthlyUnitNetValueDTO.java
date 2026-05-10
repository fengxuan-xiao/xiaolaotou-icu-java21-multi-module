package com.example.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MonthlyUnitNetValueDTO {
    private Long id;
    private String priceDate;
    private BigDecimal unitNetValue;
    private Byte isBusinessUse;
    private LocalDateTime generateTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;
    private Byte validStatus;
}
