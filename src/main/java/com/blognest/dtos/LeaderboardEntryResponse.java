package com.blognest.dtos;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardEntryResponse {

    private UUID submissionId;

    private String title;

    private String writerName;

    private double score;

    private int rank;
}