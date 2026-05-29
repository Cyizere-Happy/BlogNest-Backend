package com.blognest.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreAssignmentRequest {

    @NotNull(message = "Score is required")
    private Double score;

    private String feedback;
}
