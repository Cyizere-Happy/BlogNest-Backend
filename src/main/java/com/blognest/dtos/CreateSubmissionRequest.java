package com.blognest.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSubmissionRequest {

    private String title;

    private String content;

    private String coverImage;
}