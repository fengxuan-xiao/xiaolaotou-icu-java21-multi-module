package com.example.blo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.api.dto.ArticleDTO;
import com.example.api.dto.UserDTO;
import com.example.api.dto.common.Result;
import com.example.entity.Article;
import org.springframework.web.multipart.MultipartFile;

public interface IArticleBLO extends IService<Article> {



    IPage<ArticleDTO> getPage(Integer page, Integer pageSize);

    IPage<ArticleDTO> getPageHome(Integer page, Integer pageSize);

    //boolean save = articleService.save(article);
    ArticleDTO saveArticle(Article article, MultipartFile[] files);

    /**
     * 逻辑删除文章
     * @param id 文章ID
     * @return 是否删除成功
     */
    boolean logicDeleteById(String id);

}
