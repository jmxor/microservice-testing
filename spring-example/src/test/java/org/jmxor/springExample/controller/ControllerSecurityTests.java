package org.jmxor.springExample.controller;

import org.jmxor.springExample.config.CustomAccessDeniedHandler;
import org.jmxor.springExample.config.CustomAuthenticationEntryPoint;
import org.jmxor.springExample.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for controllers with mocked JWT authentication.
 */
@WebMvcTest({UserController.class, AdminController.class, PublicController.class})
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
@ActiveProfiles("test")
public class ControllerSecurityTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    /**
     * Test public endpoint - should be accessible without authentication
     */
    @Test
    void publicEndpoint_shouldBeAccessible() throws Exception {
        mockMvc.perform(get("/api/public/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }

    /**
     * Test a user endpoint without authentication.
     * Should return 401 Unauthorized
     */
    @Test
    void userEndpoint_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/user/profile"))
            .andExpect(status().isUnauthorized());
    }

    /**
     * Test a user endpoint with a valid JWT and user role.
     * Uses Spring Security Test's jwt() request post-processor
     */
    @Test
    void userEndpoint_withValidJwtAndUserRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/user/profile")
                .with(jwt()
                    .authorities(new SimpleGrantedAuthority("ROLE_user"))
                    .jwt(builder -> builder
                        .subject("test-user-id")
                        // Issuer is required because /user/profile endpoint includes it in its response
                        .issuer("https://keycloak.example.com/realms/test-realm")
                        .claim("preferred_username", "test-user")
                        .claim("realm_access", Map.of("roles", List.of("user")))
                    )
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("test-user"));
    }

    /**
     * Test an admin endpoint with the user role.
     * Should return 403 Forbidden.
     */
    @Test
    void adminEndpoint_withUserRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")
                .with(jwt().jwt(builder -> builder
                    .claim("realm_access", Map.of("roles", List.of("user")))
                ))
            )
            .andExpect(status().isForbidden());
    }

    /**
     * Test an admin endpoint with the admin role.
     * Should return 200 OK.
     */
    @Test
    void adminEndpoint_withAdminRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")
                .with(jwt()
                    .authorities(new SimpleGrantedAuthority("ROLE_admin"))
                    .jwt(builder -> builder
                        .subject("test-user-id")
                        .claim("preferred_username", "admin-user")
                        .claim("realm_access", Map.of("roles", List.of("admin")))
                    )
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.admin").value("admin-user"));
    }

}
