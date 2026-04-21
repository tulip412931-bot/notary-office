package com.notary.platform.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/** 资金交易类型 */
@Getter
public enum TransactionType {
    DEPOSIT("DEPOSIT", "存入"),
    RELEASE("RELEASE", "释放"),
    REFUND("REFUND", "退款");

    @EnumValue @JsonValue
    private final String code;
    private final String desc;

    TransactionType(String code, String desc) { this.code = code; this.desc = desc; }
}
