package com.notary.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notary.platform.dto.FundReleaseRequest;
import com.notary.platform.dto.ReviewRequest;
import com.notary.platform.entity.*;

import java.util.List;

public interface NotaryService {
    Page<Merchant> getPendingMerchants(int page, int size);
    void reviewMerchant(Long notaryUserId, ReviewRequest request);
    Page<Product> getPendingProducts(int page, int size);
    void reviewProduct(Long notaryUserId, ReviewRequest request);
    Page<FundEscrow> getEscrowAccounts(int page, int size);
    void releaseFund(Long notaryUserId, FundReleaseRequest request);
    Page<RefundApplication> getRefundApplications(int page, int size);
    void processRefund(Long notaryUserId, Long refundId, boolean approved, String comment);
    List<RiskAlert> getAlerts();
    void handleAlert(Long notaryUserId, Long alertId, String comment);
    Page<Complaint> getComplaints(int page, int size);
    void handleComplaint(Long notaryUserId, Long complaintId, String reply);
}
