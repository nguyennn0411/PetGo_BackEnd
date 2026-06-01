package com.example.petgo.config;

import com.example.petgo.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtTokenService {

    private final SecretKey secretKey;
    private final String issuer;
    private final long accessTokenExpirationMinutes;
    private final long refreshTokenExpirationDays;

    public JwtTokenService(
            @Value("${app.auth.jwt-secret}") String jwtSecret,
            @Value("${app.auth.issuer:petgo-backend}") String issuer,
            @Value("${app.auth.access-token-expiration-minutes:120}") long accessTokenExpirationMinutes,
            @Value("${app.auth.refresh-token-expiration-days:30}") long refreshTokenExpirationDays
    ) {
        this.secretKey = Keys.hmacShaKeyFor(normalizeSecret(jwtSecret));
        this.issuer = issuer;
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        this.refreshTokenExpirationDays = refreshTokenExpirationDays;
    }

    public String generateAccessToken(User user, List<String> roles) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(accessTokenExpirationMinutes * 60);

        return Jwts.builder()
                .issuer(issuer)
                .subject(String.valueOf(user.getId()))
                .claims(Map.of(
                        "email", user.getEmail(),
                        "roles", roles,
                        "type", "ACCESS"
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(User user, List<String> roles) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(refreshTokenExpirationDays * 24 * 60 * 60);

        return Jwts.builder()
                .issuer(issuer)
                .subject(String.valueOf(user.getId()))
                .claims(Map.of(
                        "email", user.getEmail(),
                        "roles", roles,
                        "type", "REFRESH",
                        "jti", java.util.UUID.randomUUID().toString() // 🔥 THÊM DÒNG NÀY
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    public AuthenticatedUser parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.getOrDefault("roles", List.of());

        return new AuthenticatedUser(
                Long.valueOf(claims.getSubject()),
                claims.get("email", String.class),
                roles,
                claims.get("type", String.class)
        );
    }

    public LocalDateTime getRefreshTokenExpiry(String refreshToken) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();
        return LocalDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneId.systemDefault());
    }

    private byte[] normalizeSecret(String jwtSecret) {
        byte[] raw = jwtSecret.getBytes();
        if (raw.length >= 32) {
            return raw;
        }
        String padded = jwtSecret + "0".repeat(32 - raw.length);
        return padded.getBytes();
    }
}
