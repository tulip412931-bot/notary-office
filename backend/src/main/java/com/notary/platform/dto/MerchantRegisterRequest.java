package com.notary.platform.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MerchantRegisterRequest {
    private String companyName;
    private String licenseNo;
    private String creditCode;
    private String legalPerson;
    private String legalPersonId;
    private String industryType;
    private String address;
    private String contactPhone;
}

