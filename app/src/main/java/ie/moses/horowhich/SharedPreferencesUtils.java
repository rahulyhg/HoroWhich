package ie.moses.horowhich;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

public final class SharedPreferencesUtils {

    private static final String HOROSCOPE_PREFS = "HOROSCOPE_PREFS";

    private static final String TODAYS_HOROSCOPE_SAVE_TIME_KEY = "TODAYS_HOROSCOPE_SAVE_TIME_KEY";

    private static final String TODAYS_HOROSCOPE_CREATION_TIME_KEY = "TODAYS_HOROSCOPE_CREATION_TIME_KEY";
    private static final String TODAYS_HOROSCOPE_TEXT_KEY = "TODAYS_HOROSCOPE_TEXT_KEY";

    private SharedPreferencesUtils() {
    }

    public static boolean hasTodaysHoroscope(Context context) {
        return getTodaysHoroscope(context) != null;
    }

    @Nullable
    public static Horoscope getTodaysHoroscope(Context context) {
        SharedPreferences horoscopePrefs = getHoroscopePrefs(context);
        long creationTime = horoscopePrefs.getLong(TODAYS_HOROSCOPE_CREATION_TIME_KEY, -1);
        @Nullable String horoscopeText = horoscopePrefs.getString(TODAYS_HOROSCOPE_TEXT_KEY, null);

        /**
         * TODO: Need to do something when not in debug mode and implement XOR for this.
         * */
        if((creationTime == -1 && horoscopeText != null) || (creationTime > -1 && horoscopeText == null)) {
            if(DebugUtils.IS_IN_DEBUG_MODE) {
                throw new IllegalStateException("corrupt horoscope in shared preferences");
            }
        }

        if (creationTime > -1 && horoscopeText != null) {
            Horoscope horoscope = new Horoscope(creationTime, horoscopeText);
            long horoscopeSaveTime = horoscopePrefs.getLong(TODAYS_HOROSCOPE_SAVE_TIME_KEY, -1);
            if (horoscopeSaveTime == -1) {
                if (DebugUtils.IS_IN_DEBUG_MODE) {
                    throw new IllegalStateException("no save time for horoscope");
                } else {
                    return horoscope;
                }
            }

            if (TimeUtils.happenedToday(horoscopeSaveTime)) {
                return horoscope;
            } else {
                horoscopePrefs.edit()
                        .remove(TODAYS_HOROSCOPE_CREATION_TIME_KEY)
                        .remove(TODAYS_HOROSCOPE_TEXT_KEY)
                        .apply();
            }
        }

        return null;
    }

    /**
     * TODO: Turn that exception into a checked exception.
     * */
    @SuppressLint("ApplySharedPref")
    public static void setTodaysHoroscope(Context context, Horoscope horoscope) {
        boolean success = getHoroscopePrefs(context).edit()
                .putLong(TODAYS_HOROSCOPE_SAVE_TIME_KEY, System.currentTimeMillis())
                .putLong(TODAYS_HOROSCOPE_CREATION_TIME_KEY, horoscope.getCreationTimeMillis())
                .putString(TODAYS_HOROSCOPE_TEXT_KEY, horoscope.getText())
                .commit();

        if(!success) throw new RuntimeException("horoscope not saved to shared preference");
    }

    private static SharedPreferences getHoroscopePrefs(Context context) {
        return context.getSharedPreferences(HOROSCOPE_PREFS, Context.MODE_PRIVATE);
    }

    @SuppressLint("ApplySharedPref")
    public static void clear(Context context) {
        getHoroscopePrefs(context).edit().clear().commit();
    }

}
