package com.blognest.services.impl;

import com.blognest.exceptions.DuplicateResourceException;
import com.blognest.exceptions.ResourceNotFoundException;
import com.blognest.dtos.CreateUserRequest;
import com.blognest.dtos.UpdateUserRequest;
import com.blognest.dtos.UserResponse;
import com.blognest.models.User;
import com.blognest.mappers.UserMapper;
import com.blognest.repositories.UserRepository;
import com.blognest.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        User user = UserMapper.toEntity(request);

        User savedUser = userRepository.save(user);

        return UserMapper.toResponse(savedUser);
    }

    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }

        User updatedUser = userRepository.save(user);

        return UserMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        userRepository.delete(user);
    }
}