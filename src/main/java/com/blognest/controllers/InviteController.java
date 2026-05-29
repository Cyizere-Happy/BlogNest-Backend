package com.blognest.controllers;

import com.blognest.dtos.InviteResponse;
import com.blognest.dtos.CreateInviteRequest;
import com.blognest.services.InviteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invites")
@RequiredArgsConstructor
@Tag(name = "Invites", description = "Admin invite system")
public class InviteController {

    private final InviteService inviteService;

    // POST /api/invites
    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Send invite", description = "Generates and sends an invite token to a user's email address. The sender is resolved automatically from the JWT token.")
    public ResponseEntity<InviteResponse> sendInvite(
            @RequestBody CreateInviteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inviteService.sendInvite(request));
    }

    // GET /api/invites/validate?token={token}
    @GetMapping("/validate")
    @Operation(summary = "Validate invite token", description = "Validates an invite token to verify if it is active and not expired.")
    public ResponseEntity<InviteResponse> validateInvite(@RequestParam String token) {
        return ResponseEntity.ok(inviteService.validateInvite(token));
    }

    // PATCH /api/invites/use?token={token}
    @PatchMapping("/use")
    @Operation(summary = "Mark invite as used", description = "Marks a specific invite token as used after a user successfully registers.")
    public ResponseEntity<com.blognest.dtos.ApiResponse> markInviteAsUsed(@RequestParam String token) {
        inviteService.markInviteAsUsed(token);
        return ResponseEntity.ok(com.blognest.dtos.ApiResponse.builder()
                .success(true)
                .message("Invite marked as used successfully.")
                .build());
    }

    // GET /api/invites
    @GetMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Get all invites", description = "Retrieves all invites generated in the system.")
    public ResponseEntity<List<InviteResponse>> getAllInvites() {
        return ResponseEntity.ok(inviteService.getAllInvites());
    }
}
