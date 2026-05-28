package com.blognest.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WriterApplicationResponse {

    private UUID id;

    private String motivation;

    private String sampleWriting;

    private boolean approved;

    private boolean reviewed;

    private String applicantName;

    private LocalDateTime createdAt;
}