package com.notary.platform.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FundReleaseRequest {
    private Long escrowId;
    private BigDecimal amount;
    private String remark;
}
