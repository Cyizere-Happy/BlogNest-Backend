package com.blognest.controllers;

import com.blognest.dtos.BookmarkResponse;
import com.blognest.services.BookmarkService;
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
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@Tag(name = "Bookmarks", description = "User bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    // POST /api/bookmarks?userId={uuid}&articleId={uuid}
    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN') or @securityEvaluator.isSelf(#userId)")
    @Operation(summary = "Add bookmark", description = "Bookmarks an article for a user.")
    public ResponseEntity<BookmarkResponse> addBookmark(
            @RequestParam UUID userId,
            @RequestParam UUID articleId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookmarkService.addBookmark(userId, articleId));
    }

    // DELETE /api/bookmarks?userId={uuid}&articleId={uuid}
    @DeleteMapping
    @PreAuthorize("hasRole('SUPERADMIN') or @securityEvaluator.isSelf(#userId)")
    @Operation(summary = "Remove bookmark", description = "Removes a bookmarked article for a user.")
    public ResponseEntity<com.blognest.dtos.ApiResponse> removeBookmark(
            @RequestParam UUID userId,
            @RequestParam UUID articleId) {
        bookmarkService.removeBookmark(userId, articleId);
        return ResponseEntity.ok(com.blognest.dtos.ApiResponse.builder()
                .success(true)
                .message("Bookmark removed successfully.")
                .build());
    }

    // GET /api/bookmarks/user/{userId}
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('SUPERADMIN') or @securityEvaluator.isSelf(#userId)")
    @Operation(summary = "Get user bookmarks", description = "Retrieves all bookmarked articles for a specific user.")
    public ResponseEntity<List<BookmarkResponse>> getUserBookmarks(@PathVariable UUID userId) {
        return ResponseEntity.ok(bookmarkService.getUserBookmarks(userId));
    }

    // GET /api/bookmarks/check?userId={uuid}&articleId={uuid}
    @GetMapping("/check")
    @PreAuthorize("hasRole('SUPERADMIN') or @securityEvaluator.isSelf(#userId)")
    @Operation(summary = "Check if bookmarked", description = "Checks whether a specific article is bookmarked by a user.")
    public ResponseEntity<Boolean> isBookmarked(
            @RequestParam UUID userId,
            @RequestParam UUID articleId) {
        return ResponseEntity.ok(bookmarkService.isBookmarked(userId, articleId));
    }
}
