package com.notary.platform.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/** 投诉状态 */
@Getter
public enum ComplaintStatus {
    PENDING("PENDING", "待处理"),
    PROCESSING("PROCESSING", "处理中"),
    RESOLVED("RESOLVED", "已解决"),
    CLOSED("CLOSED", "已关闭");

    @EnumValue @JsonValue
    private final String code;
    private final String desc;

    ComplaintStatus(String code, String desc) { this.code = code; this.desc = desc; }
}
