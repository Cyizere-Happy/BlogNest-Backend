package com.blognest.mappers;

import com.blognest.dtos.SubscriptionResponse;
import com.blognest.models.Subscription;

public class SubscriptionMapper {

    public static SubscriptionResponse toResponse(Subscription sub) {

        return SubscriptionResponse.builder()
                .id(sub.getId())
                .subscriberId(sub.getSubscriber().getId())
                .subscriberName(sub.getSubscriber().getFullName())
                .writerId(sub.getWriter().getId())
                .writerName(sub.getWriter().getFullName())
                .createdAt(sub.getSubscribedAt())
                .build();
    }
}