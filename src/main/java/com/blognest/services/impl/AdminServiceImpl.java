package com.blognest.services.impl;

import com.blognest.dtos.AdminDashboardResponse;
import com.blognest.models.enums.Role;
import com.blognest.repositories.*;
import com.blognest.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CompetitionRepository competitionRepository;
    private final SubmissionRepository submissionRepository;
    private final DailyMessageRepository dailyMessageRepository;

    @Override
    public AdminDashboardResponse getDashboardStats() {

        long totalUsers = userRepository.count();

        long totalWriters = userRepository.countByRole(Role.WRITER);

        long totalArticles = articleRepository.count();

        long totalCompetitions = competitionRepository.count();

        long totalSubmissions = submissionRepository.count();

        long totalDailyMessages = dailyMessageRepository.count();

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalWriters(totalWriters)
                .totalArticles(totalArticles)
                .totalCompetitions(totalCompetitions)
                .totalSubmissions(totalSubmissions)
                .totalDailyMessages(totalDailyMessages)
                .build();
    }
}