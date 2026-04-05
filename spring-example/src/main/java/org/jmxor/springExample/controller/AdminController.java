package org.jmxor.springExample.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller for admin-only endpoints.
 * All endpoints require the admin role.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('admin')")
public class AdminController {

    /**
     * Admin dashboard endpoint.
     * Returns administrative statistics and information.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
        @AuthenticationPrincipal Jwt jwt) {

        String adminName = jwt.getClaimAsString("preferred_username");

        return ResponseEntity.ok(Map.of(
            "admin", adminName,
            "message", "Welcome to the admin dashboard",
            "stats", Map.of(
                "totalUsers", 150,
                "activeUsers", 45,
                "pendingRequests", 12
            )
        ));
    }

    /**
     * List all users (simulated).
     * In a real application, this would query Keycloak's Admin API.
     */
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> listUsers() {
        // In production, you would call Keycloak Admin API
        List<Map<String, Object>> users = List.of(
            Map.of("id", "1", "username", "user1", "email", "user1@example.com"),
            Map.of("id", "2", "username", "user2", "email", "user2@example.com"),
            Map.of("id", "3", "username", "admin1", "email", "admin@example.com")
        );

        return ResponseEntity.ok(users);
    }

    /**
     * Admin configuration endpoint.
     * Demonstrates combining URL-based and method-based security.
     */
    @GetMapping("/config")
    @PreAuthorize("hasRole('admin') and #jwt.subject == authentication.name")
    public ResponseEntity<Map<String, Object>> getConfig(
        @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(Map.of(
            "keycloakRealm", "spring-boot-demo",
            "sessionTimeout", 3600,
            "maxFailedAttempts", 5,
            "passwordPolicy", "length(8) and digits(1) and upperCase(1)"
        ));
    }
}