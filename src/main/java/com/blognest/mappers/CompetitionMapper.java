package com.blognest.mappers;

import com.blognest.dtos.CompetitionResponse;
import com.blognest.models.Competition;

public class CompetitionMapper {

    public static CompetitionResponse toResponse(Competition c) {

        return CompetitionResponse.builder()
                .id(c.getId())
                .title(c.getTitle())
                .theme(c.getTheme())
                .description(c.getDescription())
                .startDate(c.getStartDate())
                .endDate(c.getEndDate())
                .active(c.isActive())
                .createdAt(c.getCreatedAt())
                .build();
    }
}