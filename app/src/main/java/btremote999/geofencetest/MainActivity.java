package btremote999.geofencetest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
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
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import btremote999.geofencetest.data.MyGeoFenceData;
import btremote999.geofencetest.utils.IdGenerator;
import btremote999.geofencetest.utils.Logger;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GeofenceMonitor.OnMonitorStatus {
    private enum EDeviceStatus {
        Unknown,
        In,
        Out
    }

    private static final int REQUEST_CODE_FOR_LOCATION = 101;
    private static final int REQUEST_CODE_FOR_SETTING = 102;

    private static final int FAB_ACTION_ADD = 0;  // default
    private static final int FAB_ACTION_DELETE = 1;

    private static final String TAG = "MainActivity";
    private SupportMapFragment mMapFragment;

    private FusedLocationProviderClient mFusedLocationClient;
    private GeofenceMonitor mGeofenceMonitor;
    private WifiNetworkMonitor mWifiNetworkMonitor;
    private MyWifiStatusCallback mWifiStatusCallback;

    private MainVM mMainVM;
    private int mFabAction = FAB_ACTION_ADD;


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

        mFab.setOnClickListener(view -> this.onFabClicked());

        // setup  google map
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mMapFragment != null)
            mMapFragment.getMapAsync(this);

        // setup location callback
        mLocationCallback = new MyLocationCallback();
        // setup wifi status callback
        mWifiStatusCallback = new MyWifiStatusCallback();


        // Broadcast Receiver
