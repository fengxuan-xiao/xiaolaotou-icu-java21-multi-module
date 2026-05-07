package com.example.blo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.api.dto.ArticleDTO;
import com.example.api.dto.UserDTO;
import com.example.api.dto.common.Result;
import com.example.entity.Article;

public interface IArticleBLO extends IService<Article> {



    IPage<ArticleDTO> getPage(Integer page, Integer pageSize);

    //boolean save = articleService.save(article);
    void saveArticle(Article article);
}
