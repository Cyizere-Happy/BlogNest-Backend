package com.blognest.services.impl;

import com.blognest.dtos.NotificationResponse;
import com.blognest.exceptions.ResourceNotFoundException;
import com.blognest.mappers.NotificationMapper;
import com.blognest.models.Notification;
import com.blognest.models.enums.NotificationType;
import com.blognest.models.User;
import com.blognest.repositories.NotificationRepository;
import com.blognest.repositories.UserRepository;
import com.blognest.services.AuthService;
import com.blognest.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Override
    public void createNotification(
            UUID receiverId,
            String title,
            String message,
            NotificationType type
    ) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = Notification.builder()
                .receiver(receiver)
                .title(title)
                .message(message)
                .type(type)
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> getUserNotifications() {
        UUID userId = authService.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return notificationRepository.findByReceiver(user)
                .stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications() {
        UUID userId = authService.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return notificationRepository.findByReceiverAndReadFalse(user)
                .stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }

    @Override
    public List<NotificationResponse> getNotificationsByType(NotificationType type) {
        UUID userId = authService.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return notificationRepository.findByReceiverAndType(user, type)
                .stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }

    @Override
    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead() {
        UUID userId = authService.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Notification> notifications =
                notificationRepository.findByReceiverAndReadFalse(user);

        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }
}