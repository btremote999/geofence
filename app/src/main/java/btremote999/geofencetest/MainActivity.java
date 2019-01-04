package btremote999.geofencetest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import btremote999.geofencetest.utils.Logger;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_CODE_FOR_LOCATION = 101;
    private static final int REQUEST_CODE_FOR_SETTING = 102;
    private static final String TAG = "MainActivity";
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;

    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(android.R.id.content) View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_INDEFINITE)
                .setAction("Action", null).show());


        // setup  google map
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mMapFragment != null)
            mMapFragment.getMapAsync(this);

        checkLocationPermission();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
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

        switch(requestCode){
            case REQUEST_CODE_FOR_SETTING:
                checkLocationPermission();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);}
    }

    // region Google Map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Logger.d(TAG, "onMapReady: ");
        mMap = googleMap;


        // Add a marker in Sydney, Australia, and move the camera.
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    // endregion Google Map

    // region Permission
    boolean mAskedPermission = false; 
    private void checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return;
        }else if(!mAskedPermission) {
            mAskedPermission = true;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FOR_LOCATION);
        }
        else {
            // Wait for permission result
            boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
            if(!showRationale){
                // Handle DO NOT ASK AGAIN
                Snackbar snackbar = Snackbar.make(mContentView, R.string.require_location_permission, Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.setting, view-> requireUserGoToSetting());
                snackbar.show();
            }
            else{
                finish();
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
        if(requestCode == REQUEST_CODE_FOR_LOCATION){
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // TODO: get last known location
                Logger.w(TAG, "onRequestPermissionsResult: granted ->proceed");
            }
            else{
                boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
                if(!showRationale){
                    // Handle DO NOT ASK AGAIN
                    Snackbar snackbar = Snackbar.make(mContentView, R.string.permission_rationale_title, Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(R.string.setting, view-> requireUserGoToSetting());
                    snackbar.show();
                }
                else{
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.require_location_permission)
                            .setMessage(R.string.location_permission_denied_message)
                            .setPositiveButton(android.R.string.yes, (dialog, i) -> {
                                dialog.dismiss();
                                finish();
                            })
                            .setNegativeButton(android.R.string.no, (dialog, i) -> {
                                dialog.dismiss();

                                new Handler(Looper.getMainLooper()).post(()->{
                                    mAskedPermission = false;
                                    checkLocationPermission();
                                });
                            })
                            .show();
                    Logger.i(TAG, "onRequestPermissionsResult: denied");
                }
            }
        }
    }
    // endregion Permission


}
