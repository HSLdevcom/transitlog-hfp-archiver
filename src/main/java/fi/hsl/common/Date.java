package fi.hsl.common;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Date {
    public static Timestamp yesterdayHour(Integer yesterdayHour) {
        Instant yesterday = Instant.now().minus(1, ChronoUnit.DAYS).atZone(ZoneOffset.UTC)
                .withHour(yesterdayHour).toInstant();
        return Timestamp.from(yesterday);
    }

    public static Instant yesterdaymidnight() {
        return Instant.now().minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
    }

    public static String today_year_month_day() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);
        Instant now = Instant.now();
        return dateTimeFormatter.format(now);
    }

    public static Instant todaymidnight() {
        return Instant.now().truncatedTo(ChronoUnit.DAYS);
    }
}
