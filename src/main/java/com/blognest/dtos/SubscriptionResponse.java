package com.blognest.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {

    private UUID id;

    private UUID subscriberId;

    private String subscriberName;

    private UUID writerId;

    private String writerName;

    private LocalDateTime createdAt;
}