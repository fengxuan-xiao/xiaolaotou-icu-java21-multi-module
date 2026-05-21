package com.example.api.bpo;

import com.example.api.dto.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "reportBPO",value = "xgh-demoBlog2-service", path = "/report")
public interface IReportDataBPO {

    //Map<String, Object> getArticleStatistics(Map<String, Object> params);
    @PostMapping("/statistics")
    //@Operation(summary = "获取文章统计数据", description = "供报表模板调用")
    public Result<Map<String, Object>> getArticleStatistics(@RequestBody(required = false) Map<String, Object> params);

    //List<Map<String, Object>> getArticleListByDate(Map<String, Object> params);
    @PostMapping("/list-by-date")
        //@Operation(summary = "获取按日期分组的文章列表", description = "供报表模板调用")
    public Result<List<Map<String, Object>>> getArticleListByDate(@RequestBody(required = false) Map<String, Object> params);

    //List<Map<String, Object>> getUserActivityStats(Map<String, Object> params);
    @PostMapping("/activity")
            //@Operation(summary = "获取用户活动统计", description = "供报表模板调用")
    public Result<List<Map<String, Object>>> getUserActivityStats(@RequestBody(required = false) Map<String, Object> params);

    //Map<String, Object> getContentAnalysis(Map<String, Object> params);
    @PostMapping("/analysis")
                //@Operation(summary = "获取内容分析数据", description = "供报表模板调用")
    public Result<Map<String, Object>> getContentAnalysis(@RequestBody(required = false) Map<String, Object> params);

}
