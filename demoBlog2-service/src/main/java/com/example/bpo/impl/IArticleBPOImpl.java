package com.example.bpo.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.api.bpo.IArticleBPO;
import com.example.api.bpo.ILoginBPO;
import com.example.api.dto.ArticleDTO;
import com.example.api.dto.UserDTO;
import com.example.api.dto.common.PageResult;
import com.example.api.dto.common.Result;
import com.example.blo.IArticleBLO;
import com.example.blo.ILoginBLO;
import com.example.entity.Article;
import com.example.entity.User;
import com.example.service.IArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@Slf4j
@Tag(name = "用户首页", description = "用户首页接口")
public class IArticleBPOImpl implements IArticleBPO {

    @Autowired
    private IArticleBLO iArticleBLO;



    @Override
    @Operation(summary = "分页查询数据", description = "分页查询数据接口")
    @GetMapping(value = "/articlesPage")
    public Result<PageResult<ArticleDTO>> articlesPage(@RequestParam(defaultValue = "1") Integer page,
                                                       @RequestParam(defaultValue = "10") Integer pageSize) {

        IPage<ArticleDTO> resultPage = iArticleBLO.getPage(page, pageSize);

        PageResult<ArticleDTO> pageResult = PageResult.of(
                resultPage.getRecords(),
                resultPage.getTotal(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize()
        );

        return Result.success(pageResult);

    }

    @Override
    @Operation(summary = "增加数据", description = "增加数据")
    @PostMapping(value = "/articles")
    public Result<ArticleDTO> articles(ArticleDTO articleDTO, MultipartFile[] files) {

        try {
            Article article = new Article();
            BeanUtils.copyProperties(articleDTO, article);

            iArticleBLO.saveArticle(article, files);

            return Result.success(articleDTO, "保存成功");
        } catch (IllegalArgumentException e) {
            log.warn("参数校验失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (RuntimeException e) {
            log.error("保存文章失败", e);
            return Result.error("保存失败: " + e.getMessage());
        }




    }

    @Override
    @Operation(summary = "删除数据", description = "删除数据")
    @DeleteMapping("/articles/{id}")
    public Result<ArticleDTO> deleteArticle(@PathVariable String id) {

        //articleService.removeById(id); // 调用MyBatis-Plus的removeById()方法删除数据
        //return Result.success("删除成功");
        //boolean success = articleService.removeById(id);

//        if (success) {
//            return Result.success("删除成功");
//        } else {
//            return Result.error("删除失败，文章不存在");
//        }

        //boolean success = IArticleBLO.logicDeleteById(Long.parseLong(id));
        boolean success = iArticleBLO.logicDeleteById(id);

        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败，文章不存在");
        }
    }
}
