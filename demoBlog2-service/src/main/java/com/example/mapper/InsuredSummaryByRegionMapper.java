package com.example.mapper;

import com.example.entity.InsuredSummaryByRegion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 各统筹区参保人员总表数据 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2026-05-09
 */
public interface InsuredSummaryByRegionMapper extends BaseMapper<InsuredSummaryByRegion> {

    /**
     * 按月份统计平均参保人数
     * @param startDate 开始日期 yyyyMM
     * @param endDate 结束日期 yyyyMM
     * @return List<Map> 包含 fee_period 和 avg_person_count
     */
    List<Map<String, Object>> selectAvgPersonCountByMonth(@Param("startDate") String startDate,@Param("endDate") String endDate);
    List<Map<String, Object>> selectAvgPaymentAmountCountByMonth(@Param("startDate") String startDate,@Param("endDate") String endDate);
    List<Map<String, Object>> selectAgeGroupDistribution(@Param("startDate") String startDate,@Param("endDate") String endDate);

}
