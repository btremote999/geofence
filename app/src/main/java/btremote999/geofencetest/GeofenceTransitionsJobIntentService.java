package btremote999.geofencetest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import btremote999.geofencetest.utils.Logger;

// refer: https://github.com/googlesamples/android-play-location/blob/master/Geofencing/app/src/main/java/com/google/android/gms/location/sample/geofencing/GeofenceTransitionsJobIntentService.java
public class GeofenceTransitionsJobIntentService extends JobIntentService {
    private static final String TAG = "GeofenceTransitionsJobIntentService";

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Logger.w(TAG, "onHandleWork: received");

    }

    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Logger.e(TAG, "onHandleIntent: haseError: ErrorCode:%d ", geofencingEvent.getErrorCode());
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
//            String geofenceTransitionDetails = getGeofenceTransitionDetails(
//                    this,
//                    geofenceTransition,
//                    triggeringGeofences
//            );

            // Send notification and log the transition details.
//            sendNotification(geofenceTransitionDetails);
//            Log.i(TAG, geofenceTransitionDetails);
            // TODO: 07/01/2019 send Intent to main activity

        } else {
            // Log the error.
            Logger.w(TAG, "onHandleIntent: invalid geofencingTransition type: %d", geofenceTransition);
        }
    }
}