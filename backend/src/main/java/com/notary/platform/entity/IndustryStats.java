package com.notary.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.notary.platform.enums.IndustryType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 行业统计缓存实体
 */
@Data
@TableName("industry_stats")
public class IndustryStats {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 行业类型 */
    private IndustryType industryType;

    /** 商户总数 */
    private Integer totalMerchants;

    /** 交易总数 */
    private Integer totalTransactions;

    /** 交易总额 */
    private BigDecimal totalAmount;

    /** 退款总数 */
    private Integer totalRefunds;

    /** 退款总额 */
    private BigDecimal refundAmount;

    /** 统计周期类型(DAILY/WEEKLY/MONTHLY) */
    private String periodType;

    /** 统计周期值 */
    private String periodValue;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
