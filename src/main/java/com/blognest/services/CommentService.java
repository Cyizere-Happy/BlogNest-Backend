package com.blognest.services;

import com.blognest.dtos.CommentResponse;
import com.blognest.dtos.CreateCommentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CommentService {

    CommentResponse createComment(CreateCommentRequest request);

    Page<CommentResponse> getCommentsByArticle(UUID articleId, Pageable pageable);

    void deleteComment(UUID commentId);
}