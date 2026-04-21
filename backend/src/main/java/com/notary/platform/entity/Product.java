package com.notary.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.notary.platform.enums.ProductStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品/服务实体
 */
@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 商品名称 */
    private String name;

    /** 商品描述 */
    private String description;

    /** 分类 */
    private String category;

    /** 原价 */
    private BigDecimal originalPrice;

    /** 售价 */
    private BigDecimal sellingPrice;

    /** 总数量 */
    private Integer totalCount;

    /** 已售数量 */
    private Integer soldCount;

    /** 商品状态 */
    private ProductStatus status;

    /** 封面图 */
    private String coverImage;

    /** 审核意见 */
    private String reviewComment;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
