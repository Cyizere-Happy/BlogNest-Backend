package com.blognest.dtos;

import com.blognest.models.enums.SeverityLevel;

import java.util.UUID;

public record AuditEvent(
        UUID actorId,
        String actorRole,
        String action,
        UUID targetId,
        String targetType,
        String oldValue,
        String newValue,
        String ipAddress,
        boolean success,
        SeverityLevel severity
) {}
