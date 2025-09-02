package org.example.exception;

// package org.example.exception;

import org.example.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, WebRequest request, List<ErrorResponse.FieldError> fieldErrors) {
        String path = (request instanceof ServletWebRequest) ? ((ServletWebRequest) request).getRequest().getRequestURI() : null;
        String traceId = UUID.randomUUID().toString();

        ErrorResponse body = new ErrorResponse();
        body.setTimestamp(Instant.now());
        body.setStatus(status.value());
        body.setError(status.getReasonPhrase());
        body.setMessage(message);
        body.setPath(path);
        body.setTraceId(traceId);
        body.setErrors(fieldErrors == null ? Collections.emptyList() : fieldErrors);

        // Log the error with traceId for operator debugging
        log.error("Error ({}): {} {} - {}", traceId, status.value(), status.getReasonPhrase(), message);

        return body;
    }

    // 400 - validation errors from @Valid in controller (request body)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> {
                    ErrorResponse.FieldError e = new ErrorResponse.FieldError();
                    e.setField(fe.getField());
                    e.setRejectedValue(String.valueOf(fe.getRejectedValue()));
                    e.setMessage(fe.getDefaultMessage());
                    return e;
                }).collect(Collectors.toList());

        ErrorResponse body = buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", request, fieldErrors);
        log.debug("Validation errors: {}", fieldErrors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // 400 - Constraint violations (query params / path vars)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getConstraintViolations().stream()
                .map(cv -> {
                    ErrorResponse.FieldError e = new ErrorResponse.FieldError();
                    e.setField(cv.getPropertyPath().toString());
                    e.setRejectedValue(String.valueOf(cv.getInvalidValue()));
                    e.setMessage(cv.getMessage());
                    return e;
                }).collect(Collectors.toList());

        ErrorResponse body = buildErrorResponse(HttpStatus.BAD_REQUEST, "Constraint violation", request, fieldErrors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // 400 - malformed JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(HttpMessageNotReadableException ex, WebRequest request) {
        ErrorResponse body = buildErrorResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request", request, null);
        log.debug("Malformed request: {}", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // 401 - unauthorized (authentication)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex, WebRequest request) {
        ErrorResponse body = buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request, null);
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    // 403 - access denied
    @ExceptionHandler({ AccessDeniedException.class, ForbiddenException.class })
    public ResponseEntity<ErrorResponse> handleForbidden(Exception ex, WebRequest request) {
        ErrorResponse body = buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request, null);
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    // 404 - resource not found
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, WebRequest request) {
        ErrorResponse body = buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // 409 - conflict (DB unique constraint)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex, WebRequest request) {
        ErrorResponse body = buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request, null);
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // DB constraint violations (e.g. duplicate key) -> 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, WebRequest request) {
        ErrorResponse body = buildErrorResponse(HttpStatus.CONFLICT, "Database error", request, null);
        log.error("DataIntegrityViolation", ex);
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // 405 - method not allowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        ErrorResponse body = buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), request, null);
        return new ResponseEntity<>(body, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // Generic fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, WebRequest request) {
        ErrorResponse body = buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request, null);
        log.error("Unhandled exception", ex);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
