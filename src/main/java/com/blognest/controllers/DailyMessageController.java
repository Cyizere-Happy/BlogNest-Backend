package com.blognest.controllers;

import com.blognest.dtos.DailyMessageResponse;
import com.blognest.dtos.CreateDailyMessageRequest;
import com.blognest.services.DailyMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/daily-messages")
@RequiredArgsConstructor
@Tag(name = "Daily Messages", description = "Admin daily broadcasts")
public class DailyMessageController {

    private final DailyMessageService dailyMessageService;

    // POST /api/daily-messages?adminId={uuid}
    @PostMapping
    @Operation(summary = "Create daily message", description = "Creates a new daily broadcast message (restricted to administrators).")
    public ResponseEntity<DailyMessageResponse> createMessage(
            @RequestParam UUID adminId,
            @RequestBody CreateDailyMessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dailyMessageService.createMessage(adminId, request));
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
