package org.example.exception;

import org.example.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, WebRequest request,
                                             List<ErrorResponse.FieldError> fieldErrors, Exception ex) {
        String path = (request instanceof ServletWebRequest)
                ? ((ServletWebRequest) request).getRequest().getRequestURI()
                : "N/A";
        String traceId = UUID.randomUUID().toString();

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .traceId(traceId)
                .errors(fieldErrors == null ? Collections.emptyList() : fieldErrors)
                .service("TaskFodge") // could be injected via config
                .build();

        // Logging strategy
        if (status.is5xxServerError()) {
            log.error("TraceID={} | Path={} | Status={} {} | Exception={} | Message={}",
                    traceId, path, status.value(), status.getReasonPhrase(),
                    ex.getClass().getSimpleName(), message, ex);
        } else if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN || status == HttpStatus.CONFLICT) {
            log.warn("TraceID={} | Path={} | Status={} {} | Exception={} | Message={}",
                    traceId, path, status.value(), status.getReasonPhrase(),
                    ex.getClass().getSimpleName(), message);
        } else {
            log.debug("TraceID={} | Path={} | Status={} {} | Exception={} | Message={}",
                    traceId, path, status.value(), status.getReasonPhrase(),
                    ex.getClass().getSimpleName(), message);
        }

        return body;
    }

    // 400 - validation errors (request body)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> ErrorResponse.FieldError.builder()
                        .field(fe.getField())
                        .rejectedValue(String.valueOf(fe.getRejectedValue()))
                        .message(fe.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        ErrorResponse body = buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", request, fieldErrors, ex);
        return ResponseEntity.badRequest().body(body);
    }

    // 400 - Constraint violations (query params / path vars)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        var fieldErrors = ex.getConstraintViolations().stream()
                .map(cv -> ErrorResponse.FieldError.builder()
                        .field(cv.getPropertyPath().toString())
                        .rejectedValue(String.valueOf(cv.getInvalidValue()))
                        .message(cv.getMessage())
                        .build())
                .collect(Collectors.toList());

        ErrorResponse body = buildErrorResponse(HttpStatus.BAD_REQUEST, "Constraint violation", request, fieldErrors, ex);
        return ResponseEntity.badRequest().body(body);
    }

    // 400 - malformed JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(HttpMessageNotReadableException ex, WebRequest request) {
        ErrorResponse body = buildErrorResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request", request, null, ex);
        return ResponseEntity.badRequest().body(body);
    }

    // 401 - unauthorized
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex, WebRequest request) {
        ErrorResponse body = buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request, null, ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // 403 - access denied
    @ExceptionHandler({AccessDeniedException.class, ForbiddenException.class})
    public ResponseEntity<ErrorResponse> handleForbidden(Exception ex, WebRequest request) {
        ErrorResponse body = buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request, null, ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // 404 - resource not found
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, WebRequest request) {
        ErrorResponse body = buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null, ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // 409 - conflict
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex, WebRequest request) {
        ErrorResponse body = buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request, null, ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // 409 - DB constraint violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, WebRequest request) {
        ErrorResponse body = buildErrorResponse(HttpStatus.CONFLICT, "Database error", request, null, ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // 405 - method not allowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        ErrorResponse body = buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), request, null, ex);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }

    // 500 - fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, WebRequest request) {
        ErrorResponse body = buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request, null, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
