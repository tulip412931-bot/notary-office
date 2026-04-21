package com.notary.platform.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/** 商户状态 */
@Getter
public enum MerchantStatus {
    PENDING("PENDING", "待审核"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已拒绝"),
    BLACKLISTED("BLACKLISTED", "已列入黑名单");

    @EnumValue @JsonValue
    private final String code;
    private final String desc;

    MerchantStatus(String code, String desc) { this.code = code; this.desc = desc; }
}
