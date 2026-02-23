package org.mirgor.common.dto.rest;

public record EntityRequestContext(
        String host,
        String path,
        String token
) {
}
