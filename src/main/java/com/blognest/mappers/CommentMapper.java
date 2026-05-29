package com.blognest.mappers;

import com.blognest.dtos.CommentResponse;
import com.blognest.models.Comment;

public class CommentMapper {

    public static CommentResponse toResponse(Comment comment) {

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.isDeleted() ? "[This comment has been deleted]" : comment.getContent())
                .articleId(comment.getArticle().getId())
                .articleTitle(comment.getArticle().getTitle())
                .userId(comment.getUser() != null ? comment.getUser().getId() : null)
                .username(comment.getUser() != null ? comment.getUser().getUsername() : "[deleted]")
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .deleted(comment.isDeleted())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}