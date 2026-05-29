package com.blognest.dtos;

import com.blognest.models.enums.SeverityLevel;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {
    private UUID id;
    private UUID actorId;
    private String actorRole;
    private String action;
    private UUID targetId;
    private String targetType;
    private String oldValue;
    private String newValue;
    private LocalDateTime timestamp;
    private String ipAddress;
    private boolean success;
    private SeverityLevel severity;
}
