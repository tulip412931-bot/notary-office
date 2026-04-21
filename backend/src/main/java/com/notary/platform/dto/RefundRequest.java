package com.notary.platform.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RefundRequest {
    private Long orderId;
    private BigDecimal refundAmount;
    private String reason;
}
