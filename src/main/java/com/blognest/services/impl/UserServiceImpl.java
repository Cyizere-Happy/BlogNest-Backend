package com.blognest.services.impl;

import com.blognest.exceptions.DuplicateResourceException;
import com.blognest.exceptions.ResourceNotFoundException;
import com.blognest.exceptions.UnauthorizedException;
import com.blognest.dtos.CreateUserRequest;
import com.blognest.dtos.UpdateUserRequest;
import com.blognest.dtos.UserResponse;
import com.blognest.models.User;
import com.blognest.mappers.UserMapper;
import com.blognest.repositories.UserRepository;
import com.blognest.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.blognest.services.AuthService authService;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Override
    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        User user = UserMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

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
    @Transactional
    public void deleteUser(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        UUID actorId = authService.getCurrentUserId();
        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found"));

        userRepository.delete(user);

        eventPublisher.publishEvent(new com.blognest.dtos.AuditEvent(
                actorId,
                actor.getRole().name(),
                "USER_DELETE",
                id,
                "User",
                null,
                null,
                getClientIp(),
                true,
                com.blognest.models.enums.SeverityLevel.WARN
        ));
    }

    @Override
    @Transactional
    public UserResponse updateRole(UUID id, com.blognest.models.enums.Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UUID actorId = authService.getCurrentUserId();
        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found"));

        if (actor.getRole() != com.blognest.models.enums.Role.SUPERADMIN) {
            throw new UnauthorizedException("Only SUPERADMIN can change user roles.");
        }

        // Prevent self-role escalation
        if (actorId.equals(id)) {
            throw new IllegalArgumentException("Cannot change your own role.");
        }

        // Prevent managing SUPERADMIN role
        if (user.getRole() == com.blognest.models.enums.Role.SUPERADMIN && !actorId.equals(id)) {
            throw new UnauthorizedException("Cannot change SUPERADMIN roles.");
        }

        String oldRole = user.getRole().name();
        user.setRole(role);
        User updated = userRepository.save(user);

        eventPublisher.publishEvent(new com.blognest.dtos.AuditEvent(
                actorId,
                actor.getRole().name(),
                "ROLE_CHANGE",
                id,
                "User",
                oldRole,
                role.name(),
                getClientIp(),
                true,
                com.blognest.models.enums.SeverityLevel.INFO
        ));

        return UserMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public UserResponse toggleSuspension(UUID id, boolean suspended) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UUID actorId = authService.getCurrentUserId();
        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found"));

        if (actor.getRole() != com.blognest.models.enums.Role.SUPERADMIN) {
            throw new UnauthorizedException("Only SUPERADMIN can manage user suspension.");
        }

        if (actorId.equals(id)) {
            throw new IllegalArgumentException("Cannot suspend your own account.");
        }

        if (user.getRole() == com.blognest.models.enums.Role.SUPERADMIN) {
            throw new UnauthorizedException("Cannot suspend a SUPERADMIN account.");
        }

        boolean oldValue = user.isActive();
        boolean newValue = !suspended; // suspended=true means active=false
        user.setActive(newValue);
        User updated = userRepository.save(user);

        eventPublisher.publishEvent(new com.blognest.dtos.AuditEvent(
                actorId,
                actor.getRole().name(),
                "USER_SUSPEND",
                id,
                "User",
                String.valueOf(!oldValue), // suspended state: true if active is false
                String.valueOf(suspended),
                getClientIp(),
                true,
                com.blognest.models.enums.SeverityLevel.WARN
        ));

        return UserMapper.toResponse(updated);
    }

    private String getClientIp() {
        try {
            org.springframework.web.context.request.ServletRequestAttributes attrs =
                    (org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                jakarta.servlet.http.HttpServletRequest request = attrs.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            // fallback
        }
        return "127.0.0.1";
    }
}