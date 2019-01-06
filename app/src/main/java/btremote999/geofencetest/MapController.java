package btremote999.geofencetest;

import android.annotation.SuppressLint;
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

//import btremote.pokegohelper.R;
//import btremote.pokegohelper.helper.BackendTask.TaskMovePlayer;
//import btremote.pokegohelper.helper.LocationInfo;
//import btremote.pokegohelper.Utils.Logger;
//import btremote.pokegohelper.helper.PokeCore;
//import btremote.pokegohelper.helper.UtilTools;
//import btremote.pokegohelper.main.catchable.CatchableWrapper;
//import btremote.pokegohelper.main.gym.GymWrapper;
//import btremote.pokegohelper.main.pokestop.PokestopWrapper;
//import btremote.pokegohelper.storage.ImageResource;

//import com.google.android.gms.maps.model.MapStyleOptions;

/**
 * Created by kklow on 9/20/16.
 */
public class MapController {

    private static final String TAG = MapController.class.getSimpleName();
//    private final HashMap<Long, Marker> mCatchableMarker = new HashMap<>();
//    private final HashMap<String, Marker> mPokestopMarkers = new HashMap<>();
//    private final HashMap<String, Marker> mGymMarkers = new HashMap<>();
    private final int SURFACE_COLOR = 0x22008577;
    private final GoogleMap map;
    private final MainActivity mMainActivity;

    private Marker mHomeMarker;
    private Marker mDestMarker;
    private Marker mSelf;
    private Circle mSelfCircle;   // pokestop
    private Circle mSelfCircle2; // encounter
    private LatLng mLastDest;

    private SparseArrayCompat<Marker> mMarkerArray = new SparseArrayCompat<>();
    private SparseArrayCompat<Object> mSurfaceArray = new SparseArrayCompat<>();

    // flag for check the map was init or not.
    public boolean mWasInit;

    public MapController(MainActivity mainActivity, GoogleMap map) {
        this.mMainActivity = mainActivity;
        this.map = map;
    }

    // add destinal so play can MOVE !!!
    public void setDestination(LatLng dest) {
//        mLastDest = dest;
//        // 20160923: remove destination flag and polyline
//        // put destination path
//        if (mDestMarker == null)
//            this.mDestMarker = addMarker(dest, R.drawable.marker_flag_blue, null);
//        else
//            this.mDestMarker.setPosition(dest);
//
//        LatLng src = PokeCore.getInstance().mCurLocation.toLatLng();
//
//        if (this.mMoveTask != null)
//            mMoveTask.cancel();
//
//        this.mMoveTask = new TaskMovePlayer(src, dest, PokeCore.getInstance().getSpeed());
//        PokeCore.getInstance().addBackendTask(mMoveTask);


    }

