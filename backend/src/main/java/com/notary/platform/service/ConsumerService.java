package com.notary.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notary.platform.dto.ComplaintRequest;
import com.notary.platform.dto.OrderRequest;
import com.notary.platform.dto.RefundRequest;
import com.notary.platform.entity.*;

import java.util.List;

public interface ConsumerService {
    Page<Order> getOrders(Long consumerId, int page, int size);
    FundEscrow getEscrowDetail(Long consumerId, Long orderId);
    void applyRefund(Long consumerId, RefundRequest request);
    void submitComplaint(Long consumerId, ComplaintRequest request);
    List<Notification> getNotifications(Long userId);
    void markNotificationRead(Long userId, Long notificationId);
    Order createOrder(Long consumerId, OrderRequest request);
    List<Product> getProducts(String category);
}
