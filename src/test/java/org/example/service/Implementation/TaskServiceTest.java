package org.example.service.Implementation;

import org.example.domain.Task;
import org.example.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTask_setsTimestampsAndSaves() {
        Task t = new Task();
        t.setTitle("Test");
        t.setDeadline(LocalDateTime.now().plusDays(1));

        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task saved = taskService.createTask(t);

        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void getById_notFound_throwsRuntime() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> taskService.getTaskById(1L));
    }
}
