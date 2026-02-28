package org.mirgor.common.dto.rest;

public record WorkspaceLoginResponse(
        String token,
        String refreshToken
) {
}
