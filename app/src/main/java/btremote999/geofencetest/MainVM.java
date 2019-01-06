package btremote999.geofencetest;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.location.Location;
import android.support.v4.util.SparseArrayCompat;

import btremote999.geofencetest.data.MyGeoFenceData;

public class MainVM extends ViewModel {
    // App State
    MutableLiveData<Integer> mState = new MutableLiveData<>();

    // Geo Fence Map Add/Edit State
    MutableLiveData<Integer> mGeofenceEditState = new MutableLiveData<>();
    MyGeoFenceData mDialogData;
    SparseArrayCompat<MyGeoFenceData> mMyGeoFenceDataList = new SparseArrayCompat<>();

    MutableLiveData<Location> mLocation = new MutableLiveData<>();

    // Working State
    MapController mMapController;
    MutableLiveData<Boolean> mMockLocation = new MutableLiveData<>();

    // Dialog Result
//    MutableLiveData<MyGeoFenceData> mAddGeoFence = new MutableLiveData<>();
//    MutableLiveData<MyGeoFenceData> mEditGeoFence = new MutableLiveData<>();

    private boolean mIsMapReady = false;
    boolean mMonitoringLocationUpdate = false;

    public MainVM(){
        mMockLocation.setValue(false);
        mState.setValue(0);
        mGeofenceEditState.setValue(0);
    }

    public boolean isMapReady(){
        return mIsMapReady;
    }

    public void setMapReady(boolean ready){
        mIsMapReady = ready;
        // check for the
    }


}
