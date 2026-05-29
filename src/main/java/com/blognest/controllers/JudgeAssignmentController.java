package com.blognest.controllers;

import com.blognest.dtos.AssignmentResponse;
import com.blognest.dtos.CreateAssignmentRequest;
import com.blognest.dtos.ScoreAssignmentRequest;
import com.blognest.services.JudgeAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Tag(name = "Judge Assignments", description = "Admin assignment creation and Judge evaluation pipeline")
public class JudgeAssignmentController {

    private final JudgeAssignmentService assignmentService;

    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Assign task to judge", description = "Creates a new task assignment for a JUDGE (Restricted to SUPERADMIN).")
    public ResponseEntity<AssignmentResponse> createAssignment(@Valid @RequestBody CreateAssignmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assignmentService.createAssignment(request));
    }

    @PatchMapping("/{id}/score")
    @PreAuthorize("hasAnyRole('JUDGE', 'SUPERADMIN')")
    @Operation(summary = "Score assignment", description = "Submit evaluation score and feedback for an assigned task (Restricted to assigned JUDGE or SUPERADMIN).")
    public ResponseEntity<AssignmentResponse> scoreAssignment(
            @PathVariable UUID id,
            @Valid @RequestBody ScoreAssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.scoreAssignment(id, request));
    }

    @GetMapping("/my-pending")
    @PreAuthorize("hasAnyRole('JUDGE', 'SUPERADMIN')")
    @Operation(summary = "Get my pending tasks", description = "Retrieves all pending evaluation tasks for the logged-in JUDGE.")
    public ResponseEntity<List<AssignmentResponse>> getMyPendingAssignments() {
        return ResponseEntity.ok(assignmentService.getMyPendingAssignments());
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('JUDGE', 'SUPERADMIN')")
    @Operation(summary = "Get all my tasks", description = "Retrieves all evaluation tasks (both pending and completed) for the logged-in JUDGE.")
    public ResponseEntity<List<AssignmentResponse>> getMyAssignments() {
        return ResponseEntity.ok(assignmentService.getMyAssignments());
    }
}
