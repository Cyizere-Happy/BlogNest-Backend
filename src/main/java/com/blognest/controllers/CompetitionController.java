package com.blognest.controllers;

import com.blognest.dtos.CompetitionResponse;
import com.blognest.dtos.CreateCompetitionRequest;
import com.blognest.services.CompetitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/competitions")
@RequiredArgsConstructor
@Tag(name = "Competitions", description = "Writing competitions")
public class CompetitionController {

    private final CompetitionService competitionService;

    // POST /api/competitions
    @PostMapping
    @Operation(summary = "Create competition", description = "Creates a new writing competition.")
    public ResponseEntity<CompetitionResponse> createCompetition(
            @RequestBody CreateCompetitionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(competitionService.createCompetition(request));
    }

    // GET /api/competitions
    @GetMapping
    @Operation(summary = "Get all competitions", description = "Retrieves all competitions in the system.")
    public ResponseEntity<List<CompetitionResponse>> getAllCompetitions() {
        return ResponseEntity.ok(competitionService.getAllCompetitions());
    }

    // GET /api/competitions/active
    @GetMapping("/active")
    @Operation(summary = "Get active competitions", description = "Retrieves currently active competitions that are open for submissions.")
    public ResponseEntity<List<CompetitionResponse>> getActiveCompetitions() {
        return ResponseEntity.ok(competitionService.getActiveCompetitions());
    }

    // PATCH /api/competitions/{id}/close
    @PatchMapping("/{id}/close")
    @Operation(summary = "Close competition", description = "Closes a competition and triggers winner selection based on submission scores.")
    public ResponseEntity<CompetitionResponse> closeCompetition(@PathVariable UUID id) {
        return ResponseEntity.ok(competitionService.closeCompetition(id));
    }
}
