package com.blognest.mappers;

import com.blognest.dtos.WriterApplicationResponse;
import com.blognest.models.WriterApplication;

public class WriterApplicationMapper {

    public static WriterApplicationResponse toResponse(WriterApplication app) {

        return WriterApplicationResponse.builder()
                .id(app.getId())
                .motivation(app.getMotivation())
                .sampleWriting(app.getSampleWriting())
                .approved(app.isApproved())
                .reviewed(app.isReviewed())
                .applicantName(
                        app.getApplicant() != null
                                ? app.getApplicant().getFullName()
                                : null
                )
                .createdAt(app.getCreatedAt())
                .build();
    }
}