package com.example.blo.impl;

import com.example.blo.IReportDataBLO;
import com.example.mapper.ArticleMapper;
import com.example.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class IReportDataBLOImpl implements IReportDataBLO {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private UserMapper userMapper;



    @Override
    public Map<String, Object> getArticleStatistics(Map<String, Object> params) {
        log.info("获取文章统计数据，参数: {}", params);

        Map<String, Object> result = new HashMap<>();

        try {
            Integer totalArticles = Math.toIntExact(articleMapper.selectCount(null));
            Integer publishedArticles = articleMapper.selectPublishedCount();
            Integer draftArticles = articleMapper.selectDraftCount();
            Long totalViews = articleMapper.selectTotalViewCount();

            result.put("totalArticles", totalArticles);
            result.put("publishedArticles", publishedArticles);
            result.put("draftArticles", draftArticles);
            result.put("totalViews", totalViews);
            result.put("success", true);
        } catch (Exception e) {
            log.error("获取文章统计数据失败", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getArticleListByDate(Map<String, Object> params) {
        log.info("获取按日期分组的文章列表，参数: {}", params);

        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");

        return articleMapper.selectArticleListByDateRange(startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getUserActivityStats(Map<String, Object> params) {
        log.info("获取用户活动统计，参数: {}", params);

        return userMapper.selectUserActivityStats(params);
    }

    @Override
    public Map<String, Object> getContentAnalysis(Map<String, Object> params) {
        log.info("获取内容分析数据，参数: {}", params);

        Map<String, Object> result = new HashMap<>();

        try {
            List<Map<String, Object>> categoryStats = articleMapper.selectCategoryStatistics();
            List<Map<String, Object>> tagStats = articleMapper.selectTagStatistics();

            result.put("categoryStats", categoryStats);
            result.put("tagStats", tagStats);
            result.put("success", true);
        } catch (Exception e) {
            log.error("获取内容分析数据失败", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }
}
