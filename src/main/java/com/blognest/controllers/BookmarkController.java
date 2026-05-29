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

    // POST /api/bookmarks?articleId={uuid}
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Add bookmark", description = "Bookmarks an article for the authenticated user.")
    public ResponseEntity<BookmarkResponse> addBookmark(
            @RequestParam UUID articleId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookmarkService.addBookmark(articleId));
    }

    // DELETE /api/bookmarks?articleId={uuid}
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Remove bookmark", description = "Removes a bookmarked article for the authenticated user.")
    public ResponseEntity<com.blognest.dtos.ApiResponse> removeBookmark(
            @RequestParam UUID articleId) {
        bookmarkService.removeBookmark(articleId);
        return ResponseEntity.ok(com.blognest.dtos.ApiResponse.builder()
                .success(true)
                .message("Bookmark removed successfully.")
                .build());
    }

    // GET /api/bookmarks/me
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my bookmarks", description = "Retrieves all bookmarked articles for the currently authenticated user.")
    public ResponseEntity<List<BookmarkResponse>> getMyBookmarks() {
        return ResponseEntity.ok(bookmarkService.getUserBookmarks());
    }

    // GET /api/bookmarks/check?articleId={uuid}
    @GetMapping("/check")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Check if bookmarked", description = "Checks whether a specific article is bookmarked by the authenticated user.")
    public ResponseEntity<Boolean> isBookmarked(
            @RequestParam UUID articleId) {
        return ResponseEntity.ok(bookmarkService.isBookmarked(articleId));
    }
}
