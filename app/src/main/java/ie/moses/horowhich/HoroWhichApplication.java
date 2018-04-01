package ie.moses.horowhich;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;

public class HoroWhichApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }
    }
}
