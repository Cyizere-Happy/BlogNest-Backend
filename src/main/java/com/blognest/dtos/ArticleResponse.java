package com.blognest.dtos;

import com.blognest.models.enums.ArticleCategory;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleResponse {

    private UUID id;

    private String title;

    private String slug;

    private String content;

    private String coverImage;

    private ArticleCategory category;

    private boolean published;

    private int views;

    private int likesCount;

    private int commentsCount;

    private int readingTime;

    private UUID authorId;

    private String authorName;

    private LocalDateTime createdAt;
}