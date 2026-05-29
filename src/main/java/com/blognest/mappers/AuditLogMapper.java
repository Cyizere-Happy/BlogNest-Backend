package com.blognest.mappers;

import com.blognest.dtos.AuditLogResponse;
import com.blognest.models.AuditLog;

public class AuditLogMapper {

    public static AuditLogResponse toResponse(AuditLog log) {
        if (log == null) return null;

        return AuditLogResponse.builder()
                .id(log.getId())
                .actorId(log.getActorId())
                .actorRole(log.getActorRole())
                .action(log.getAction())
                .targetId(log.getTargetId())
                .targetType(log.getTargetType())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .timestamp(log.getTimestamp())
                .ipAddress(log.getIpAddress())
                .success(log.isSuccess())
                .severity(log.getSeverity())
                .build();
    }
}
