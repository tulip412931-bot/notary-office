package com.notary.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notary.platform.dto.ComplaintRequest;
import com.notary.platform.dto.OrderRequest;
import com.notary.platform.dto.RefundRequest;
import com.notary.platform.entity.*;
import com.notary.platform.enums.*;
import com.notary.platform.exception.BusinessException;
import com.notary.platform.mapper.*;
import com.notary.platform.service.ConsumerService;
import com.notary.platform.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 消费者服务实现 - 订单管理、退款、投诉等
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final FundEscrowMapper fundEscrowMapper;
    private final FundTransactionMapper fundTransactionMapper;
    private final RefundApplicationMapper refundApplicationMapper;
    private final ComplaintMapper complaintMapper;
    private final NotificationMapper notificationMapper;
    private final ContractMapper contractMapper;
    private final MerchantMapper merchantMapper;
    private final NotificationService notificationService;

    @Override
    public Page<Order> getOrders(Long consumerId, int page, int size) {
        return orderMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getConsumerId, consumerId)
                        .orderByDesc(Order::getCreateTime));
    }

    @Override
    public FundEscrow getEscrowDetail(Long consumerId, Long orderId) {
        FundEscrow escrow = fundEscrowMapper.selectOne(
                new LambdaQueryWrapper<FundEscrow>()
                        .eq(FundEscrow::getOrderId, orderId)
                        .eq(FundEscrow::getConsumerId, consumerId));
        if (escrow == null) {
            throw new BusinessException("未找到托管记录");
        }
        return escrow;
    }

    @Override
    @Transactional
    public void applyRefund(Long consumerId, RefundRequest request) {
        // 查询订单
        Order order = orderMapper.selectById(request.getOrderId());
        if (order == null || !order.getConsumerId().equals(consumerId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getPayStatus() != PayStatus.PAID) {
            throw new BusinessException("订单状态不支持退款");
        }

        // 查看是否已有进行中的退款申请
        Long existingCount = refundApplicationMapper.selectCount(
                new LambdaQueryWrapper<RefundApplication>()
                        .eq(RefundApplication::getOrderId, request.getOrderId())
                        .notIn(RefundApplication::getStatus,
                                RefundStatus.COMPLETED, RefundStatus.REJECTED, RefundStatus.MERCHANT_REJECTED));
        if (existingCount > 0) {
            throw new BusinessException("已有进行中的退款申请");
        }

        // 校验退款金额
        FundEscrow escrow = fundEscrowMapper.selectOne(
                new LambdaQueryWrapper<FundEscrow>().eq(FundEscrow::getOrderId, order.getId()));
        if (escrow == null) {
            throw new BusinessException("未找到资金托管记录");
        }
        if (request.getRefundAmount().compareTo(escrow.getRemainingAmount()) > 0) {
            throw new BusinessException("退款金额不能超过剩余托管金额");
        }

        // 创建退款申请
        RefundApplication refund = new RefundApplication();
        refund.setOrderId(order.getId());
        refund.setConsumerId(consumerId);
        refund.setMerchantId(order.getMerchantId());
        refund.setRefundAmount(request.getRefundAmount());
        refund.setReason(request.getReason());
        refund.setStatus(RefundStatus.PENDING);
        refundApplicationMapper.insert(refund);

        // 更新订单支付状态
        order.setPayStatus(PayStatus.REFUNDING);
        orderMapper.updateById(order);

        // 通知商户
        Merchant merchant = merchantMapper.selectById(order.getMerchantId());
        if (merchant != null) {
            notificationService.sendNotification(merchant.getUserId(),
                    "新退款申请", "您有一笔新的退款申请，订单号: " + order.getOrderNo(), "REFUND");
        }

        log.info("消费者{}提交退款申请，订单ID: {}, 退款金额: {}", consumerId, order.getId(), request.getRefundAmount());
    }

    @Override
    public void submitComplaint(Long consumerId, ComplaintRequest request) {
        Complaint complaint = new Complaint();
        complaint.setComplainantId(consumerId);
        complaint.setMerchantId(request.getMerchantId());
        complaint.setOrderId(request.getOrderId());
        complaint.setTitle(request.getTitle());
        complaint.setContent(request.getContent());
        complaint.setStatus(ComplaintStatus.PENDING);
        complaintMapper.insert(complaint);
        log.info("消费者{}提交投诉，商户ID: {}", consumerId, request.getMerchantId());
    }

    @Override
    public List<Notification> getNotifications(Long userId) {
        return notificationMapper.selectList(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .orderByDesc(Notification::getCreateTime));
    }

    @Override
    public void markNotificationRead(Long userId, Long notificationId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || !notification.getUserId().equals(userId)) {
            throw new BusinessException("通知不存在");
        }
        notification.setIsRead(true);
        notificationMapper.updateById(notification);
    }

    @Override
    @Transactional
    public Order createOrder(Long consumerId, OrderRequest request) {
        // 查询商品
        Product product = productMapper.selectById(request.getProductId());
        if (product == null || product.getStatus() != ProductStatus.ON_SALE) {
            throw new BusinessException("商品不存在或已下架");
        }
        if (product.getSoldCount() >= product.getTotalCount()) {
            throw new BusinessException("商品已售罄");
        }

        // 生成订单号
        String orderNo = "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setConsumerId(consumerId);
        order.setMerchantId(product.getMerchantId());
        order.setProductId(product.getId());
        order.setProductName(product.getName());
        order.setAmount(product.getSellingPrice());
        order.setPayStatus(PayStatus.UNPAID);
        order.setPayChannel(request.getPayChannel());
        order.setEscrowStatus(EscrowStatus.PENDING);
        orderMapper.insert(order);

        // 模拟支付成功，更新订单状态
        order.setPayStatus(PayStatus.PAID);
        order.setPayTime(LocalDateTime.now());
        order.setEscrowStatus(EscrowStatus.IN_ESCROW);
        orderMapper.updateById(order);

        // 更新商品已售数量
        product.setSoldCount(product.getSoldCount() + 1);
        productMapper.updateById(product);

        // 创建资金托管记录
        FundEscrow escrow = new FundEscrow();
        escrow.setOrderId(order.getId());
        escrow.setConsumerId(consumerId);
        escrow.setMerchantId(product.getMerchantId());
        escrow.setTotalAmount(product.getSellingPrice());
        escrow.setReleasedAmount(BigDecimal.ZERO);
        escrow.setRemainingAmount(product.getSellingPrice());
        escrow.setStatus(FundEscrowStatus.ACTIVE);
        fundEscrowMapper.insert(escrow);

        // 记录资金交易流水 - 存入
        FundTransaction transaction = new FundTransaction();
        transaction.setEscrowId(escrow.getId());
        transaction.setOrderId(order.getId());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(product.getSellingPrice());
        transaction.setOperatorId(consumerId);
        transaction.setRemark("消费者支付，资金进入托管账户");
        fundTransactionMapper.insert(transaction);

        // 模拟生成合同
        String contractNo = "CNT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Contract contract = new Contract();
        contract.setOrderId(order.getId());
        contract.setConsumerId(consumerId);
        contract.setMerchantId(product.getMerchantId());
        contract.setContractNo(contractNo);
        contract.setContractType("预付费服务合同");
        contract.setContentJson("{\"service\":\"" + product.getName() + "\",\"amount\":" + product.getSellingPrice()
                + ",\"terms\":\"本合同受公证处监管，资金托管至服务完成\"}");
        contract.setSignStatus(SignStatus.FULLY_SIGNED);
        contract.setConsumerSignTime(LocalDateTime.now());
        contract.setMerchantSignTime(LocalDateTime.now());
        contractMapper.insert(contract);

        // 更新订单合同编号
        order.setContractNo(contractNo);
        orderMapper.updateById(order);

        log.info("消费者{}创建订单成功，订单号: {}", consumerId, orderNo);
        return order;
    }

    @Override
    public List<Product> getProducts(String category) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, ProductStatus.ON_SALE);
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Product::getCategory, category);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        return productMapper.selectList(wrapper);
    }
}
