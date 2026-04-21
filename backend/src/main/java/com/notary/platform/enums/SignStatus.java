package com.notary.platform.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/** 合同签署状态 */
@Getter
public enum SignStatus {
    UNSIGNED("UNSIGNED", "未签署"),
    CONSUMER_SIGNED("CONSUMER_SIGNED", "消费者已签"),
    FULLY_SIGNED("FULLY_SIGNED", "已完全签署");

    @EnumValue @JsonValue
    private final String code;
    private final String desc;

    SignStatus(String code, String desc) { this.code = code; this.desc = desc; }
}
