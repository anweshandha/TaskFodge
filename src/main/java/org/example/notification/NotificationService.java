package org.example.notification;

import org.example.events.TaskNotificationEvent;

public interface NotificationService {
    /**
     * Send notification about the given event.
     * Implementations should be resilient (retry or persist to queue on failure).
     */
    void send(TaskNotificationEvent event);
}
