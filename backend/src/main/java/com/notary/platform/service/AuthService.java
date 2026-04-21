package com.notary.platform.service;

import com.notary.platform.dto.LoginRequest;
import com.notary.platform.dto.RegisterRequest;
import com.notary.platform.entity.SysUser;

import java.util.Map;

public interface AuthService {
    Map<String, Object> login(LoginRequest request);
    void register(RegisterRequest request);
    SysUser getUserInfo(Long userId);
}
