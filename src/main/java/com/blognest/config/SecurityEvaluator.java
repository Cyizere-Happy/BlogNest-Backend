package com.blognest.config;

import com.blognest.models.enums.AssignmentType;
import com.blognest.repositories.*;
import com.blognest.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("securityEvaluator")
@RequiredArgsConstructor
public class SecurityEvaluator {

    private final AuthService authService;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;
    private final JudgeAssignmentRepository judgeAssignmentRepository;

    public boolean isSelf(UUID userId) {
        if (userId == null) return false;
        try {
            return userId.equals(authService.getCurrentUserId());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isArticleAuthor(UUID articleId) {
        if (articleId == null) return false;
        try {
            UUID currentUserId = authService.getCurrentUserId();
            return articleRepository.findById(articleId)
                    .map(article -> article.getAuthor().getId().equals(currentUserId))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCommentAuthor(UUID commentId) {
        if (commentId == null) return false;
        try {
            UUID currentUserId = authService.getCurrentUserId();
            return commentRepository.findById(commentId)
                    .map(comment -> comment.getUser() != null && comment.getUser().getId().equals(currentUserId))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isNotificationOwner(UUID notificationId) {
        if (notificationId == null) return false;
        try {
            UUID currentUserId = authService.getCurrentUserId();
            return notificationRepository.findById(notificationId)
                    .map(notification -> notification.getReceiver().getId().equals(currentUserId))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAssignedJudge(UUID assignmentId) {
        if (assignmentId == null) return false;
        try {
            UUID currentUserId = authService.getCurrentUserId();
            return judgeAssignmentRepository.findById(assignmentId)
                    .map(assignment -> assignment.getJudge().getId().equals(currentUserId))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasAssignment(UUID targetId, AssignmentType type) {
        if (targetId == null || type == null) return false;
        try {
            UUID currentUserId = authService.getCurrentUserId();
            return judgeAssignmentRepository.existsByJudge_IdAndTargetIdAndType(currentUserId, targetId, type);
        } catch (Exception e) {
            return false;
        }
    }
}
