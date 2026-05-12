package com.example.api.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
public class ImportRecordDTO {
    private Long id;
    private String title;
    private String content;
    private String authorName;
    private String tags;
    private String status;
    private String failReason;
    private Integer rowIndex;
    private LocalDateTime importTime;
    private String operator;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
