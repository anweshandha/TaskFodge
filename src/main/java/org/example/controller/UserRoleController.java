package org.example.controller;

import org.example.domain.Role;
import org.example.domain.User;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.example.service.Implementation.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class UserRoleController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuditService auditService;

    /**
     * Assign a role to a user.
     */
    @PostMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<?> assignRole(@PathVariable Long userId, @PathVariable String roleName) {
        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch or create role
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));

        // Assign role
        Set<Role> roles = user.getRoles();
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);

        // Audit log
        auditService.record("ROLE_ASSIGNED", "Assigned role " + roleName + " to user " + user.getEmail());

        return ResponseEntity.ok("Role assigned successfully");
    }

    /**
     * Remove a role from a user.
     */
    @DeleteMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<?> removeRole(@PathVariable Long userId, @PathVariable String roleName) {
        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch role if exists
        Optional<Role> maybeRole = roleRepository.findByName(roleName);
        if (maybeRole.isPresent()) {
            Set<Role> roles = user.getRoles();
            roles.removeIf(r -> r.getName().equals(roleName));
            user.setRoles(roles);
            userRepository.save(user);

            // Audit log
            auditService.record("ROLE_REMOVED", "Removed role " + roleName + " from user " + user.getEmail());
        }

        return ResponseEntity.noContent().build();
    }
}
