package com.blognest.config;

import com.blognest.dtos.AuditEvent;
import com.blognest.services.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditEventListener {

    private final AuditService auditService;

    @Async
    @EventListener
    public void handleAuditEvent(AuditEvent event) {
        auditService.logEvent(event);
    }
}