    public void updateAllGyms() {
//        Logger.d(TAG, "updateAllGym");
//        List<GymWrapper> gymWrapperList = PokeCore.getInstance().mGymContainer.getAllGymWrapper();
//        synchronized (mGymMarkers) {
//            // clone for prepare removeList
//            List<String> prepareRemoveList = new ArrayList<>(mGymMarkers.size());
//            for(String key: mGymMarkers.keySet()){
//                prepareRemoveList.add(key);
//            }
//
//
//            for (GymWrapper gymWrapper : gymWrapperList) {
//                // check for existent and perform update
//                if (prepareRemoveList.contains(gymWrapper.mId)) {
//                    Marker m = mGymMarkers.get(gymWrapper.mId);
//                    // update the marker
//                    updateGymWrapper(m);
//
//                    prepareRemoveList.remove(gymWrapper.mId);
//                } else {
//                    // add  non-exist
//                    addGymWrapper(gymWrapper);
//                }
//            }
//
//            // 1. remove marker
//            // 2. remove pokestop
//            for (String key : prepareRemoveList) {
//                Marker marker = mPokestopMarkers.get(key);
//                if(marker != null)
//                    marker.remove();
//                mGymMarkers.remove(key);
//
//            }
//        }
    }

//    private void addGymWrapper(GymWrapper gymWrapper) {
//        Logger.d(TAG, "addGymWrapper: called");
//        try {
//            int resId = mImageResource.getMarkerResId(gymWrapper);
//            if(resId != -1) {
//                // add to map
//                Marker marker = this.addMarker(
//                        UtilTools.getLatLng(gymWrapper.gym),
//                        resId,
//                        gymWrapper
//                );
//                // add to container
//                this.mGymMarkers.put(gymWrapper.mId, marker);
//            }else {
//                Logger.e(TAG, "addGymWrapper: resId is -1");
//                Logger.logException(new RuntimeException("Invalid gywWrapper"));
//            }
//
//        }catch(Exception e){
//            Logger.e(TAG, "addGymWrapper: Exception-%s", e.getMessage());
//            Logger.logException(e);
//        }
//
//    }
//
//    private void updateGymWrapper(Marker m) {
////        Logger.d(TAG, "updateGymWrapper: called");
//        try {
//            GymWrapper gymWrapper = (GymWrapper) m.getTag();
//
//            int resId = mImageResource.getMarkerResId(gymWrapper);
//            if (resId == -1) {
//                // something wrong
//                Logger.e(TAG, "updateGymWrapper: failed to get resource");
//                m.remove();
//            } else
//                m.setIcon(BitmapDescriptorFactory.fromResource(resId));
//        }catch(Exception e){
//            Logger.e(TAG, "updateGymWrapper: Exception-%s", e.getMessage());
//            Logger.logException(e);
//        }
//
//
//    }
//
//    public void updateSpeed(){
//        try {
//            if (mLastDest != null) {
//                setDestination(mLastDest);
//            }
//        }catch(Exception e){
//            Logger.logException(e);
//        }
//
//    }
//    public void updatePokeStop(String pokestopId) {
//        synchronized (mPokestopMarkers) {
//            if(mPokestopMarkers.containsKey(pokestopId)){
//                Marker m = mPokestopMarkers.get(pokestopId);
//                PokestopWrapper psw = (PokestopWrapper) m.getTag();
//                Logger.d(TAG, "updatePokeStop By Id: %s, hasLure:%s, name:%s, distance:%.2f",
//                         pokestopId,
//                         psw.lureState,
//                         psw.getName(),
//                         psw.pokestop.getDistance());
//
//                updatePokestopWrapper(m);
//
//            }
//        }
//    }
//
//
//    private void addPokestopWrapper(PokestopWrapper psw) {
//        // add to map
//        Marker marker = this.addMarker(
//                UtilTools.getLatLng(psw.pokestop),
//                mImageResource.getMarkerResId(psw),
//                psw
//        );
//
//        // add to container
//        this.mPokestopMarkers.put(psw.id, marker);
//
//
//    }

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
        map.setPadding(0,0,Common.dpToPx(20), Common.dpToPx(80));
//        map.setOnMarkerClickListener(new MyOnMarkerClickListener());

        updateSelf(centerLocation);
        map.moveCamera(CameraUpdateFactory.zoomTo(18.5f));
        map.moveCamera(CameraUpdateFactory.newLatLng(mSelf.getPosition()));
    }


//    private boolean onClick_PokestopWrapper(PokestopWrapper psw) {
//        if (psw.pokestop.canLoot()) {
//            // TODO: change to property mainListener integration
//            MainListener listener = (MainListener) mMainActivity;
//            listener.onLootPokestop(psw);
//            return true;
//        } else {
//            //Logger.d(TAG, "onClick_Pokestop - cannot loot ");
//            return false;
//        }
//    }
//
//    private void onClick_Catchable(CatchableWrapper cw) {
//        // perform catch using main activity
//        try {
//            mMainActivity.onEncounterCatchable(cw);
//        } catch (Exception e) {
//            Logger.logException(e);
//        }
//
//
//    }


