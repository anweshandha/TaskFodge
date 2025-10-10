package org.example.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(
        name = "tasks",
        indexes = {
                @Index(name = "idx_task_deadline", columnList = "deadline"),
                @Index(name = "idx_task_status", columnList = "task_status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status", nullable = false)
    private TaskStatus taskStatus = TaskStatus.PENDING; // default value

    @Enumerated(EnumType.STRING)
    @Column(name = "task_priority", nullable = false)
    private Priority taskPriority = Priority.MEDIUM; // default value

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id", referencedColumnName = "id")
    private User assignedTo;

    // Automatically manage timestamps
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
