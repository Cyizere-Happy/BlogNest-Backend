package com.blognest.services;

import com.blognest.dtos.ReactionStatusResponse;
import com.blognest.models.enums.ReactionType;
import java.util.UUID;

public interface ReactionService {
    ReactionStatusResponse reactToArticle(UUID articleId, ReactionType type);
    ReactionStatusResponse removeReaction(UUID articleId);
    boolean hasReacted(UUID articleId);
    ReactionType getReactionType(UUID articleId);
}
