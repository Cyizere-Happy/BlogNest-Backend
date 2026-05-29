package com.blognest.services.impl;

import com.blognest.dtos.SubscriptionResponse;
import com.blognest.exceptions.DuplicateResourceException;
import com.blognest.exceptions.ResourceNotFoundException;
import com.blognest.mappers.SubscriptionMapper;
import com.blognest.models.Subscription;
import com.blognest.models.User;
import com.blognest.repositories.SubscriptionRepository;
import com.blognest.repositories.UserRepository;
import com.blognest.services.AuthService;
import com.blognest.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    // SUBSCRIBE
    @Override
    public SubscriptionResponse subscribe(UUID writerId) {
        UUID subscriberId = authService.getCurrentUserId();

        if (subscriberId.equals(writerId)) {
            throw new IllegalArgumentException("You cannot subscribe to yourself");
        }

        User subscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber not found"));

        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new ResourceNotFoundException("Writer not found"));

        boolean exists = subscriptionRepository
                .existsBySubscriberAndWriter(subscriber, writer);

        if (exists) {
            throw new DuplicateResourceException("Already subscribed");
        }

        Subscription sub = Subscription.builder()
                .subscriber(subscriber)
                .writer(writer)
                .build();

        return SubscriptionMapper.toResponse(subscriptionRepository.save(sub));
    }

    // UNSUBSCRIBE
    @Override
    public void unsubscribe(UUID writerId) {
        UUID subscriberId = authService.getCurrentUserId();

        User subscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber not found"));

        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new ResourceNotFoundException("Writer not found"));

        Subscription sub = subscriptionRepository
                .findBySubscriberAndWriter(subscriber, writer)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        subscriptionRepository.delete(sub);
    }

    // GET WRITER FOLLOWERS
    @Override
    public List<SubscriptionResponse> getWriterSubscribers(UUID writerId) {

        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new ResourceNotFoundException("Writer not found"));

        return subscriptionRepository.findByWriter(writer)
                .stream()
                .map(SubscriptionMapper::toResponse)
                .toList();
    }

    // GET USER FOLLOWING
    @Override
    public List<SubscriptionResponse> getUserSubscriptions() {
        UUID subscriberId = authService.getCurrentUserId();

        User subscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return subscriptionRepository.findBySubscriber(subscriber)
                .stream()
                .map(SubscriptionMapper::toResponse)
                .toList();
    }
}