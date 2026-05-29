package com.blognest.services;

import com.blognest.dtos.SubscriptionResponse;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {

    SubscriptionResponse subscribe(UUID writerId);

    void unsubscribe(UUID writerId);

    List<SubscriptionResponse> getWriterSubscribers(UUID writerId);

    List<SubscriptionResponse> getUserSubscriptions();
}