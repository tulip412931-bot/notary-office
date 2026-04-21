package com.notary.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.notary.platform.enums.ComplaintStatus;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 投诉实体
 */
@Data
@TableName("complaint")
public class Complaint {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 投诉人ID */
    private Long complainantId;

    /** 商户ID */
    private Long merchantId;

    /** 订单ID */
    private Long orderId;

    /** 投诉标题 */
    private String title;

    /** 投诉内容 */
    private String content;

    /** 状态 */
    private ComplaintStatus status;

    /** 回复内容 */
    private String reply;

    /** 处理人ID */
    private Long handlerId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
