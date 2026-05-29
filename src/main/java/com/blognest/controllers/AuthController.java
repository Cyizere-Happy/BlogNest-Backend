package com.blognest.controllers;

import com.blognest.dtos.CreateUserRequest;
import com.blognest.dtos.LoginRequest;
import com.blognest.dtos.LoginResponse;
import com.blognest.dtos.UserResponse;
import com.blognest.exceptions.ResourceNotFoundException;
import com.blognest.exceptions.UnauthorizedException;
import com.blognest.models.RefreshToken;
import com.blognest.models.User;
import com.blognest.repositories.RefreshTokenRepository;
import com.blognest.repositories.UserRepository;
import com.blognest.services.JwtService;
import com.blognest.services.RateLimiterService;
import com.blognest.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, and token refresh")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RateLimiterService rateLimiterService;
    private final ApplicationEventPublisher eventPublisher;

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Registers a new user account on the platform.")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody CreateUserRequest request, HttpServletRequest servletRequest) {
        String ip = getClientIp(servletRequest);
        if (!rateLimiterService.tryConsume("register:" + ip, 3, 3600)) {
            throw new com.blognest.exceptions.TooManyRequestsException("Too many registration attempts. Please try again in an hour.");
        }
        UserResponse userResponse = userService.createUser(request);
        
        eventPublisher.publishEvent(new com.blognest.dtos.AuditEvent(
                userResponse.getId(),
                userResponse.getRole().name(),
                "USER_REGISTER",
                userResponse.getId(),
                "User",
                null,
                null,
                ip,
                true,
                com.blognest.models.enums.SeverityLevel.INFO
        ));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userResponse);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user and returns access token in JSON body and refresh token in HttpOnly cookie.")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest, HttpServletResponse response) {
        String ip = getClientIp(servletRequest);
        if (!rateLimiterService.tryConsume("login:" + ip, 5, 60)) {
            eventPublisher.publishEvent(new com.blognest.dtos.AuditEvent(
                    null,
                    "NONE",
                    "LOGIN_RATE_LIMIT",
                    null,
                    "User",
                    null,
                    "IP: " + ip + ", Username: " + request.getUsername(),
                    ip,
                    false,
                    com.blognest.models.enums.SeverityLevel.WARN
            ));
            throw new com.blognest.exceptions.TooManyRequestsException("Too many login attempts. Please try again in a minute.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (!user.isEnabled()) {
                eventPublisher.publishEvent(new com.blognest.dtos.AuditEvent(
                        user.getId(),
                        user.getRole().name(),
                        "LOGIN_FAILURE_SUSPENDED",
                        user.getId(),
                        "User",
                        null,
                        null,
                        ip,
                        false,
                        com.blognest.models.enums.SeverityLevel.WARN
                ));
                throw new UnauthorizedException("Account is suspended.");
            }

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            setRefreshTokenCookie(response, refreshToken);

            eventPublisher.publishEvent(new com.blognest.dtos.AuditEvent(
                    user.getId(),
                    user.getRole().name(),
                    "LOGIN_SUCCESS",
                    user.getId(),
                    "User",
                    null,
                    null,
                    ip,
                    true,
                    com.blognest.models.enums.SeverityLevel.INFO
            ));

            return ResponseEntity.ok(LoginResponse.builder()
                    .accessToken(accessToken)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .build());

        } catch (org.springframework.security.core.AuthenticationException e) {
            User user = userRepository.findByUsername(request.getUsername()).orElse(null);
            UUID actorId = user != null ? user.getId() : null;
            String actorRole = user != null ? user.getRole().name() : "NONE";

            eventPublisher.publishEvent(new com.blognest.dtos.AuditEvent(
                    actorId,
                    actorRole,
                    "LOGIN_FAILURE",
                    actorId,
                    "User",
                    null,
                    e.getMessage(),
                    ip,
                    false,
                    com.blognest.models.enums.SeverityLevel.WARN
            ));
            throw new UnauthorizedException("Invalid username or password.");
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Uses the refresh token in the HttpOnly cookie to generate a new short-lived access token.")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String token = extractRefreshTokenFromCookie(request);
        if (token == null) {
            throw new UnauthorizedException("Refresh token is missing.");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token."));

        if (refreshToken.isRevoked()) {
            throw new UnauthorizedException("Refresh token has been revoked.");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token has expired. Please login again.");
        }

        User user = refreshToken.getUser();
        if (!user.isEnabled()) {
            throw new UnauthorizedException("Account is suspended.");
        }

        // Generate new AccessToken
        String newAccessToken = jwtService.generateAccessToken(user);

        // Optional: Rotate the refresh token
        String newRefreshToken = jwtService.generateRefreshToken(user);
        setRefreshTokenCookie(response, newRefreshToken);

        return ResponseEntity.ok(LoginResponse.builder()
                .accessToken(newAccessToken)
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build());
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Clears user session and revokes the active refresh token.")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String ip = getClientIp(request);
        String token = extractRefreshTokenFromCookie(request);
        UUID userId = null;
        String role = "NONE";
        if (token != null) {
            var rtOpt = refreshTokenRepository.findByToken(token);
            if (rtOpt.isPresent()) {
                User user = rtOpt.get().getUser();
                userId = user.getId();
                role = user.getRole().name();
                refreshTokenRepository.delete(rtOpt.get());
            }
        }
        clearRefreshTokenCookie(response);

        if (userId != null) {
            eventPublisher.publishEvent(new com.blognest.dtos.AuditEvent(
                    userId,
                    role,
                    "LOGOUT",
                    userId,
                    "User",
                    null,
                    null,
                    ip,
                    true,
                    com.blognest.models.enums.SeverityLevel.INFO
            ));
        }

        return ResponseEntity.noContent().build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(false) // Set to true in prod (HTTPS)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 days
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
