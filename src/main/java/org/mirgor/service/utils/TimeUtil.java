package org.mirgor.service.utils;

import org.mirgor.common.dto.TimeInterval;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;

public class TimeUtil {

    public static TimeInterval getPrevMonthRange(ZoneId zoneId) {
        var reference = LocalDateTime.now().minusHours(1);
        var yearMonth = YearMonth.from(reference);

        var startTime = yearMonth.atDay(1).atStartOfDay();
        var endTime = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        return new TimeInterval(
                startTime,
                endTime
        );
    }
}
