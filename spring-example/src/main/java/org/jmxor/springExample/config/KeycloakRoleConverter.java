package org.jmxor.springExample.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    // Prefix for Spring Security roles (ROLE_admin, ROLE_user, etc.)
    private static final String ROLE_PREFIX = "ROLE_";

    // CLient ID for extracting client-specific roles
    @Value( "${spring.security.oauth2.resourceserver.client-id}")
    private static String CLIENT_ID;

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Extract realm-level roles
        authorities.addAll(extractRealmRoles(jwt));

        // Extract client-level roles
        authorities.addAll(extractClientRoles(jwt));

        return authorities;
    }

    /**
     * Extracts roles from the realm_access claim.
     * These are global roles assigned to users in the realm.
     */
    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || !realmAccess.containsKey("roles")) {
            return List.of();
        }

        List<String> roles = (List<String>) realmAccess.get("roles");

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                .collect(Collectors.toList());
    }

    /**
     * Extracts roles from the resource_access claim.
     * These are client-specific roles for fine-grained permissions.
     */
    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractClientRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");

        if (resourceAccess == null || !resourceAccess.containsKey(CLIENT_ID)) {
            return List.of();
        }

        Map<String, Object> clientRoles = (Map<String, Object>) resourceAccess.get(CLIENT_ID);

        if (clientRoles == null || !clientRoles.containsKey("roles")) {
            return List.of();
        }

        List<String> roles = (List<String>) clientRoles.get("roles");

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + "CLIENT_" + role))
                .collect(Collectors.toList());
    }
}
