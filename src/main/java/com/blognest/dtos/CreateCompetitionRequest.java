package com.blognest.dtos;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCompetitionRequest {

    private String title;

    private String theme;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;
}