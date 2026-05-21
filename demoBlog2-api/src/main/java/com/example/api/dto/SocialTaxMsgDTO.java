package com.example.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SocialTaxMsgDTO {
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
    private Byte sendStatus;
    private LocalDateTime sendTime;
    private Byte receiveStatus;
    private String receiveMsg;
    private LocalDateTime receiveTime;
    private String mqTopic;
    private String mqGroup;
    private Byte status;
    private Integer retryCount;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Byte isDeleted;
}
