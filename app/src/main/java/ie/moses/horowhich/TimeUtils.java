package ie.moses.horowhich;

import org.joda.time.DateTime;

public final class TimeUtils {

    private TimeUtils() {
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
