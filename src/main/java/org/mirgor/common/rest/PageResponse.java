package org.mirgor.common.rest;

import java.util.List;

public record PageResponse<T>(
        List<T> data,
        int totalPages,
        long totalElements,
        boolean hasNext
) {
}
