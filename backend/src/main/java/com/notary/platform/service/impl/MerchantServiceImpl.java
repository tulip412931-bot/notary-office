package com.notary.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notary.platform.dto.MerchantRegisterRequest;
import com.notary.platform.dto.ProductRequest;
import com.notary.platform.entity.*;
import com.notary.platform.enums.*;
import com.notary.platform.exception.BusinessException;
import com.notary.platform.mapper.*;
import com.notary.platform.service.MerchantService;
import com.notary.platform.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商户服务实现 - 商户注册、商品管理、核销、资金明细等
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantMapper merchantMapper;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final FundEscrowMapper fundEscrowMapper;
    private final FundTransactionMapper fundTransactionMapper;
    private final RefundApplicationMapper refundApplicationMapper;
    private final NotificationService notificationService;
    private final SysUserMapper userMapper;

    @Override
    public void submitRegistration(Long userId, MerchantRegisterRequest request) {
        // 检查是否已注册过
        Long count = merchantMapper.selectCount(
                new LambdaQueryWrapper<Merchant>().eq(Merchant::getUserId, userId));
        if (count > 0) {
            throw new BusinessException("已提交过注册申请");
        }

        Merchant merchant = new Merchant();
        merchant.setUserId(userId);
        merchant.setCompanyName(request.getCompanyName());
        merchant.setLicenseNo(request.getLicenseNo());
        merchant.setCreditCode(request.getCreditCode());
        merchant.setLegalPerson(request.getLegalPerson());
        merchant.setLegalPersonId(request.getLegalPersonId());
        merchant.setIndustryType(IndustryType.valueOf(request.getIndustryType()));
        merchant.setAddress(request.getAddress());
        merchant.setContactPhone(request.getContactPhone());
        merchant.setStatus(MerchantStatus.PENDING);
        merchantMapper.insert(merchant);

        log.info("商户{}提交注册申请，公司名称: {}", userId, request.getCompanyName());
    }

    @Override
    public Merchant getMerchantInfo(Long userId) {
        Merchant merchant = merchantMapper.selectOne(
                new LambdaQueryWrapper<Merchant>().eq(Merchant::getUserId, userId));
        if (merchant == null) {
            throw new BusinessException("商户信息不存在，请先提交注册申请");
        }
        return merchant;
    }

    @Override
    public Page<Product> getProducts(Long userId, int page, int size) {
        Merchant merchant = getMerchantByUserId(userId);
        return productMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getMerchantId, merchant.getId())
                        .orderByDesc(Product::getCreateTime));
    }

    @Override
    public void addProduct(Long userId, ProductRequest request) {
        Merchant merchant = getMerchantByUserId(userId);
        if (merchant.getStatus() != MerchantStatus.APPROVED) {
            throw new BusinessException("商户审核未通过，无法添加商品");
        }

        Product product = new Product();
        product.setMerchantId(merchant.getId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setTotalCount(request.getTotalCount());
        product.setSoldCount(0);
        product.setStatus(ProductStatus.PENDING);
        product.setCoverImage(request.getCoverImage());
        productMapper.insert(product);

        log.info("商户{}添加商品: {}", merchant.getId(), request.getName());
    }

    @Override
    public void updateProduct(Long userId, Long productId, ProductRequest request) {
        Merchant merchant = getMerchantByUserId(userId);
        Product product = productMapper.selectById(productId);
        if (product == null || !product.getMerchantId().equals(merchant.getId())) {
            throw new BusinessException("商品不存在");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setTotalCount(request.getTotalCount());
        product.setCoverImage(request.getCoverImage());
        productMapper.updateById(product);
    }

    @Override
    @Transactional
    public void submitVerification(Long userId, Long orderId, String remark) {
        // 核销：商户确认已提供服务，申请释放资金
        Merchant merchant = getMerchantByUserId(userId);
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getMerchantId().equals(merchant.getId())) {
            throw new BusinessException("订单不存在");
        }
        if (order.getPayStatus() != PayStatus.PAID) {
            throw new BusinessException("订单状态不支持核销");
        }

        // 查找托管记录
        FundEscrow escrow = fundEscrowMapper.selectOne(
                new LambdaQueryWrapper<FundEscrow>()
                        .eq(FundEscrow::getOrderId, orderId)
                        .eq(FundEscrow::getStatus, FundEscrowStatus.ACTIVE));
        if (escrow == null) {
            throw new BusinessException("未找到有效的托管记录");
        }

        // 更新订单托管状态为待释放（需公证处审批释放资金）
        log.info("商户{}提交核销申请，订单ID: {}，备注: {}", merchant.getId(), orderId, remark);

        // 通知公证处有新的核销申请
        if (merchant.getNotaryId() != null) {
            SysUser notaryUser = userMapper.selectById(merchant.getNotaryId());
            if (notaryUser != null) {
                notificationService.sendNotification(notaryUser.getId(),
                        "新核销申请", "商户「" + merchant.getCompanyName() + "」提交了核销申请，订单号: " + order.getOrderNo(), "VERIFICATION");
            }
        }
    }

    @Override
    public Page<Order> getOrders(Long userId, int page, int size) {
        Merchant merchant = getMerchantByUserId(userId);
        return orderMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getMerchantId, merchant.getId())
                        .orderByDesc(Order::getCreateTime));
    }

    @Override
    public Map<String, Object> getFundDetail(Long userId) {
        Merchant merchant = getMerchantByUserId(userId);

        // 统计总托管金额
        List<FundEscrow> escrows = fundEscrowMapper.selectList(
                new LambdaQueryWrapper<FundEscrow>().eq(FundEscrow::getMerchantId, merchant.getId()));

        BigDecimal totalEscrow = BigDecimal.ZERO;
        BigDecimal totalReleased = BigDecimal.ZERO;
        BigDecimal totalRemaining = BigDecimal.ZERO;

        for (FundEscrow e : escrows) {
            totalEscrow = totalEscrow.add(e.getTotalAmount());
            totalReleased = totalReleased.add(e.getReleasedAmount());
            totalRemaining = totalRemaining.add(e.getRemainingAmount());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalEscrow", totalEscrow);
        result.put("totalReleased", totalReleased);
        result.put("totalRemaining", totalRemaining);
        result.put("escrowRecords", escrows);
        return result;
    }

    @Override
    @Transactional
    public void handleRefund(Long userId, Long refundId, boolean approved, String comment) {
        Merchant merchant = getMerchantByUserId(userId);
        RefundApplication refund = refundApplicationMapper.selectById(refundId);
        if (refund == null || !refund.getMerchantId().equals(merchant.getId())) {
            throw new BusinessException("退款申请不存在");
        }
        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new BusinessException("退款申请已处理");
        }

        refund.setMerchantComment(comment);
        refund.setOperatorId(userId);

        if (approved) {
            // 商户同意，转交公证处处理
            refund.setStatus(RefundStatus.MERCHANT_APPROVED);
            notificationService.sendNotification(refund.getConsumerId(),
                    "退款进度更新", "商户已同意您的退款申请，等待公证处处理", "REFUND");
        } else {
            refund.setStatus(RefundStatus.MERCHANT_REJECTED);
            // 恢复订单状态
            Order order = orderMapper.selectById(refund.getOrderId());
            if (order != null) {
                order.setPayStatus(PayStatus.PAID);
                orderMapper.updateById(order);
            }
            notificationService.sendNotification(refund.getConsumerId(),
                    "退款被拒绝", "商户拒绝了您的退款申请，原因: " + comment, "REFUND");
        }
        refundApplicationMapper.updateById(refund);

        log.info("商户{}处理退款申请{}，结果: {}", merchant.getId(), refundId, approved ? "同意" : "拒绝");
    }

    @Override
    public List<FundTransaction> getTransactions(Long userId) {
        Merchant merchant = getMerchantByUserId(userId);
        List<FundEscrow> escrows = fundEscrowMapper.selectList(
                new LambdaQueryWrapper<FundEscrow>().eq(FundEscrow::getMerchantId, merchant.getId()));
        if (escrows.isEmpty()) {
            return List.of();
        }
        List<Long> escrowIds = escrows.stream().map(FundEscrow::getId).toList();
        return fundTransactionMapper.selectList(
                new LambdaQueryWrapper<FundTransaction>()
                        .in(FundTransaction::getEscrowId, escrowIds)
                        .orderByDesc(FundTransaction::getCreateTime));
    }

    private Merchant getMerchantByUserId(Long userId) {
        Merchant merchant = merchantMapper.selectOne(
                new LambdaQueryWrapper<Merchant>().eq(Merchant::getUserId, userId));
        if (merchant == null) {
            throw new BusinessException("商户信息不存在");
        }
        return merchant;
    }
}
