package com.blognest.controllers;

import com.blognest.dtos.AuditLogResponse;
import com.blognest.services.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
@Tag(name = "Audit Logs", description = "System oversight and activity logging (Restricted to SUPERADMIN)")
public class AuditLogController {

    private final AuditService auditService;

    @GetMapping
    @Operation(summary = "Get all audit logs", description = "Retrieves a paginated list of all system audit logs.")
    public ResponseEntity<Page<AuditLogResponse>> getAllAuditLogs(
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        return ResponseEntity.ok(auditService.getAllAuditLogs(pageable));
    }

    @GetMapping("/actor/{actorId}")
    @Operation(summary = "Get audit logs by actor", description = "Retrieves a paginated list of audit logs filtered by actor ID.")
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogsByActor(
            @PathVariable UUID actorId,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        return ResponseEntity.ok(auditService.getAuditLogsByActor(actorId, pageable));
    }
}
