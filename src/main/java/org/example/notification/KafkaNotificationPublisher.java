package org.example.notification;

import org.example.events.TaskNotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaNotificationPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaNotificationPublisher.class);
    private final KafkaTemplate<String, TaskNotificationEvent> kafkaTemplate;

    @Autowired
    public KafkaNotificationPublisher(KafkaTemplate<String, TaskNotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(TaskNotificationEvent event) {
        log.info("Publishing Kafka event: {} for task {}", event.getType(), event.getTaskId());
        kafkaTemplate.send("task-notifications", String.valueOf(event.getTaskId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null)
                        log.error("Failed to send Kafka message for task {}", event.getTaskId(), ex);
                    else
                        log.info("Kafka message sent successfully for task {}", event.getTaskId());
                });
    }
}