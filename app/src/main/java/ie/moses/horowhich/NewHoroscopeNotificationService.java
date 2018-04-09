package ie.moses.horowhich;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.facebook.Profile;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class NewHoroscopeNotificationService extends Service {

    private static final String TAG = NewHoroscopeNotificationService.class.getSimpleName();

    public static final String HAS_NEW_HOROSCOPE = "HAS_NEW_HOROSCOPE";
    public static final String NEW_HOROSCOPE = "NEW_HOROSCOPE";

    private DatabaseReference _horoscopeReference;
    private List<Horoscope> _horoscopes;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Profile.getCurrentProfile() != null) {
            _horoscopeReference = FirebaseUtils.getHoroscopesDatabaseReference(Profile.getCurrentProfile().getId());
            _horoscopeReference.addValueEventListener(new HoroscopesValueEventListener() {

                boolean initialDataLoaded;

                @Override
                public void onDataChanged(List<Horoscope> horoscopes) {
                    if (!initialDataLoaded) {
                        _horoscopes = horoscopes;
                        initialDataLoaded = true;
                    } else {
                        if (horoscopes.size() > _horoscopes.size()) {
                            showNotification("New Horoscope!", horoscopes.get(horoscopes.size() - 1).getText());
                        }

                        _horoscopes = horoscopes;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "database error", databaseError.toException());
                }
            });
        }
    }

    /**
     * TODO: Should be moved to util class.
     * */
    private void showNotification(String title, String content) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }else if (DebugUtils.DEBUG_MODE){
                throw new IllegalStateException("notifi");
            }
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.drawable.leo_icon_light) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(content)// message for notification
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) // set alarm sound for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        notificationManager.notify(0, mBuilder.build());
    }
}
