package org.example.repository;

import org.example.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    boolean existsByUserId(String userId);

    User findByEmail(String email);

    User findByUserName(String userName);
}
