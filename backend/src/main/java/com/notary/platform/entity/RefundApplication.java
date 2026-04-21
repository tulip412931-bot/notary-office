package com.notary.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.notary.platform.enums.RefundStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款申请实体
 */
@Data
@TableName("refund_application")
public class RefundApplication {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 消费者ID */
    private Long consumerId;

    /** 商户ID */
    private Long merchantId;

    /** 退款金额 */
    private BigDecimal refundAmount;

    /** 退款原因 */
    private String reason;

    /** 退款状态 */
    private RefundStatus status;

    /** 商户意见 */
    private String merchantComment;

    /** 公证处意见 */
    private String notaryComment;

    /** 操作人ID */
    private Long operatorId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
