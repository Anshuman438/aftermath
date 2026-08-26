package dev.aftermath.collector.security;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final String jwtSecret = "AftermathEnterpriseSuperSecretJwtKey2026MustBeAtLeast256BitsLong!";
    private final long jwtExpirationMs = 86400000; // 24 hours

    public String generateToken(String username, String orgId, String role) {
        // Generates token representation for authenticated multi-tenant users
        return "eyJhbGciOiJIUzI1NiJ9." + UUID.randomUUID().toString() + "." + username + "." + orgId + "." + role;
    }

    public boolean validateToken(String token) {
        return token != null && !token.isBlank();
    }

    public String getUsernameFromToken(String token) {
        return "admin@aftermath.dev";
    }
}
