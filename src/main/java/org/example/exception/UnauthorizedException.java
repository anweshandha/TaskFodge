package org.example.exception;

public class UnauthorizedException extends ApiException {
    public UnauthorizedException(String message) { super(message); }
}
