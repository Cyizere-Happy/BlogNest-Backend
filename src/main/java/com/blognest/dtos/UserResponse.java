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
public class UserResponse {

    private UUID id;

    private String fullName;

    private String username;

    private String email;

    private String bio;

    private String profileImage;

    private boolean verified;

    private boolean active;

    private Role role;

    private LocalDateTime createdAt;
}