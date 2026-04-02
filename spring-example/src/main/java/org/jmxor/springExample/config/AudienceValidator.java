package org.jmxor.springExample.config;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.List;

/**
 * Validates JWT audience claim.
 * Ensures the token was intended for this application.
 */
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final List<String> allowedAudiences;

    private static final OAuth2Error INVALID_AUDIENCE = new OAuth2Error(
            "invalid_token",
            "The required audience is missing",
            null
    );

    public AudienceValidator(String... audiences) {
        this.allowedAudiences = Arrays.asList(audiences);
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        List<String> audiences = jwt.getAudience();

        // Check if any allowed audience is present
        if (audiences != null) {
            for (String allowed : allowedAudiences) {
                if (audiences.contains(allowed)) {
                    return OAuth2TokenValidatorResult.success();
                }
            }
        }

        // Keycloak may not include audience for some configurations
        // In that case, check the azp (authorized party) claim
        String azp = jwt.getClaimAsString("azp");
        if (azp != null && allowedAudiences.contains(azp)) {
            return OAuth2TokenValidatorResult.success();
        }

        return OAuth2TokenValidatorResult.failure(INVALID_AUDIENCE);
    }
}
