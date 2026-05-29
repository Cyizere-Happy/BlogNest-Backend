package com.blognest.services.impl;

import com.blognest.dtos.BookmarkResponse;
import com.blognest.exceptions.DuplicateResourceException;
import com.blognest.exceptions.ResourceNotFoundException;
import com.blognest.mappers.BookmarkMapper;
import com.blognest.models.Article;
import com.blognest.models.Bookmark;
import com.blognest.models.User;
import com.blognest.repositories.ArticleRepository;
import com.blognest.repositories.BookmarkRepository;
import com.blognest.repositories.UserRepository;
import com.blognest.services.AuthService;
import com.blognest.services.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final AuthService authService;

    @Override
    public BookmarkResponse addBookmark(UUID articleId) {
        UUID userId = authService.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        if (bookmarkRepository.existsByUserAndArticle(user, article)) {
            throw new DuplicateResourceException("Article is already bookmarked");
        }

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .article(article)
                .savedAt(LocalDateTime.now())
                .build();

        return BookmarkMapper.toResponse(bookmarkRepository.save(bookmark));
    }

    @Override
    public void removeBookmark(UUID articleId) {
        UUID userId = authService.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        Bookmark bookmark = bookmarkRepository.findByUserAndArticle(user, article)
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found"));

        bookmarkRepository.delete(bookmark);
    }

    @Override
    public List<BookmarkResponse> getUserBookmarks() {
        UUID userId = authService.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return bookmarkRepository.findByUser(user)
                .stream()
                .map(BookmarkMapper::toResponse)
                .toList();
    }

    @Override
    public boolean isBookmarked(UUID articleId) {
        UUID userId = authService.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        return bookmarkRepository.existsByUserAndArticle(user, article);
    }
}
