package com.blognest.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private UUID id;

    private String content;

    private UUID articleId;

    private String articleTitle;

    private UUID userId;

    private String username;

    private LocalDateTime createdAt;
}