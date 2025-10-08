package org.example.security;

import org.example.domain.Role;
import org.example.domain.User;
import org.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Loads UserDetails for Spring Security.
 * Supports lookup by username or email.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
            throw new UsernameNotFoundException("Username or email cannot be empty");
        }

        // Lookup user by username first, then by email
        User user = userRepository.findByUserName(usernameOrEmail);
        if (user == null) {
            user = userRepository.findByEmail(usernameOrEmail);
        }

        if (user == null) {
            log.warn("User not found for identifier: {}", usernameOrEmail);
            throw new UsernameNotFoundException("User not found with identifier: " + usernameOrEmail);
        }

        // Map roles to Spring Security authorities
        Set<GrantedAuthority> authorities = user.getRoles() == null ?
                Set.of() :
                user.getRoles().stream()
                        .map(Role::getName)
                        .filter(name -> name != null && !name.isBlank())
                        .map(SimpleGrantedAuthority::new) // Use role names directly
                        .collect(Collectors.toSet());

        // Build UserDetails object
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName() != null ? user.getUserName() : user.getEmail())
                .password(user.getPassword()) // BCrypt hashed password
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
