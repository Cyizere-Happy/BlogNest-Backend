package com.blognest.services;

import com.blognest.dtos.BookmarkResponse;

import java.util.List;
import java.util.UUID;

public interface BookmarkService {

    BookmarkResponse addBookmark(UUID articleId);

    void removeBookmark(UUID articleId);

    List<BookmarkResponse> getUserBookmarks();

    boolean isBookmarked(UUID articleId);
}
