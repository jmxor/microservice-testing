package org.jmxor.springExample.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

import java.time.Duration;

/**
 * Custom JWT decoder configuration.
 * Adds additional validation beyond the default issuer validation.
 */
@Configuration
public class JwtConfig {

    @Value("${spring.security.oauth2.resource-server.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${spring.security.oauth2.resource-server.jwt.jwk-set-uri}")
    private String jwkSetUri;

    /**
     * Creates a custom JWT decoder with additional validators.
     * Validates issuer, audience, and adds clock skew tolerance.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        // Create decoder from JWK Set URI
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withJwkSetUri(jwkSetUri)
                .build();

        // Combine multiple validators
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
                // Validate issuer matches expected value
                JwtValidators.createDefaultWithIssuer(issuerUri),

                // Add a custom audience validator
                // Determine if it is possible to remove the 'test-client' audience from the main
                // audience validator without breaking the KeycloakIntegrationTest
                new AudienceValidator("spring-boot-app", "account", "test-client"),

                // Add timestamp validator with clock skew tolerance
                new JwtTimestampValidator(Duration.ofSeconds(60))
        );

        jwtDecoder.setJwtValidator(validator);

        return jwtDecoder;
    }
}