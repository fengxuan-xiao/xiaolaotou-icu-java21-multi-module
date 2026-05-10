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
 * 各月单位净值总表数据
 * </p>
 *
 * @author CodeGenerator
 * @since 2026-05-09
 */
@Getter
@Setter
@TableName("monthly_unit_net_value")
public class MonthlyUnitNetValue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 定价日期，格式：20260103
     */
    @TableField("price_date")
    private String priceDate;

    /**
     * 单位净值
     */
    @TableField("unit_net_value")
    private BigDecimal unitNetValue;

    /**
     * 是否业务使用：1=是，0=否
     */
    @TableField("is_business_use")
    private Byte isBusinessUse;

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
