package com.blognest.mappers;

import com.blognest.dtos.CommentResponse;
import com.blognest.models.Comment;

public class CommentMapper {

    public static CommentResponse toResponse(Comment comment) {

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .articleId(comment.getArticle().getId())
                .articleTitle(comment.getArticle().getTitle())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}