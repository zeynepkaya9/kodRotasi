package com.jsy.platform.auth.api;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String displayName,
        String email
) {
}
