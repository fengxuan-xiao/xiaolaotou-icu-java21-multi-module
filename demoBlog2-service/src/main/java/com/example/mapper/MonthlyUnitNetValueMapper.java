package com.example.mapper;

import com.example.entity.MonthlyUnitNetValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 各月单位净值总表数据 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2026-05-09
 */
public interface MonthlyUnitNetValueMapper extends BaseMapper<MonthlyUnitNetValue> {
    List<Map<String, Object>> selectUnitnetValueByPriceDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

}
