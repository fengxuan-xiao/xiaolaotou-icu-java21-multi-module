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
 * 各统筹区参保人员总表数据
 * </p>
 *
 * @author CodeGenerator
 * @since 2026-05-09
 */
@Getter
@Setter
@TableName("insured_summary_by_region")
public class InsuredSummaryByRegion implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 统筹区
     */
    @TableField("region_name")
    private String regionName;

    /**
     * 统筹区编号
     */
    @TableField("region_code")
    private String regionCode;

    /**
     * 费款所属期，格式：202501
     */
    @TableField("fee_period")
    private String feePeriod;

    /**
     * 对应费款所属期，格式：202501
     */
    @TableField("rel_fee_period")
    private String relFeePeriod;

    /**
     * 人员数量
     */
    @TableField("person_count")
    private Integer personCount;

    /**
     * 平均年龄
     */
    @TableField("avg_age")
    private BigDecimal avgAge;

    /**
     * 平均缴费基数
     */
    @TableField("avg_payment_base")
    private BigDecimal avgPaymentBase;

    /**
     * 平均缴费金额
     */
    @TableField("avg_payment_amount")
    private BigDecimal avgPaymentAmount;

    /**
     * 数据生成时间
     */
    @TableField("generate_time")
    private LocalDateTime generateTime;

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
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 有效标志：1=有效，0=无效
     */
    @TableField("valid_status")
    private Byte validStatus;
}
