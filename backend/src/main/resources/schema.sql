-- 公证处预付费资金监管平台 - 数据库Schema
-- 使用前请先创建数据库: CREATE DATABASE IF NOT EXISTS notary_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(200) NOT NULL COMMENT '密码(BCrypt)',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `role` VARCHAR(20) NOT NULL COMMENT '角色: CONSUMER/MERCHANT/NOTARY/REGULATOR',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `id_card` VARCHAR(30) DEFAULT NULL COMMENT '身份证号',
    `status` INT DEFAULT 1 COMMENT '状态: 1-正常 0-禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`),
    KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

CREATE TABLE IF NOT EXISTS `merchant` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商户ID',
    `user_id` BIGINT NOT NULL COMMENT '关联用户ID',
    `company_name` VARCHAR(200) NOT NULL COMMENT '企业名称',
    `license_no` VARCHAR(100) DEFAULT NULL COMMENT '营业执照号',
    `credit_code` VARCHAR(100) DEFAULT NULL COMMENT '统一社会信用代码',
    `legal_person` VARCHAR(50) DEFAULT NULL COMMENT '法人姓名',
    `legal_person_id` VARCHAR(30) DEFAULT NULL COMMENT '法人身份证号',
    `industry_type` VARCHAR(20) NOT NULL COMMENT '行业类型',
    `address` VARCHAR(500) DEFAULT NULL COMMENT '经营地址',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态',
    `notary_id` BIGINT DEFAULT NULL COMMENT '关联公证处用户ID',
    `review_comment` VARCHAR(500) DEFAULT NULL COMMENT '审核意见',
    `review_time` DATETIME DEFAULT NULL COMMENT '审核时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_industry` (`industry_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商户表';

CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商户ID',
    `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `description` TEXT DEFAULT NULL COMMENT '商品描述',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '分类',
    `original_price` DECIMAL(12,2) DEFAULT NULL COMMENT '原价',
    `selling_price` DECIMAL(12,2) NOT NULL COMMENT '售价',
    `total_count` INT DEFAULT 0 COMMENT '总数量',
    `sold_count` INT DEFAULT 0 COMMENT '已售数量',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '商品状态',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图',
    `review_comment` VARCHAR(500) DEFAULT NULL COMMENT '审核意见',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品/服务表';

CREATE TABLE IF NOT EXISTS `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
    `consumer_id` BIGINT NOT NULL COMMENT '消费者ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商户ID',
    `product_id` BIGINT DEFAULT NULL COMMENT '商品ID',
    `product_name` VARCHAR(200) DEFAULT NULL COMMENT '商品名称',
    `amount` DECIMAL(12,2) NOT NULL COMMENT '订单金额',
    `pay_status` VARCHAR(20) NOT NULL DEFAULT 'UNPAID' COMMENT '支付状态',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `pay_channel` VARCHAR(50) DEFAULT NULL COMMENT '支付渠道',
    `escrow_status` VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT '托管状态',
    `contract_no` VARCHAR(50) DEFAULT NULL COMMENT '合同编号',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_consumer_id` (`consumer_id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_pay_status` (`pay_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

