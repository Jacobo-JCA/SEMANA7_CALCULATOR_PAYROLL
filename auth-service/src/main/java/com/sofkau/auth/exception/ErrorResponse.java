package com.sofkau.auth.exception;

public record ErrorResponse(
        int status,
        String message,
        long timestamp
) {
    public ErrorResponse(int status, String message) {
        this(status, message, System.currentTimeMillis());
    }
}
