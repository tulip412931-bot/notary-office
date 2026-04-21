package com.notary.platform.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private Long productId;
    private String payChannel;
}
