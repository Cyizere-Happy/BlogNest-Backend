package com.blognest.controllers;

import com.blognest.dtos.AdminDashboardResponse;
import com.blognest.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin dashboard")
public class AdminController {

    private final AdminService adminService;

    // GET /api/admin/dashboard
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Get dashboard statistics", description = "Retrieves statistics for the admin dashboard (e.g. counts of users, articles, etc.).")
    public ResponseEntity<AdminDashboardResponse> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }
}
