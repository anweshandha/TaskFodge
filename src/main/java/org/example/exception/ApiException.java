package org.example.exception;

public abstract class ApiException extends RuntimeException {
    public ApiException(String message) { super(message); }
    public ApiException(String message, Throwable cause) { super(message, cause); }
}
