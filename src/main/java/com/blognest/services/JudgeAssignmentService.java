package com.blognest.services;

import com.blognest.dtos.AssignmentResponse;
import com.blognest.dtos.CreateAssignmentRequest;
import com.blognest.dtos.ScoreAssignmentRequest;

import java.util.List;
import java.util.UUID;

public interface JudgeAssignmentService {

    AssignmentResponse createAssignment(CreateAssignmentRequest request);

    AssignmentResponse scoreAssignment(UUID assignmentId, ScoreAssignmentRequest request);

    List<AssignmentResponse> getMyPendingAssignments();

    List<AssignmentResponse> getMyAssignments();
}
