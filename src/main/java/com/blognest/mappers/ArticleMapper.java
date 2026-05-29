package com.blognest.mappers;

import com.blognest.dtos.ArticleResponse;
import com.blognest.dtos.CreateArticleRequest;
import com.blognest.models.Article;
import com.blognest.models.User;

import java.util.UUID;

public class ArticleMapper {

    public static Article toEntity(CreateArticleRequest request, User author) {

        return Article.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .coverImage(request.getCoverImage())
                .category(request.getCategory())
                .published(request.isPublished())
                .author(author)
                .views(0)
                .likesCount(0)
                .commentsCount(0)
                .readingTime(calculateReadingTime(request.getContent()))
                .build();
    }

    public static ArticleResponse toResponse(Article article) {

        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .slug(article.getSlug())
                .content(article.getContent())
                .coverImage(article.getCoverImage())
                .category(article.getCategory())
                .published(article.isPublished())
                .views(article.getViews())
                .likesCount(article.getLikesCount())
                .commentsCount(article.getCommentsCount())
                .readingTime(article.getReadingTime())
                .authorId(article.getAuthor().getId())
                .authorName(article.getAuthor().getFullName())
                .tags(article.getTags() != null ?
                        article.getTags().stream()
                                .map(com.blognest.models.Tag::getName)
                                .collect(java.util.stream.Collectors.toSet())
                        : java.util.Collections.emptySet())
                .createdAt(article.getCreatedAt())
                .build();
    }

    private static int calculateReadingTime(String content) {

        if (content == null) return 0;

        int words = content.split("\\s+").length;

        return Math.max(1, words / 200);
    }
}