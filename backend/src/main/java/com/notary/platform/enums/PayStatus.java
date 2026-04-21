package com.notary.platform.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/** 支付状态 */
@Getter
public enum PayStatus {
    UNPAID("UNPAID", "未支付"),
    PAID("PAID", "已支付"),
    REFUNDING("REFUNDING", "退款中"),
    REFUNDED("REFUNDED", "已退款");

    @EnumValue @JsonValue
    private final String code;
    private final String desc;

    PayStatus(String code, String desc) { this.code = code; this.desc = desc; }
}
