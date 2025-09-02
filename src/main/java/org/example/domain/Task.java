package org.example.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;

    @Enumerated(EnumType.STRING)
    private Priority taskPriority;

    private LocalDateTime createdAt;   // required for setCreatedAt()
    private LocalDateTime updatedAt;   // required for setUpdatedAt()
    private LocalDateTime deadline;    // required for getDeadline()

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;           // required for getAssignedTo()
}