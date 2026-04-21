package com.notary.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.notary.platform.dto.LoginRequest;
import com.notary.platform.dto.RegisterRequest;
import com.notary.platform.entity.SysUser;
import com.notary.platform.enums.UserRole;
import com.notary.platform.exception.BusinessException;
import com.notary.platform.mapper.SysUserMapper;
import com.notary.platform.service.AuthService;
import com.notary.platform.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务实现 - 登录、注册、用户信息
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Map<String, Object> login(LoginRequest request) {
        // 查找用户
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.getUsername()));
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        // 校验密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        // 校验状态
        if (user.getStatus() != 1) {
            throw new BusinessException(403, "账号已被禁用");
        }
        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().getCode());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("role", user.getRole().getCode());
        result.put("avatar", user.getAvatar());
        return result;
    }

    @Override
    public void register(RegisterRequest request) {
        // 检查用户名是否已存在
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        // 检查手机号是否已注册
        Long phoneCount = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, request.getPhone()));
        if (phoneCount > 0) {
            throw new BusinessException("手机号已被注册");
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.valueOf(request.getRole()));
        user.setIdCard(request.getIdCard());
        user.setStatus(1);
        userMapper.insert(user);
    }

    @Override
    public SysUser getUserInfo(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null); // 不返回密码
        return user;
    }
}
