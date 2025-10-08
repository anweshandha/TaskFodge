package org.example.service.Implementation;

import org.example.domain.Task;
import org.example.domain.TaskStatus;
import org.example.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Save a new task
     */
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    /**
     * Get all tasks
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Get task by ID
     */
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    /**
     * Delete task by ID
     */
    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }

    /**
     * Get tasks whose deadline is within the next `minutes`
     */
    public List<Task> getTasksWithCloseDeadline(long minutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime to = now.plusMinutes(minutes);
        return taskRepository.findByDeadlineBetween(now, to);
    }

    /**
     * No-arg version with default 30 minutes
     */
    public List<Task> getTasksWithCloseDeadline() {
        return getTasksWithCloseDeadline(30L);
    }

    /**
     * Optional: get tasks by status with pagination
     */
    public List<Task> getTasksByStatus(TaskStatus status, int page, int size) {
        return taskRepository.findByTaskStatus(status, org.springframework.data.domain.PageRequest.of(page, size))
                .getContent();
    }
}
