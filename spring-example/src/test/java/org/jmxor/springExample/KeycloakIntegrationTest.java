package org.jmxor.springExample;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureTestRestTemplate
public class KeycloakIntegrationTest {
    //Start a Keycloak container with test real configuration
    @Container
    static KeycloakContainer keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:26.5.5")
        .withRealmImportFile("keycloak/realm-test.json");

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Configure Spring to use the Testcontainer's Keycloak instance
     *
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add(
            "spring.security.oauth2.resource-server.jwt.issuer-uri",
            () -> keycloak.getAuthServerUrl() + "/realms/test-realm"
        );
        registry.add(
            "spring.security.oauth2.resource-server.jwt.jwk-set-uri",
            () -> keycloak.getAuthServerUrl() + "/realms/test-realm/protocol/openid-connect/certs"
        );
        registry.add("keycloak.admin.client-secret", () -> "pucU7aWL43iZZHthABztMgvqT7TJ4xqT");

    }

    /**
     * Helper method to get access token from keycloak
     */
    private String getAccessToken(String username, String password) {
        String tokenUrl = keycloak.getAuthServerUrl() + "/realms/test-realm/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=password" +
            "&client_id=test-client" +
            "&client_secret=pucU7aWL43iZZHthABztMgvqT7TJ4xqT" +
            "&username=" + username +
            "&password=" + password;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(tokenUrl, request, Map.class);


        return (String) response.get("access_token");
    }

    @Test
    void protectedEndpoint_withValid_Token_shouldSucceed() {
        String token = getAccessToken("testuser", "testpassword");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<Map> response = restTemplate.exchange(
            "/api/user/profile",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().containsKey("username"));
    }

}

