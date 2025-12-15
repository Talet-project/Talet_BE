package com.talet.talet.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JWTTokenUtil {
    private final long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7;   // 7일
    private final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 30; // 30일
    private final long SIGN_UP_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24; // 1일
    private final long ADMIN_TOKEN_EXPIRATION = 1000L * 60 * 60; // 1시간
    private final String TOKEN_PREFIX = "Bearer ";
    private String key;
    private final SecretKey secretKey;

    public JWTTokenUtil() {
        key = "L9v2mKqp3E8hYc4SxN7WgRjD5zUqVt0pBmAeCrXsYzTnMkJfQwLbEiHgZdRuTsAv";
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(String identifier) {
        return generateToken(identifier, RedisTokenType.ACCESS_TOKEN);
    }

    public String createRefreshToken(String identifier) {
        return generateToken(identifier, RedisTokenType.REFRESH_TOKEN);
    }

    public String createSignUpToken(String identifier) {
        return generateToken(identifier, RedisTokenType.SIGN_UP_TOKEN);
    }

    public String createAdminToken() {
        RedisTokenType admin = RedisTokenType.ADMIN_TOKEN;
        Instant now = Instant.now();
        Instant expire = now.plus(admin.getDuration());
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenType", admin.getTokenTypeClaim());       // ★ 필수 (필터에서 구분)
        claims.put("identifier", "admin");         // "admin" 또는 랜덤 UUID 등
        claims.put("roles", List.of("ROLE_ADMIN"));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject("admin")                  // subject는 고정값/식별자 아무거나 OK
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expire))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateToken(String identifier, RedisTokenType type) {
        Instant now = Instant.now();
        Instant expiry = now.plus(type.getDuration());
        return Jwts.builder()
                .setSubject(identifier)
                .claim("tokenType", type.getTokenTypeClaim()) // 👈 타입 클레임 추가
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getPureToken(String token) {
        return token.replace(TOKEN_PREFIX, "");
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getIdentifierFromToken(String token) {
        return getAllClaimsFromToken(getPureToken(token)).getSubject();
    }

    public String getTokenType(String token) {
        return getAllClaimsFromToken(getPureToken(token)).get("tokenType", String.class);
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
