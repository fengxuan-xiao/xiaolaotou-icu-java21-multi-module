package com.example.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import feign.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文章表 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2026-05-11
 */
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 逻辑删除文章
     * @param id 文章ID
     * @return 影响行数
     */
    int logicDeleteById(@Param("id") Long id);


    /**
     * 分页查询文章（包含附件信息）
     * @param page 分页对象
     * @return 文章分页结果
     */
    IPage<Article> selectArticlePageWithAttachments(Page<Article> page);


    /**
     * 统计已发布文章数量
     * @return 已发布文章数
     */
    Integer selectPublishedCount();

    /**
     * 统计草稿文章数量
     * @return 草稿文章数
     */
    Integer selectDraftCount();

    /**
     * 统计总浏览量
     * @return 总浏览量
     */
    Long selectTotalViewCount();

    /**
     * 按日期范围查询文章列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 文章列表
     */
    List<Map<String, Object>> selectArticleListByDateRange(@Param("startDate") String startDate,
                                                           @Param("endDate") String endDate);


    /**
     * 分类统计
     * @return 分类统计数据
     */
    List<Map<String, Object>> selectCategoryStatistics();

    /**
     * 标签统计
     * @return 标签统计数据
     */
    List<Map<String, Object>> selectTagStatistics();

    List<Map<String, Object>> selectTodoTasks();

    Article selectArticleByProcessInstanceId(@Param("processInstanceId") String processInstanceId);

    int updateAuditStatus(@Param("articleId") Long articleId,
                          @Param("auditStatus") Byte auditStatus,
                          @Param("taskId") String taskId,
                          @Param("auditRemark") String auditRemark);

    int publishArticle(@Param("articleId") Long articleId);

}
