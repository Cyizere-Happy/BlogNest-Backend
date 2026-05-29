package com.blognest.services.impl;

import com.blognest.dtos.CommentResponse;
import com.blognest.dtos.CreateCommentRequest;
import com.blognest.exceptions.ResourceNotFoundException;
import com.blognest.exceptions.UnauthorizedException;
import com.blognest.mappers.CommentMapper;
import com.blognest.models.Article;
import com.blognest.models.Comment;
import com.blognest.models.User;
import com.blognest.repositories.ArticleRepository;
import com.blognest.repositories.CommentRepository;
import com.blognest.repositories.UserRepository;
import com.blognest.services.AuthService;
import com.blognest.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final int MAX_COMMENT_DEPTH = 3;

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final AuthService authService;

    @Override
    @Transactional
    public CommentResponse createComment(CreateCommentRequest request) {
        UUID userId = authService.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

            if (!parent.getArticle().getId().equals(article.getId())) {
                throw new IllegalArgumentException("Parent comment must belong to the same article.");
            }

            // Depth calculation
            int depth = 1;
            Comment current = parent;
            while (current.getParent() != null) {
                depth++;
                current = current.getParent();
            }

            if (depth >= MAX_COMMENT_DEPTH) {
                throw new IllegalArgumentException("Maximum comment reply depth of " + MAX_COMMENT_DEPTH + " reached.");
            }
        }

        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .article(article)
                .parent(parent)
                .build();

        Comment saved = commentRepository.save(comment);

        // Atomic commentsCount increment
        articleRepository.incrementCommentsCount(article.getId());

        return CommentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByArticle(UUID articleId, Pageable pageable) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        return commentRepository.findByArticleAndParentIsNull(article, pageable)
                .map(CommentMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteComment(UUID commentId) {
        UUID userId = authService.getCurrentUserId();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        // Validation: only author of the comment can delete it
        if (comment.getUser() == null || !comment.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this comment.");
        }

        // Soft delete if the comment has replies to preserve the tree structure
        if (!comment.getReplies().isEmpty()) {
            comment.setDeleted(true);
            comment.setContent(null);
            comment.setUser(null);
            commentRepository.save(comment);
        } else {
            // Physical delete
            commentRepository.delete(comment);
            articleRepository.decrementCommentsCount(comment.getArticle().getId());
        }
    }
}