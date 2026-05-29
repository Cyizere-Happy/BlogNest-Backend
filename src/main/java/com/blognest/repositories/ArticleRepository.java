package com.blognest.repositories;

import com.blognest.models.Article;
import com.blognest.models.User;
import com.blognest.models.enums.ArticleCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ArticleRepository extends JpaRepository<Article, UUID> {

    Optional<Article> findBySlug(String slug);

    Page<Article> findByPublishedTrue(Pageable pageable);

    Page<Article> findByAuthor(User author, Pageable pageable);

    Page<Article> findByFeaturedTrue(Pageable pageable);

    Page<Article> findByCategory(ArticleCategory category, Pageable pageable);

    Page<Article> findByAuthorAndPublishedTrue(User author, Pageable pageable);

    Page<Article> findByTags_NameIgnoreCase(String tagName, Pageable pageable);

    @Modifying
    @Query("UPDATE Article a SET a.likesCount = a.likesCount + 1 WHERE a.id = :id")
    void incrementLikesCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Article a SET a.likesCount = a.likesCount - 1 WHERE a.id = :id AND a.likesCount > 0")
    void decrementLikesCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Article a SET a.commentsCount = a.commentsCount + 1 WHERE a.id = :id")
    void incrementCommentsCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Article a SET a.commentsCount = a.commentsCount - 1 WHERE a.id = :id AND a.commentsCount > 0")
    void decrementCommentsCount(@Param("id") UUID id);
}