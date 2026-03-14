package org.mirgor.common.rest;

public record EntityRequestContext(
        String host,
        String path,
        String token
) {
}
