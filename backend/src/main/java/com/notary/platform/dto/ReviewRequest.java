package com.notary.platform.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long id;
    private Boolean approved;
    private String comment;
}
