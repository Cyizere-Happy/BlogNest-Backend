package com.blognest.controllers;

import com.blognest.dtos.CreateUserRequest;
import com.blognest.dtos.LoginRequest;
import com.blognest.dtos.LoginResponse;
import com.blognest.dtos.UserResponse;
import com.blognest.dtos.RegistrationResponse;
import com.blognest.dtos.VerificationResponse;
import com.blognest.mappers.UserMapper;
import com.blognest.exceptions.ResourceNotFoundException;
import com.blognest.exceptions.UnauthorizedException;
import com.blognest.models.RefreshToken;
import com.blognest.models.User;
import com.blognest.repositories.RefreshTokenRepository;
import com.blognest.repositories.UserRepository;
import com.blognest.services.EmailService;
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
import org.springframework.beans.factory.annotation.Value;
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
    private final EmailService emailService;

    @Value("${rate-limit.login.max:5}")
    private int loginMaxAttempts;

    @Value("${rate-limit.login.seconds:60}")
    private long loginWindowSeconds;

    @Value("${rate-limit.register.max:3}")
    private int registerMaxAttempts;

    @Value("${rate-limit.register.seconds:3600}")
    private long registerWindowSeconds;

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Registers a new user account on the platform. Can optionally accept an invite token to register with a specific invited role (e.g. JUDGE).")
    public ResponseEntity<RegistrationResponse> register(
            @Valid @RequestBody CreateUserRequest request,
            @RequestParam(value = "token", required = false) String inviteToken,
            HttpServletRequest servletRequest) {
        String ip = getClientIp(servletRequest);
        if (!rateLimiterService.tryConsume("register:" + ip, registerMaxAttempts, registerWindowSeconds)) {
            throw new com.blognest.exceptions.TooManyRequestsException("Too many registration attempts. Please try again later.");
        }
        UserResponse userResponse = userService.createUser(request, inviteToken);
        
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

        // Generate email verification token
        String token = jwtService.generateVerificationToken(userResponse.getUsername());
        try {
            String verifyLink = "http://localhost:8080/api/auth/verify?token=" + token;
            emailService.sendSimpleEmail(
                    userResponse.getEmail(),
                    "Verify your BlogNest Email",
                    "Thank you for registering at BlogNest! Please click the link below to verify your email and activate your account:\n\n" + verifyLink
            );
        } catch (Exception e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
        }

        RegistrationResponse registrationResponse = RegistrationResponse.builder()
                .message("Registration successful! Please check your email to verify and activate your account.")
                .user(userResponse)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registrationResponse);
    }

    @GetMapping("/verify")
    @Operation(summary = "Verify email", description = "Verifies a user's email address and activates their account using a verification token.")
    public ResponseEntity<VerificationResponse> verifyEmail(@RequestParam String token) {
        try {
            if (jwtService.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(VerificationResponse.builder()
                                .success(false)
                                .message("Verification token has expired. Please register again.")
                                .build());
            }

            String username = jwtService.extractUsername(token);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (user.isVerified()) {
                return ResponseEntity.ok(VerificationResponse.builder()
                        .success(true)
                        .message("Account is already verified.")
                        .user(UserMapper.toResponse(user))
                        .build());
            }

            user.setActive(true);
            user.setVerified(true);
            User savedUser = userRepository.save(user);

            // Log auditing event
            eventPublisher.publishEvent(new com.blognest.dtos.AuditEvent(
                    user.getId(),
                    user.getRole().name(),
                    "USER_EMAIL_VERIFIED",
                    user.getId(),
                    "User",
                    "false",
                    "true",
                    "127.0.0.1",
                    true,
                    com.blognest.models.enums.SeverityLevel.INFO
            ));

            return ResponseEntity.ok(VerificationResponse.builder()
                    .success(true)
                    .message("Email verified successfully! Your account is now active and you can log in.")
                    .user(UserMapper.toResponse(savedUser))
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(VerificationResponse.builder()
                            .success(false)
                            .message("Invalid verification token: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user and returns access token in JSON body and refresh token in HttpOnly cookie.")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest, HttpServletResponse response) {
        String ip = getClientIp(servletRequest);
        if (!rateLimiterService.tryConsume("login:" + ip, loginMaxAttempts, loginWindowSeconds)) {
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
            throw new com.blognest.exceptions.TooManyRequestsException("Too many login attempts. Please try again later.");
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
