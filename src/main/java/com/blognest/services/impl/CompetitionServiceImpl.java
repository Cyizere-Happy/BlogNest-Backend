package com.blognest.services.impl;

import com.blognest.dtos.*;
import com.blognest.exceptions.ResourceNotFoundException;
import com.blognest.mappers.CompetitionMapper;
import com.blognest.models.Competition;
import com.blognest.repositories.CompetitionRepository;
import com.blognest.services.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionRepository competitionRepository;

    @Override
    public CompetitionResponse createCompetition(CreateCompetitionRequest request) {

        Competition competition = Competition.builder()
                .title(request.getTitle())
                .theme(request.getTheme())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(true)
                .build();

        return CompetitionMapper.toResponse(
                competitionRepository.save(competition)
        );
    }

    @Override
    public List<CompetitionResponse> getAllCompetitions() {

        return competitionRepository.findAll()
                .stream()
                .map(CompetitionMapper::toResponse)
                .toList();
    }

    @Override
    public List<CompetitionResponse> getActiveCompetitions() {

        return competitionRepository.findByActiveTrue()
                .stream()
                .map(CompetitionMapper::toResponse)
                .toList();
    }

    @Override
    public CompetitionResponse closeCompetition(UUID id) {

        Competition comp = competitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));

        comp.setActive(false);

        return CompetitionMapper.toResponse(
                competitionRepository.save(comp)
        );
    }
}