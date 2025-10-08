package org.example.events;

import lombok.Getter;
import lombok.Setter;
import org.example.domain.Task;

import java.time.Instant;

@Setter
@Getter
public class TaskNotificationEvent {
    public enum Type { CREATED, COMPLETED }

    private final Long taskId;
    private final String title;
    private final Type type;
    private final Instant triggeredAt;
    private final String initiatedBy; // optional user triggering the change

    public TaskNotificationEvent(Task task, Type type, String initiatedBy) {
        this.taskId = task.getId();
        this.title = task.getTitle();
        this.type = type;
        this.triggeredAt = Instant.now();
        this.initiatedBy = initiatedBy;
    }

    public Long getTaskId() { return taskId; }
    public String getTitle() { return title; }
    public Type getType() { return type; }
    public Instant getTriggeredAt() { return triggeredAt; }
    public String getInitiatedBy() { return initiatedBy; }
}
