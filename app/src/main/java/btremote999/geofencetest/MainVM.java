package btremote999.geofencetest;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.location.Location;

public class MainVM extends ViewModel {
    MutableLiveData<Integer> mState = new MutableLiveData<>();
    MutableLiveData<Location> mLocation = new MutableLiveData<>();

    // Working State
    MutableLiveData<Boolean> mMockLocation = new MutableLiveData<>();

    private boolean mIsMapReady = false;
    boolean mMonitoringLocationUpdate = false;

    public MainVM(){
        mMockLocation.setValue(false);
        mState.setValue(0);
    }

    public boolean isMapReady(){
        return mIsMapReady;
    }

    public void setMapReady(boolean ready){
        mIsMapReady = ready;
        // check for the
    }


}
