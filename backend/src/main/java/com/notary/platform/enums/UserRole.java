package com.notary.platform.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRole {
    CONSUMER("CONSUMER", "消费者"),
    MERCHANT("MERCHANT", "商户"),
    NOTARY("NOTARY", "公证处"),
    REGULATOR("REGULATOR", "监管方");

    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;

    UserRole(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
