package com.blognest.controllers;

import com.blognest.dtos.UserResponse;
import com.blognest.dtos.CreateUserRequest;
import com.blognest.dtos.UpdateUserRequest;
import com.blognest.services.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management")
public class UserController {

    private final UserService userService;

    // POST /api/users
    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Create user", description = "Creates a new user account in the system.")
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }

    // GET /api/users
    @GetMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Get all users", description = "Retrieves a list of all registered users.")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN') or @securityEvaluator.isSelf(#id)")
    @Operation(summary = "Get user by ID", description = "Retrieves details of a user by their unique identifier.")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // PUT /api/users/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN') or @securityEvaluator.isSelf(#id)")
    @Operation(summary = "Update user", description = "Updates profile information for an existing user.")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    // DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Delete user", description = "Bans or deletes a user account from the system.")
    public ResponseEntity<com.blognest.dtos.ApiResponse> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(com.blognest.dtos.ApiResponse.builder()
                .success(true)
                .message("User deleted successfully.")
                .build());
    }

    // PATCH /api/users/{id}/role
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Update user role", description = "Updates the role of a user (Restricted to SUPERADMIN).")
    public ResponseEntity<UserResponse> updateRole(
            @PathVariable UUID id,
            @RequestParam com.blognest.models.enums.Role role) {
        return ResponseEntity.ok(userService.updateRole(id, role));
    }

    // PATCH /api/users/{id}/suspend
    @PatchMapping("/{id}/suspend")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Toggle user suspension", description = "Suspends or unsuspends a user account (Restricted to SUPERADMIN).")
    public ResponseEntity<UserResponse> toggleSuspension(
            @PathVariable UUID id,
            @RequestParam boolean suspended) {
        return ResponseEntity.ok(userService.toggleSuspension(id, suspended));
    }
}
