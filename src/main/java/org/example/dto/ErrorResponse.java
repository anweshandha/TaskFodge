package org.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;

    private int status;
    private String error;
    private String message;
    private String path;
    private String traceId;
    private String service;
    private List<FieldError> errors;
    private Map<String, Object> metadata;

    // Factory method for internal HttpStatus-like implementation
    public static ErrorResponse of(HttpStatusLike status, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.getCode())
                .error(status.getReason())
                .message(message)
                .path(path)
                .traceId(UUID.randomUUID().toString())
                .service("TaskFodge")
                .build();
    }

    // Overload for using Spring HttpStatus directly
    public static ErrorResponse of(HttpStatus status, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .traceId(UUID.randomUUID().toString())
                .service("TaskFodge")
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FieldError {
        private String field;
        private String rejectedValue;
        private String message;
    }

    public interface HttpStatusLike {
        int getCode();
        String getReason();
    }
}