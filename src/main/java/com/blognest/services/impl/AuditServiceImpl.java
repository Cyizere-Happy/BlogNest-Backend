package com.blognest.services.impl;

import com.blognest.dtos.AuditEvent;
import com.blognest.dtos.AuditLogResponse;
import com.blognest.exceptions.ResourceNotFoundException;
import com.blognest.exceptions.UnauthorizedException;
import com.blognest.mappers.AuditLogMapper;
import com.blognest.models.AuditLog;
import com.blognest.models.User;
import com.blognest.models.enums.Role;
import com.blognest.repositories.AuditLogRepository;
import com.blognest.repositories.UserRepository;
import com.blognest.services.AuthService;
import com.blognest.services.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW) // Production-grade forensic safety
    public void logEvent(AuditEvent event) {
        AuditLog log = AuditLog.builder()
                .actorId(event.actorId())
                .actorRole(event.actorRole())
                .action(event.action())
                .targetId(event.targetId())
                .targetType(event.targetType())
                .oldValue(event.oldValue())
                .newValue(event.newValue())
                .timestamp(LocalDateTime.now())
                .ipAddress(event.ipAddress())
                .success(event.success())
                .severity(event.severity())
                .build();

        auditLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAllAuditLogs(Pageable pageable) {
        UUID actorId = authService.getCurrentUserId();
        User currentUser = userRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (currentUser.getRole() != Role.SUPERADMIN) {
            throw new UnauthorizedException("Only SUPERADMIN can access system audit logs.");
        }

        return auditLogRepository.findAll(pageable)
                .map(AuditLogMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogsByActor(UUID actorId, Pageable pageable) {
        UUID currentUserId = authService.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (currentUser.getRole() != Role.SUPERADMIN) {
            throw new UnauthorizedException("Only SUPERADMIN can access system audit logs.");
        }

        return auditLogRepository.findByActorId(actorId, pageable)
                .map(AuditLogMapper::toResponse);
    }
}
