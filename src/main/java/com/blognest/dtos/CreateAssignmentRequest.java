package com.blognest.dtos;

import com.blognest.models.enums.AssignmentType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAssignmentRequest {

    @NotNull(message = "Judge ID is required")
    private UUID judgeId;

    @NotNull(message = "Target ID is required")
    private UUID targetId;

    @NotNull(message = "Assignment Type is required")
    private AssignmentType type;
}
