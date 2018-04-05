package ie.moses.horowhich;

import org.joda.time.DateTime;

/**
 * TODO: Duplicate code.
 * */
public final class TimeUtils {

    private TimeUtils() {
    }

    public static boolean happenedToday(final long timeMillis) {
        DateTime now = new DateTime();
        DateTime startOfToday = toStartOfDay(now);
        DateTime endOfToday = toEndOfDay(now);

        return timeMillis > startOfToday.getMillis() &&
                timeMillis < endOfToday.getMillis();
    }

    public static boolean happenedYesterday(final long timeMillis) {
        DateTime now = new DateTime();
        DateTime yesterday = now.minusDays(1);
        DateTime startOfYesterday = toStartOfDay(yesterday);
        DateTime endOfYesterday = toEndOfDay(yesterday);

        return timeMillis > startOfYesterday.getMillis() &&
                timeMillis < endOfYesterday.getMillis();
    }

    public static DateTime toStartOfDay(final DateTime dateTime) {
        return dateTime.withMillisOfDay(0);
    }

    public static DateTime toEndOfDay(final DateTime dateTime) {
        return dateTime.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withMillisOfSecond(999);
    }

}
