package org.example.repository;

import org.example.domain.Task;
import org.example.domain.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find tasks whose deadline is within [from, to]
    List<Task> findByDeadlineBetween(LocalDateTime from, LocalDateTime to);

    // Paginated listing
    Page<Task> findAll(Pageable pageable);

    // Find tasks by status with pagination
    Page<Task> findByTaskStatus(TaskStatus taskStatus, Pageable pageable);
}
