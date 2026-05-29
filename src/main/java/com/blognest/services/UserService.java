package com.blognest.services;

import com.blognest.dtos.CreateUserRequest;
import com.blognest.dtos.UpdateUserRequest;
import com.blognest.dtos.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse createUser(CreateUserRequest request, String inviteToken);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(UUID id);

    UserResponse updateUser(UUID id, UpdateUserRequest request);

    void deleteUser(UUID id);

    UserResponse updateRole(UUID id, com.blognest.models.enums.Role role);

    UserResponse toggleSuspension(UUID id, boolean suspended);
}