CREATE TABLE IF NOT EXISTS `fund_escrow` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '托管ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `consumer_id` BIGINT NOT NULL COMMENT '消费者ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商户ID',
    `total_amount` DECIMAL(12,2) NOT NULL COMMENT '托管总金额',
    `released_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '已释放金额',
    `remaining_amount` DECIMAL(12,2) NOT NULL COMMENT '剩余金额',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_consumer_id` (`consumer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资金托管记录表';

CREATE TABLE IF NOT EXISTS `fund_transaction` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '流水ID',
    `escrow_id` BIGINT NOT NULL COMMENT '托管记录ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `type` VARCHAR(20) NOT NULL COMMENT '交易类型: DEPOSIT/RELEASE/REFUND',
    `amount` DECIMAL(12,2) NOT NULL COMMENT '交易金额',
    `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_escrow_id` (`escrow_id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资金交易流水表';

CREATE TABLE IF NOT EXISTS `refund_application` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '退款ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `consumer_id` BIGINT NOT NULL COMMENT '消费者ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商户ID',
    `refund_amount` DECIMAL(12,2) NOT NULL COMMENT '退款金额',
    `reason` VARCHAR(500) DEFAULT NULL COMMENT '退款原因',
    `status` VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT '退款状态',
    `merchant_comment` VARCHAR(500) DEFAULT NULL COMMENT '商户意见',
    `notary_comment` VARCHAR(500) DEFAULT NULL COMMENT '公证处意见',
    `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_consumer_id` (`consumer_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='退款申请表';

CREATE TABLE IF NOT EXISTS `complaint` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '投诉ID',
    `complainant_id` BIGINT NOT NULL COMMENT '投诉人ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商户ID',
    `order_id` BIGINT DEFAULT NULL COMMENT '订单ID',
    `title` VARCHAR(200) NOT NULL COMMENT '投诉标题',
    `content` TEXT DEFAULT NULL COMMENT '投诉内容',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    `reply` TEXT DEFAULT NULL COMMENT '回复内容',
    `handler_id` BIGINT DEFAULT NULL COMMENT '处理人ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_complainant_id` (`complainant_id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投诉表';

CREATE TABLE IF NOT EXISTS `contract` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '合同ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `consumer_id` BIGINT NOT NULL COMMENT '消费者ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商户ID',
    `contract_no` VARCHAR(50) NOT NULL COMMENT '合同编号',
    `contract_type` VARCHAR(50) DEFAULT NULL COMMENT '合同类型',
    `content_json` TEXT DEFAULT NULL COMMENT '合同内容(JSON)',
    `sign_status` VARCHAR(30) NOT NULL DEFAULT 'UNSIGNED' COMMENT '签署状态',
    `consumer_sign_time` DATETIME DEFAULT NULL COMMENT '消费者签署时间',
    `merchant_sign_time` DATETIME DEFAULT NULL COMMENT '商户签署时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_contract_no` (`contract_no`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合同表';

CREATE TABLE IF NOT EXISTS `notification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT DEFAULT NULL COMMENT '内容',
    `type` VARCHAR(50) DEFAULT NULL COMMENT '通知类型',
    `is_read` TINYINT(1) DEFAULT 0 COMMENT '是否已读',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

CREATE TABLE IF NOT EXISTS `risk_alert` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '预警ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商户ID',
    `alert_type` VARCHAR(30) NOT NULL COMMENT '预警类型',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `level` VARCHAR(10) NOT NULL COMMENT '风险等级: LOW/MEDIUM/HIGH',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '处理状态',
    `handler_id` BIGINT DEFAULT NULL COMMENT '处理人ID',
    `handle_time` DATETIME DEFAULT NULL COMMENT '处理时间',
    `handle_comment` VARCHAR(500) DEFAULT NULL COMMENT '处理意见',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`),
    KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='风险预警表';

CREATE TABLE IF NOT EXISTS `industry_stats` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID',
    `industry_type` VARCHAR(20) NOT NULL COMMENT '行业类型',
    `total_merchants` INT DEFAULT 0 COMMENT '商户总数',
    `total_transactions` INT DEFAULT 0 COMMENT '交易总数',
    `total_amount` DECIMAL(14,2) DEFAULT 0.00 COMMENT '交易总额',
    `total_refunds` INT DEFAULT 0 COMMENT '退款总数',
    `refund_amount` DECIMAL(14,2) DEFAULT 0.00 COMMENT '退款总额',
    `period_type` VARCHAR(20) DEFAULT NULL COMMENT '统计周期类型',
    `period_value` VARCHAR(20) DEFAULT NULL COMMENT '统计周期值',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_industry` (`industry_type`),
    KEY `idx_period` (`period_type`, `period_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行业统计缓存表';
