package com.notary.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.notary.platform.enums.IndustryType;
import com.notary.platform.enums.MerchantStatus;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商户实体
 */
@Data
@TableName("merchant")
public class Merchant {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联用户ID */
    private Long userId;

    /** 企业名称 */
    private String companyName;

    /** 营业执照号 */
    private String licenseNo;

    /** 统一社会信用代码 */
    private String creditCode;

    /** 法人姓名 */
    private String legalPerson;

    /** 法人身份证号 */
    private String legalPersonId;

    /** 行业类型 */
    private IndustryType industryType;

    /** 经营地址 */
    private String address;

    /** 联系电话 */
    private String contactPhone;

    /** 审核状态 */
    private MerchantStatus status;

    /** 关联公证处ID */
    private Long notaryId;

    /** 审核意见 */
    private String reviewComment;

    /** 审核时间 */
    private LocalDateTime reviewTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
