package com.notary.platform.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/** 托管状态 */
@Getter
public enum EscrowStatus {
    PENDING("PENDING", "待托管"),
    IN_ESCROW("IN_ESCROW", "托管中"),
    PARTIAL_RELEASED("PARTIAL_RELEASED", "部分释放"),
    FULLY_RELEASED("FULLY_RELEASED", "已全部释放"),
    REFUNDED("REFUNDED", "已退款");

    @EnumValue @JsonValue
    private final String code;
    private final String desc;

    EscrowStatus(String code, String desc) { this.code = code; this.desc = desc; }
}
