package com.blognest.controllers;

import com.blognest.dtos.WriterApplicationResponse;
import com.blognest.dtos.CreateWriterApplicationRequest;
import com.blognest.services.WriterApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/writer-applications")
@RequiredArgsConstructor
@Tag(name = "Writer Applications", description = "Apply to become a writer")
public class WriterApplicationController {

    private final WriterApplicationService writerApplicationService;

    // POST /api/writer-applications?userId={uuid}
    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN') or @securityEvaluator.isSelf(#userId)")
    @Operation(summary = "Submit application", description = "Submits a new writer application for a user.")
    public ResponseEntity<WriterApplicationResponse> apply(
            @RequestParam UUID userId,
            @RequestBody CreateWriterApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(writerApplicationService.apply(userId, request));
    }

    // GET /api/writer-applications
    @GetMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Get all applications", description = "Retrieves all writer applications in the system.")
    public ResponseEntity<List<WriterApplicationResponse>> getAllApplications() {
        return ResponseEntity.ok(writerApplicationService.getAllApplications());
    }

    // GET /api/writer-applications/pending
    @GetMapping("/pending")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Get pending applications", description = "Retrieves a list of pending writer applications waiting for approval.")
    public ResponseEntity<List<WriterApplicationResponse>> getPendingApplications() {
        return ResponseEntity.ok(writerApplicationService.getPendingApplications());
    }

    // PATCH /api/writer-applications/{id}/approve
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Approve application", description = "Approves a writer application and promotes the user to ADMIN.")
    public ResponseEntity<WriterApplicationResponse> approveApplication(@PathVariable UUID id) {
        return ResponseEntity.ok(writerApplicationService.approveApplication(id));
    }

    // PATCH /api/writer-applications/{id}/reject
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Reject application", description = "Rejects a writer application.")
    public ResponseEntity<WriterApplicationResponse> rejectApplication(@PathVariable UUID id) {
        return ResponseEntity.ok(writerApplicationService.rejectApplication(id));
    }
}
