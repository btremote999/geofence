package btremote999.geofencetest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.GeofencingEvent;

import btremote999.geofencetest.utils.Logger;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = BroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "onReceive: ");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

    }
}
