package btremote999.geofencetest.data;

import com.google.android.gms.location.Geofence;
import com.google.gson.annotations.Expose;

public class MyGeoFenceData {
    public int id;
    @Expose public String name;
    @Expose public Double lat;  // longitude
    @Expose public Double lng;    // latitude
    @Expose public MyArea surface;

    private Geofence mGeofence;  // actual android's geofence object

    public Geofence getGeofence() {
        if(mGeofence == null){
            MyCircle circle = (MyCircle) surface;
            float fRadius = circle.radius;
            mGeofence = new Geofence.Builder()
                    .setRequestId(String.valueOf(id))
                    .setCircularRegion(lat, lng, fRadius)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setLoiteringDelay(1000)
                    .build();
        }

        return mGeofence;
    }
}
