package com.blognest.repositories;

import com.blognest.models.Article;
import com.blognest.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleRepository extends JpaRepository<Article, UUID> {

    Optional<Article> findBySlug(String slug);

    List<Article> findByPublishedTrue();

    List<Article> findByAuthor(User author);

    List<Article> findByFeaturedTrue();
}