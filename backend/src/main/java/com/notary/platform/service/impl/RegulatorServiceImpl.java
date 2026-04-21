package com.notary.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notary.platform.entity.*;
import com.notary.platform.enums.*;
import com.notary.platform.exception.BusinessException;
import com.notary.platform.mapper.*;
import com.notary.platform.service.NotificationService;
import com.notary.platform.service.RegulatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 监管方服务实现 - 行业统计、商户管理、风险预警、黑名单
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegulatorServiceImpl implements RegulatorService {

    private final IndustryStatsMapper industryStatsMapper;
    private final MerchantMapper merchantMapper;
    private final RiskAlertMapper riskAlertMapper;
    private final OrderMapper orderMapper;
    private final FundEscrowMapper fundEscrowMapper;
    private final RefundApplicationMapper refundApplicationMapper;
    private final NotificationService notificationService;

    @Override
    public List<IndustryStats> getIndustryStats(String periodType, String periodValue) {
        LambdaQueryWrapper<IndustryStats> wrapper = new LambdaQueryWrapper<>();
        if (periodType != null && !periodType.isEmpty()) {
            wrapper.eq(IndustryStats::getPeriodType, periodType);
        }
        if (periodValue != null && !periodValue.isEmpty()) {
            wrapper.eq(IndustryStats::getPeriodValue, periodValue);
        }
        wrapper.orderByDesc(IndustryStats::getCreateTime);
        return industryStatsMapper.selectList(wrapper);
    }

    @Override
    public Page<Merchant> getMerchantList(String status, String industry, int page, int size) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Merchant::getStatus, MerchantStatus.valueOf(status));
        }
        if (industry != null && !industry.isEmpty()) {
            wrapper.eq(Merchant::getIndustryType, IndustryType.valueOf(industry));
        }
        wrapper.orderByDesc(Merchant::getCreateTime);
        return merchantMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public Merchant getMerchantDetail(Long merchantId) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }
        return merchant;
    }

    @Override
    public List<RiskAlert> getRiskAlerts(String level) {
        LambdaQueryWrapper<RiskAlert> wrapper = new LambdaQueryWrapper<>();
        if (level != null && !level.isEmpty()) {
            wrapper.eq(RiskAlert::getLevel, AlertLevel.valueOf(level));
        }
        wrapper.orderByDesc(RiskAlert::getCreateTime);
        return riskAlertMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void addToBlacklist(Long merchantId, String reason) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }
        if (merchant.getStatus() == MerchantStatus.BLACKLISTED) {
            throw new BusinessException("商户已在黑名单中");
        }

        merchant.setStatus(MerchantStatus.BLACKLISTED);
        merchant.setReviewComment("列入黑名单原因: " + reason);
        merchantMapper.updateById(merchant);

        // 通知商户
        notificationService.sendNotification(merchant.getUserId(),
                "账户异常通知", "您的商户账户已被列入黑名单，原因: " + reason, "SYSTEM");

        log.info("监管方将商户{}列入黑名单，原因: {}", merchantId, reason);
    }

    @Override
    @Transactional
    public void removeFromBlacklist(Long merchantId) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }
        if (merchant.getStatus() != MerchantStatus.BLACKLISTED) {
            throw new BusinessException("商户不在黑名单中");
        }

        merchant.setStatus(MerchantStatus.APPROVED);
        merchant.setReviewComment(null);
        merchantMapper.updateById(merchant);

        notificationService.sendNotification(merchant.getUserId(),
                "账户恢复通知", "您的商户账户已恢复正常", "SYSTEM");

        log.info("监管方将商户{}移出黑名单", merchantId);
    }

    @Override
    public Map<String, Object> exportReport(String periodType, String periodValue) {
        // 生成统计报表数据
        Map<String, Object> report = new HashMap<>();
        report.put("periodType", periodType);
        report.put("periodValue", periodValue);
        report.put("generateTime", LocalDateTime.now());

        // 商户统计
        Long totalMerchants = merchantMapper.selectCount(null);
        Long approvedMerchants = merchantMapper.selectCount(
                new LambdaQueryWrapper<Merchant>().eq(Merchant::getStatus, MerchantStatus.APPROVED));
        Long blacklistedMerchants = merchantMapper.selectCount(
                new LambdaQueryWrapper<Merchant>().eq(Merchant::getStatus, MerchantStatus.BLACKLISTED));
        report.put("totalMerchants", totalMerchants);
        report.put("approvedMerchants", approvedMerchants);
        report.put("blacklistedMerchants", blacklistedMerchants);

        // 订单统计
        Long totalOrders = orderMapper.selectCount(null);
        Long paidOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>().eq(Order::getPayStatus, PayStatus.PAID));
        Long refundedOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>().eq(Order::getPayStatus, PayStatus.REFUNDED));
        report.put("totalOrders", totalOrders);
        report.put("paidOrders", paidOrders);
        report.put("refundedOrders", refundedOrders);

        // 资金统计
        List<FundEscrow> escrows = fundEscrowMapper.selectList(null);
        BigDecimal totalEscrow = escrows.stream()
                .map(FundEscrow::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalReleased = escrows.stream()
                .map(FundEscrow::getReleasedAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        report.put("totalEscrowAmount", totalEscrow);
        report.put("totalReleasedAmount", totalReleased);

        // 行业统计
        List<IndustryStats> stats = industryStatsMapper.selectList(
                new LambdaQueryWrapper<IndustryStats>()
                        .eq(periodType != null, IndustryStats::getPeriodType, periodType)
                        .eq(periodValue != null, IndustryStats::getPeriodValue, periodValue));
        report.put("industryStats", stats);

        return report;
    }

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 商户总数和各状态数量
        stats.put("totalMerchants", merchantMapper.selectCount(null));
        stats.put("pendingMerchants", merchantMapper.selectCount(
                new LambdaQueryWrapper<Merchant>().eq(Merchant::getStatus, MerchantStatus.PENDING)));
        stats.put("approvedMerchants", merchantMapper.selectCount(
                new LambdaQueryWrapper<Merchant>().eq(Merchant::getStatus, MerchantStatus.APPROVED)));
        stats.put("blacklistedMerchants", merchantMapper.selectCount(
                new LambdaQueryWrapper<Merchant>().eq(Merchant::getStatus, MerchantStatus.BLACKLISTED)));

        // 订单统计
        stats.put("totalOrders", orderMapper.selectCount(null));

        // 资金统计
        List<FundEscrow> escrows = fundEscrowMapper.selectList(null);
        BigDecimal totalEscrow = escrows.stream()
                .map(FundEscrow::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalRemaining = escrows.stream()
                .map(FundEscrow::getRemainingAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalEscrowAmount", totalEscrow);
        stats.put("totalRemainingAmount", totalRemaining);

        // 风险预警统计
        stats.put("pendingAlerts", riskAlertMapper.selectCount(
                new LambdaQueryWrapper<RiskAlert>().eq(RiskAlert::getStatus, AlertStatus.PENDING)));

        // 退款统计
        stats.put("pendingRefunds", refundApplicationMapper.selectCount(
                new LambdaQueryWrapper<RefundApplication>()
                        .in(RefundApplication::getStatus, RefundStatus.PENDING, RefundStatus.MERCHANT_APPROVED)));

        return stats;
    }
}
