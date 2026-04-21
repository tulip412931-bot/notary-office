package com.notary.platform.service.impl;

import com.notary.platform.entity.Notification;
import com.notary.platform.mapper.NotificationMapper;
import com.notary.platform.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 通知服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public void sendNotification(Long userId, String title, String content, String type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setIsRead(false);
        notificationMapper.insert(notification);
        log.info("发送通知给用户{}: {}", userId, title);
    }
}
