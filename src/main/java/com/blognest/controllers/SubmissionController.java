package com.blognest.controllers;

import com.blognest.dtos.SubmissionResponse;
import com.blognest.dtos.CreateSubmissionRequest;
import com.blognest.services.SubmissionService;
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
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
@Tag(name = "Submissions", description = "Competition submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    // POST /api/submissions?competitionId={uuid}
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Submit to competition", description = "Submits a story/article to an active writing competition. The writer is resolved automatically from the JWT token.")
    public ResponseEntity<SubmissionResponse> submit(
            @RequestParam UUID competitionId,
            @RequestBody CreateSubmissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(submissionService.submit(competitionId, request));
    }

    // GET /api/submissions/competition/{competitionId}
    @GetMapping("/competition/{competitionId}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Get submissions by competition", description = "Retrieves all submissions made to a specific competition.")
    public ResponseEntity<List<SubmissionResponse>> getByCompetition(
            @PathVariable UUID competitionId) {
        return ResponseEntity.ok(submissionService.getByCompetition(competitionId));
    }

    // GET /api/submissions/writer/{writerId}
    @GetMapping("/writer/{writerId}")
    @PreAuthorize("hasRole('SUPERADMIN') or @securityEvaluator.isSelf(#writerId)")
    @Operation(summary = "Get submissions by writer", description = "Retrieves all submissions made by a specific writer.")
    public ResponseEntity<List<SubmissionResponse>> getByWriter(@PathVariable UUID writerId) {
        return ResponseEntity.ok(submissionService.getByWriter(writerId));
    }

    // PATCH /api/submissions/{id}/score?score={value}
    @PatchMapping("/{id}/score")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Score submission", description = "Grades/scores a submission. Used by admins or reviewers.")
    public ResponseEntity<SubmissionResponse> scoreSubmission(
            @PathVariable UUID id,
            @RequestParam double score) {
        return ResponseEntity.ok(submissionService.scoreSubmission(id, score));
    }

    // PATCH /api/submissions/{id}/approve
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Approve submission", description = "Approves a submission for a competition.")
    public ResponseEntity<SubmissionResponse> approveSubmission(@PathVariable UUID id) {
        return ResponseEntity.ok(submissionService.approveSubmission(id));
    }
}
