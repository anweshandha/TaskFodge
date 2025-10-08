package org.example.controller;

import org.example.domain.Role;
import org.example.domain.User;
import org.example.dto.AuthRequest;
import org.example.dto.AuthResponse;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.example.security.JwtUtil;
import org.example.service.Implementation.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuditService auditService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Default role assignment
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        user.setRoles(Collections.singleton(userRole));

        User saved = userRepository.save(user);

        auditService.record("USER_CREATED", "User created: " + saved.getEmail());
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        // Load user by email or username
        User user = userRepository.findByEmail(req.getUsername());
        if (user == null) {
            user = userRepository.findByUserName(req.getUsername());
        }
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        // Validate password
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).build();
        }

        var roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        String token = jwtUtil.generateToken(
                user.getEmail() != null ? user.getEmail() : user.getUserName(),
                roles
        );

        auditService.record("USER_LOGIN", "User logged in: " + user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, jwtUtil.getExpirationMillis()));
    }
}