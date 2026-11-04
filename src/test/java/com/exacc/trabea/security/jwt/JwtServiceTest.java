package com.exacc.trabea.security.jwt;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private static final String SECRET = "test-secret-for-jwt-signing-at-least-32-bytes-long";

    @Test void parsesValidTokenAndActiveRole() {
        var service = new JwtService(SECRET, 60_000);
        var claims = service.parse(service.generateToken("user@example.com", "Manager"));
        assertEquals("user@example.com", claims.getSubject());
        assertEquals("MANAGER", claims.get("activeRole"));
    }

    @Test void rejectsExpiredToken() throws InterruptedException {
        var service = new JwtService(SECRET, 1);
        var token = service.generateToken("user@example.com", "Manager");
        Thread.sleep(20);
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> service.parse(token));
    }
}
