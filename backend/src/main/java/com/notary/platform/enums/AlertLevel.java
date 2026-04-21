package com.notary.platform.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/** 风险等级 */
@Getter
public enum AlertLevel {
    LOW("LOW", "低"),
    MEDIUM("MEDIUM", "中"),
    HIGH("HIGH", "高");

    @EnumValue @JsonValue
    private final String code;
    private final String desc;

    AlertLevel(String code, String desc) { this.code = code; this.desc = desc; }
}
