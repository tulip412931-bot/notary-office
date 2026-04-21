package com.notary.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.notary.platform.enums.TransactionType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资金交易流水实体
 */
@Data
@TableName("fund_transaction")
public class FundTransaction {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 托管记录ID */
    private Long escrowId;

    /** 订单ID */
    private Long orderId;

    /** 交易类型 */
    private TransactionType type;

    /** 交易金额 */
    private BigDecimal amount;

    /** 操作人ID */
    private Long operatorId;

    /** 备注 */
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
