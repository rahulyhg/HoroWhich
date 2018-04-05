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

    @Nullable
    public static Horoscope getTodaysHoroscope(Context context) {
        SharedPreferences horoscopePrefs = getHoroscopePrefs(context);
        long creationTime = horoscopePrefs.getLong(TODAYS_HOROSCOPE_CREATION_TIME_KEY, -1);
        @Nullable String horoscopeText = horoscopePrefs.getString(TODAYS_HOROSCOPE_TEXT_KEY, null);
        if (creationTime > -1 && horoscopeText != null) {
            Horoscope horoscope = new Horoscope(creationTime, horoscopeText);
            long horoscopeSaveTime = horoscopePrefs.getLong(TODAYS_HOROSCOPE_SAVE_TIME_KEY, -1);
            if(horoscopeSaveTime == -1) {
                if(DebugUtils.DEBUG_MODE) {
                    throw new IllegalStateException("no save time for horoscope");
                }else {
                    return horoscope;
                }
            }

            if (TimeUtils.happenedYesterday(horoscopeSaveTime)) {
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

    @SuppressLint("ApplySharedPref")
    public static void setTodaysHoroscope(Context context, Horoscope horoscope) {
        getHoroscopePrefs(context).edit()
                .putLong(TODAYS_HOROSCOPE_SAVE_TIME_KEY, System.currentTimeMillis())
                .putLong(TODAYS_HOROSCOPE_CREATION_TIME_KEY, horoscope.getCreationTimeMillis())
                .putString(TODAYS_HOROSCOPE_TEXT_KEY, horoscope.getText())
                .commit();
    }

    private static SharedPreferences getHoroscopePrefs(Context context) {
        return context.getSharedPreferences(HOROSCOPE_PREFS, Context.MODE_PRIVATE);
    }

}
