package com.blognest.mappers;

import com.blognest.dtos.SubmissionResponse;
import com.blognest.models.Submission;

public class SubmissionMapper {

    public static SubmissionResponse toResponse(Submission s) {

        return SubmissionResponse.builder()
                .id(s.getId())
                .title(s.getTitle())
                .content(s.getContent())
                .coverImage(s.getCoverImage())
                .score(s.getScore())
                .approved(s.isApproved())
                .competitionTitle(
                        s.getCompetition() != null ? s.getCompetition().getTitle() : null
                )
                .writerName(
                        s.getWriter() != null ? s.getWriter().getFullName() : null
                )
                .build();
    }
}