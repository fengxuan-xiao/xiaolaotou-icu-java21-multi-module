package com.example.api.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
public class BlogAttachmentsDTO {
    private Long id;
    private Long articleId;
    private Long uploadUserId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private Byte isDelete;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
