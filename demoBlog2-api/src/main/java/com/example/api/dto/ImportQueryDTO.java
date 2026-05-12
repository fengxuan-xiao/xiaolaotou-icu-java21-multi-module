package com.example.api.dto;

import lombok.Data;

@Data
public class ImportQueryDTO {

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 搜索关键词 (姓名/手机号等)
     */
    private String keyword;

    /**
     * 导入状态筛选 (SUCCESS, FAIL, ALL)
     */
    private String status;

    /**
     * 开始时间 (yyyy-MM-dd HH:mm:ss)
     */
    private String startDate;

    /**
     * 结束时间 (yyyy-MM-dd HH:mm:ss)
     */
    private String endDate;
}
