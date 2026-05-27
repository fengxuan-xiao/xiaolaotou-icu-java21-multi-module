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
 * 文章表
 * </p>
 *
 * @author CodeGenerator
 * @since 2026-05-22
 */
@Getter
@Setter
@TableName("article")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文章标题
     */
    @TableField("title")
    private String title;

    /**
     * 文章内容
     */
    @TableField("content")
    private String content;

    /**
     * 作者ID
     */
    @TableField("author_id")
    private Long authorId;

    /**
     * 作者名称
     */
    @TableField("author_name")
    private String authorName;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 最后修改时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 0未删除 1已删除
     */
    @TableField("is_deleted")
    private Byte isDeleted;

    /**
     * 0草稿 1已发布
     */
    @TableField("status")
    private Byte status;

    /**
     * 文章摘要
     */
    @TableField("summary")
    private String summary;

    /**
     * 封面图
     */
    @TableField("cover_image")
    private String coverImage;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 标签
     */
    @TableField("tags")
    private String tags;

    /**
     * 浏览量
     */
    @TableField("view_count")
    private Long viewCount;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 评论数
     */
    @TableField("comment_count")
    private Integer commentCount;

    /**
     * 排序权重
     */
    @TableField("sort")
    private Integer sort;

    /**
     * Flowable流程实例ID
     */
    @TableField("process_instance_id")
    private String processInstanceId;

    /**
     * 当前待办任务ID
     */
    @TableField("task_id")
    private String taskId;

    /**
     * 审核状态：0草稿、1待初审、2初审通过、3待复核、4复核通过、5驳回
     */
    @TableField("audit_status")
    private Byte auditStatus;

    /**
     * 审核意见/驳回理由
     */
    @TableField("audit_remark")
    private String auditRemark;
}
