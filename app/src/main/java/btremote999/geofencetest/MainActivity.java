package btremote999.geofencetest;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import btremote999.geofencetest.utils.Logger;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_CODE_FOR_LOCATION = 101;
    private static final int REQUEST_CODE_FOR_SETTING = 102;
    private static final String TAG = "MainActivity";
    private SupportMapFragment mMapFragment;

    private FusedLocationProviderClient mFusedLocationClient;
    private MainVM mMainVM;


    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(android.R.id.content) View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // setup View Model
        setupViewModel();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_INDEFINITE)
                .setAction("Action", null).show());

        // setup  google map
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mMapFragment != null)
            mMapFragment.getMapAsync(this);

        // setup location callback
        mLocationCallback = new MyLocationCallback();

        mMainVM.mState.setValue(StateFlow.CHECK_PERMISSION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        monitorLocationUpdate();
    }


    @Override
    protected void onPause() {
        unMonitorLocationUpdate();
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case REQUEST_CODE_FOR_SETTING:
                // re-check location status
                mMainVM.mState.setValue(StateFlow.CHECK_PERMISSION);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void setupViewModel() {
        mMainVM = ViewModelProviders.of(this).get(MainVM.class);

        mMainVM.mState.observe(this, this::onStateChanged);
        mMainVM.mLocation.observe(this, this::onLocationChanged);
    }

    private void onStateChanged(@NonNull Integer state) {
        Logger.d(TAG, "onStateChanged: %s", StateFlow.toString(state));
        switch(state){
            // app starting
            case StateFlow.CHECK_PERMISSION:
                checkLocationPermission();
                break;
            case StateFlow.GRANT_PERMISSION:
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FOR_LOCATION);
                break;
            case StateFlow.SHOW_REQUEST_PERMISSION_RATIONALE:
                showRequestPermissionRationale();
                break;
            case StateFlow.GRANT_PERMISSION_DENIED_PERMANENTLY:
                // Handle DO NOT ASK AGAIN
                Snackbar snackbar = Snackbar.make(mContentView, R.string.require_location_permission, Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.setting, view -> requireUserGoToSetting());
                snackbar.show();
                break;
            // permission granted
            case StateFlow.PERMISSION_GRANTED:
                // all set
                if(mMainVM.isMapReady()) {
                    Logger.d(TAG, "onStateChanged: map ready -> app ready");
                    mMainVM.mState.setValue(StateFlow.APP_READY);
                }
                break;

            case StateFlow.APP_READY:
                getLastKnownLocation();
                monitorLocationUpdate();
                break;

        }
    }

    private void showRequestPermissionRationale() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.require_rationale_permission_title)
                .setMessage(R.string.require_rationale_permission_message)
                .setPositiveButton(android.R.string.yes, (dlg, i) -> {
                    dlg.dismiss();
                    mMainVM.mState.setValue(StateFlow.GRANT_PERMISSION);
                })
                .setNegativeButton(android.R.string.no, (dlg, i) -> {
                    dlg.dismiss();
                    finish();
                })
                .show();
    }

    // region Google Map
    private MapController mMapController;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Logger.d(TAG, "onMapReady: ");
        mMapController = new MapController(this, googleMap);

        Location location = mMainVM.mLocation.getValue();
        if (location != null) {
            if (mMapController.mWasInit)
                mMapController.updateSelf(location);
            else
                mMapController.init(location);
        }

        googleMap.setOnMapClickListener(this::onMapClicked);
        mMainVM.setMapReady(true);

        //noinspection ConstantConditions
        int state = mMainVM.mState.getValue();
        if(state >= StateFlow.PERMISSION_GRANTED){
            Logger.d(TAG, "onMapReady: permission granted -> app ready");
            mMainVM.mState.setValue(StateFlow.APP_READY);
        }
    }

    private void onMapClicked(LatLng latLng) {
        // Validation for Mock Location
        if (BuildConfig.DEBUG) {
            Location location = new Location("mock");
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);
            mMainVM.mLocation.setValue(location);
        }
    }

    // endregion Google Map

    // region Permission
    boolean mAskedPermission = false;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMainVM.mState.setValue(StateFlow.PERMISSION_GRANTED);
            return;
        } else if (!mAskedPermission) {
            mAskedPermission = true;
            mMainVM.mState.setValue(StateFlow.GRANT_PERMISSION);
        } else {
            // Wait for permission result
            boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
            if (!showRationale) {
                mMainVM.mState.setValue(StateFlow.GRANT_PERMISSION_DENIED_PERMANENTLY);
            } else {
                mMainVM.mState.setValue(StateFlow.SHOW_REQUEST_PERMISSION_RATIONALE);

            }
        }
    }

    private void requireUserGoToSetting() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + this.getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        this.startActivityForResult(myAppSettings, REQUEST_CODE_FOR_SETTING);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_FOR_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO: get last known location
                Logger.w(TAG, "onRequestPermissionsResult: granted ->proceed");
                mMainVM.mState.setValue(StateFlow.PERMISSION_GRANTED);
            } else {
                boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
                if (!showRationale) {
                    mMainVM.mState.setValue(StateFlow.GRANT_PERMISSION_DENIED_PERMANENTLY);
//                    // Handle DO NOT ASK AGAIN
//                    Snackbar snackbar = Snackbar.make(mContentView, R.string.permission_rationale_title, Snackbar.LENGTH_INDEFINITE);
//                    snackbar.setAction(R.string.setting, view -> requireUserGoToSetting());
//                    snackbar.show();
                } else {
//                    new AlertDialog.Builder(this)
//                            .setTitle(R.string.require_location_permission)
//                            .setMessage(R.string.location_permission_denied_message)
//                            .setPositiveButton(android.R.string.yes, (dialog, i) -> {
//                                dialog.dismiss();
//                                finish();
//                            })
//                            .setNegativeButton(android.R.string.no, (dialog, i) -> {
//                                dialog.dismiss();
//
//                                new Handler(Looper.getMainLooper()).post(() -> {
//                                    // reset
//                                    mAskedPermission = false;
//                                    checkLocationPermission();
//                                });
//                            })
//                            .show();
                    mMainVM.mState.setValue(StateFlow.SHOW_REQUEST_PERMISSION_RATIONALE);
                    Logger.i(TAG, "onRequestPermissionsResult: denied");
                }
            }
        }
    }
    // endregion Permission

    // region Location Monitor
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    /**
     * request for last know location
     */
    private void getLastKnownLocation() {
        if (mFusedLocationClient == null)
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null)
                        mMainVM.mLocation.setValue(location);
                    else
                        Logger.w(TAG, "getLastKnownLocation: location is null -> failed to get location");
                });

        monitorLocationUpdate();
    }


    private void onLocationChanged(Location location) {
        if (location == null) {
            // TODO: show message ? retry ?
        } else {
            Logger.i(TAG, "onLocationChanged: %s", location);
            if (mMapController != null) {
                // setup last location on the map ?
                if (mMapController.mWasInit)
                    mMapController.updateSelf(location);
                else
                    mMapController.init(location);
            }
        }
    }



    private void monitorLocationUpdate() {
        // add location update
        if (mFusedLocationClient != null) {
            Logger.d(TAG, "monitorLocationUpdate: ");

            // create location request if needed
            if (mLocationRequest == null) {
                mLocationRequest = new LocationRequest();

                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            }


            //noinspection ConstantConditions
            if (mMainVM.mState.getValue() >= StateFlow.PERMISSION_GRANTED){
                if(!mMainVM.mMonitoringLocationUpdate) {
                    mMainVM.mMonitoringLocationUpdate = true;
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                            mLocationCallback,
                            null);
                }
            }else{
                Logger.d(TAG, "monitorLocationUpdate: permission not granted -> skip");
            }
        }
    }

    private void unMonitorLocationUpdate() {
        Logger.d(TAG, "unMonitorLocationUpdate: ");
        // remove location update
        if (mFusedLocationClient != null)
            if (mMainVM.mMonitoringLocationUpdate) {
                mMainVM.mMonitoringLocationUpdate = false;
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }
    }

    // endregion Location Monitor

    private class MyLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Boolean mockLocation = mMainVM.mMockLocation.getValue();

            // skip actual location if mock location activated
            if (mockLocation != null && mockLocation == true) {
                Logger.d(TAG, "onLocationResult: mock location -> abort");
                return;
            }

            if (locationResult == null) {
                Logger.d(TAG, "onLocationResult: null -> abort");
                return;
            }

            // get more recent location
            Location location = locationResult.getLastLocation();
            if (location != null) {
                Logger.d(TAG, "onLocationResult: %s", location);
                mMainVM.mLocation.setValue(location);
            } else {
                Logger.d(TAG, "onLocationResult: no location");
            }
        }
    }




}
