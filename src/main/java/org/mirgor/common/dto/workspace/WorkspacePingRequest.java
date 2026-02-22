package org.mirgor.common.dto.workspace;

public record WorkspacePingRequest(
        String username,
        String password
) {
}
