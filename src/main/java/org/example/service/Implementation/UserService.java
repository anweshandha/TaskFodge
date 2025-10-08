package org.example.service.Implementation;

import org.example.domain.Role;
import org.example.domain.User;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    /** Create a new user */
    public User createUser(User user) {
        return userRepository.save(user);
    }

    /** Get all users */
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /** Get user by ID */
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    /** Update user */
    public User updateUser(Long id, User updatedUser) {
        User user = getUserById(id);
        user.setUserName(updatedUser.getUserName());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(updatedUser.getPassword()); // encode externally
        return userRepository.save(user);
    }

    /** Delete user */
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    /** Assign roles dynamically */
    public User assignRoles(Long id, Set<Role> roles) {
        User user = getUserById(id);
        Set<Role> assignedRoles = roles.stream()
                .map(role -> roleRepository.findByName(role.getName())
                        .orElseGet(() -> roleRepository.save(Role.builder().name(role.getName()).build())))
                .collect(Collectors.toSet());
        user.setRoles(assignedRoles);
        return userRepository.save(user);
    }

    /** Existence checks */
    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUserName(username);
    }

    public boolean isUserIdTaken(String userId) {
        return userRepository.existsByUserId(userId);
    }
}
