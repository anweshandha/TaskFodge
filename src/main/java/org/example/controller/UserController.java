package org.example.controller;

import org.example.domain.Role;
import org.example.domain.User;
import org.example.service.Implementation.AuditService;
import org.example.service.Implementation.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuditService auditService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User saved = userService.createUser(user);
        auditService.record("USER_CREATED", "Created user: " + saved.getEmail());
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("principal == #id or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        auditService.record("USER_UPDATED", "Updated user: " + updated.getEmail());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        userService.deleteUser(id);
        auditService.record("USER_DELETED", "Deleted user: " + user.getEmail());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> assignRoles(@PathVariable Long id, @RequestBody Set<Role> roles) {
        User updated = userService.assignRoles(id, roles);
        auditService.record("ROLES_UPDATED", "Updated roles for user: " + updated.getEmail());
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> isEmailTaken(@PathVariable String email) {
        return ResponseEntity.ok(userService.isEmailTaken(email));
    }

    @GetMapping("/exists/username/{username}")
    public ResponseEntity<Boolean> isUsernameTaken(@PathVariable String username) {
        return ResponseEntity.ok(userService.isUsernameTaken(username));
    }

    @GetMapping("/exists/userId/{userId}")
    public ResponseEntity<Boolean> isUserIdTaken(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.isUserIdTaken(userId));
    }
}
