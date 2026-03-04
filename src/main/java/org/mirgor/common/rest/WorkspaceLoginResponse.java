package org.mirgor.common.rest;

public record WorkspaceLoginResponse(
        String token,
        String refreshToken
) {
}
