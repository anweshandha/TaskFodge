package org.example.repository;

import org.example.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends BaseRepository<Task,Long> {
    List<Task> findByDeadlineBetween(LocalDateTime from, LocalDateTime to);
}
