package com.blognest.repositories;

import com.blognest.models.Article;
import com.blognest.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findByArticle(Article article);

    Page<Comment> findByArticleAndParentIsNull(Article article, Pageable pageable);
}