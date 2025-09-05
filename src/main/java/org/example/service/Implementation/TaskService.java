package org.example.service.Implementation;

import org.apache.coyote.BadRequestException;
import org.example.Logging.LogUtils;
import org.example.domain.Task;
import org.example.exception.NotFoundException;
import org.example.repository.TaskRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TaskService extends BaseServiceImpl<Task, Long> {
    private static final Logger log = LogUtils.getLogger(TaskService.class);
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) throws BadRequestException {
        log.info("Creating task with title={}", task.getTitle());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        if (task.getDeadline() != null && task.getDeadline().isBefore(task.getCreatedAt())) {
            throw new BadRequestException("Deadline cannot be before creation date!");
        }
        Task saved = taskRepository.save(task);
        log.info("Task created successfully with id={}", saved.getId());
        return saved;
    }

    public Task getTaskByIdOrThrow(Long id) {
        log.debug("Fetching task with id={}", id);
        return findById(id).orElseThrow(() -> new NotFoundException("Task not found!"));
    }

    public Task updateTask(Long taskId, Task updatedTask) {
        log.info("Updating task id={}", taskId);
        Task task = findById(taskId).orElseThrow(() -> new NotFoundException("Task not found!"));
        if (updatedTask.getTitle() != null) {
            task.setTitle(updatedTask.getTitle());
        }
        if (updatedTask.getTaskStatus() != null) {
            task.setTaskStatus(updatedTask.getTaskStatus());
        }
        if (updatedTask.getTaskPriority() != null) {
            task.setTaskPriority(updatedTask.getTaskPriority());
        }
        if (updatedTask.getDeadline() != null) {
            task.setDeadline(updatedTask.getDeadline());
        }
        if (updatedTask.getAssignedTo() != null) {
            task.setAssignedTo(updatedTask.getAssignedTo());
        }

        task.setUpdatedAt(LocalDateTime.now());
        Task saved = taskRepository.save(task);
        log.info("Task updated successfully id={}", saved.getId());
        return saved;
    }
    public List<Task> getTasksWithCloseDeadline() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next24Hours = now.plusHours(24); // tasks due in next 24h
        return taskRepository.findByDeadlineBetween(now, next24Hours);
    }
    // Delete task
    public void deleteTask(Long id) {
        log.warn("Deleting task id={}", id);
        taskRepository.deleteById(id);
    }
}
