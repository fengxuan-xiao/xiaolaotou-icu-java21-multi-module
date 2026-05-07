package com.example.api.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
public class ContentDTO {
    private Integer id;
    private String title;
    private String content;
    private LocalDateTime time;
}
