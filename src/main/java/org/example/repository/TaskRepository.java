package org.example.repository;

import org.example.domain.Task;
import org.example.domain.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find tasks with deadline in a given range
    List<Task> findByDeadlineBetween(LocalDateTime from, LocalDateTime to);

    // Pagination
    Page<Task> findAll(Pageable pageable);

    // Tasks filtered by status with pagination
    Page<Task> findByTaskStatus(TaskStatus taskStatus, Pageable pageable);
}
