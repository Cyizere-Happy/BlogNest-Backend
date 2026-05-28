package com.blognest.dtos;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionResponse {

    private UUID id;

    private String title;

    private String content;

    private String coverImage;

    private double score;

    private boolean approved;

    private String competitionTitle;

    private String writerName;
}