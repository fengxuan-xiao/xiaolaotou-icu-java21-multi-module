package com.example.api.dto;

import lombok.Data;

@Data
public class VisualizationQueryDTO {
    private String province;
    private String city;
    private String district;

    // 格式: yyyyMM (例如: 202301)
    private String startDate;
    private String endDate;
}