//    public void setHomeMarker(LocationInfo homeLocation) {
//        this.mHomeMarker = addMarker(homeLocation.toLatLng(),
//                                     R.drawable.marker_home,
//                                     homeLocation);
//    }
//
//    public void remove(CatchableWrapper cw) {
//        synchronized (mCatchableMarker) {
//            if (mCatchableMarker.containsKey(cw.mCatchableId)) {
//                // remove marker from map
//                Marker m = mCatchableMarker.get(cw.mCatchableId);
//                m.remove();
//
//                // remove marker from container
//                mCatchableMarker.remove(cw.mCatchableId);
//            }
//        }
//    }
//
//    public synchronized void updateCatchableList() {
//        // update catchable pokemon
//        Logger.d(TAG, "updateCatchableList for all");
//        List<CatchableWrapper> catchableList = PokeCore.getInstance().mCatchableContainer.getCatchableList();
//        synchronized (this.mCatchableMarker) {
//
//            // not workign
////            HashMap<Long, Marker> prepareRemoveList = new HashMap<>();
////            prepareRemoveList.putAll(mCatchableMarker);
//            List<Long> prepareRemoveList = new ArrayList<>(mCatchableMarker.size());
//            for (Long key : mCatchableMarker.keySet()) {
//                long id = key;
//                prepareRemoveList.add(id);
//            }
//
//
//            for (CatchableWrapper cw : catchableList) {
//                if (prepareRemoveList.contains(cw.mCatchableId)) {
//                    // nothing to do.
//                    // skip for remove
//                    prepareRemoveList.remove(cw.mCatchableId);
//                } else {
//                    LatLng latLng= cw.getLatLng();
//                    // new catchable
//                    if(latLng != null) {
//                        Marker m = addMarker(cw.getLatLng(),
//                                             mImageResource.getMarkerResId(cw.pokemonId),
//                                             cw);
//                        mCatchableMarker.put(cw.mCatchableId, m);
//                    }
//
//                }
//
//            }
//
//            // 1. remove marker
//            // 2. remove pokestop
//            for (Long key : prepareRemoveList) {
//                Logger.d(TAG, "updateCatchableList: remove key:%d", key );
//                Marker marker = mCatchableMarker.get(key);
//                if (marker != null)
//                    marker.remove();
//                mCatchableMarker.remove(key);
//            }
//
//        }
//    }
//
//
//    public void updatePokestopWrapper(String pokestopId) {
//        // update the pokestop marker
//        Logger.d(TAG, "updatePokestop for poketstopId=%s", pokestopId);
//        Marker m = mPokestopMarkers.get(pokestopId);
//        if (m != null) {
//            updatePokestopWrapper(m);
//        } else {
//            Logger.e(TAG, "updatePokestop. marker not found -> abort");
//        }
//
//
//    }
//
//    public synchronized void updateAllPokestop() {
//        Logger.d(TAG, "updatePokestop for all");
//        List<PokestopWrapper> pokestopWrapperList = PokeCore.getInstance().mPokestopContainer.getAllPokestopWrapper();
//        synchronized (mPokestopMarkers) {
//            // clone for prepare removeList
////            HashMap<String, Marker> prepareRemoveList = new HashMap<>(mPokestopMarkers.size());
//            List<String> prepareRemoveList = new ArrayList<>(mPokestopMarkers.size());
//            for(String key: mPokestopMarkers.keySet()){
//                prepareRemoveList.add(key);
//            }
//
////            prepareRemoveList.putAll(mPokestopMarkers);
//
//            for (PokestopWrapper psw : pokestopWrapperList) {
//                // check for existent and perform update
//                if (prepareRemoveList.contains(psw.id)) {
//                    Marker m = mPokestopMarkers.get(psw.id);
//                    // update the marker
//                    updatePokestopWrapper(m);
//
//                    prepareRemoveList.remove(psw.id);
//                } else {
//                    // add  non-exist
//                    addPokestopWrapper(psw);
//                }
//            }
//
//            // 1. remove marker
//            // 2. remove pokestop
//            for (String key : prepareRemoveList) {
//                Marker marker = mPokestopMarkers.get(key);
//                if(marker != null)
//                    marker.remove();
//                mPokestopMarkers.remove(key);
//
//            }
//        }
//    }

    public void updateSelf(@NonNull Location location) {
//        Logger.d(TAG, "updateSelf: (%.6f, %.6f)", latLng.latitude, latLng.longitude);
        if (this.mSelf == null) {
            this.mSelf = map.addMarker(
                    new MarkerOptions()
                            .position(toLatLng(location)));
            this.mSelf.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_man));
        } else {
            this.mSelf.setPosition(toLatLng(location));
        }


