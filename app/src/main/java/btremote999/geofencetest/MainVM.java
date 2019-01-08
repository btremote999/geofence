package btremote999.geofencetest;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.location.Location;
import android.support.v4.util.SparseArrayCompat;

import com.google.android.gms.location.Geofence;

import btremote999.geofencetest.data.MyGeoFenceData;

public class MainVM extends ViewModel {
    // App State
    MutableLiveData<Integer> mState = new MutableLiveData<>();

    // Geo Fence Map Add/Edit State
    MutableLiveData<Integer> mGeofenceEditState = new MutableLiveData<>();
    MyGeoFenceData mDialogData;

    // Set when a Geofence has been selected from  Map
    MutableLiveData<MyGeoFenceData> mSelectedGeoFence = new MutableLiveData<>();
    // Collection of GeoFenceData
    SparseArrayCompat<MyGeoFenceData> mMyGeoFenceDataList = new SparseArrayCompat<>();

    // Location Changed
    MutableLiveData<Location> mLocation = new MutableLiveData<>();

    // Working Object
    MapController mMapController;
    // Not used for the moment
    MutableLiveData<Boolean> mMockLocation = new MutableLiveData<>();

    // GeoFencing result
    MutableLiveData<Integer> mGeofenceTransition  = new MutableLiveData<>();
    MutableLiveData<Integer> mWifiNetworkState  = new MutableLiveData<>();



    private boolean mIsMapReady = false;
    boolean mMonitoringLocationUpdate = false;

    public MainVM(){
        mMockLocation.setValue(false);
        mState.setValue(0);
        mGeofenceEditState.setValue(0);
        mGeofenceTransition.setValue(Geofence.GEOFENCE_TRANSITION_EXIT);
    }

    public boolean isMapReady(){
        return mIsMapReady;
    }

    public void setMapReady(boolean ready){
        mIsMapReady = ready;
        // check for the
    }


}
