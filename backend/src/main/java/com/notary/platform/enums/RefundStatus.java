package com.notary.platform.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/** 退款申请状态 */
@Getter
public enum RefundStatus {
    PENDING("PENDING", "待处理"),
    MERCHANT_APPROVED("MERCHANT_APPROVED", "商户已同意"),
    MERCHANT_REJECTED("MERCHANT_REJECTED", "商户已拒绝"),
    NOTARY_PROCESSING("NOTARY_PROCESSING", "公证处处理中"),
    COMPLETED("COMPLETED", "已完成"),
    REJECTED("REJECTED", "已拒绝");

    @EnumValue @JsonValue
    private final String code;
    private final String desc;

    RefundStatus(String code, String desc) { this.code = code; this.desc = desc; }
}
