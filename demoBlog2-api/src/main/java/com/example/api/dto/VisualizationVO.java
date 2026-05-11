package com.example.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class VisualizationVO {
    private ChartData chart1;
    private ChartData chart2;
    private ChartData chart3;
    private List<PieData> chart4;

    @Data
    public static class ChartData {
        private List<String> categories;
        private List<Double> data;
    }

    @Data
    public static class PieData {
        private Double value;
        private String name;
    }
}
