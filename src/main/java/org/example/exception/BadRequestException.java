package org.example.exception;

public class BadRequestException extends ApiException {
    public BadRequestException(String message) { super(message); }
}
