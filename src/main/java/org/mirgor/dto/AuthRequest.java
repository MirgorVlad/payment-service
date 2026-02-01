package org.mirgor.dto;

import jakarta.validation.constraints.NotNull;

public record AuthRequest(
        @NotNull
        String email,
        @NotNull
        String password
) {
}
