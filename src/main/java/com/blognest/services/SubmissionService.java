package com.blognest.services;

import com.blognest.dtos.CreateSubmissionRequest;
import com.blognest.dtos.SubmissionResponse;

import java.util.List;
import java.util.UUID;

public interface SubmissionService {

    SubmissionResponse submit(UUID competitionId, CreateSubmissionRequest request);

    List<SubmissionResponse> getByCompetition(UUID competitionId);

    List<SubmissionResponse> getByWriter(UUID writerId);

    SubmissionResponse scoreSubmission(UUID submissionId, double score);

    SubmissionResponse approveSubmission(UUID submissionId);
}