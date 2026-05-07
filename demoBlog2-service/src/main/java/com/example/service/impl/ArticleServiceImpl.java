package com.example.service.impl;

import com.example.entity.Article;
import com.example.mapper.ArticleMapper;
import com.example.service.IArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 文章表 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2026-05-06
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

}
