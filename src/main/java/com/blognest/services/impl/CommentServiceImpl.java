package com.blognest.services.impl;

import com.blognest.dtos.CommentResponse;
import com.blognest.dtos.CreateCommentRequest;
import com.blognest.exceptions.ResourceNotFoundException;
import com.blognest.mappers.CommentMapper;
import com.blognest.models.Article;
import com.blognest.models.Comment;
import com.blognest.models.User;
import com.blognest.repositories.ArticleRepository;
import com.blognest.repositories.CommentRepository;
import com.blognest.repositories.UserRepository;
import com.blognest.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    @Override
    public CommentResponse createComment(UUID userId, CreateCommentRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .article(article)
                .build();

        Comment saved = commentRepository.save(comment);

        return CommentMapper.toResponse(saved);
    }

    @Override
    public List<CommentResponse> getCommentsByArticle(UUID articleId) {

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        return commentRepository.findByArticle(article)
                .stream()
                .map(CommentMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteComment(UUID commentId, UUID userId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("You cannot delete this comment");
        }

        commentRepository.delete(comment);
    }
}