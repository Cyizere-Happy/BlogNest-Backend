package com.blognest.dtos;

import com.blognest.models.enums.NotificationType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNotificationRequest {

    private UUID receiverId;

    private String title;

    private String message;

    private NotificationType type;
}