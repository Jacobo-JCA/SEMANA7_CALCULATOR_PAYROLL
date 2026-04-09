package com.sofkau.auth.dto;

public record LoginResponse(
    String accessToken,
    String tokenType,
    Long userId,
    String username,
    String role
) {
    public LoginResponse(String accessToken, Long userId, String username, String role) {
        this(accessToken, "Bearer", userId, username, role);
    }
}
