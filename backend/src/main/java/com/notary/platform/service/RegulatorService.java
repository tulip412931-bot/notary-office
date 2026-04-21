package com.notary.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notary.platform.entity.*;

import java.util.List;
import java.util.Map;

public interface RegulatorService {
    List<IndustryStats> getIndustryStats(String periodType, String periodValue);
    Page<Merchant> getMerchantList(String status, String industry, int page, int size);
    Merchant getMerchantDetail(Long merchantId);
    List<RiskAlert> getRiskAlerts(String level);
    void addToBlacklist(Long merchantId, String reason);
    void removeFromBlacklist(Long merchantId);
    Map<String, Object> exportReport(String periodType, String periodValue);
    Map<String, Object> getDashboardStats();
}
