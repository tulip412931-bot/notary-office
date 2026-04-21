package com.notary.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notary.platform.dto.*;
import com.notary.platform.entity.*;
import com.notary.platform.service.NotaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公证处控制器 - 审核商户/商品、资金释放、退款处理、风险预警
 */
@Tag(name = "公证处管理", description = "审核商户/商品、资金托管管理、退款处理、风险预警")
@RestController
@RequestMapping("/api/notary")
@RequiredArgsConstructor
public class NotaryController {

    private final NotaryService notaryService;

    @Operation(summary = "获取待审核商户列表")
    @GetMapping("/merchants/pending")
    public Result<Page<Merchant>> getPendingMerchants(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(notaryService.getPendingMerchants(page, size));
    }

    @Operation(summary = "审核商户")
    @PostMapping("/merchants/review")
    public Result<Void> reviewMerchant(@RequestBody ReviewRequest request) {
        SysUser user = AuthController.getCurrentUser();
        notaryService.reviewMerchant(user.getId(), request);
        return Result.success();
    }

    @Operation(summary = "获取待审核商品列表")
    @GetMapping("/products/pending")
    public Result<Page<Product>> getPendingProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(notaryService.getPendingProducts(page, size));
    }

    @Operation(summary = "审核商品")
    @PostMapping("/products/review")
    public Result<Void> reviewProduct(@RequestBody ReviewRequest request) {
        SysUser user = AuthController.getCurrentUser();
        notaryService.reviewProduct(user.getId(), request);
        return Result.success();
    }

    @Operation(summary = "获取资金托管账户列表")
    @GetMapping("/escrow")
    public Result<Page<FundEscrow>> getEscrowAccounts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(notaryService.getEscrowAccounts(page, size));
    }

    @Operation(summary = "释放托管资金")
    @PostMapping("/escrow/release")
    public Result<Void> releaseFund(@RequestBody FundReleaseRequest request) {
        SysUser user = AuthController.getCurrentUser();
        notaryService.releaseFund(user.getId(), request);
        return Result.success();
    }

    @Operation(summary = "获取退款申请列表（商户已同意的）")
    @GetMapping("/refunds")
    public Result<Page<RefundApplication>> getRefundApplications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(notaryService.getRefundApplications(page, size));
    }

    @Operation(summary = "处理退款申请")
    @PostMapping("/refunds/{refundId}")
    public Result<Void> processRefund(@PathVariable Long refundId,
                                      @RequestParam boolean approved,
                                      @RequestParam(required = false) String comment) {
        SysUser user = AuthController.getCurrentUser();
        notaryService.processRefund(user.getId(), refundId, approved, comment);
        return Result.success();
    }

    @Operation(summary = "获取风险预警列表")
    @GetMapping("/alerts")
    public Result<List<RiskAlert>> getAlerts() {
        return Result.success(notaryService.getAlerts());
    }

    @Operation(summary = "处理风险预警")
    @PostMapping("/alerts/{alertId}")
    public Result<Void> handleAlert(@PathVariable Long alertId,
                                    @RequestParam String comment) {
        SysUser user = AuthController.getCurrentUser();
        notaryService.handleAlert(user.getId(), alertId, comment);
        return Result.success();
    }

    @Operation(summary = "获取投诉列表")
    @GetMapping("/complaints")
    public Result<Page<Complaint>> getComplaints(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(notaryService.getComplaints(page, size));
    }

    @Operation(summary = "处理投诉")
    @PostMapping("/complaints/{complaintId}")
    public Result<Void> handleComplaint(@PathVariable Long complaintId,
                                        @RequestParam String reply) {
        SysUser user = AuthController.getCurrentUser();
        notaryService.handleComplaint(user.getId(), complaintId, reply);
        return Result.success();
    }
}
