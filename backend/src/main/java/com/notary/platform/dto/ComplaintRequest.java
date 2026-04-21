package com.notary.platform.dto;

import lombok.Data;

@Data
public class ComplaintRequest {
    private Long merchantId;
    private Long orderId;
    private String title;
    private String content;
}
