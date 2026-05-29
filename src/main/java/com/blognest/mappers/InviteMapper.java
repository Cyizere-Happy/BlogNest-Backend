package com.blognest.mappers;

import com.blognest.dtos.InviteResponse;
import com.blognest.models.Invite;

public class InviteMapper {

    public static InviteResponse toResponse(Invite invite) {
        return InviteResponse.builder()
                .id(invite.getId())
                .email(invite.getEmail())
                .token(invite.getToken())
                .role(invite.getRole())
                .used(invite.isUsed())
                .sentByName(invite.getSentBy() != null ? invite.getSentBy().getFullName() : null)
                .expiresAt(invite.getExpiresAt())
                .createdAt(invite.getCreatedAt())
                .build();
    }
}
