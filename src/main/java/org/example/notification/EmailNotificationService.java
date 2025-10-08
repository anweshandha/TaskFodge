package org.example.notification;

import org.example.events.TaskNotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

/**
 * Simple email notification implementation (stub).
 * Replace sending logic with JavaMailSender or any email provider SDK.
 *
 * Uses Spring Retry for transient failures; annotate with @Retryable to retry on runtime exceptions.
 */
@Service
public class EmailNotificationService implements NotificationService {

    private final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    @Override
    @Retryable(value = { RuntimeException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    public void send(TaskNotificationEvent event) {
        // TODO: integrate JavaMailSender or external provider.
        // For now log and simulate success.
        log.info("EmailNotificationService -> sending email for taskId={} eventType={} initiatedBy={}",
                event.getTaskId(), event.getType(), event.getInitiatedBy());
        // If failure happens, throw RuntimeException to trigger retry.
    }
}

