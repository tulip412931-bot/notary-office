package com.notary.platform.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/** 风险预警类型 */
@Getter
public enum AlertType {
    LARGE_WITHDRAWAL("LARGE_WITHDRAWAL", "大额提现"),
    HIGH_FREQUENCY("HIGH_FREQUENCY", "高频交易"),
    LOW_BALANCE("LOW_BALANCE", "余额不足"),
    ABNORMAL_REFUND("ABNORMAL_REFUND", "异常退款");

    @EnumValue @JsonValue
    private final String code;
    private final String desc;

    AlertType(String code, String desc) { this.code = code; this.desc = desc; }
}
