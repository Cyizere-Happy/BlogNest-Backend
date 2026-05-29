package com.blognest.controllers;

import com.blognest.dtos.ArticleResponse;
import com.blognest.dtos.CreateArticleRequest;
import com.blognest.dtos.UpdateArticleRequest;
import com.blognest.models.enums.ArticleCategory;
import com.blognest.services.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@Tag(name = "Articles", description = "Article creation and browsing")
public class ArticleController {

    private final ArticleService articleService;

    // POST /api/articles?authorId={uuid}
    @PostMapping
    @Operation(summary = "Create article", description = "Creates a new article post authored by the specified user ID.")
    public ResponseEntity<ArticleResponse> createArticle(
            @RequestParam UUID authorId,
            @RequestBody CreateArticleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(articleService.createArticle(authorId, request));
    }

    // GET /api/articles
    @GetMapping
    @Operation(summary = "Get all articles (paginated)", description = "Retrieves all articles including drafts and published ones in a paginated list.")
    public ResponseEntity<Page<ArticleResponse>> getAllArticles(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        return ResponseEntity.ok(articleService.getAllArticles(pageable));
    }

    // GET /api/articles/published
    @GetMapping("/published")
    @Operation(summary = "Get published articles (paginated)", description = "Retrieves a paginated list of all publicly published articles.")
    public ResponseEntity<Page<ArticleResponse>> getPublishedArticles(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        return ResponseEntity.ok(articleService.getPublishedArticles(pageable));
    }

    // GET /api/articles/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Get article by ID", description = "Retrieves details of a specific article by its ID.")
    public ResponseEntity<ArticleResponse> getArticleById(@PathVariable UUID id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    // GET /api/articles/slug/{slug}
    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get article by slug", description = "Retrieves details of a specific article by its URL-friendly slug.")
    public ResponseEntity<ArticleResponse> getArticleBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(articleService.getArticleBySlug(slug));
    }

    // GET /api/articles/author/{authorId}
    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get articles by author ID (paginated)", description = "Retrieves all articles written by a specific author in a paginated list.")
    public ResponseEntity<Page<ArticleResponse>> getArticlesByAuthor(
            @PathVariable UUID authorId,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        return ResponseEntity.ok(articleService.getArticlesByAuthor(authorId, pageable));
    }

    // GET /api/articles/category/{category}
    @GetMapping("/category/{category}")
    @Operation(summary = "Get articles by category (paginated)", description = "Filters and retrieves articles by category in a paginated list.")
    public ResponseEntity<Page<ArticleResponse>> getArticlesByCategory(
            @PathVariable ArticleCategory category,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        return ResponseEntity.ok(articleService.getArticlesByCategory(category, pageable));
    }

    // GET /api/articles/tag/{tagName}
    @GetMapping("/tag/{tagName}")
    @Operation(summary = "Get articles by tag (paginated)", description = "Filters and retrieves articles by tag name in a paginated list.")
    public ResponseEntity<Page<ArticleResponse>> getArticlesByTag(
            @PathVariable String tagName,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        return ResponseEntity.ok(articleService.getArticlesByTag(tagName, pageable));
    }

    // PUT /api/articles/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Update article", description = "Updates details (title, content, category, tags, status) of an existing article.")
    public ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable UUID id,
            @RequestBody UpdateArticleRequest request) {
        return ResponseEntity.ok(articleService.updateArticle(id, request));
    }

    // DELETE /api/articles/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete article", description = "Deletes an article from the system.")
    public ResponseEntity<com.blognest.dtos.ApiResponse> deleteArticle(@PathVariable UUID id) {
        articleService.deleteArticle(id);
        return ResponseEntity.ok(com.blognest.dtos.ApiResponse.builder()
                .success(true)
                .message("Article deleted successfully.")
                .build());
    }
}
