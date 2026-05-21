package com.example.bpo.impl;

import com.example.api.bpo.IReportDataBPO;
import com.example.api.dto.common.Result;
import com.example.blo.IReportDataBLO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report")
@Slf4j
@Tag(name = "报表数据接口", description = "提供JimuReport报表所需的数据接口")
public class IReportDataBPOImpl implements IReportDataBPO {

    @Resource
    private IReportDataBLO reportDataBLO;

    @Override
    @PostMapping("/statistics")
    @Operation(summary = "获取文章统计数据", description = "供报表模板调用")
    public Result<Map<String, Object>> getArticleStatistics(@RequestBody(required = false) Map<String, Object> params) {
        try {
            Map<String, Object> data = reportDataBLO.getArticleStatistics(params);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取文章统计数据失败", e);
            return Result.error("获取数据失败: " + e.getMessage());
        }
    }

    @Override
    @PostMapping("/list-by-date")
    @Operation(summary = "获取按日期分组的文章列表", description = "供报表模板调用")
    public Result<List<Map<String, Object>>> getArticleListByDate(@RequestBody(required = false) Map<String, Object> params) {
        try {
            List<Map<String, Object>> data = reportDataBLO.getArticleListByDate(params);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取文章列表失败", e);
            return Result.error("获取数据失败: " + e.getMessage());
        }
    }

    @Override
    @PostMapping("/activity")
    @Operation(summary = "获取用户活动统计", description = "供报表模板调用")
    public Result<List<Map<String, Object>>> getUserActivityStats(@RequestBody(required = false) Map<String, Object> params) {
        try {
            List<Map<String, Object>> data = reportDataBLO.getUserActivityStats(params);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取用户活动统计失败", e);
            return Result.error("获取数据失败: " + e.getMessage());
        }
    }

    @Override
    @PostMapping("/analysis")
    @Operation(summary = "获取内容分析数据", description = "供报表模板调用")
    public Result<Map<String, Object>> getContentAnalysis(@RequestBody(required = false) Map<String, Object> params) {
        try {
            Map<String, Object> data = reportDataBLO.getContentAnalysis(params);
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取内容分析数据失败", e);
            return Result.error("获取数据失败: " + e.getMessage());
        }
    }
}
