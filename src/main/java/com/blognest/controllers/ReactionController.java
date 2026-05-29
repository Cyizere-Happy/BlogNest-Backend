package com.blognest.controllers;

import com.blognest.dtos.ReactionStatusResponse;
import com.blognest.models.enums.ReactionType;
import com.blognest.services.ReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/articles/{articleId}/react")
@RequiredArgsConstructor
@Tag(name = "Reactions", description = "Article reaction interactions")
public class ReactionController {

    private final ReactionService reactionService;

    @PostMapping
    @Operation(summary = "React to article", description = "Adds or updates a reaction to the specified article.")
    public ResponseEntity<ReactionStatusResponse> reactToArticle(
            @PathVariable UUID articleId,
            @RequestParam(defaultValue = "LIKE") ReactionType type) {
        return ResponseEntity.ok(reactionService.reactToArticle(articleId, type));
    }

    @DeleteMapping
    @Operation(summary = "Remove reaction from article", description = "Removes the user's reaction from the specified article.")
    public ResponseEntity<ReactionStatusResponse> removeReaction(@PathVariable UUID articleId) {
        return ResponseEntity.ok(reactionService.removeReaction(articleId));
    }

    @GetMapping
    @Operation(summary = "Get user's reaction status", description = "Retrieves the active reaction type of the user for this article, or null.")
    public ResponseEntity<ReactionType> getReactionType(@PathVariable UUID articleId) {
        return ResponseEntity.ok(reactionService.getReactionType(articleId));
    }
}
