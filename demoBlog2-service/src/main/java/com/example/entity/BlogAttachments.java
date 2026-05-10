package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 博客-文章附件表（含逻辑删除）
 * </p>
 *
 * @author CodeGenerator
 * @since 2026-05-09
 */
@Getter
@Setter
@TableName("blog_attachments")
public class BlogAttachments implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关联文章ID
     */
    @TableField("article_id")
    private Long articleId;

    /**
     * 上传人ID
     */
    @TableField("upload_user_id")
    private Long uploadUserId;

    /**
     * 原始文件名(用于展示)
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件存储路径/OSS访问地址
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件大小(单位：字节)
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 文件MIME类型/后缀名
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 是否删除：0-未删除 1-已删除
     */
    @TableField("is_delete")
    private Byte isDelete;

    /**
     * 上传时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
