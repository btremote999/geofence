package btremote999.geofencetest;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import btremote999.geofencetest.data.MyGeoFenceData;
import btremote999.geofencetest.utils.Logger;

public class GeofenceMonitor implements OnCompleteListener<Void> {
    public interface OnMonitorStatus {
        void onMonitorStatusUpdate(int statusCode);
    }

    private static final String TAG = "GeofenceMonitor";
    private static final int BROADCAST_REQ_CODE = 201;
    private final GeofencingClient mGeofencingClient;


    private List<Geofence> mGeofenceList = new ArrayList<>();
    private boolean mIsMonitoring;
    private final OnMonitorStatus mListener;


    //Used when requesting to add or remove geofences.
    private PendingIntent mGeofencePendingIntent;
    private Context mContext;

    public GeofenceMonitor(@NonNull Context context, @NonNull OnMonitorStatus listener) {
        mContext = context;
        mGeofencingClient = LocationServices.getGeofencingClient(context);
        mListener = listener;
    }

    /**
     * Start / Restart Geofence Monitor service
     * when the GeoFenceData Changed
     */
    @SuppressLint("MissingPermission")
    private void startGeofenceMonitor() {
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this)
//            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    Logger.i(TAG, "onSuccess: ");
//                }
//            })
//        .addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                ApiException exception = (ApiException) e;
//                Logger.w(TAG, "onFailure: %d: %s",
//                        exception.getStatusCode(),
//                        exception.getCause());
//            }
//        });
        ;
    }

    /**
     * Stop Geofence Montior service
     */
    public void stopGeofenceMonitor() {
        this.mIsMonitoring = false;

        if (mGeofencePendingIntent != null)
            mGeofencingClient.removeGeofences(mGeofencePendingIntent);
    }

    /**
     * new MyGeoFence
     * Action:
     * 1. added new Geofence into the list
     * 2. update the geofence request
     *
     * @param data
     */
    public void addGeofence(MyGeoFenceData data) {
        mGeofenceList.add(data.getGeofence());
        startGeofenceMonitor();
    }

    @SuppressLint("MissingPermission")
    public void removeGeofence(MyGeoFenceData data) {
        // search and destroy
        for (Geofence gf : mGeofenceList) {
            int id = Integer.valueOf(gf.getRequestId());
            if (data.id == id) {
                // destroy it
                mGeofenceList.remove(gf);
                break;
            }
        }

        if (mGeofenceList.size() > 0)
            startGeofenceMonitor();
        else
            stopGeofenceMonitor();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER );

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
//        Intent intent = new Intent(mContext, BroadcastReceiver.class);
        Intent intent = new Intent(mContext, GeofenceTransitionsJobIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        mGeofencePendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        Logger.d(TAG, "onComplete: success=%s", task.isSuccessful());
        if (!task.isSuccessful()) {
//            Note: found status code 1000
//            GEOFENCE_NOT_AVAILABLE (code '1000')
//            https://stackoverflow.com/questions/40093750/google-geofencing-not-working-always-geofence-not-available
            Logger.w(TAG, "onComplete: not success -> api exception");
            Exception exception = task.getException();

            ApiException apiException = (ApiException) exception;
            assert apiException != null;
            mListener.onMonitorStatusUpdate(apiException.getStatusCode());
        } else {
            Logger.d(TAG, "onComplete: success");
            mListener.onMonitorStatusUpdate(LocationStatusCodes.SUCCESS);
        }

    }


}
