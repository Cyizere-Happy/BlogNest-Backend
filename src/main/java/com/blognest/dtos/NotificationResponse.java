package com.blognest.dtos;

import com.blognest.models.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private UUID id;

    private String title;

    private String message;

    private boolean read;

    private NotificationType type;

    private LocalDateTime createdAt;
}