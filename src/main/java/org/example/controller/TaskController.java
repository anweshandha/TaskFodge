package org.example.controller;

import org.example.domain.Task;
import org.example.service.Implementation.AuditService;
import org.example.service.Implementation.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private AuditService auditService;

    /** Create a new task */
    @PostMapping
    public Task createTask(@RequestBody Task task) {
        Task saved = taskService.saveTask(task);
        auditService.record("TASK_CREATED", "Created task: " + saved.getTitle());
        return saved;
    }

    /** Get all tasks */
    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    /** Get task by ID */
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    /** Delete task by ID */
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        taskService.deleteTask(id);
        auditService.record("TASK_DELETED", "Deleted task: " + task.getTitle());
    }

    /** Get tasks with deadline within next 30 minutes (default) */
    @GetMapping("/close-deadline")
    public List<Task> getTasksWithCloseDeadline() {
        return taskService.getTasksWithCloseDeadline();
    }

    /** Get tasks with deadline within custom minutes */
    @GetMapping("/close-deadline/{minutes}")
    public List<Task> getTasksWithCloseDeadline(@PathVariable long minutes) {
        return taskService.getTasksWithCloseDeadline(minutes);
    }
}
