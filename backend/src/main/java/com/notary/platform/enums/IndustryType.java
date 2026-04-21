package com.notary.platform.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/** 行业类型 */
@Getter
public enum IndustryType {
    EDUCATION("EDUCATION", "教育培训"),
    FITNESS("FITNESS", "健身"),
    BEAUTY("BEAUTY", "美容美发"),
    ELDERLY("ELDERLY", "养老服务"),
    OTHER("OTHER", "其他");

    @EnumValue @JsonValue
    private final String code;
    private final String desc;

    IndustryType(String code, String desc) { this.code = code; this.desc = desc; }
}
