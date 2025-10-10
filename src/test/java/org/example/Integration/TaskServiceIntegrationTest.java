package org.example.Integration;

import org.example.TaskFodgeApplication;
import org.example.domain.Task;
import org.example.repository.TaskRepository;
import org.junit.Test;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

@SpringBootTest(classes = TaskFodgeApplication.class)
@Testcontainers
public class TaskServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private TaskRepository taskRepository;

    @BeforeAll
    static void setup() {
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
    }

    @Test
    public void saveAndRetrieveTask() {
        Task t = new Task();
        t.setTitle("integration");
        t.setCreatedAt(LocalDateTime.now());
        t.setUpdatedAt(LocalDateTime.now());

        Task saved = taskRepository.save(t);
        Assertions.assertNotNull(taskRepository.findById(saved.getId()));
    }
}