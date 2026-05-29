package com.blognest.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkResponse {

    private UUID id;

    private UUID userId;

    private UUID articleId;

    private String articleTitle;

    private String articleSlug;

    private String authorName;

    private LocalDateTime savedAt;
}
