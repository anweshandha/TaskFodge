package org.example.dto;

//package org.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Generic, extensible API error response model.
 * Inspired by RFC 7807 + enterprise extensions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // ignore null fields in JSON
public class ErrorResponse {

    /** Timestamp when the error occurred */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;

    /** HTTP status code (e.g., 404) */
    private int status;

    /** Short reason (e.g., "Not Found") */
    private String error;

    /** Human-readable message (developer or user-facing) */
    private String message;

    /** Request path */
    private String path;

    /** Unique trace identifier for debugging */
    private String traceId;

    /** Application or microservice that threw the error */
    private String service;

    /** Validation errors (field-level) */
    private List<FieldError> errors;

    /** Additional metadata (extensible for future needs) */
    private Map<String, Object> metadata;

    // Factory method for quick creation
    public static ErrorResponse of(HttpStatusLike status, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.getCode())
                .error(status.getReason())
                .message(message)
                .path(path)
                .traceId(UUID.randomUUID().toString())
                .service("TaskFodge") // can be injected from config
                .build();
    }

    /**
     * Represents a field-level validation error.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FieldError {
        private String field;         // e.g., "email"
        private String rejectedValue; // e.g., "not-an-email"
        private String message;       // e.g., "must be a valid email"
    }

    /**
     * Interface to generalize HttpStatus-like codes (so we can use custom enums too).
     */
    public interface HttpStatusLike {
        int getCode();
        String getReason();
    }
}
