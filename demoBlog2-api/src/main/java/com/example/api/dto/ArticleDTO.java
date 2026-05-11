package com.example.api.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
public class ArticleDTO {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String authorName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Byte isDeleted;
    private Byte status;
    private String summary;
    private String coverImage;
    private Long categoryId;
    private String tags;
    private Long viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer sort;
}
