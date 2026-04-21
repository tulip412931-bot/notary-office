package com.notary.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.notary.platform.enums.EscrowStatus;
import com.notary.platform.enums.PayStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("orders")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单号 */
    private String orderNo;

    /** 消费者ID */
    private Long consumerId;

    /** 商户ID */
    private Long merchantId;

    /** 商品ID */
    private Long productId;

    /** 商品名称(冗余) */
    private String productName;

    /** 订单金额 */
    private BigDecimal amount;

    /** 支付状态 */
    private PayStatus payStatus;

    /** 支付时间 */
    private LocalDateTime payTime;

    /** 支付渠道 */
    private String payChannel;

    /** 托管状态 */
    private EscrowStatus escrowStatus;

    /** 合同编号 */
    private String contractNo;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
