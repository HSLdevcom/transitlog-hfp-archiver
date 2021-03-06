package fi.hsl.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateTest {
    public static final String yyyy_MM_dd = "\\d+-\\d{2}-\\d{2}$";
    private Date date;

    @BeforeEach
    void setUp() {
        this.date = new Date();
    }

    @Test
    void todaymidnight() {
        Instant todayMidnight = Date.todaymidnight();
        verifyDateEquals(todayMidnight, Instant.now());
        verifyMidnight(todayMidnight);

    }

    private void verifyDateEquals(Instant actual, Instant required) {
        OffsetDateTime utcTime = required.atZone(ZoneOffset.UTC).toOffsetDateTime();
        int year = utcTime.getYear();
        int month = utcTime.getMonthValue();
        int day = utcTime.getDayOfMonth();
        assertEquals(day, actual.atZone(ZoneOffset.UTC).getDayOfMonth());
        assertEquals(month, actual.atZone(ZoneOffset.UTC).getMonthValue());
        assertEquals(year, actual.atZone(ZoneOffset.UTC).getYear());
    }

    private void verifyMidnight(Instant instant) {
        assertEquals(0, instant.atZone(ZoneOffset.UTC).getHour());
        assertEquals(0, instant.atZone(ZoneOffset.UTC).getMinute());
        assertEquals(0, instant.atZone(ZoneOffset.UTC).getSecond());
    }

    @Test
    void yesterdaymidnight() {
        Instant yesterdaymidnight = Date.yesterdaymidnight();
        verifyMidnight(yesterdaymidnight);
        verifyDateEquals(yesterdaymidnight, Instant.now().minus(1, ChronoUnit.DAYS));
    }

    @Test
    void todayAsDate() {
        String formattedDate = Date.today_year_month_day();
        Pattern dateFormat = Pattern.compile(yyyy_MM_dd);
        Matcher matcher = dateFormat.matcher(formattedDate);
        assertTrue(matcher.matches());

    }
}