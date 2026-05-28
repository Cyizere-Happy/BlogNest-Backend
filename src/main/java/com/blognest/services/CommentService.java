package com.blognest.services;

import com.blognest.dtos.CommentResponse;
import com.blognest.dtos.CreateCommentRequest;

import java.util.List;
import java.util.UUID;

public interface CommentService {

    CommentResponse createComment(UUID userId, CreateCommentRequest request);

    List<CommentResponse> getCommentsByArticle(UUID articleId);

    void deleteComment(UUID commentId, UUID userId);
}