package com.blognest.controllers;

import com.blognest.dtos.NotificationResponse;
import com.blognest.models.enums.NotificationType;
import com.blognest.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "In-app notifications")
public class NotificationController {

    private final NotificationService notificationService;

    // GET /api/notifications/me
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my notifications", description = "Retrieves all notifications for the authenticated user.")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications() {
        return ResponseEntity.ok(notificationService.getUserNotifications());
    }

    // GET /api/notifications/me/unread
    @GetMapping("/me/unread")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my unread notifications", description = "Retrieves only unread notifications for the authenticated user.")
    public ResponseEntity<List<NotificationResponse>> getMyUnreadNotifications() {
        return ResponseEntity.ok(notificationService.getUnreadNotifications());
    }

    // GET /api/notifications/me/type/{type}
    @GetMapping("/me/type/{type}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my notifications by type", description = "Retrieves notifications filtered by type for the authenticated user.")
    public ResponseEntity<List<NotificationResponse>> getMyNotificationsByType(
            @PathVariable NotificationType type) {
        return ResponseEntity.ok(notificationService.getNotificationsByType(type));
    }

    // PATCH /api/notifications/{id}/read
    @PatchMapping("/{id}/read")
    @PreAuthorize("hasRole('SUPERADMIN') or @securityEvaluator.isNotificationOwner(#id)")
    @Operation(summary = "Mark notification as read", description = "Marks a specific notification as read by its ID.")
    public ResponseEntity<com.blognest.dtos.ApiResponse> markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(com.blognest.dtos.ApiResponse.builder()
                .success(true)
                .message("Notification marked as read successfully.")
                .build());
    }

    // PATCH /api/notifications/me/read-all
    @PatchMapping("/me/read-all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark all my notifications as read", description = "Marks all unread notifications for the authenticated user as read.")
    public ResponseEntity<com.blognest.dtos.ApiResponse> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok(com.blognest.dtos.ApiResponse.builder()
                .success(true)
                .message("All notifications marked as read successfully.")
                .build());
    }
}
