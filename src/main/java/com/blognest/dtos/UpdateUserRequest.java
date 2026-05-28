package com.blognest.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    private String fullName;

    private String bio;

    private String profileImage;
}