package com.example.api.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
public class InsuredSummaryByRegionDTO {
    private Long id;
    private String regionName;
    private String regionCode;
    private String feePeriod;
    private String relFeePeriod;
    private Integer personCount;
    private BigDecimal avgAge;
    private BigDecimal avgPaymentBase;
    private BigDecimal avgPaymentAmount;
    private LocalDateTime generateTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String remark;
    private Byte validStatus;
}
