package ie.moses.horowhich;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkChangeReceiver extends SpecializedBroadcastReceiver {

    private static final String TAG = NetworkChangeReceiver.class.getSimpleName();

    private final NetworkStateChangedListener _networkStateChangedListener;

    public NetworkChangeReceiver(final NetworkStateChangedListener listener) {
        _networkStateChangedListener = listener;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            final NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            final NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            final boolean isNetworkAvailable = wifi.isAvailable() || mobile.isAvailable();
            _networkStateChangedListener.onNetworkStateChanged(isNetworkAvailable);
        } else {
            if (DebugUtils.IS_IN_DEBUG_MODE) {
                throw new RuntimeException("connectivity manager is null");
            } else {
                Log.e(TAG, "connectivity manager is null");
            }
        }
    }

    protected IntentFilter getIntentFilter() {
        return new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    }

}
