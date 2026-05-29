package com.blognest.services.impl;

import com.blognest.models.RefreshToken;
import com.blognest.models.User;
import com.blognest.repositories.RefreshTokenRepository;
import com.blognest.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret:9a67475f4625b59747a83d73b06e8b4e7235a64353f8689ff30ef75fef3e21544a49646b6a6d6f51465243545657595b5d5f61636567696b6d6f71737577797b}")
    private String secret;

    @Value("${jwt.expiration.ms:900000}") // Default 15 minutes (900,000 ms)
    private long expirationMs;

    @Value("${jwt.refresh.expiration.days:7}") // Default 7 days
    private long refreshExpirationDays;

    private final RefreshTokenRepository refreshTokenRepository;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("role", user.getRole().name());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    @Override
    public String generateVerificationToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24 hours
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    @Override
    @Transactional
    public String generateRefreshToken(User user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId())
                .orElseGet(() -> RefreshToken.builder().user(user).build());

        refreshToken.setToken(token);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(refreshExpirationDays));
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    @Override
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    @Override
    public UUID extractUserId(String token) {
        String userIdStr = extractClaims(token).get("userId", String.class);
        return UUID.fromString(userIdStr);
    }

    @Override
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    @Override
    public boolean validateToken(String token, String expectedUsername) {
        final String username = extractUsername(token);
        return (username.equals(expectedUsername) && !isTokenExpired(token));
    }
}
