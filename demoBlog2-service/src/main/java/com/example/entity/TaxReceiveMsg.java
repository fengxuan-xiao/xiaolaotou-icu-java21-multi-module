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
 * 税务局接收缴费消息表
 * </p>
 *
 * @author CodeGenerator
 * @since 2026-05-22
 */
@Getter
@Setter
@TableName("tax_receive_msg")
public class TaxReceiveMsg implements Serializable {

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
     * 接收状态 0待处理 1处理中 2处理成功 3处理失败
     */
    @TableField("receive_status")
    private Byte receiveStatus;

    /**
     * 业务处理时间
     */
    @TableField("process_time")
    private LocalDateTime processTime;

    /**
     * 处理结果 0成功 1失败 2重复数据
     */
    @TableField("process_result")
    private Byte processResult;

    /**
     * 处理说明、异常原因
     */
    @TableField("process_msg")
    private String processMsg;

    /**
     * 回执状态 0未回执 1已回执
     */
    @TableField("reply_status")
    private Byte replyStatus;

    /**
     * 回执发送时间
     */
    @TableField("reply_time")
    private LocalDateTime replyTime;

    /**
     * 是否重复消息 0否 1是
     */
    @TableField("is_repeat")
    private Byte isRepeat;

    /**
     * 业务状态
     */
    @TableField("status")
    private Byte status;

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
