package com.notary.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 通知实体
 */
@Data
@TableName("notification")
public class Notification {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 标题 */
    private String title;

    /** 内容 */
    private String content;

    /** 通知类型 */
    private String type;

    /** 是否已读 */
    private Boolean isRead;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
