package com.notary.platform.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/** 资金托管记录状态 */
@Getter
public enum FundEscrowStatus {
    ACTIVE("ACTIVE", "活跃"),
    COMPLETED("COMPLETED", "已完成"),
    REFUNDED("REFUNDED", "已退款");

    @EnumValue @JsonValue
    private final String code;
    private final String desc;

    FundEscrowStatus(String code, String desc) { this.code = code; this.desc = desc; }
}
