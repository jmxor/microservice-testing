package org.jmxor.springExample.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties (
    @NotEmpty(message = "Configuration property CORS allowed origins cannot be empty")
    List<String> corsAllowedOrigins,

    @NotEmpty(message = "Configuration property Public paths cannot be empty")
    List<String> publicPaths,

    @NotEmpty(message = "Configuration property JWT allowed audiences cannot be empty")
    List<String> jwtAllowedAudiences
) {}
