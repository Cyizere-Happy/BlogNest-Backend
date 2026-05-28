package com.blognest.services;

import com.blognest.dtos.LeaderboardEntryResponse;
import com.blognest.dtos.CompetitionWinnerResponse;

import java.util.List;
import java.util.UUID;

public interface LeaderboardService {

    List<LeaderboardEntryResponse> getLeaderboard(UUID competitionId);

    CompetitionWinnerResponse getWinners(UUID competitionId);
}