package com.blognest.services.impl;

import com.blognest.dtos.ReactionStatusResponse;
import com.blognest.exceptions.ResourceNotFoundException;
import com.blognest.models.Article;
import com.blognest.models.ArticleReaction;
import com.blognest.models.User;
import com.blognest.models.enums.ReactionType;
import com.blognest.repositories.ArticleReactionRepository;
import com.blognest.repositories.ArticleRepository;
import com.blognest.repositories.UserRepository;
import com.blognest.services.AuthService;
import com.blognest.services.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

    private final ArticleReactionRepository articleReactionRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Override
    @Transactional
    public ReactionStatusResponse reactToArticle(UUID articleId, ReactionType type) {
        UUID userId = authService.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        boolean exists = articleReactionRepository.findByUserAndArticle(user, article).isPresent();

        articleReactionRepository.findByUserAndArticle(user, article)
                .ifPresentOrElse(
                        existingReaction -> {
                            existingReaction.setReactionType(type);
                            articleReactionRepository.save(existingReaction);
                        },
                        () -> {
                            ArticleReaction reaction = ArticleReaction.builder()
                                    .user(user)
                                    .article(article)
                                    .reactionType(type)
                                    .build();
                            articleReactionRepository.save(reaction);
                            articleRepository.incrementLikesCount(articleId);
                        }
                );

        int updatedLikesCount = article.getLikesCount() + (exists ? 0 : 1);

        return ReactionStatusResponse.builder()
                .success(true)
                .message("Reaction of type " + type + " registered successfully.")
                .articleId(articleId)
                .likesCount(updatedLikesCount)
                .reactionType(type)
                .build();
    }

    @Override
    @Transactional
    public ReactionStatusResponse removeReaction(UUID articleId) {
        UUID userId = authService.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        boolean existed = articleReactionRepository.findByUserAndArticle(user, article).isPresent();

        articleReactionRepository.findByUserAndArticle(user, article)
                .ifPresent(reaction -> {
                    articleReactionRepository.delete(reaction);
                    articleRepository.decrementLikesCount(articleId);
                });

        int updatedLikesCount = Math.max(0, article.getLikesCount() - (existed ? 1 : 0));

        return ReactionStatusResponse.builder()
                .success(true)
                .message("Reaction removed successfully.")
                .articleId(articleId)
                .likesCount(updatedLikesCount)
                .reactionType(null)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasReacted(UUID articleId) {
        UUID userId = authService.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        return articleReactionRepository.existsByUserAndArticle(user, article);
    }

    @Override
    @Transactional(readOnly = true)
    public ReactionType getReactionType(UUID articleId) {
        UUID userId = authService.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        return articleReactionRepository.findByUserAndArticle(user, article)
                .map(ArticleReaction::getReactionType)
                .orElse(null);
    }
}
