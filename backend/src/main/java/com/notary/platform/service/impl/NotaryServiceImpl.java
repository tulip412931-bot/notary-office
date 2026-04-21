package com.notary.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notary.platform.dto.FundReleaseRequest;
import com.notary.platform.dto.ReviewRequest;
import com.notary.platform.entity.*;
import com.notary.platform.enums.*;
import com.notary.platform.exception.BusinessException;
import com.notary.platform.mapper.*;
import com.notary.platform.service.NotaryService;
import com.notary.platform.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 公证处服务实现 - 审核商户/商品、资金释放、退款处理、风险预警
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotaryServiceImpl implements NotaryService {

    private final MerchantMapper merchantMapper;
    private final ProductMapper productMapper;
    private final FundEscrowMapper fundEscrowMapper;
    private final FundTransactionMapper fundTransactionMapper;
    private final RefundApplicationMapper refundApplicationMapper;
    private final OrderMapper orderMapper;
    private final RiskAlertMapper riskAlertMapper;
    private final ComplaintMapper complaintMapper;
    private final NotificationService notificationService;

    @Override
    public Page<Merchant> getPendingMerchants(int page, int size) {
        return merchantMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Merchant>()
                        .eq(Merchant::getStatus, MerchantStatus.PENDING)
                        .orderByAsc(Merchant::getCreateTime));
    }

    @Override
    @Transactional
    public void reviewMerchant(Long notaryUserId, ReviewRequest request) {
        Merchant merchant = merchantMapper.selectById(request.getId());
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }
        if (merchant.getStatus() != MerchantStatus.PENDING) {
            throw new BusinessException("该商户已审核");
        }

        merchant.setNotaryId(notaryUserId);
        merchant.setReviewComment(request.getComment());
        merchant.setReviewTime(LocalDateTime.now());

        if (Boolean.TRUE.equals(request.getApproved())) {
            merchant.setStatus(MerchantStatus.APPROVED);
            notificationService.sendNotification(merchant.getUserId(),
                    "商户审核通过", "恭喜，您的商户注册申请已通过审核", "REVIEW");
        } else {
            merchant.setStatus(MerchantStatus.REJECTED);
            notificationService.sendNotification(merchant.getUserId(),
                    "商户审核未通过", "您的商户注册申请未通过审核，原因: " + request.getComment(), "REVIEW");
        }
        merchantMapper.updateById(merchant);

        log.info("公证处{}审核商户{}，结果: {}", notaryUserId, merchant.getId(),
                request.getApproved() ? "通过" : "拒绝");
    }

    @Override
    public Page<Product> getPendingProducts(int page, int size) {
        return productMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getStatus, ProductStatus.PENDING)
                        .orderByAsc(Product::getCreateTime));
    }

    @Override
    @Transactional
    public void reviewProduct(Long notaryUserId, ReviewRequest request) {
        Product product = productMapper.selectById(request.getId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (product.getStatus() != ProductStatus.PENDING) {
            throw new BusinessException("该商品已审核");
        }

        product.setReviewComment(request.getComment());

        if (Boolean.TRUE.equals(request.getApproved())) {
            product.setStatus(ProductStatus.ON_SALE);
        } else {
            product.setStatus(ProductStatus.REJECTED);
        }
        productMapper.updateById(product);

        // 通知商户
        Merchant merchant = merchantMapper.selectById(product.getMerchantId());
        if (merchant != null) {
            String msg = request.getApproved()
                    ? "您的商品「" + product.getName() + "」已通过审核并上架"
                    : "您的商品「" + product.getName() + "」审核未通过，原因: " + request.getComment();
            notificationService.sendNotification(merchant.getUserId(), "商品审核结果", msg, "REVIEW");
        }

        log.info("公证处{}审核商品{}，结果: {}", notaryUserId, product.getId(),
                request.getApproved() ? "通过" : "拒绝");
    }

    @Override
    public Page<FundEscrow> getEscrowAccounts(int page, int size) {
        return fundEscrowMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<FundEscrow>()
                        .orderByDesc(FundEscrow::getCreateTime));
    }

    @Override
    @Transactional
    public void releaseFund(Long notaryUserId, FundReleaseRequest request) {
        FundEscrow escrow = fundEscrowMapper.selectById(request.getEscrowId());
        if (escrow == null) {
            throw new BusinessException("托管记录不存在");
        }
        if (escrow.getStatus() == FundEscrowStatus.COMPLETED || escrow.getStatus() == FundEscrowStatus.REFUNDED) {
            throw new BusinessException("托管记录已结束");
        }

        BigDecimal releaseAmount = request.getAmount();
        if (releaseAmount.compareTo(escrow.getRemainingAmount()) > 0) {
            throw new BusinessException("释放金额不能超过剩余托管金额");
        }

        // 更新托管记录
        escrow.setReleasedAmount(escrow.getReleasedAmount().add(releaseAmount));
        escrow.setRemainingAmount(escrow.getRemainingAmount().subtract(releaseAmount));

        if (escrow.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            escrow.setStatus(FundEscrowStatus.COMPLETED);
            // 更新订单托管状态
            Order order = orderMapper.selectById(escrow.getOrderId());
            if (order != null) {
                order.setEscrowStatus(EscrowStatus.FULLY_RELEASED);
                orderMapper.updateById(order);
            }
        } else {
            // 更新订单为部分释放
            Order order = orderMapper.selectById(escrow.getOrderId());
            if (order != null) {
                order.setEscrowStatus(EscrowStatus.PARTIAL_RELEASED);
                orderMapper.updateById(order);
            }
        }
        fundEscrowMapper.updateById(escrow);

        // 记录交易流水
        FundTransaction transaction = new FundTransaction();
        transaction.setEscrowId(escrow.getId());
        transaction.setOrderId(escrow.getOrderId());
        transaction.setType(TransactionType.RELEASE);
        transaction.setAmount(releaseAmount);
        transaction.setOperatorId(notaryUserId);
        transaction.setRemark(request.getRemark());
        fundTransactionMapper.insert(transaction);

        // 通知商户资金已释放
        Merchant merchant = merchantMapper.selectById(escrow.getMerchantId());
        if (merchant != null) {
            notificationService.sendNotification(merchant.getUserId(),
                    "资金释放通知", "公证处已释放托管资金 " + releaseAmount + " 元", "FUND");
        }

        log.info("公证处{}释放资金，托管ID: {}, 金额: {}", notaryUserId, escrow.getId(), releaseAmount);
    }

    @Override
    public Page<RefundApplication> getRefundApplications(int page, int size) {
        return refundApplicationMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<RefundApplication>()
                        .in(RefundApplication::getStatus, RefundStatus.MERCHANT_APPROVED, RefundStatus.NOTARY_PROCESSING)
                        .orderByAsc(RefundApplication::getCreateTime));
    }

    @Override
    @Transactional
    public void processRefund(Long notaryUserId, Long refundId, boolean approved, String comment) {
        RefundApplication refund = refundApplicationMapper.selectById(refundId);
        if (refund == null) {
            throw new BusinessException("退款申请不存在");
        }
        if (refund.getStatus() != RefundStatus.MERCHANT_APPROVED && refund.getStatus() != RefundStatus.NOTARY_PROCESSING) {
            throw new BusinessException("退款申请状态不支持处理");
        }

        refund.setNotaryComment(comment);
        refund.setOperatorId(notaryUserId);

        if (approved) {
            refund.setStatus(RefundStatus.COMPLETED);

            // 执行退款 - 从托管账户退回
            FundEscrow escrow = fundEscrowMapper.selectOne(
                    new LambdaQueryWrapper<FundEscrow>().eq(FundEscrow::getOrderId, refund.getOrderId()));
            if (escrow != null) {
                BigDecimal refundAmount = refund.getRefundAmount();
                escrow.setRemainingAmount(escrow.getRemainingAmount().subtract(refundAmount));
                if (escrow.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    escrow.setRemainingAmount(BigDecimal.ZERO);
                    escrow.setStatus(FundEscrowStatus.REFUNDED);
                }
                fundEscrowMapper.updateById(escrow);

                // 记录退款流水
                FundTransaction transaction = new FundTransaction();
                transaction.setEscrowId(escrow.getId());
                transaction.setOrderId(refund.getOrderId());
                transaction.setType(TransactionType.REFUND);
                transaction.setAmount(refundAmount);
                transaction.setOperatorId(notaryUserId);
                transaction.setRemark("公证处批准退款: " + comment);
                fundTransactionMapper.insert(transaction);
            }

            // 更新订单状态
            Order order = orderMapper.selectById(refund.getOrderId());
            if (order != null) {
                order.setPayStatus(PayStatus.REFUNDED);
                order.setEscrowStatus(EscrowStatus.REFUNDED);
                orderMapper.updateById(order);
            }

            notificationService.sendNotification(refund.getConsumerId(),
                    "退款成功", "您的退款申请已处理完成，退款金额: " + refund.getRefundAmount() + " 元", "REFUND");
        } else {
            refund.setStatus(RefundStatus.REJECTED);

            // 恢复订单状态
            Order order = orderMapper.selectById(refund.getOrderId());
            if (order != null) {
                order.setPayStatus(PayStatus.PAID);
                orderMapper.updateById(order);
            }

            notificationService.sendNotification(refund.getConsumerId(),
                    "退款被拒绝", "公证处拒绝了您的退款申请，原因: " + comment, "REFUND");
        }
        refundApplicationMapper.updateById(refund);

        log.info("公证处{}处理退款{}，结果: {}", notaryUserId, refundId, approved ? "通过" : "拒绝");
    }

    @Override
    public List<RiskAlert> getAlerts() {
        return riskAlertMapper.selectList(
                new LambdaQueryWrapper<RiskAlert>()
                        .eq(RiskAlert::getStatus, AlertStatus.PENDING)
                        .orderByDesc(RiskAlert::getLevel)
                        .orderByAsc(RiskAlert::getCreateTime));
    }

    @Override
    @Transactional
    public void handleAlert(Long notaryUserId, Long alertId, String comment) {
        RiskAlert alert = riskAlertMapper.selectById(alertId);
        if (alert == null) {
            throw new BusinessException("预警记录不存在");
        }
        if (alert.getStatus() == AlertStatus.PROCESSED) {
            throw new BusinessException("该预警已处理");
        }

        alert.setStatus(AlertStatus.PROCESSED);
        alert.setHandlerId(notaryUserId);
        alert.setHandleTime(LocalDateTime.now());
        alert.setHandleComment(comment);
        riskAlertMapper.updateById(alert);

        log.info("公证处{}处理风险预警{}", notaryUserId, alertId);
    }

    @Override
    public Page<Complaint> getComplaints(int page, int size) {
        return complaintMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Complaint>()
                        .orderByAsc(Complaint::getStatus)
                        .orderByDesc(Complaint::getCreateTime));
    }

    @Override
    @Transactional
    public void handleComplaint(Long notaryUserId, Long complaintId, String reply) {
        Complaint complaint = complaintMapper.selectById(complaintId);
        if (complaint == null) {
            throw new BusinessException("投诉不存在");
        }

        complaint.setReply(reply);
        complaint.setHandlerId(notaryUserId);
        complaint.setStatus(ComplaintStatus.RESOLVED);
        complaintMapper.updateById(complaint);

        notificationService.sendNotification(complaint.getComplainantId(),
                "投诉回复", "您的投诉「" + complaint.getTitle() + "」已处理，回复: " + reply, "COMPLAINT");

        log.info("公证处{}处理投诉{}", notaryUserId, complaintId);
    }
}
