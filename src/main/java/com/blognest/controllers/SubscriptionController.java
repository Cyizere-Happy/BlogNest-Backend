package com.blognest.controllers;

import com.blognest.dtos.SubscriptionResponse;
import com.blognest.services.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Writer subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // POST /api/subscriptions?subscriberId={uuid}&writerId={uuid}
    @PostMapping
    @Operation(summary = "Subscribe to writer", description = "Subscribes a user to updates from a specific writer.")
    public ResponseEntity<SubscriptionResponse> subscribe(
            @RequestParam UUID subscriberId,
            @RequestParam UUID writerId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subscriptionService.subscribe(subscriberId, writerId));
    }

    // DELETE /api/subscriptions?subscriberId={uuid}&writerId={uuid}
    @DeleteMapping
    @Operation(summary = "Unsubscribe from writer", description = "Unsubscribes a user from updates from a specific writer.")
    public ResponseEntity<com.blognest.dtos.ApiResponse> unsubscribe(
            @RequestParam UUID subscriberId,
            @RequestParam UUID writerId) {
        subscriptionService.unsubscribe(subscriberId, writerId);
        return ResponseEntity.ok(com.blognest.dtos.ApiResponse.builder()
                .success(true)
                .message("Unsubscribed successfully.")
                .build());
    }

    // GET /api/subscriptions/writer/{writerId}/subscribers
    @GetMapping("/writer/{writerId}/subscribers")
    @Operation(summary = "Get writer subscribers", description = "Retrieves all subscribers for a specific writer.")
    public ResponseEntity<List<SubscriptionResponse>> getWriterSubscribers(
            @PathVariable UUID writerId) {
        return ResponseEntity.ok(subscriptionService.getWriterSubscribers(writerId));
    }

    // GET /api/subscriptions/user/{subscriberId}/following
    @GetMapping("/user/{subscriberId}/following")
    @Operation(summary = "Get subscribed writers", description = "Retrieves all subscriptions (writers followed) for a user.")
    public ResponseEntity<List<SubscriptionResponse>> getUserSubscriptions(
            @PathVariable UUID subscriberId) {
        return ResponseEntity.ok(subscriptionService.getUserSubscriptions(subscriberId));
    }
}