//        if (this.mSelfCircle == null) {
//            this.mSelfCircle = map.addCircle(
//                    new CircleOptions().center(mSelf.getPosition())
//                            .radius(mPokeStopLootRange)
//                            .strokeWidth(2)
//                            .strokeColor(R.color.colorPrimaryAccent));
//        } else {
//            mSelfCircle.setCenter(mSelf.getPosition());
//        }
//
//        if (this.mSelfCircle2 == null) {
//            this.mSelfCircle2 = map.addCircle(
//                    new CircleOptions().center(mSelf.getPosition())
//                            .radius(75.0f)
//                            .strokeWidth(2)
//                            .strokeColor(R.color.colorSecondAccent));
//        } else {
//            mSelfCircle2.setCenter(mSelf.getPosition());
//        }


    }

    private LatLng toLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

//    public boolean moveNearPokestop(PokestopWrapper psw){
//        Marker m = mPokestopMarkers.get(psw.id);
//        if(m != null) {
//            LatLng latlng = UtilTools.randLatLng(m.getPosition(), 10, 40);
//            setDestination(latlng);
//            return true;
//        }else {
//            Logger.d(TAG, "moveNearPokestop: failed to find pokestop marker:%s", psw.id);
//            return false;
//        }
//    }

    public void addGeoFence(MyGeoFenceData myGeoFenceData) {
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

//    private void updatePokestopWrapper(Marker m) {
//        // check the icon state if is differnt (failed to keep previous state)
//        PokestopWrapper psw = (PokestopWrapper) m.getTag();
//        int resId = mImageResource.getMarkerResId(psw);
//        m.setIcon(BitmapDescriptorFactory.fromResource(resId));
//    }




//    private class MyOnMarkerClickListener implements GoogleMap.OnMarkerClickListener {
//        @Override
//        public boolean onMarkerClick(Marker marker) {
////            Object obj = marker.getTag();
////            if (obj != null) {
////                if (obj instanceof PokestopWrapper) {
////                    if (!onClick_PokestopWrapper((PokestopWrapper) obj)) {
////                        // cannot loot -> change the action to move destinatin
////                        LatLng latlng = UtilTools.randLatLng(marker.getPosition(), 60, 100);
////                        setDestination(latlng);
////                    }
////                } else if (obj instanceof CatchableWrapper) {
////                    onClick_Catchable((CatchableWrapper) obj);
////                } else if( obj instanceof GymWrapper){
////                    if(!onClick_GymWrapper((GymWrapper) obj, marker)){
////                        LatLng latlng = UtilTools.randLatLng(marker.getPosition(), 60, 100);
////                        setDestination(latlng);
////                    }
////                }
////
////            }
//
//
//            return false;
//        }
//    }




//    private boolean onClick_GymWrapper(GymWrapper gymWrapper, Marker marker) {
//        // check the gymwrapper in range or not
//        double distance = UtilTools.getDistance(mSelf.getPosition(), marker.getPosition());
//
//        // Debug for the new gymInfomation
//        Logger.d(TAG, "onClick_GymWrapper: called");
//
//
//        // same distance range as pokestop range
//        if (distance < mPokeStopLootRange) {
//            Logger.d(TAG, "onClick_GymWrapper - can loot ");
//            //change to property mainListener integration
//            MainListener listener = mMainActivity;
//            listener.onOpenGym(gymWrapper);
//            return true;
//        } else {
//            Logger.d(TAG, "onClick_GymWrapper - cannot loot ");
//            return false;
//        }
//    }

}
