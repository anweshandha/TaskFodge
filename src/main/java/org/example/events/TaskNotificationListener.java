package org.example.events;



import org.example.notification.NotificationService;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Async listener for TaskNotificationEvent using Spring Events.
 * This is the lightweight starting point for event-driven behavior.
 */
@Component
public class TaskNotificationListener {

    private final Logger log = LoggerFactory.getLogger(TaskNotificationListener.class);
    private final NotificationService notificationService;

    public TaskNotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Async("taskExecutor")
    @EventListener(TaskNotificationEvent.class)
    public void onTaskNotification(TaskNotificationEvent event) {
        // Optionally push traceId into MDC (could be part of event)
        MDC.put("traceId", event.getTriggeredAt().toString());

        try {
            log.info("Received TaskNotificationEvent: taskId={} type={}", event.getTaskId(), event.getType());
            // delegate to concrete notification channels (email/slack). Each implementation handles retries/errors.
            notificationService.send(event);
        } catch (Exception ex) {
            // handle exception locally (log + persist to DB/queue if needed)
            log.error("Failed to send notification for taskId={}. Persisting failure for manual retry.", event.getTaskId(), ex);
            // TODO: persist to a 'failed_notifications' table or push to Kafka dead-letter topic for later inspection.
        } finally {
            MDC.clear();
        }
    }
}
