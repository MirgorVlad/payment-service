package org.mirgor.dto;

public record AuthRequest(
        String email,
        String password
) {
}
