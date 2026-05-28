package com.blognest.dtos;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitionResponse {

    private UUID id;

    private String title;

    private String theme;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean active;

    private LocalDateTime createdAt;
}