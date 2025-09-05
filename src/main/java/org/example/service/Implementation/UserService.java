package org.example.service.Implementation;

import org.example.Logging.LogUtils;
import org.example.domain.Role;
import org.example.domain.User;
import org.example.repository.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserService extends BaseServiceImpl<User, Long> {

    private static final Logger log = LogUtils.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Create User (with password hashing)
    public User createUser(User user) {
        log.info("Creating user with email={}", user.getEmail());
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        if (userRepository.existsByUsername(user.getUserName())) {
            throw new RuntimeException("Username already exists!");
        }
        if(userRepository.existsByUserId(String.valueOf(user.getId()))){
            throw new RuntimeException("Userid already exists!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        log.info("User created successfully with id={}", saved.getId());
        return saved;
    }

    // Get all users
    public List<User> getAllUsers() {
        log.debug("Fetching all users...");
        return userRepository.findAll();
    }

    // Get user by ID
    public User getUserById(Long id) {
        log.debug("Fetching user with id={}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

    // Update user
    public User updateUser(Long id, User updatedUser) {
        log.info("Updating user id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (updatedUser.getUserName() != null) {
            user.setUserName(updatedUser.getUserName());
        }
        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null) {
            user.setPassword(updatedUser.getPassword());
        }
        if (updatedUser.getRoles() != null) {
            user.setRoles(updatedUser.getRoles());
        }
        User saved = userRepository.save(user);
        log.info("User updated successfully id={}", saved.getId());
        return saved;
    }

    // Delete user
    public void deleteUser(Long id) {
        log.warn("Deleting user id={}", id);
        userRepository.deleteById(id);
    }

    // Assign role
    public User assignRoles(Long userId, Set<Role> roles) {
        User user = getUserById(userId);
        user.setRoles(roles);
        return userRepository.save(user);
    }
}