package org.jmxor.springExample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS configuration for cross-origin requests.
 * Required when the frontend and backed are on different origins.
 */
@Configuration
public class CorsConfig {
    private final SecurityProperties securityProperties;

    public CorsConfig(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    /**
     * Configures CORS settings for the application.
     * Allows credentials (cookies, authorization header) for OAuth2 flows.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        List<String> corsAllowedOrigins = securityProperties.corsAllowedOrigins();
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow specified origins
        configuration.setAllowedOrigins(corsAllowedOrigins);

        // Allow common HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        //Allow authorization headers for JWT tokens
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));

        //Export headers that the frontend might need
        configuration.setExposedHeaders(Arrays.asList("X-Total-Count", "X-Page-Number"));

        // Allow credentials for cookie-based sessions if needed
        configuration.setAllowCredentials(true);

        // Cache preflight responses for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
