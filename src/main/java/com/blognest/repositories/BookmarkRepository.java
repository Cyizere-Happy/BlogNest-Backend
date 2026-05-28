package com.blognest.repositories;

import com.blognest.models.Article;
import com.blognest.models.Bookmark;
import com.blognest.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {

    List<Bookmark> findByUser(User user);

    Optional<Bookmark> findByUserAndArticle(User user, Article article);

    boolean existsByUserAndArticle(User user, Article article);
}