package com.blognest.mappers;

import com.blognest.dtos.NotificationResponse;
import com.blognest.models.Notification;

public class NotificationMapper {

    public static NotificationResponse toResponse(Notification notification) {

        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .read(notification.isRead())
                .type(notification.getType())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}