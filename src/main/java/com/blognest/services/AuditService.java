package com.blognest.services;

import com.blognest.dtos.AuditEvent;
import com.blognest.dtos.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AuditService {

    void logEvent(AuditEvent event);

    Page<AuditLogResponse> getAllAuditLogs(Pageable pageable);

    Page<AuditLogResponse> getAuditLogsByActor(UUID actorId, Pageable pageable);
}
