package com.notary.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notary.platform.dto.MerchantRegisterRequest;
import com.notary.platform.dto.ProductRequest;
import com.notary.platform.entity.*;

import java.util.List;
import java.util.Map;

public interface MerchantService {
    void submitRegistration(Long userId, MerchantRegisterRequest request);
    Merchant getMerchantInfo(Long userId);
    Page<Product> getProducts(Long userId, int page, int size);
    void addProduct(Long userId, ProductRequest request);
    void updateProduct(Long userId, Long productId, ProductRequest request);
    void submitVerification(Long userId, Long orderId, String remark);
    Page<Order> getOrders(Long userId, int page, int size);
    Map<String, Object> getFundDetail(Long userId);
    void handleRefund(Long userId, Long refundId, boolean approved, String comment);
    List<FundTransaction> getTransactions(Long userId);
}
