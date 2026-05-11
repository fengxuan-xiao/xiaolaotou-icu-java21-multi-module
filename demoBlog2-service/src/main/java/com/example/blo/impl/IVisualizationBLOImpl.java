package com.example.blo.impl;

import com.example.api.dto.VisualizationQueryDTO;
import com.example.api.dto.VisualizationVO;
import com.example.blo.IVisualizationBLO;
import com.example.mapper.InsuredSummaryByRegionMapper;
import com.example.mapper.MonthlyUnitNetValueMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class IVisualizationBLOImpl implements IVisualizationBLO {


    @Resource
    private InsuredSummaryByRegionMapper insuredSummaryByRegionMapper;

    @Resource
    private MonthlyUnitNetValueMapper monthlyUnitNetValueMapper;

    @Override
    public VisualizationVO getDate(VisualizationQueryDTO visualizationQueryDTO) {

        // 1. 参数校验
        if (visualizationQueryDTO.getStartDate() == null || visualizationQueryDTO.getEndDate() == null) {
            throw new IllegalArgumentException("日期范围不能为空");
        }

        String startDate = visualizationQueryDTO.getStartDate();
        String endDate = visualizationQueryDTO.getEndDate();

        log.info("查询可视化数据，日期范围: {} - {}", startDate, endDate);

        // 2. 查询数据库获取按月统计的数据
        List<Map<String, Object>> rawData1 = insuredSummaryByRegionMapper.selectAvgPersonCountByMonth(startDate, endDate);
        List<Map<String, Object>> rawData2 = monthlyUnitNetValueMapper.selectUnitnetValueByPriceDate(startDate, endDate);
        List<Map<String, Object>> rawData3 = insuredSummaryByRegionMapper.selectAvgPaymentAmountCountByMonth(startDate, endDate);
        List<Map<String, Object>> rawData4 = insuredSummaryByRegionMapper.selectAgeGroupDistribution(startDate, endDate);

        // 3. 组装前端要求的格式
        VisualizationVO vo = new VisualizationVO();

        // 构建 chart1 - 平均参保人数（整数）
        vo.setChart1(buildChartData(rawData1, "fee_period", "avg_person_count", false));

        // 构建 chart2 - 单位净值（保留6位小数）
        vo.setChart2(buildChartData(rawData2, "price_date", "unit_net_value", true));

        // 构建 chart3 - 平均缴费金额（保留6位小数）
        vo.setChart3(buildChartData(rawData3, "fee_period", "avg_payment_amount_count", true));

        // 构建 chart4 - 饼图数据（平均年龄分布）
        vo.setChart4(buildPercentagePieData(rawData4));

        log.info("可视化数据组装完成");

        return vo;
    }

    /**
     * 构建图表数据（柱状图/折线图）
     * @param rawData 原始数据
     * @param categoryField 分类字段名
     * @param dataField 数据字段名
     * @param keepSixDecimal 是否保留6位小数
     * @return ChartData
     */
    private VisualizationVO.ChartData buildChartData(List<Map<String, Object>> rawData,
                                                     String categoryField,
                                                     String dataField,
                                                     boolean keepSixDecimal) {
        VisualizationVO.ChartData chartData = new VisualizationVO.ChartData();

        List<String> categories = new ArrayList<>();
        List<Double> data = new ArrayList<>();

        if (rawData == null || rawData.isEmpty()) {
            chartData.setCategories(categories);
            chartData.setData(data);
            return chartData;
        }

        for (Map<String, Object> row : rawData) {
            String category = (String) row.get(categoryField);
            Object valueObj = row.get(dataField);

            categories.add(category != null ? category : "");

            if (valueObj != null) {
                double value = ((Number) valueObj).doubleValue();

                if (keepSixDecimal) {
                    // 保留6位小数，四舍五入
                    BigDecimal bd = BigDecimal.valueOf(value);
                    bd = bd.setScale(6, BigDecimal.ROUND_HALF_UP);
                    data.add(bd.doubleValue());
                } else {
                    // 取整数
                    data.add((double) Math.round(value));
                }
            } else {
                data.add(0.0);
            }
        }

        chartData.setCategories(categories);
        chartData.setData(data);

        return chartData;
    }

    /**
     * 构建饼图数据
     * @param rawData 原始数据
     * @param nameField 名称字段名
     * @param valueField 值字段名
     * @return List<PieData>
     */
    private List<VisualizationVO.PieData> buildPieData(List<Map<String, Object>> rawData,
                                                       String nameField,
                                                       String valueField) {
        List<VisualizationVO.PieData> pieDataList = new ArrayList<>();

        if (rawData == null || rawData.isEmpty()) {
            return pieDataList;
        }

        for (Map<String, Object> row : rawData) {
            VisualizationVO.PieData pieData = new VisualizationVO.PieData();

            String name = (String) row.get(nameField);
            Object valueObj = row.get(valueField);

            pieData.setName(name != null ? name : "未知");

            if (valueObj != null) {
                double value = ((Number) valueObj).doubleValue();
                pieData.setValue((double) Math.round(value));
            } else {
                pieData.setValue(0.0);
            }

            pieDataList.add(pieData);
        }

        return pieDataList;
    }



    /**
     * 构建百分比饼图数据（总和为100）
     * @param rawData 原始数据，包含 age_group 和 person_count
     * @return List<PieData>
     */
    private List<VisualizationVO.PieData> buildPercentagePieData(List<Map<String, Object>> rawData) {
        List<VisualizationVO.PieData> pieDataList = new ArrayList<>();

        if (rawData == null || rawData.isEmpty()) {
            return pieDataList;
        }

        // 1. 计算总人数
        int totalCount = 0;
        for (Map<String, Object> row : rawData) {
            Object countObj = row.get("person_count");
            if (countObj != null) {
                totalCount += ((Number) countObj).intValue();
            }
        }

        // 避免除以零
        if (totalCount == 0) {
            return pieDataList;
        }

        // 2. 计算每个年龄段的百分比
        double totalPercentage = 0.0;
        for (int i = 0; i < rawData.size(); i++) {
            Map<String, Object> row = rawData.get(i);
            VisualizationVO.PieData pieData = new VisualizationVO.PieData();

            String ageGroup = (String) row.get("age_group");
            Object countObj = row.get("person_count");

            pieData.setName(ageGroup != null ? ageGroup : "未知");

            int count = countObj != null ? ((Number) countObj).intValue() : 0;

            // 计算百分比，保留2位小数
            double percentage = (double) count / totalCount * 100;
            BigDecimal bd = BigDecimal.valueOf(percentage);
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            double roundedPercentage = bd.doubleValue();

            // 最后一个元素使用 100 - 已累加的百分比，确保总和正好是100
            if (i == rawData.size() - 1) {
                roundedPercentage = 100.0 - totalPercentage;
            }

            pieData.setValue(roundedPercentage);
            totalPercentage += roundedPercentage;

            pieDataList.add(pieData);
        }

        return pieDataList;
    }
}
