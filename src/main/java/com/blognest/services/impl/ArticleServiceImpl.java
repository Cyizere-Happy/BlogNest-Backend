package com.blognest.services.impl;

import com.blognest.dtos.ArticleResponse;
import com.blognest.dtos.CreateArticleRequest;
import com.blognest.dtos.UpdateArticleRequest;
import com.blognest.exceptions.ResourceNotFoundException;
import com.blognest.mappers.ArticleMapper;
import com.blognest.models.*;
import com.blognest.models.enums.NotificationType;
import com.blognest.repositories.*;
import com.blognest.services.EmailService;
import com.blognest.services.NotificationService;
import com.blognest.services.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Override
    public ArticleResponse createArticle(UUID authorId, CreateArticleRequest request) {

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

        Article article = ArticleMapper.toEntity(request, author);
        article.setSlug(generateSlug(request.getTitle()));

        Article saved = articleRepository.save(article);

        if (saved.isPublished()) {
            publishArticleEvent(saved);
        }

        return ArticleMapper.toResponse(saved);
    }

    @Override
    public List<ArticleResponse> getAllArticles() {
        return articleRepository.findAll()
                .stream()
                .map(ArticleMapper::toResponse)
                .toList();
    }

    @Override
    public List<ArticleResponse> getPublishedArticles() {
        return articleRepository.findByPublishedTrue()
                .stream()
                .map(ArticleMapper::toResponse)
                .toList();
    }

    @Override
    public ArticleResponse getArticleById(UUID id) {

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        return ArticleMapper.toResponse(article);
    }

    @Override
    public ArticleResponse getArticleBySlug(String slug) {

        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        return ArticleMapper.toResponse(article);
    }

    @Override
    public ArticleResponse updateArticle(UUID id, UpdateArticleRequest request) {

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        if (request.getTitle() != null) {
            article.setTitle(request.getTitle());
            article.setSlug(generateSlug(request.getTitle()));
        }

        if (request.getContent() != null) {
            article.setContent(request.getContent());
        }

        if (request.getCoverImage() != null) {
            article.setCoverImage(request.getCoverImage());
        }

        if (request.getCategory() != null) {
            article.setCategory(request.getCategory());
        }

        if (request.getPublished() != null) {
            article.setPublished(request.getPublished());
        }

        Article updated = articleRepository.save(article);

        if (updated.isPublished()) {
            publishArticleEvent(updated);
        }

        return ArticleMapper.toResponse(updated);
    }

    @Override
    public void deleteArticle(UUID id) {

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        articleRepository.delete(article);
    }

    private void publishArticleEvent(Article article) {

        User writer = article.getAuthor();

        List<Subscription> subscribers =
                subscriptionRepository.findByWriter(writer);

        for (Subscription sub : subscribers) {

            User user = sub.getSubscriber();

            // 1. NOTIFICATION
            notificationService.createNotification(
                    user.getId(),
                    "New Article Published",
                    writer.getFullName() + " published: " + article.getTitle(),
                    NotificationType.ARTICLE
            );

            // 2. EMAIL (ASYNC)
            try {
                Context context = new Context();
                context.setVariable("username", user.getFullName());
                context.setVariable("title", article.getTitle());
                context.setVariable("url", "https://blognest.com/articles/" + article.getSlug());

                emailService.sendTemplateEmail(
                        user.getEmail(),
                        "New Article: " + article.getTitle(),
                        "email/article-published",
                        context
                );

            } catch (Exception e) {
                System.out.println("Email failed: " + e.getMessage());
            }
        }
    }

    private String generateSlug(String title) {
        return title.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
    }
}