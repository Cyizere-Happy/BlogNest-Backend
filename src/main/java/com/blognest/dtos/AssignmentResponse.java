package com.blognest.dtos;

import com.blognest.models.enums.AssignmentStatus;
import com.blognest.models.enums.AssignmentType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentResponse {
    private UUID id;
    private UUID judgeId;
    private String judgeName;
    private UUID targetId;
    private AssignmentType type;
    private AssignmentStatus status;
    private Double score;
    private String feedback;
    private String assignedByName;
    private LocalDateTime assignedAt;
    private LocalDateTime completedAt;
}
