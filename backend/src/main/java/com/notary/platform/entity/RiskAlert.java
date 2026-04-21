package com.notary.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.notary.platform.enums.AlertLevel;
import com.notary.platform.enums.AlertStatus;
import com.notary.platform.enums.AlertType;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 风险预警实体
 */
@Data
@TableName("risk_alert")
public class RiskAlert {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 预警类型 */
    private AlertType alertType;

    /** 描述 */
    private String description;

    /** 风险等级 */
    private AlertLevel level;

    /** 处理状态 */
    private AlertStatus status;

    /** 处理人ID */
    private Long handlerId;

    /** 处理时间 */
    private LocalDateTime handleTime;

    /** 处理意见 */
    private String handleComment;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
