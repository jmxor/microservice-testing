package org.jmxor.springExample.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller for public endpoints.
 * No authentication is required for these endpoints.
 */
@RestController
@RequestMapping("/api/public")
public class PublicController {
    /**
     * Health check endpoint - useful for load balancers and monitoring.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "Application is running"
        ));
    }

    /**
     * Public information endpoint - no authentication needed.
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(Map.of(
                "application", "Keycloak Spring Boot Demo",
                "version", "1.0.0",
                "authentication", "Keycloak OAuth2/OIDC"
        ));
    }
}
