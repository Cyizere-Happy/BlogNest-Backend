package com.blognest.services;

import com.blognest.dtos.CreateCompetitionRequest;
import com.blognest.dtos.CompetitionResponse;

import java.util.List;
import java.util.UUID;

public interface CompetitionService {

    CompetitionResponse createCompetition(CreateCompetitionRequest request);

    List<CompetitionResponse> getAllCompetitions();

    List<CompetitionResponse> getActiveCompetitions();

    CompetitionResponse closeCompetition(UUID id);
}