package org.example.controller;


import org.apache.coyote.BadRequestException;
import org.example.domain.Task;
import org.example.service.Implementation.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController extends BaseController<Task, Long> {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        super(taskService); // pass to BaseController if constructor exists
        this.taskService = taskService;
    }

    // Only add custom endpoints here
    @PostMapping("/custom")
    public ResponseEntity<Task> createTaskWithValidation(@RequestBody Task task) throws BadRequestException {
        return ResponseEntity.ok(taskService.createTask(task));
    }

    @GetMapping("/deadline-soon")
    public ResponseEntity<List<Task>> getTasksWithCloseDeadline() {
        // Example custom business endpoint
        return ResponseEntity.ok(taskService.getTasksWithCloseDeadline());
    }
}
