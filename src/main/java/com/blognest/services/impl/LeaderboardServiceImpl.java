package com.blognest.services.impl;

import com.blognest.dtos.LeaderboardEntryResponse;
import com.blognest.dtos.CompetitionWinnerResponse;
import com.blognest.exceptions.ResourceNotFoundException;
import com.blognest.models.Submission;
import com.blognest.repositories.SubmissionRepository;
import com.blognest.services.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private final SubmissionRepository submissionRepository;

    @Override
    public List<LeaderboardEntryResponse> getLeaderboard(UUID competitionId) {

        List<Submission> submissions =
                submissionRepository.getLeaderboard(competitionId);

        AtomicInteger rank = new AtomicInteger(1);

        return submissions.stream()
                .map(s -> LeaderboardEntryResponse.builder()
                        .submissionId(s.getId())
                        .title(s.getTitle())
                        .writerName(
                                s.getWriter() != null
                                        ? s.getWriter().getFullName()
                                        : null
                        )
                        .score(s.getScore())
                        .rank(rank.getAndIncrement())
                        .build()
                )
                .toList();
    }

    @Override
    public CompetitionWinnerResponse getWinners(UUID competitionId) {

        List<LeaderboardEntryResponse> leaderboard = getLeaderboard(competitionId);

        if (leaderboard.isEmpty()) {
            throw new ResourceNotFoundException("No submissions found for competition");
        }

        List<LeaderboardEntryResponse> winners =
                leaderboard.stream()
                        .limit(3)
                        .toList();

        return CompetitionWinnerResponse.builder()
                .competitionId(competitionId)
                .competitionTitle("Competition") // can be improved later
                .winners(winners)
                .build();
    }
}