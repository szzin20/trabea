package com.exacc.trabea.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.security.SecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private final SecretKey key;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms:3600000}") long expirationMs) {
        if (expirationMs <= 0) throw new IllegalStateException("app.jwt.expiration-ms harus positif");
        byte[] keyBytes;
        if (secret == null || secret.isBlank()) {
            keyBytes = new byte[32];
            new SecureRandom().nextBytes(keyBytes);
            log.warn("APP_JWT_SECRET tidak diset; memakai secret sementara. Atur APP_JWT_SECRET agar token tetap valid setelah restart.");
        } else {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            if (keyBytes.length < 32) {
                throw new IllegalStateException("APP_JWT_SECRET harus berisi minimal 32 byte");
            }
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    public String generateToken(String email, String activeRole) {
        Instant now = Instant.now();
        return Jwts.builder().subject(email).claim("activeRole", RoleAuthority.canonical(activeRole))
                .issuedAt(Date.from(now)).expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key).compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public long getExpirationMs() { return expirationMs; }
}
