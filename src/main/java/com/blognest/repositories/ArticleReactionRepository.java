package com.blognest.repositories;

import com.blognest.models.Article;
import com.blognest.models.ArticleReaction;
import com.blognest.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ArticleReactionRepository extends JpaRepository<ArticleReaction, UUID> {

    boolean existsByUserAndArticle(User user, Article article);

    Optional<ArticleReaction> findByUserAndArticle(User user, Article article);
}
