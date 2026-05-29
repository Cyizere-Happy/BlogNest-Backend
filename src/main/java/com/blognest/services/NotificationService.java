package com.blognest.services;

import com.blognest.dtos.NotificationResponse;
import com.blognest.models.enums.NotificationType;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    void createNotification(
            UUID receiverId,
            String title,
            String message,
            NotificationType type
    );

    List<NotificationResponse> getUserNotifications();

    List<NotificationResponse> getUnreadNotifications();

    List<NotificationResponse> getNotificationsByType(NotificationType type);

    void markAsRead(UUID notificationId);

    void markAllAsRead();
}