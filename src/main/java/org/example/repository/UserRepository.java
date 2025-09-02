package org.example.repository;

import org.example.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<User,Long> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByUserId(String userId);

    User findByEmail(String email);

    User findByUserIdAndEmail(String userId, String email);
}
