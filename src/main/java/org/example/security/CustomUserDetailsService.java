package org.example.security;

import org.example.domain.Role;
import org.example.domain.User;
import org.example.repository.UserRepository;
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
@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
            throw new UsernameNotFoundException("Username or email cannot be empty");
        }

        // Lookup by username or email
        User user = userRepository.findByUserName(usernameOrEmail)
                .orElse(userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException(
                                "User not found with identifier: " + usernameOrEmail)));

        // Map roles to Spring Security authorities
        Set<GrantedAuthority> authorities = user.getRoles() == null ? Set.of() :
                user.getRoles().stream()
                        .map(Role::getName)
                        .filter(name -> name != null && !name.isBlank())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());

        // Build UserDetails
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
