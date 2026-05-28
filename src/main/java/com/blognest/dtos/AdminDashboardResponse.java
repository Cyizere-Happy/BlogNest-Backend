package com.blognest.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {

    private long totalUsers;

    private long totalWriters;

    private long totalArticles;

    private long totalCompetitions;

    private long totalSubmissions;

    private long totalDailyMessages;
}