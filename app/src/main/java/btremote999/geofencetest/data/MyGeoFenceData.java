package btremote999.geofencetest.data;

import com.google.gson.annotations.Expose;

public class MyGeoFenceData {
    public int id;
    @Expose public String name;
    @Expose public Double lat;  // longitude
    @Expose public Double lng;    // latitude
    @Expose public MyArea surface;
}
