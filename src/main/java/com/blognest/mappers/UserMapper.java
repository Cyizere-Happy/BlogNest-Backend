package com.blognest.mappers;

import com.blognest.dtos.CreateUserRequest;
import com.blognest.dtos.UserResponse;
import com.blognest.models.User;
import com.blognest.models.enums.Role;

public class UserMapper {

    public static User toEntity(CreateUserRequest request) {

        return User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .bio(request.getBio())
                .profileImage(request.getProfileImage())
                .role(Role.USER)
                .build();
    }

    public static UserResponse toResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImage(user.getProfileImage())
                .verified(user.isVerified())
                .active(user.isActive())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}