package com.notary.platform.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/** 风险预警处理状态 */
@Getter
public enum AlertStatus {
    PENDING("PENDING", "待处理"),
    PROCESSED("PROCESSED", "已处理");

    @EnumValue @JsonValue
    private final String code;
    private final String desc;

    AlertStatus(String code, String desc) { this.code = code; this.desc = desc; }
}
