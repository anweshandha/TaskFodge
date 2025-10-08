package org.example.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventType;

    private String message;

    private String actor; // optional: who performed the action

    @Column(nullable = false)
    private Instant createdAt;

    // Automatically set timestamp before persisting
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}