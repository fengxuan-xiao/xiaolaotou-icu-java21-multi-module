package com.example.mapper;

import com.example.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户登录注册表 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2026-05-06
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 查询用户活动统计
     * @param params 查询参数
     * @return 用户活动统计数据
     */
    List<Map<String, Object>> selectUserActivityStats(Map<String, Object> params);

    User selectUserWithRoles(@Param("username") String username);
}
