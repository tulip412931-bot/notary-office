package com.notary.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.notary.platform.enums.SignStatus;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 合同实体
 */
@Data
@TableName("contract")
public class Contract {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 消费者ID */
    private Long consumerId;

    /** 商户ID */
    private Long merchantId;

    /** 合同编号 */
    private String contractNo;

    /** 合同类型 */
    private String contractType;

    /** 合同内容(JSON) */
    private String contentJson;

    /** 签署状态 */
    private SignStatus signStatus;

    /** 消费者签署时间 */
    private LocalDateTime consumerSignTime;

    /** 商户签署时间 */
    private LocalDateTime merchantSignTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
