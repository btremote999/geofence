package btremote999.geofencetest;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import btremote999.geofencetest.data.MyCircle;
import btremote999.geofencetest.data.MyGeoFenceData;
import btremote999.geofencetest.utils.Common;


/**
 * Created by kklow on 9/20/16.
 */
public class MapController {

    private static final String TAG = MapController.class.getSimpleName();
    private final int SURFACE_COLOR = 0x22008577;
    private final GoogleMap map;
    private Marker mSelf;

    private SparseArrayCompat<Marker> mMarkerArray = new SparseArrayCompat<>();
    private SparseArrayCompat<Object> mSurfaceArray = new SparseArrayCompat<>();
    private final MutableLiveData<MyGeoFenceData> mSelectedMarker;

    // flag for check the map was init or not.
    public boolean mWasInit;

    public MapController(MainActivity mainActivity, GoogleMap map, MutableLiveData<MyGeoFenceData> selectedMarkerLiveData) {
        MainActivity mainActivity1 = mainActivity;
        this.map = map;
        this.mSelectedMarker = selectedMarkerLiveData;
    }

    /**
     *
     * @param centerLocation
     */
    @SuppressLint("MissingPermission")
    public void init(Location centerLocation) {
        // init flag update
        mWasInit = true;

        // Google Map configuration
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setIndoorEnabled(true);
        map.setMaxZoomPreference(20.0f);
        map.setMinZoomPreference(2.0f);
        map.setBuildingsEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        map.setMyLocationEnabled(true);
        map.setPadding(0,Common.dpToPx(24),Common.dpToPx(20), Common.dpToPx(80));
        map.setOnMarkerClickListener(new MyOnMarkerClickListener());

        updateSelf(centerLocation);
        map.moveCamera(CameraUpdateFactory.zoomTo(18.5f));
        map.moveCamera(CameraUpdateFactory.newLatLng(mSelf.getPosition()));
    }



    void updateSelf(@NonNull Location location) {
//        Logger.d(TAG, "updateSelf: (%.6f, %.6f)", latLng.latitude, latLng.longitude);
        if (this.mSelf == null) {
            this.mSelf = map.addMarker(
                    new MarkerOptions()
                            .position(toLatLng(location)));
            this.mSelf.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_man));
        } else {
            this.mSelf.setPosition(toLatLng(location));
        }
    }

    private LatLng toLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }


    void addGeoFence(MyGeoFenceData myGeoFenceData) {
        LatLng latLng = new LatLng(myGeoFenceData.lat, myGeoFenceData.lng);

        // create marker
        Marker marker = map.addMarker(
                new MarkerOptions()
                        .position(latLng)
                        .title(myGeoFenceData.name)
        );
        marker.setTitle(myGeoFenceData.name);
        marker.setTag(myGeoFenceData);

        // create surface
        addSurface(myGeoFenceData, latLng);


        this.mMarkerArray.put(myGeoFenceData.id, marker);


    }

    private void addSurface(MyGeoFenceData myGeoFenceData, LatLng latLng) {
        Object surface = null;
        if(myGeoFenceData.surface instanceof MyCircle){
            // create circle
            surface = map.addCircle(
                    new CircleOptions()
                            .center(latLng)
                            .strokeWidth(1.0f)
                            .fillColor(SURFACE_COLOR)
                            .radius(((MyCircle) myGeoFenceData.surface).radius)
            );
        }

        if(surface != null)
            this.mSurfaceArray.put(myGeoFenceData.id, surface);

    }


    private <T> Marker addMarker(LatLng loc, int resId, T tag) {
        Marker marker = map.addMarker(
                new MarkerOptions().position(loc));
        // set custom marker
        marker.setIcon(BitmapDescriptorFactory.fromResource(resId));

        // set tag
        marker.setTag(tag);

        return marker;
    }


    private class MyOnMarkerClickListener implements GoogleMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            Object obj = marker.getTag();
            if(obj instanceof MyGeoFenceData) {
                MyGeoFenceData fenceData = (MyGeoFenceData) obj;
                mSelectedMarker.setValue(fenceData);
            }else{
                // possible select self
                mSelectedMarker.setValue(null);
            }


            return false;
        }
    }

    void removeMarker(@NonNull MyGeoFenceData target) {
        int id = target.id;
        Marker marker = this.mMarkerArray.get(id);
        Object surface= this.mSurfaceArray.get(id);

        if(marker != null) {
            this.mMarkerArray.remove(id);
            marker.remove();
        }
        if(surface instanceof Circle){
            this.mSurfaceArray.remove(id);
            Circle circle = (Circle) surface;
            circle.remove();
        }

    }
}
