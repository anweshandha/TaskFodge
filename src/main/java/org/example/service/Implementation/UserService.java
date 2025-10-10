package org.example.service.Implementation;

import org.example.domain.Role;
import org.example.domain.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /** Create a user */
    public User createUser(User user) {
        return userRepository.save(user);
    }

    /** Get all users */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /** Get user by ID */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    /** Update user */
    public User updateUser(Long id, User updatedUser) {
        User user = getUserById(id);
        user.setUserName(updatedUser.getUserName());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(updatedUser.getPassword());
        user.setRoles(updatedUser.getRoles());
        return userRepository.save(user);
    }

    /** Delete user */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /** Assign roles to a user */
    public User assignRoles(Long id, Set<Role> roles) {
        User user = getUserById(id);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    /** Existence checks */
    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUserName(username);
    }

    public boolean isUserIdTaken(Long userId) {
        return userRepository.existsById(userId);
    }

    /** Optional helpers */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
}
