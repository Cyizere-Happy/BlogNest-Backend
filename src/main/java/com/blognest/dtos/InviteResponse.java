package com.blognest.dtos;

import com.blognest.models.enums.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InviteResponse {

    private UUID id;

    private String email;

    private String token;

    private Role role;

    private boolean used;

    private String sentByName;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;
}
