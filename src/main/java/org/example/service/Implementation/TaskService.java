package org.example.service.Implementation;

import org.apache.coyote.BadRequestException;
import org.example.domain.Task;
import org.example.exception.NotFoundException;
import org.example.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TaskService extends BaseServiceImpl<Task, Long> {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) throws BadRequestException {
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        if (task.getDeadline() != null && task.getDeadline().isBefore(task.getCreatedAt())) {
            throw new BadRequestException("Deadline cannot be before creation date!");
        }
        return taskRepository.save(task);
    }

    public Task getTaskByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Task not found!"));
    }

    public Task updateTask(Long taskId, Task updatedTask) {
        Task task = findById(taskId).orElseThrow(() -> new NotFoundException("Task not found!"));
        task.setTitle(updatedTask.getTitle());
        task.setTaskStatus(updatedTask.getTaskStatus());
        task.setTaskPriority(updatedTask.getTaskPriority());
        task.setDeadline(updatedTask.getDeadline());
        task.setAssignedTo(updatedTask.getAssignedTo());
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }
    public List<Task> getTasksWithCloseDeadline() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next24Hours = now.plusHours(24); // tasks due in next 24h
        return taskRepository.findByDeadlineBetween(now, next24Hours);
    }
}
