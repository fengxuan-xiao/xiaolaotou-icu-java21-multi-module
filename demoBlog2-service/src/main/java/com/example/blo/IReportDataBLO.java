package com.example.blo;

import java.util.List;
import java.util.Map;

public interface IReportDataBLO {

    Map<String, Object> getArticleStatistics(Map<String, Object> params);

    List<Map<String, Object>> getArticleListByDate(Map<String, Object> params);

    List<Map<String, Object>> getUserActivityStats(Map<String, Object> params);

    Map<String, Object> getContentAnalysis(Map<String, Object> params);
}
