package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 社保局税务缴费消息表
 * </p>
 *
 * @author CodeGenerator
 * @since 2026-05-22
 */
@Getter
@Setter
@TableName("social_tax_msg")
public class SocialTaxMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 数据唯一编号
     */
    @TableField("data_no")
    private String dataNo;

    /**
     * 消息唯一标识
     */
    @TableField("msg_id")
    private String msgId;

    /**
     * 单位名称
     */
    @TableField("unit_name")
    private String unitName;

    /**
     * 单位编号
     */
    @TableField("unit_code")
    private String unitCode;

    /**
     * 费款所属期
     */
    @TableField("fee_period")
    private String feePeriod;

    /**
     * 对应费款所属期
     */
    @TableField("fee_period_ref")
    private String feePeriodRef;

    /**
     * 统筹区编码
     */
    @TableField("area_code")
    private String areaCode;

    /**
     * 缴费金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 税务流水号
     */
    @TableField("tax_serial_no")
    private String taxSerialNo;

    /**
     * 发送状态 0待发送 1发送中 2已发送 3发送失败 4已回执
     */
    @TableField("send_status")
    private Byte sendStatus;

    /**
     * 消息发送时间
     */
    @TableField("send_time")
    private LocalDateTime sendTime;

    /**
     * 回执状态 0未回执 1处理成功 2处理失败 3重复消息
     */
    @TableField("receive_status")
    private Byte receiveStatus;

    /**
     * 税务回执详情
     */
    @TableField("receive_msg")
    private String receiveMsg;

    /**
     * 接收回执时间
     */
    @TableField("receive_time")
    private LocalDateTime receiveTime;

    /**
     * 消息队列主题
     */
    @TableField("mq_topic")
    private String mqTopic;

    /**
     * 消费分组
     */
    @TableField("mq_group")
    private String mqGroup;

    /**
     * 业务状态
     */
    @TableField("status")
    private Byte status;

    /**
     * 消息重试次数
     */
    @TableField("retry_count")
    private Integer retryCount;

    /**
     * 备注信息
     */
    @TableField("remark")
    private String remark;

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

    /**
     * 逻辑删除 0正常 1删除
     */
    @TableField("is_deleted")
    private Byte isDeleted;
}
