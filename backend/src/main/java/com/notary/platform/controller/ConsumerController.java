package com.notary.platform.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notary.platform.dto.*;
import com.notary.platform.entity.*;
import com.notary.platform.service.ConsumerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消费者控制器 - 下单、查看订单、退款、投诉、通知
 */
@Tag(name = "消费者管理", description = "消费者相关操作：下单、订单查询、退款、投诉")
@RestController
@RequestMapping("/api/consumer")
@RequiredArgsConstructor
public class ConsumerController {

    private final ConsumerService consumerService;

    @Operation(summary = "浏览商品列表")
    @GetMapping("/products")
    public Result<List<Product>> getProducts(@RequestParam(required = false) String category) {
        return Result.success(consumerService.getProducts(category));
    }

    @Operation(summary = "创建订单（含模拟支付和合同签署）")
    @PostMapping("/orders")
    public Result<Order> createOrder(@RequestBody OrderRequest request) {
        SysUser user = AuthController.getCurrentUser();
        return Result.success(consumerService.createOrder(user.getId(), request));
    }

    @Operation(summary = "查询我的订单列表")
    @GetMapping("/orders")
    public Result<Page<Order>> getOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        SysUser user = AuthController.getCurrentUser();
        return Result.success(consumerService.getOrders(user.getId(), page, size));
    }

    @Operation(summary = "查看订单资金托管详情")
    @GetMapping("/escrow/{orderId}")
    public Result<FundEscrow> getEscrowDetail(@PathVariable Long orderId) {
        SysUser user = AuthController.getCurrentUser();
        return Result.success(consumerService.getEscrowDetail(user.getId(), orderId));
    }

    @Operation(summary = "提交退款申请")
    @PostMapping("/refund")
    public Result<Void> applyRefund(@RequestBody RefundRequest request) {
        SysUser user = AuthController.getCurrentUser();
        consumerService.applyRefund(user.getId(), request);
        return Result.success();
    }

    @Operation(summary = "提交投诉")
    @PostMapping("/complaint")
    public Result<Void> submitComplaint(@RequestBody ComplaintRequest request) {
        SysUser user = AuthController.getCurrentUser();
        consumerService.submitComplaint(user.getId(), request);
        return Result.success();
    }

    @Operation(summary = "获取我的通知列表")
    @GetMapping("/notifications")
    public Result<List<Notification>> getNotifications() {
        SysUser user = AuthController.getCurrentUser();
        return Result.success(consumerService.getNotifications(user.getId()));
    }

    @Operation(summary = "标记通知为已读")
    @PutMapping("/notifications/{id}/read")
    public Result<Void> markNotificationRead(@PathVariable Long id) {
        SysUser user = AuthController.getCurrentUser();
        consumerService.markNotificationRead(user.getId(), id);
        return Result.success();
    }
}
