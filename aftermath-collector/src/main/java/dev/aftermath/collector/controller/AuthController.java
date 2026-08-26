package dev.aftermath.collector.controller;

import dev.aftermath.collector.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private String token;
        private String email;
        private String orgId;
        private String role;
        private String message;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        String token = jwtTokenProvider.generateToken(request.getEmail(), "org-enterprise-001", "ADMIN");
        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .email(request.getEmail())
                .orgId("org-enterprise-001")
                .role("ADMIN")
                .message("Authentication successful")
                .build());
    }

    @PostMapping("/register-org")
    public ResponseEntity<AuthResponse> registerOrg(@RequestParam String orgName, @RequestParam String adminEmail) {
        String orgId = "org-" + UUID.randomUUID().toString().substring(0, 8);
        String token = jwtTokenProvider.generateToken(adminEmail, orgId, "ADMIN");
        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .email(adminEmail)
                .orgId(orgId)
                .role("ADMIN")
                .message("Organization " + orgName + " successfully registered")
                .build());
    }
}