//        mGeofenceBroadcastReceiver = new GeofenceBroadcastReceiver();
//        registerReceiver(mGeofenceBroadcastReceiver,
//                new IntentFilter());

        mMainVM.mState.setValue(StateFlow.CHECK_PERMISSION);
    }

    private void onFabClicked() {
        Logger.d(TAG, "onFabClicked: ");
        // TODO: 06/01/2019 fab button serve 2 functions:
        //showGeoFenceEdit(mFabAction);
        if (mFabAction == FAB_ACTION_ADD) {
            mMainVM.mGeofenceEditState.setValue(StateFlow.GEO_FENCE_ADD_START);
        } else if (mFabAction == FAB_ACTION_DELETE) {
            // delete GeoFance
            MyGeoFenceData target = mMainVM.mSelectedGeoFence.getValue();
            if (target != null) {
                // remove geofence from container
                mMainVM.mMyGeoFenceDataList.remove(target.id);

                // remove geofence from map
                mMainVM.mMapController.removeMarker(target);

                // remove geofence from monitor
                mGeofenceMonitor.removeGeofence(target);

                mMainVM.mSelectedGeoFence.setValue(null);


            }
        }


    }

    private void showGeoFenceEdit() {
        // Show Dialog Fragment
        GeofenceAddDialog dlg = new GeofenceAddDialog();
        dlg.show(getSupportFragmentManager(), GeofenceAddDialog.TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        monitorLocationUpdate();

        // Wifi start monitor
        if (mWifiNetworkMonitor == null) {
            mWifiNetworkMonitor = new WifiNetworkMonitor();
        }
        mWifiNetworkMonitor.startWifiMonitor(this, mWifiStatusCallback);

    }

    @Override
    protected void onDestroy() {
        if (mGeofenceMonitor != null)
            mGeofenceMonitor.stopGeofenceMonitor();

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        unMonitorLocationUpdate();

        // Wifi stop monitor
        if (mWifiNetworkMonitor != null) {
            mWifiNetworkMonitor.stopWifiMonitor(this);
        }

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
            Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(viewIntent);
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

    @Override
    protected void onNewIntent(Intent intent) {

        if (Consts.GEOFENCE.equals(intent.getAction())) {
            Logger.d(TAG, "onNewIntent: received geofence intent");
//            Intent geofenceEvent = intent.getParcelableExtra(Consts.DATA);

            // Get the transition type.
            int geofenceTransition = intent.getIntExtra(Consts.DATA, -1);
            Logger.i(TAG, "onNewIntent: geofenceTransition = %d", geofenceTransition);
            mMainVM.mGeofenceTransition.setValue(geofenceTransition);
        } else {
            super.onNewIntent(intent);
        }
    }

    private void setupViewModel() {
        mMainVM = ViewModelProviders.of(this).get(MainVM.class);

        mMainVM.mState.observe(this, this::onStateChanged);
        mMainVM.mLocation.observe(this, this::onLocationChanged);
        mMainVM.mGeofenceEditState.observe(this, this::onGeoFenceEditStateChanged);

        // monitor selected marker
        mMainVM.mSelectedGeoFence.observe(this, this::onSelectedGeofenceChanged);

        // Geofencing changed
        mMainVM.mGeofenceTransition.observe(this, this::changeDisplayStatus);
        mMainVM.mWifiNetworkState.observe(this, this::changeDisplayStatus);
    }


    /**
     * Map's Marker has been selection changed -> change Fab Action
     * @param geoFenceData
     */
    private void onSelectedGeofenceChanged(MyGeoFenceData geoFenceData) {
        // trigger when geoFenceData change
        if (geoFenceData == null) {
            // unselected
            mFab.setImageResource(R.drawable.ic_action_add);
            mFabAction = FAB_ACTION_ADD;
        } else {
            // selected
            mFab.setImageResource(R.drawable.ic_action_delete);
            mFabAction = FAB_ACTION_DELETE;
        }

    }

    private void onStateChanged(@NonNull Integer state) {
        Logger.d(TAG, "onStateChanged: %s", StateFlow.toString(state));
        switch (state) {
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
                if (mMainVM.isMapReady()) {
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

    private void onGeoFenceEditStateChanged(Integer state) {
        if (state == null)
            return;
        Logger.d(TAG, "onGeoFenceEditState: %s", StateFlow.toString(state));
        switch (state) {
            // show the dialog for add geofence
            case StateFlow.GEO_FENCE_ADD_START:

                showGeoFenceEdit();
                break;


            case StateFlow.GEO_FENCE_PICK_LOCATION:
                Logger.d(TAG, "onGeoFenceEditState: do nothing -> waiting for user pick a location");
                // selected a location (point) from map)
                // show the dialog again
                // do nothing 
                break;

            case StateFlow.GEO_FENCE_PICK_LOCATION_DONE:
                // location selected -> update dialog data
                showGeoFenceEdit();
                break;

            case StateFlow.GEO_FENCE_ADD_COMPLETED:

                // set id
                int id = IdGenerator.newId();
                MyGeoFenceData dialogData = mMainVM.mDialogData;
                dialogData.id = id;


                // (Container) add new GeoFenceDataList
                mMainVM.mMyGeoFenceDataList.put(id, dialogData);
                // (Map) add to map marker
                mMainVM.mMapController.addGeoFence(dialogData);
                // (Geofence Monitor)
                if (mGeofenceMonitor == null)
                    mGeofenceMonitor = new GeofenceMonitor(this, this);
                mGeofenceMonitor.addGeofence(dialogData);


                // reset
                mMainVM.mDialogData = null;
                mMainVM.mGeofenceEditState.setValue(StateFlow.NONE);
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Logger.d(TAG, "onMapReady: ");
        mMainVM.mMapController = new MapController(this, googleMap, mMainVM.mSelectedGeoFence);

        Location location = mMainVM.mLocation.getValue();
        if (location != null) {
            if (mMainVM.mMapController.mWasInit)
                mMainVM.mMapController.updateSelf(location);
            else
                mMainVM.mMapController.init(location);
        }

        googleMap.setOnMapClickListener(this::onMapClicked);
        mMainVM.setMapReady(true);

        //noinspection ConstantConditions
        int state = mMainVM.mState.getValue();
        if (state >= StateFlow.PERMISSION_GRANTED) {
            Logger.d(TAG, "onMapReady: permission granted -> app ready");
            mMainVM.mState.setValue(StateFlow.APP_READY);
        }
    }

    private void onMapClicked(LatLng latLng) {
        // Validation for Mock Location
        int geoFenceState = mMainVM.mGeofenceEditState.getValue();
        if (geoFenceState == StateFlow.GEO_FENCE_PICK_LOCATION) {

            mMainVM.mDialogData.lat = latLng.latitude;
            mMainVM.mDialogData.lng = latLng.longitude;

            // update State
            mMainVM.mGeofenceEditState.setValue(StateFlow.GEO_FENCE_PICK_LOCATION_DONE);
            return;
        }

        if (mMainVM.mSelectedGeoFence.getValue() != null) {
            // unselect geo fence
            mMainVM.mSelectedGeoFence.setValue(null);
        }


        boolean allowClick = false;
        if (allowClick) {
            Location location = new Location("mock");
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);
            mMainVM.mLocation.setValue(location);
        }
    }

    // endregion Google Map

    // region Permission
    boolean mAskedPermission = false;

    @TargetApi(Build.VERSION_CODES.M)
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

    @TargetApi(Build.VERSION_CODES.M)
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
    @SuppressLint("MissingPermission")
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
        if (location != null) {
            Logger.i(TAG, "onLocationChanged: %s", location);
            if (mMainVM.mMapController != null) {
                // setup last location on the map ?
                if (mMainVM.mMapController.mWasInit)
                    mMainVM.mMapController.updateSelf(location);
                else
                    mMainVM.mMapController.init(location);
            }
        }
    }


    @SuppressLint("MissingPermission")
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
            if (mMainVM.mState.getValue() >= StateFlow.PERMISSION_GRANTED) {
                if (!mMainVM.mMonitoringLocationUpdate) {
                    mMainVM.mMonitoringLocationUpdate = true;
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                            mLocationCallback,
                            null);
                }
            } else {
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

    // region GeoFence Monitor
    //=================================================================

    /**
     * Listener when Ge
     *
     * @param statusCode
     */
    @Override
    public void onMonitorStatusUpdate(int statusCode) {
        @StringRes int msgId;
        if(statusCode == LocationStatusCodes.SUCCESS){
            msgId = R.string.geofence_add_monitor_success;
        }else {
            switch (statusCode) {
                case LocationStatusCodes.GEOFENCE_NOT_AVAILABLE:
                    msgId = R.string.geofence_not_available;
                    break;
                case LocationStatusCodes.ERROR:
                    msgId = R.string.unknown_geofence_error;
                    break;
                case LocationStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                    msgId = R.string.geofence_too_many_geofences;
                    break;
                case LocationStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    msgId = R.string.geofence_too_many_pending_intents;
                    break;
                default:
                    msgId = R.string.unknown_geofence_error;
                    break;
            }
        }
        Snackbar.make(mContentView, msgId, Snackbar.LENGTH_LONG).show();

    }

    //=================================================================
    // endregion GeoFence Monitor

    // region [Output Result ]
    //=================================================================

    @BindView(R.id.tvStatus) TextView mTvStatus;

    private void changeDisplayStatus(Integer dummy) {
        updateStatus();
    }


    // Decision check and show the current status - in / out
    // Rule1: Wifi Network connected -> in (even is outside geofence)
    // Rule2: Geofence in / out
    private void updateStatus() {
        EDeviceStatus status;
        Integer transition = mMainVM.mGeofenceTransition.getValue();
        Integer wifiStatus = mMainVM.mWifiNetworkState.getValue();

        // Rule 1: Wifi Network connected -> in (even is outside geofence)
        if (wifiStatus != null && wifiStatus == WifiNetworkMonitor.WIFI_CONNECTED) {
            status = EDeviceStatus.In;
        } else {
            // Rule 2: Geofence in / out
            if (transition == null) {
                status = EDeviceStatus.Unknown;
            } else {

                switch (transition) {
                    case Geofence.GEOFENCE_TRANSITION_ENTER:
                        status = EDeviceStatus.In;
                        break;
                    case Geofence.GEOFENCE_TRANSITION_EXIT:
                        status = EDeviceStatus.Out;
                        break;
                    default:
                        status = EDeviceStatus.Unknown;
                }
            }
        }

        mTvStatus.setText(getString(R.string.status, status));


    }
    //=================================================================
    // endregion [Output Result]


    private class MyLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Boolean mockLocation = mMainVM.mMockLocation.getValue();

            // skip actual location if mock location activated
            if (mockLocation != null && mockLocation) {
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

    public class MyWifiStatusCallback implements WifiNetworkMonitor.IWifiStatusCallback {

        @Override
        public void onWifiStateChange(int wifiState) {
            Logger.d(TAG, "onWifiStateChange: %d", wifiState);
            mMainVM.mWifiNetworkState.postValue(wifiState);
        }
    }


}
