package com.blognest.mappers;

import com.blognest.dtos.DailyMessageResponse;
import com.blognest.models.DailyMessage;

public class DailyMessageMapper {

    public static DailyMessageResponse toResponse(DailyMessage message) {

        return DailyMessageResponse.builder()
                .id(message.getId())
                .title(message.getTitle())
                .message(message.getMessage())
                .date(message.getDate())
                .createdByName(
                        message.getCreatedBy() != null
                                ? message.getCreatedBy().getFullName()
                                : null
                )
                .createdAt(message.getCreatedAt())
                .build();
    }
}