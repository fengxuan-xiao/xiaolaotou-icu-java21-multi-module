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
 * Excel导入记录表
 * </p>
 *
 * @author CodeGenerator
 * @since 2026-05-11
 */
@Getter
@Setter
@TableName("import_record")
public class ImportRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
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
     * 作者名称
     */
    @TableField("author_name")
    private String authorName;

    /**
     * 标签
     */
    @TableField("tags")
    private String tags;

    /**
     * 导入状态: SUCCESS-成功, FAIL-失败
     */
    @TableField("status")
    private String status;

    /**
     * 失败原因
     */
    @TableField("fail_reason")
    private String failReason;

    /**
     * Excel行号
     */
    @TableField("row_index")
    private Integer rowIndex;

    /**
     * 导入时间
     */
    @TableField("import_time")
    private LocalDateTime importTime;

    /**
     * 操作人
     */
    @TableField("operator")
    private String operator;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
