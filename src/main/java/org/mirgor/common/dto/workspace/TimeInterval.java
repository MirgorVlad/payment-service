package org.mirgor.common.dto.workspace;

import java.time.LocalDateTime;

public record TimeInterval(
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
