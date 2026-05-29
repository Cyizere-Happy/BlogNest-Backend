package com.blognest.controllers;

import com.blognest.dtos.CommentResponse;
import com.blognest.dtos.CreateCommentRequest;
import com.blognest.services.AuthService;
import com.blognest.services.CommentService;
import com.blognest.services.RateLimiterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Article comments")
public class CommentController {

    private final CommentService commentService;
    private final RateLimiterService rateLimiterService;
    private final AuthService authService;

    @org.springframework.beans.factory.annotation.Value("${rate-limit.comment.max:10}")
    private int commentMaxAttempts;

    @org.springframework.beans.factory.annotation.Value("${rate-limit.comment.seconds:60}")
    private long commentWindowSeconds;

    @PostMapping
    @Operation(summary = "Create comment", description = "Posts a new comment or reply on an article.")
    public ResponseEntity<CommentResponse> createComment(
            @RequestBody CreateCommentRequest request) {
        UUID userId = authService.getCurrentUserId();
        if (!rateLimiterService.tryConsume("comment:" + userId, commentMaxAttempts, commentWindowSeconds)) {
            throw new com.blognest.exceptions.TooManyRequestsException("Too many comment requests. Please wait before posting again.");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(request));
    }

    @GetMapping("/article/{articleId}")
    @Operation(summary = "Get comments by article", description = "Retrieves all root comments (paginated) associated with a given article ID.")
    public ResponseEntity<Page<CommentResponse>> getCommentsByArticle(
            @PathVariable UUID articleId,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByArticle(articleId, pageable));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('SUPERADMIN') or @securityEvaluator.isCommentAuthor(#commentId)")
    @Operation(summary = "Delete comment", description = "Deletes or soft-deletes a comment.")
    public ResponseEntity<com.blognest.dtos.ApiResponse> deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(com.blognest.dtos.ApiResponse.builder()
                .success(true)
                .message("Comment deleted successfully.")
                .build());
    }
}
