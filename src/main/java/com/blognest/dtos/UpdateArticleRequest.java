package com.blognest.dtos;

import com.blognest.models.enums.ArticleCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateArticleRequest {

    private String title;

    private String content;

    private String coverImage;

    private ArticleCategory category;

    private Boolean published;
}