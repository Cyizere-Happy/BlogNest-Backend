package com.blognest.services;

import com.blognest.dtos.BookmarkResponse;

import java.util.List;
import java.util.UUID;

public interface BookmarkService {

    BookmarkResponse addBookmark(UUID userId, UUID articleId);

    void removeBookmark(UUID userId, UUID articleId);

    List<BookmarkResponse> getUserBookmarks(UUID userId);

    boolean isBookmarked(UUID userId, UUID articleId);
}
