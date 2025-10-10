package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.domain.Task;
//import org.example.dto.TaskDto;
import org.example.service.Implementation.AuditService;
import org.example.service.Implementation.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks") // ✅ Versioned endpoint
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final AuditService auditService;

    /** ✅ Create a new task */
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task taskDto) {
        Task saved = taskService.saveTask(taskDto);
        auditService.record("TASK_CREATED", "Created task: " + saved.getTitle());
        return ResponseEntity.ok(saved);
    }

    /** ✅ Get all tasks */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }
    @GetMapping(value = "/list", headers = "API-Version=1")
    public ResponseEntity<List<Task>> listTasksV1() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    /** ✅ Get task by ID */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    /** ✅ Delete task by ID */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        taskService.deleteTask(id);
        auditService.record("TASK_DELETED", "Deleted task: " + task.getTitle());
        return ResponseEntity.noContent().build();
    }

    /** ✅ Get tasks with deadline within next 30 minutes */
    @GetMapping("/close-deadline")
    public ResponseEntity<List<Task>> getTasksWithCloseDeadline() {
        return ResponseEntity.ok(taskService.getTasksWithCloseDeadline());
    }

    /** ✅ Get tasks with deadline within custom minutes */
    @GetMapping("/close-deadline/{minutes}")
    public ResponseEntity<List<Task>> getTasksWithCloseDeadline(@PathVariable long minutes) {
        return ResponseEntity.ok(taskService.getTasksWithCloseDeadline(minutes));
    }
}
