package com.blognest.controllers;

import com.blognest.dtos.DailyMessageResponse;
import com.blognest.dtos.CreateDailyMessageRequest;
import com.blognest.services.DailyMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/daily-messages")
@RequiredArgsConstructor
@Tag(name = "Daily Messages", description = "Admin daily broadcasts")
public class DailyMessageController {

    private final DailyMessageService dailyMessageService;

    // POST /api/daily-messages
    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Create daily message", description = "Creates a new daily broadcast message. The sender is resolved automatically from the JWT token.")
    public ResponseEntity<DailyMessageResponse> createMessage(
            @RequestBody CreateDailyMessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dailyMessageService.createMessage(request));
    }

    // GET /api/daily-messages
    @GetMapping
    @Operation(summary = "Get all daily messages", description = "Retrieves all daily broadcast messages in reverse chronological order.")
    public ResponseEntity<List<DailyMessageResponse>> getAllMessages() {
        return ResponseEntity.ok(dailyMessageService.getAllMessages());
    }

    // GET /api/daily-messages/today
    @GetMapping("/today")
    @Operation(summary = "Get today's message", description = "Retrieves the active daily message for today.")
    public ResponseEntity<DailyMessageResponse> getTodayMessage() {
        return ResponseEntity.ok(dailyMessageService.getTodayMessage());
    }
}
