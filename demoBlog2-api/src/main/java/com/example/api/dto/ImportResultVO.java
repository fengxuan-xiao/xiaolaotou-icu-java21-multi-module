package com.example.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultVO {

    /**
     * 总处理行数
     */
    private Integer totalCount;

    /**
     * 成功导入行数
     */
    private Integer successCount;

    /**
     * 失败行数
     */
    private Integer failCount;

    /**
     * 失败详情列表
     */
    private List<ImportErrorDetail> failDetails;

    /**
     * 内部静态类：错误详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportErrorDetail {
        /**
         * Excel 行号 (从1开始)
         */
        private Integer rowIndex;

        /**
         * 错误原因
         */
        private String reason;

        /**
         * 原始数据快照 (可选，方便用户排查)
         */
        private String rawDataSnapshot;
    }
}
