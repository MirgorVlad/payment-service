package org.mirgor.dto.workspace;

public record WorkspacePingRequest(
        String login,
        String password
) {
}
