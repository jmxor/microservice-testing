package org.jmxor.springExample.user;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getOrCreateUserById(UUID id) {
        return userRepository.findById(id)
            .orElseGet(() -> {
                User newUser = new User();
                newUser.setId(id);
                return userRepository.save(newUser);
            });
    }

    public User getOrCreateUser(Jwt jwt) {
        return getOrCreateUserById(UUID.fromString(jwt.getSubject()));
    }
}
