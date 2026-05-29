package com.blognest.controllers;

import com.blognest.dtos.CompetitionWinnerResponse;
import com.blognest.dtos.LeaderboardEntryResponse;
import com.blognest.services.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Leaderboard", description = "Competition rankings")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    // GET /api/leaderboard/{competitionId}
    @GetMapping("/{competitionId}")
    @Operation(summary = "Get competition leaderboard", description = "Retrieves the leaderboard showing all submissions ranked by score for a specific competition.")
    public ResponseEntity<List<LeaderboardEntryResponse>> getLeaderboard(
            @PathVariable UUID competitionId) {
        return ResponseEntity.ok(leaderboardService.getLeaderboard(competitionId));
    }

    // GET /api/leaderboard/{competitionId}/winners
    @GetMapping("/{competitionId}/winners")
    @Operation(summary = "Get competition winners", description = "Retrieves the top-3 winners for a completed competition.")
    public ResponseEntity<CompetitionWinnerResponse> getWinners(
            @PathVariable UUID competitionId) {
        return ResponseEntity.ok(leaderboardService.getWinners(competitionId));
    }
}
