package com.blognest.services;

import com.blognest.dtos.ArticleResponse;
import com.blognest.dtos.CreateArticleRequest;
import com.blognest.dtos.UpdateArticleRequest;
import com.blognest.models.enums.ArticleCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ArticleService {

    ArticleResponse createArticle(CreateArticleRequest request);

    Page<ArticleResponse> getAllArticles(Pageable pageable);

    Page<ArticleResponse> getPublishedArticles(Pageable pageable);

    ArticleResponse getArticleById(UUID id);

    ArticleResponse getArticleBySlug(String slug);

    ArticleResponse updateArticle(UUID id, UpdateArticleRequest request);

    void deleteArticle(UUID id);

    Page<ArticleResponse> getArticlesByAuthor(UUID authorId, Pageable pageable);

    Page<ArticleResponse> getArticlesByCategory(ArticleCategory category, Pageable pageable);

    Page<ArticleResponse> getArticlesByTag(String tagName, Pageable pageable);
}