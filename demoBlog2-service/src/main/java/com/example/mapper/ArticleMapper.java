package com.example.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import feign.Param;

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

}
