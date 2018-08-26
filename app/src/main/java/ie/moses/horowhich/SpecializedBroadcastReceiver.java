package ie.moses.horowhich;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

public abstract class SpecializedBroadcastReceiver extends BroadcastReceiver {

    protected abstract IntentFilter getIntentFilter();

    public void registerReceiver(final Context context) {
        registerSpecializedBroadcastReceiver(context, this);
    }

    public static void registerSpecializedBroadcastReceiver(final Context context, final SpecializedBroadcastReceiver receiver) {
        context.registerReceiver(receiver, receiver.getIntentFilter());
    }

}
