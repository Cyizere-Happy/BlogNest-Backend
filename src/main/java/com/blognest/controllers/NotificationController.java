package com.blognest.controllers;

import com.blognest.dtos.NotificationResponse;
import com.blognest.models.enums.NotificationType;
import com.blognest.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "In-app notifications")
public class NotificationController {

    private final NotificationService notificationService;

    // GET /api/notifications/user/{userId}
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user notifications", description = "Retrieves all notifications for a specific user.")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    // GET /api/notifications/user/{userId}/unread
    @GetMapping("/user/{userId}/unread")
    @Operation(summary = "Get unread notifications", description = "Retrieves only unread notifications for a specific user.")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    // GET /api/notifications/user/{userId}/type/{type}
    @GetMapping("/user/{userId}/type/{type}")
    @Operation(summary = "Get notifications by type", description = "Retrieves notifications filtered by type for a specific user.")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByType(
            @PathVariable UUID userId,
            @PathVariable NotificationType type) {
        return ResponseEntity.ok(notificationService.getNotificationsByType(userId, type));
    }

    // PATCH /api/notifications/{id}/read
    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Marks a specific notification as read by its ID.")
    public ResponseEntity<com.blognest.dtos.ApiResponse> markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(com.blognest.dtos.ApiResponse.builder()
                .success(true)
                .message("Notification marked as read successfully.")
                .build());
    }

    // PATCH /api/notifications/user/{userId}/read-all
    @PatchMapping("/user/{userId}/read-all")
    @Operation(summary = "Mark all notifications as read", description = "Marks all unread notifications for a user as read.")
    public ResponseEntity<com.blognest.dtos.ApiResponse> markAllAsRead(@PathVariable UUID userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(com.blognest.dtos.ApiResponse.builder()
                .success(true)
                .message("All notifications marked as read successfully.")
                .build());
    }
}
