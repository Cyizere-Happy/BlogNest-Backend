package com.blognest.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationResponse {
    private boolean success;
    private String message;
    private UserResponse user;
}
