package com.blognest.dtos;

import com.blognest.models.enums.ReactionType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionStatusResponse {
    private boolean success;
    private String message;
    private UUID articleId;
    private int likesCount;
    private ReactionType reactionType;
}
