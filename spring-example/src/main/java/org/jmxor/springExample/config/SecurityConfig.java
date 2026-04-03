package org.jmxor.springExample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import java.util.List;

/**
 * Security configuration for Keycloak integration.
 * Configures JWT validation, role extraction, and endpoint protection.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final SecurityProperties securityProperties;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(
        SecurityProperties securityProperties,
        CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
        CustomAccessDeniedHandler customAccessDeniedHandler
    ) {
        this.securityProperties = securityProperties;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    /**
     * Main security filter chain configuration
     * Sets up OAuth2 resource server with JWT support.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        List<String> publicPaths = securityProperties.publicPaths();
        http
            // Disable CSRF for stateless APIs
            .csrf(AbstractHttpConfigurer::disable)

            // Use stateless sessions - no server-side session storage
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Configure endpoint authorization
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - no authentication required
                .requestMatchers(publicPaths.toArray(String[]::new)).permitAll()

                // Admin endpoints require admin role
                .requestMatchers("/api/admin/**").hasRole("admin")

                // User endpoints require user or admin role
                .requestMatchers("/api/user/**").hasAnyRole("user", "admin")

                // All other requests require authentication
                .anyRequest().authenticated()
            )

            // Configure Oauth2 resource server with JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )

            // Custom error handlers
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
            )

            // Add security headers
            .headers(headers -> headers
                .contentSecurityPolicy(csp ->
                    csp.policyDirectives("default-src 'self'")
                )
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                .xssProtection(xss -> xss
                    .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                )
            );

        return http.build();
    }

    /**
     * Configures how JWT claims are converted to Spring Security authorities.
     * Keycloak stores roles in a nested structure that needs custom extraction
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }
}
