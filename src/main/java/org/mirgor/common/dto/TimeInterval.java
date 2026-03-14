package org.mirgor.common.dto;

import java.time.LocalDateTime;

public record TimeInterval(
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
