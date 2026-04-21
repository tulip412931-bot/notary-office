package com.notary.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notary.platform.dto.Result;
import com.notary.platform.entity.*;
import com.notary.platform.service.RegulatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 监管方控制器 - 行业统计、商户管理、风险预警、黑名单、报表导出
 */
@Tag(name = "监管方管理", description = "行业统计、商户监管、风险预警、黑名单管理")
@RestController
@RequestMapping("/api/regulator")
@RequiredArgsConstructor
public class RegulatorController {

    private final RegulatorService regulatorService;

    @Operation(summary = "获取监管大盘统计数据")
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        return Result.success(regulatorService.getDashboardStats());
    }

    @Operation(summary = "获取行业统计数据")
    @GetMapping("/industry-stats")
    public Result<List<IndustryStats>> getIndustryStats(
            @RequestParam(required = false) String periodType,
            @RequestParam(required = false) String periodValue) {
        return Result.success(regulatorService.getIndustryStats(periodType, periodValue));
    }

    @Operation(summary = "获取商户列表（支持按状态、行业筛选）")
    @GetMapping("/merchants")
    public Result<Page<Merchant>> getMerchantList(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String industry,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(regulatorService.getMerchantList(status, industry, page, size));
    }

    @Operation(summary = "获取商户详情")
    @GetMapping("/merchants/{merchantId}")
    public Result<Merchant> getMerchantDetail(@PathVariable Long merchantId) {
        return Result.success(regulatorService.getMerchantDetail(merchantId));
    }

    @Operation(summary = "获取风险预警列表")
    @GetMapping("/risk-alerts")
    public Result<List<RiskAlert>> getRiskAlerts(
            @RequestParam(required = false) String level) {
        return Result.success(regulatorService.getRiskAlerts(level));
    }

    @Operation(summary = "将商户列入黑名单")
    @PostMapping("/blacklist/{merchantId}")
    public Result<Void> addToBlacklist(@PathVariable Long merchantId,
                                       @RequestParam String reason) {
        regulatorService.addToBlacklist(merchantId, reason);
        return Result.success();
    }

    @Operation(summary = "将商户移出黑名单")
    @DeleteMapping("/blacklist/{merchantId}")
    public Result<Void> removeFromBlacklist(@PathVariable Long merchantId) {
        regulatorService.removeFromBlacklist(merchantId);
        return Result.success();
    }

    @Operation(summary = "导出统计报表")
    @GetMapping("/report")
    public Result<Map<String, Object>> exportReport(
            @RequestParam(required = false) String periodType,
            @RequestParam(required = false) String periodValue) {
        return Result.success(regulatorService.exportReport(periodType, periodValue));
    }
}
