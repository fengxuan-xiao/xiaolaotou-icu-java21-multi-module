package com.example.blo.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.dto.ArticleDTO;
import com.example.api.dto.common.Result;
import com.example.blo.IArticleBLO;
import com.example.entity.Article;
import com.example.mapper.ArticleMapper;
import com.example.utils.Idempotent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IArticleBLOImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleBLO  {




    @Override
    public IPage<ArticleDTO> getPage(Integer page, Integer pageSize) {
        Page<Article> articlePage = new Page<>(page, pageSize);
        IPage<Article> result = this.page(articlePage);

        Page<ArticleDTO> dtoPage = new Page<>();
        dtoPage.setCurrent(result.getCurrent());
        dtoPage.setSize(result.getSize());
        dtoPage.setTotal(result.getTotal());
        dtoPage.setPages(result.getPages());

        List<ArticleDTO> dtoList = result.getRecords().stream()
                .map(article -> {
                    ArticleDTO dto = new ArticleDTO();
                    BeanUtils.copyProperties(article, dto);
                    return dto;
                })
                .collect(Collectors.toList());

        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    @Override
    @Idempotent(key = "save_article", expireTime = 3000, message = "文章保存中，请勿重复提交")
    @Transactional(rollbackFor = Exception.class)
    public void saveArticle(Article article) {
        log.info("开始保存文章: {}", article);

        if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("文章标题不能为空");
        }

        if (article.getContent() == null || article.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("文章内容不能为空");
        }

        boolean saved = this.save(article);
        if (saved) {
            Result.success(article, "保存成功");
        } else {
            throw new RuntimeException("文章保存失败");
        }



        log.info("文章保存成功，ID: {}", article.getId());
    }
}
