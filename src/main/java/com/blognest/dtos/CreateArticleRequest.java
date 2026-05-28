package com.blognest.dtos;

import com.blognest.models.enums.ArticleCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateArticleRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private String coverImage;

    private ArticleCategory category;

    private boolean published;
}