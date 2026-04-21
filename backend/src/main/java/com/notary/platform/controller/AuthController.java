package com.notary.platform.controller;

import com.notary.platform.dto.LoginRequest;
import com.notary.platform.dto.RegisterRequest;
import com.notary.platform.dto.Result;
import com.notary.platform.entity.SysUser;
import com.notary.platform.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器 - 登录、注册、获取用户信息
 */
@Tag(name = "认证管理", description = "用户登录、注册、获取用户信息")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> data = authService.login(request);
        return Result.success(data);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.success();
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/userInfo")
    public Result<SysUser> getUserInfo() {
        SysUser currentUser = getCurrentUser();
        SysUser user = authService.getUserInfo(currentUser.getId());
        return Result.success(user);
    }

    /**
     * 从SecurityContext获取当前登录用户
     */
    public static SysUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SysUser) {
            return (SysUser) auth.getPrincipal();
        }
        throw new RuntimeException("未获取到当前用户信息");
    }
}
