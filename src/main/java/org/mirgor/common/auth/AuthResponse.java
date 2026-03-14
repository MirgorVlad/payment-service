package org.mirgor.common.auth;

public record AuthResponse(
        String token,
        String role
) {
}
