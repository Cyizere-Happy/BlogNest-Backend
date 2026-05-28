package com.blognest.repositories;

import com.blognest.models.Article;
import com.blognest.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findByArticle(Article article);
}