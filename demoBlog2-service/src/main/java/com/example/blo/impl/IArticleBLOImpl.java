package com.example.blo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.dto.ArticleDTO;
import com.example.api.dto.BlogAttachmentsDTO;
import com.example.api.dto.common.Result;
import com.example.blo.IArticleBLO;
import com.example.entity.Article;
import com.example.entity.BlogAttachments;
import com.example.mapper.ArticleMapper;
import com.example.mapper.BlogAttachmentsMapper;
import com.example.service.IBlogAttachmentsService;
import com.example.service.IFileStorageService;
import com.example.utils.Idempotent;
import com.example.utils.UserContextUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IArticleBLOImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleBLO  {

    @Resource
    private BlogAttachmentsMapper blogAttachmentsMapper;

    @Autowired
    private IFileStorageService fileStorageService;

    @Resource
    private IBlogAttachmentsService blogAttachmentsService;

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

                    List<BlogAttachments> attachments = blogAttachmentsService.list(
                            new LambdaQueryWrapper<BlogAttachments>()
                                    .eq(BlogAttachments::getArticleId, article.getId())
                                    .eq(BlogAttachments::getIsDelete, 0)
                    );

                    if (attachments != null && !attachments.isEmpty()) {
                        List<BlogAttachmentsDTO> attachmentDTOs = attachments.stream()
                                .map(att -> {
                                    BlogAttachmentsDTO attDto = new BlogAttachmentsDTO();
                                    BeanUtils.copyProperties(att, attDto);
                                    return attDto;
                                })
                                .collect(Collectors.toList());
                        dto.setAttachments(attachmentDTOs);
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    @Override
    @Idempotent(key = "save_article", expireTime = 3000, message = "文章保存中，请勿重复提交")
    @Transactional(rollbackFor = Exception.class)
    public void saveArticle(Article article, MultipartFile[] files) {
        log.info("开始保存文章: {}", article);

        if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("文章标题不能为空");
        }

        if (article.getContent() == null || article.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("文章内容不能为空");
        }

        // 获取当前登录用户ID
        Long currentUserId = UserContextUtil.requireCurrentUserId();
        log.info("当前操作用户ID: {}", currentUserId);
        if (currentUserId == null) {
            // 如果拦截器没生效，这里可以根据业务决定是抛异常还是给默认值
            currentUserId = 1L;
        }


        // 如果是新增操作（ID为空）
        if (article.getId() == null) {
            article.setAuthorId(currentUserId);
            article.setViewCount(0L);
            article.setStatus(article.getStatus() == null ? 1 : article.getStatus()); // 默认发布
            //article.setIsTop(article.getIsTop() == null ? 0 : article.getIsTop());

            // 自动生成摘要：取内容的前100个字符
            if (article.getSummary() == null || article.getSummary().isEmpty()) {
                String plainText = article.getContent().replaceAll("\\n", "").replaceAll("#", "");
                article.setSummary(plainText.length() > 100 ? plainText.substring(0, 100) + "..." : plainText);
            }
        } else {
            // 如果是更新操作，保留原有的阅读量和作者ID
            Article oldArticle = this.getById(article.getId());
            if (oldArticle != null) {
                article.setViewCount(oldArticle.getViewCount());
                article.setAuthorId(oldArticle.getAuthorId());
            }
        }

        boolean saved = this.save(article);
        if (saved) {
            Result.success(article, "保存成功");
        } else {
            throw new RuntimeException("文章保存失败");
        }


        // 验证文章ID是否生成
        if (article.getId() == null) {
            throw new RuntimeException("文章ID生成失败");
        }

        log.info("文章保存成功，ID: {}", article.getId());



        // 如果有附件，则保存附件信息
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        String filePath = fileStorageService.storeFile(file, article.getId());

                        BlogAttachments attachment = new BlogAttachments();
                        attachment.setArticleId(article.getId());
                        attachment.setFileName(file.getOriginalFilename());
                        attachment.setFileSize(file.getSize());
                        attachment.setFileType(file.getContentType());

                        // 生成唯一文件名并保存文件（这里简化处理，实际项目中应保存到指定目录或OSS）
                        //String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                        attachment.setFilePath(filePath); // 实际项目中应该是完整的访问路径

                        // 设置其他字段
                        attachment.setUploadUserId(currentUserId); // 假设当前用户ID为1，实际应从认证信息中获取
                        attachment.setIsDelete((byte) 0); // 未删除
                        attachment.setCreateTime(LocalDateTime.now());
                        attachment.setUpdateTime(LocalDateTime.now());

                        blogAttachmentsMapper.insert(attachment);

                        log.info("附件保存成功: {}", file.getOriginalFilename());
                    } catch (Exception e) {
                        log.error("附件保存失败: {}", file.getOriginalFilename(), e);
                        throw new RuntimeException("附件保存失败: " + file.getOriginalFilename(), e);
                    }
                }
            }
        }







        log.info("文章保存成功，ID: {}", article.getId());
    }



    @Override
    public boolean logicDeleteById(String id) {
        int rows = baseMapper.logicDeleteById(Long.parseLong(id));
        return rows > 0;
    }
}
