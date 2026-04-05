package org.jmxor.springExample.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for authenticated user endpoints.
 * Requires a valid JWT token with appropriate roles.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    /**
     * Returns the profile of the currently authenticated user.
     * Extracts user information from the JWT token claims.
     *
     * @param jwt The JWT token injected by Spring Security
     */
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('user', 'admin')")
    public ResponseEntity<Map<String, Object>> getProfile(
        @AuthenticationPrincipal Jwt jwt) {

        Map<String, Object> profile = new HashMap<>();

        // Standard claims
        profile.put("subject", jwt.getSubject());
        profile.put("username", jwt.getClaimAsString("preferred_username"));
        profile.put("email", jwt.getClaimAsString("email"));
        profile.put("emailVerified", jwt.getClaimAsBoolean("email_verified"));
        profile.put("name", jwt.getClaimAsString("name"));
        profile.put("givenName", jwt.getClaimAsString("given_name"));
        profile.put("familyName", jwt.getClaimAsString("family_name"));

        // Token metadata
        profile.put("issuedAt", jwt.getIssuedAt());
        profile.put("expiresAt", jwt.getExpiresAt());
        profile.put("issuer", jwt.getIssuer().toString());

        return ResponseEntity.ok(profile);
    }

    /**
     * Returns the roles assigned to the current user.
     * Useful for frontend applications to determine UI permissions.
     */
    @GetMapping("/roles")
    @PreAuthorize("hasAnyRole('user', 'admin')")
    public ResponseEntity<Map<String, Object>> getRoles(
        @AuthenticationPrincipal Jwt jwt) {

        Map<String, Object> roles = new HashMap<>();

        // Extract realm roles
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null) {
            roles.put("realmRoles", realmAccess.get("roles"));
        }

        // Extract client roles
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess != null) {
            roles.put("clientRoles", resourceAccess);
        }

        return ResponseEntity.ok(roles);
    }

    /**
     * Protected endpoint for regular users.
     * Demonstrates role-based access control.
     */
    @GetMapping("/data")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Map<String, Object>> getUserData(
        @AuthenticationPrincipal Jwt jwt) {

        String username = jwt.getClaimAsString("preferred_username");

        return ResponseEntity.ok(Map.of(
            "message", "Hello, " + username + "!",
            "data", "This is user-specific data",
            "accessLevel", "user"
        ));
    }
}