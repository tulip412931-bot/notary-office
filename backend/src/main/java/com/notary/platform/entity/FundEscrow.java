package com.notary.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.notary.platform.enums.FundEscrowStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资金托管记录实体
 */
@Data
@TableName("fund_escrow")
public class FundEscrow {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 消费者ID */
    private Long consumerId;

    /** 商户ID */
    private Long merchantId;

    /** 托管总金额 */
    private BigDecimal totalAmount;

    /** 已释放金额 */
    private BigDecimal releasedAmount;

    /** 剩余金额 */
    private BigDecimal remainingAmount;

    /** 状态 */
    private FundEscrowStatus status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
