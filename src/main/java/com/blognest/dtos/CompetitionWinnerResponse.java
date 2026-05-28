package com.blognest.dtos;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitionWinnerResponse {

    private UUID competitionId;

    private String competitionTitle;

    private List<LeaderboardEntryResponse> winners;
}