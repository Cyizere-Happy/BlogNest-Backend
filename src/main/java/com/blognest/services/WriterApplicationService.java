package com.blognest.services;

import com.blognest.dtos.CreateWriterApplicationRequest;
import com.blognest.dtos.WriterApplicationResponse;

import java.util.List;
import java.util.UUID;

public interface WriterApplicationService {

    WriterApplicationResponse apply(CreateWriterApplicationRequest request);

    List<WriterApplicationResponse> getAllApplications();

    List<WriterApplicationResponse> getPendingApplications();

    WriterApplicationResponse approveApplication(UUID applicationId);

    WriterApplicationResponse rejectApplication(UUID applicationId);
}