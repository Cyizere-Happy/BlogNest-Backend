package com.blognest.controllers;

import com.blognest.dtos.SubscriptionResponse;
import com.blognest.services.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Writer subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // POST /api/subscriptions?writerId={uuid}
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Subscribe to writer", description = "Subscribes the authenticated user to updates from a specific writer.")
    public ResponseEntity<SubscriptionResponse> subscribe(
            @RequestParam UUID writerId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subscriptionService.subscribe(writerId));
    }

    // DELETE /api/subscriptions?writerId={uuid}
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Unsubscribe from writer", description = "Unsubscribes the authenticated user from updates from a specific writer.")
    public ResponseEntity<com.blognest.dtos.ApiResponse> unsubscribe(
            @RequestParam UUID writerId) {
        subscriptionService.unsubscribe(writerId);
        return ResponseEntity.ok(com.blognest.dtos.ApiResponse.builder()
                .success(true)
                .message("Unsubscribed successfully.")
                .build());
    }

    // GET /api/subscriptions/writer/{writerId}/subscribers
    @GetMapping("/writer/{writerId}/subscribers")
    @PreAuthorize("hasRole('SUPERADMIN') or @securityEvaluator.isSelf(#writerId)")
    @Operation(summary = "Get writer subscribers", description = "Retrieves all subscribers for a specific writer.")
    public ResponseEntity<List<SubscriptionResponse>> getWriterSubscribers(
            @PathVariable UUID writerId) {
        return ResponseEntity.ok(subscriptionService.getWriterSubscribers(writerId));
    }

    // GET /api/subscriptions/me/following
    @GetMapping("/me/following")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my subscriptions", description = "Retrieves all writers the authenticated user is subscribed to.")
    public ResponseEntity<List<SubscriptionResponse>> getMySubscriptions() {
        return ResponseEntity.ok(subscriptionService.getUserSubscriptions());
    }
}
