package com.blognest.services;

import com.blognest.models.User;
import io.jsonwebtoken.Claims;

import java.util.UUID;

public interface JwtService {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    Claims extractClaims(String token);

    String extractUsername(String token);

    UUID extractUserId(String token);

    boolean isTokenExpired(String token);

    boolean validateToken(String token, String expectedUsername);

    String generateVerificationToken(String username);
}
