package com.notary.platform.service;

/**
 * 通知服务 - 发送系统通知
 */
public interface NotificationService {
    void sendNotification(Long userId, String title, String content, String type);
}
