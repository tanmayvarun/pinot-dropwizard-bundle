package io.dropwizard.pinot.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.Calendar;
import java.util.Date;

@UtilityClass
public final class DateUtils {

    public static final String IST_ZONE = "Asia/Calcutta";
    public static final ZoneId IST_ZONE_ID = ZoneId.of(IST_ZONE);
    public static final int SECONDS_IN_DAY = 86400;

    public static int weekOfYear() {
        ZonedDateTime now = ZonedDateTime.now(IST_ZONE_ID);
        return now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }

    public int getCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(Calendar.MONTH);
    }

    public int getCurrentDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    public static Date currentTime() {
        return new Date(System.currentTimeMillis());
    }

    private static Date addSecondsToCurrentTime(long seconds, boolean add) {
        int multiplier = 1;
        if (!add) {
            multiplier = -1;
        }
        long newTime = System.currentTimeMillis() + multiplier * seconds * 1000;
        return new Date(newTime);
    }

    public static Date addSecondsToCurrentTime(long seconds) {
        return addSecondsToCurrentTime(seconds, true);
    }

    public static Date addSecondsToTimestamp(Date dateTime, long seconds) {
        return addSecondsToTimestampInternal(dateTime, seconds, true);
    }

    public static Date subtractSecondsFromTimestamp(Date dateTime, long seconds) {
        return addSecondsToTimestampInternal(dateTime, seconds, false);
    }

    private static Date addSecondsToTimestampInternal(Date dateTime, long seconds, boolean add) {
        int multiplier = 1;

        if (!add) {
            multiplier = -1;
        }

        long newTime = dateTime.getTime() + multiplier * seconds * 1000;

        return new Date(newTime);
    }

    public static Date subtractSecondsFromCurrentTime(long seconds) {
        return addSecondsToCurrentTime(seconds, false);
    }

    public static long timeDifferenceInSeconds(Date beforeTime, Date afterTime) {
        if (afterTime.before(beforeTime)) {
            throw new IllegalArgumentException("After time is less than before time");
        }
        return (afterTime.getTime() - beforeTime.getTime()) / 1000;
    }

    public static Date addDaysToCurrentTime(long days) {
        long seconds = days * SECONDS_IN_DAY;
        return addSecondsToCurrentTime(seconds, true);
    }

    public static Date subtractDaysFromCurrentTime(long days) {
        long seconds = days * SECONDS_IN_DAY;
        return addSecondsToCurrentTime(seconds, false);
    }

    public static String formatLocalDate(DateTimeFormatter dateTimeFormatter, LocalDate localDate) {
        return dateTimeFormatter.format(localDate);
    }

    public static String formatDate(DateTimeFormatter dateTimeFormatter, Date date) {
        return dateTimeFormatter.format(fromDate(date));
    }

    public static LocalDate fromDate(Date date) {
        return LocalDate.from(date.toInstant().atZone(IST_ZONE_ID));
    }

    public static int currentYearMonth() {
        ZonedDateTime dateTime = ZonedDateTime.now(IST_ZONE_ID);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        return Integer.parseInt(dateTime.format(formatter));
    }

    /**
     * 1970 is considered as time ZERO
     * Rules:
     * 1. Non negative value of timestamp
     * 2. time is newer than last 30 years.
     * @param timestamp
     * @return
     */
    public static boolean looksLikeValidTimestamp(long timestamp) {
        return timestamp > 0 && new Date(timestamp).after(subtractDaysFromCurrentTime(365 * 30));
    }
}
