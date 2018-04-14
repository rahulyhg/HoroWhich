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
import com.facebook.ProfileTracker;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class NotificationService extends Service {

    private static final String TAG = NotificationService.class.getCanonicalName();

    /**
     * TODO: Can this field be local variable? Will it get garbage collected?
     * */
    private DatabaseReference _horoscopeReference;
    private List<Horoscope> _horoscopes;

    private boolean _serviceStarted;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Notification service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Profile.getCurrentProfile() != null) {
            if(_serviceStarted) {
                Log.w(TAG, "notification service already started...");
                return super.onStartCommand(intent, flags, startId);
            }else {
                Log.i(TAG, "notification service started");
                _serviceStarted = true;
            }

            _horoscopeReference = FirebaseUtils.getHoroscopesDatabaseReference(Profile.getCurrentProfile().getId());
            _horoscopeReference.addValueEventListener(new HoroscopesValueEventListener() {

                boolean initialDataLoaded;

                @Override
                public void onDataChanged(List<Horoscope> horoscopes) {
                    if (!initialDataLoaded) {
                        Log.i(TAG, "initial data loaded");
                        _horoscopes = horoscopes;
                        initialDataLoaded = true;
                    } else {
                        boolean newHoroscope = horoscopes.size() > _horoscopes.size();

                        if (newHoroscope && !SharedPreferencesUtils.hasTodaysHoroscope(NotificationService.this)) {
                            Log.d(TAG, "new horoscope notification");
                            showNotification("New Horoscope!", horoscopes.get(horoscopes.size() - 1).getText());
//                        }else if(!newHoroscope) {
                            /**
                             * TODO: Doesn't make sense because always appears right after "new horoscope notification"
                             * due to onDataChanged() being called a second time when the app removes that horoscope
                             * from firebase once it's fetched it. Consider making a new EventValueListener which shows
                             * if an item has been added or deleted (and the item which was added or deleted).
                             * */
//                            Log.d(TAG, "no new horoscope to display");
                        }else if(!SharedPreferencesUtils.hasTodaysHoroscope(NotificationService.this)) {
                            Log.d(TAG, "already have a horoscope for today");
                        }

                        _horoscopes = horoscopes;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "database error", databaseError.toException());
                }
            });
        }else {
            Log.w(TAG, "profile is null, fetching...");
            Profile.fetchProfileForCurrentAccessToken();
            new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                    onStartCommand(intent, flags, startId);
                }
            };
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /**
         * TODO: Not sure if this is necessary, but should
         * definitely be removing firebase listeners.
         * */
        _serviceStarted = false;
    }

    /**
     * TODO: Should be moved to util class.
     */
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
            } else if (DebugUtils.DEBUG_MODE) {
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
