package org.example.repository;

import org.example.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    Optional<User> findByEmail(String email);

    Optional<User> findByUserName(String userName);
}
