package org.example.service.Implementation;

import org.example.domain.Task;
import org.example.domain.TaskStatus;
import org.example.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Create a new Task — sets timestamps automatically.
     */
    public Task createTask(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    /**
     * Save or update an existing Task.
     */
    public Task saveTask(Task task) {
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    /**
     * Get all tasks.
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Get task by ID.
     */
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    /**
     * Delete task by ID.
     */
    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }

    /**
     * Get tasks with deadlines within the next `minutes`.
     */
    public List<Task> getTasksWithCloseDeadline(long minutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime to = now.plusMinutes(minutes);
        return taskRepository.findByDeadlineBetween(now, to);
    }

    /**
     * Default version: next 30 minutes.
     */
    public List<Task> getTasksWithCloseDeadline() {
        return getTasksWithCloseDeadline(30L);
    }

    /**
     * Get tasks by status (paginated).
     */
    public List<Task> getTasksByStatus(TaskStatus status, int page, int size) {
        return taskRepository.findByTaskStatus(status, PageRequest.of(page, size))
                .getContent();
    }

    /**
     * Update existing task fields.
     */
    public Task updateTask(Long id, Task updatedTask) {
        Task existing = getTaskById(id);
        existing.setTitle(updatedTask.getTitle());
        existing.setDescription(updatedTask.getDescription());
        existing.setDeadline(updatedTask.getDeadline());
        existing.setTaskStatus(updatedTask.getTaskStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(existing);
    }
}
