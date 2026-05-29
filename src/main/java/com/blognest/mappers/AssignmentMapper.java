package com.blognest.mappers;

import com.blognest.dtos.AssignmentResponse;
import com.blognest.models.JudgeAssignment;

public class AssignmentMapper {

    public static AssignmentResponse toResponse(JudgeAssignment assignment) {
        if (assignment == null) return null;

        return AssignmentResponse.builder()
                .id(assignment.getId())
                .judgeId(assignment.getJudge().getId())
                .judgeName(assignment.getJudge().getFullName())
                .targetId(assignment.getTargetId())
                .type(assignment.getType())
                .status(assignment.getStatus())
                .score(assignment.getScore())
                .feedback(assignment.getFeedback())
                .assignedByName(assignment.getAssignedBy() != null ? assignment.getAssignedBy().getFullName() : null)
                .assignedAt(assignment.getAssignedAt())
                .completedAt(assignment.getCompletedAt())
                .build();
    }
}
