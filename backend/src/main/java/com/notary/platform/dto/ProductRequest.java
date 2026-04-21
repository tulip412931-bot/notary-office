package com.notary.platform.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private String category;
    private BigDecimal originalPrice;
    private BigDecimal sellingPrice;
    private Integer totalCount;
    private String coverImage;
}
