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
public class DailyMessageResponse {

    private UUID id;

    private String title;

    private String message;

    private LocalDate date;

    private String createdByName;

    private LocalDateTime createdAt;
}