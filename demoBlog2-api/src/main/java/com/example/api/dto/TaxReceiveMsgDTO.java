package com.example.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TaxReceiveMsgDTO {
    private Long id;
    private String dataNo;
    private String msgId;
    private String unitName;
    private String unitCode;
    private String feePeriod;
    private String feePeriodRef;
    private String areaCode;
    private BigDecimal amount;
    private String taxSerialNo;
    private Byte receiveStatus;
    private LocalDateTime processTime;
    private Byte processResult;
    private String processMsg;
    private Byte replyStatus;
    private LocalDateTime replyTime;
    private Byte isRepeat;
    private Byte status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Byte isDeleted;
}
