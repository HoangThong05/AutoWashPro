package com.autowashpro.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Secret key — dài ít nhất 32 ký tự
    private static final String SECRET = "autowashpro-secret-key-swp391-2026";
    private static final long EXPIRATION_MS = 15 * 60 * 1000; // 15 phút

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // Tạo Access Token
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getKey())
                .compact();
    }

    // Lấy email từ token
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // Lấy role từ token
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // Kiểm tra token còn hợp lệ không
    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}