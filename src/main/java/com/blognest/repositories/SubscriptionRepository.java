package com.blognest.repositories;

import com.blognest.models.Subscription;
import com.blognest.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    List<Subscription> findByWriter(User writer);

    List<Subscription> findBySubscriber(User subscriber);

    Optional<Subscription> findBySubscriberAndWriter(
            User subscriber,
            User writer
    );

    boolean existsBySubscriberAndWriter(
            User subscriber,
            User writer
    );
}