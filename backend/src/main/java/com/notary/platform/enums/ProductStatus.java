package com.notary.platform.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/** 商品状态 */
@Getter
public enum ProductStatus {
    PENDING("PENDING", "待审核"),
    ON_SALE("ON_SALE", "在售"),
    OFF_SALE("OFF_SALE", "已下架"),
    REJECTED("REJECTED", "已拒绝");

    @EnumValue @JsonValue
    private final String code;
    private final String desc;

    ProductStatus(String code, String desc) { this.code = code; this.desc = desc; }
}
