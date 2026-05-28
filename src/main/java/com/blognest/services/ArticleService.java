package com.blognest.services;

import com.blognest.dtos.ArticleResponse;
import com.blognest.dtos.CreateArticleRequest;
import com.blognest.dtos.UpdateArticleRequest;

import java.util.List;
import java.util.UUID;

public interface ArticleService {

    ArticleResponse createArticle(UUID authorId, CreateArticleRequest request);

    List<ArticleResponse> getAllArticles();

    List<ArticleResponse> getPublishedArticles();

    ArticleResponse getArticleById(UUID id);

    ArticleResponse getArticleBySlug(String slug);

    ArticleResponse updateArticle(UUID id, UpdateArticleRequest request);

    void deleteArticle(UUID id);
}