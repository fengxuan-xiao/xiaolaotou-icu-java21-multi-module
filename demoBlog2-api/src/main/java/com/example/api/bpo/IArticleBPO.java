package com.example.api.bpo;

import com.example.api.dto.ArticleDTO;
import com.example.api.dto.common.PageResult;
import com.example.api.dto.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(contextId = "articleBPO",value = "xgh-demoBlog2-service", path = "/api")
public interface IArticleBPO {

    @GetMapping("/articlesPage")
    public Result<PageResult<ArticleDTO>> articlesPage(@RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer pageSize);

    // 新增文章 (POST)  Result<T>统一使用Result<T>封装数据，统一返回JSON数据
    @PostMapping("/articles")
    public Result<ArticleDTO> articles(@RequestBody ArticleDTO articleDTO);

    // 删除文章 (DELETE)
    @DeleteMapping("/articles/{id}")
    public Result<ArticleDTO> deleteArticle(@RequestParam(defaultValue = "1") Integer id);

}
