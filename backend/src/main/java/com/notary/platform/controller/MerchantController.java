package com.notary.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notary.platform.dto.*;
import com.notary.platform.entity.*;
import com.notary.platform.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商户控制器 - 注册、商品管理、核销、资金、退款处理
 */
@Tag(name = "商户管理", description = "商户注册、商品管理、核销、资金明细、退款处理")
@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @Operation(summary = "提交商户注册申请")
    @PostMapping("/register")
    public Result<Void> submitRegistration(@RequestBody MerchantRegisterRequest request) {
        SysUser user = AuthController.getCurrentUser();
        merchantService.submitRegistration(user.getId(), request);
        return Result.success();
    }

    @Operation(summary = "获取商户信息")
    @GetMapping("/info")
    public Result<Merchant> getMerchantInfo() {
        SysUser user = AuthController.getCurrentUser();
        return Result.success(merchantService.getMerchantInfo(user.getId()));
    }

    @Operation(summary = "获取商品列表")
    @GetMapping("/products")
    public Result<Page<Product>> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        SysUser user = AuthController.getCurrentUser();
        return Result.success(merchantService.getProducts(user.getId(), page, size));
    }

    @Operation(summary = "添加商品")
    @PostMapping("/products")
    public Result<Void> addProduct(@RequestBody ProductRequest request) {
        SysUser user = AuthController.getCurrentUser();
        merchantService.addProduct(user.getId(), request);
        return Result.success();
    }

    @Operation(summary = "更新商品")
    @PutMapping("/products/{id}")
    public Result<Void> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        SysUser user = AuthController.getCurrentUser();
        merchantService.updateProduct(user.getId(), id, request);
        return Result.success();
    }

    @Operation(summary = "提交核销（确认已提供服务）")
    @PostMapping("/verification/{orderId}")
    public Result<Void> submitVerification(@PathVariable Long orderId,
                                           @RequestParam(required = false) String remark) {
        SysUser user = AuthController.getCurrentUser();
        merchantService.submitVerification(user.getId(), orderId, remark);
        return Result.success();
    }

    @Operation(summary = "获取订单列表")
    @GetMapping("/orders")
    public Result<Page<Order>> getOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        SysUser user = AuthController.getCurrentUser();
        return Result.success(merchantService.getOrders(user.getId(), page, size));
    }

    @Operation(summary = "获取资金明细")
    @GetMapping("/fund")
    public Result<Map<String, Object>> getFundDetail() {
        SysUser user = AuthController.getCurrentUser();
        return Result.success(merchantService.getFundDetail(user.getId()));
    }

    @Operation(summary = "获取资金交易流水")
    @GetMapping("/transactions")
    public Result<List<FundTransaction>> getTransactions() {
        SysUser user = AuthController.getCurrentUser();
        return Result.success(merchantService.getTransactions(user.getId()));
    }

    @Operation(summary = "处理退款申请")
    @PostMapping("/refund/{refundId}")
    public Result<Void> handleRefund(@PathVariable Long refundId,
                                     @RequestParam boolean approved,
                                     @RequestParam(required = false) String comment) {
        SysUser user = AuthController.getCurrentUser();
        merchantService.handleRefund(user.getId(), refundId, approved, comment);
        return Result.success();
    }
}
