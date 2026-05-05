package com.grupo1.backGrupo1.security;

import com.grupo1.backGrupo1.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret:my-super-secret-key-my-super-secret-key-12345}")
    private String secret;

    @Value("${jwt.expiration-ms:86400000}")
    private long expirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    public String generateToken(User user) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .claim("userId", user.getId())
                .claim("role", user.getRole())
                .signWith(getSigningKey())
                .compact();
    }
    public String generateRefreshToken(User user) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(refreshExpirationMs)))
                .signWith(getSigningKey())
                .compact();
    }

    public io.jsonwebtoken.Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String extractUsername(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new RuntimeException("Token inválido");
            }
            String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("\"sub\"\\s*:\\s*\"([^\"]+)\"");
            java.util.regex.Matcher m = p.matcher(payloadJson);
            if (m.find()) return m.group(1);
            java.util.regex.Pattern p2 = java.util.regex.Pattern.compile("\"email\"\\s*:\\s*\"([^\"]+)\"");
            java.util.regex.Matcher m2 = p2.matcher(payloadJson);
            if (m2.find()) return m2.group(1);
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível extrair username do token", e);
        }
    }

}
