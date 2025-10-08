package org.example.service.Implementation;

import org.example.domain.AuditLog;
import org.example.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void record(String eventType, String message) {
        AuditLog audit = AuditLog.builder()
                .eventType(eventType)
                .message(message)
                .createdAt(Instant.now())
                .build();
        try {
            auditLogRepository.save(audit);
            log.info("AUDIT | {} | {}", eventType, message);
        } catch (Exception ex) {
            log.error("Failed to save audit log", ex);
        }
    }
}
