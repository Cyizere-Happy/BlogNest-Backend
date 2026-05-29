package com.blognest.mappers;

import com.blognest.dtos.BookmarkResponse;
import com.blognest.models.Bookmark;

public class BookmarkMapper {

    public static BookmarkResponse toResponse(Bookmark bookmark) {
        return BookmarkResponse.builder()
                .id(bookmark.getId())
                .userId(bookmark.getUser() != null ? bookmark.getUser().getId() : null)
                .articleId(bookmark.getArticle() != null ? bookmark.getArticle().getId() : null)
                .articleTitle(bookmark.getArticle() != null ? bookmark.getArticle().getTitle() : null)
                .articleSlug(bookmark.getArticle() != null ? bookmark.getArticle().getSlug() : null)
                .authorName(
                        bookmark.getArticle() != null && bookmark.getArticle().getAuthor() != null
                                ? bookmark.getArticle().getAuthor().getFullName()
                                : null
                )
                .savedAt(bookmark.getSavedAt())
                .build();
    }
}
