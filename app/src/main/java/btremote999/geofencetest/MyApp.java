package btremote999.geofencetest;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import btremote999.geofencetest.utils.Logger;

public class MyApp extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "MyApp";

    private Activity mLastShowActivity;
    private Integer mLastTransition;

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(this);
    }

    public boolean isMainActivityInForeground(){
        if(mLastShowActivity == null)
            return false;

        if(mLastShowActivity instanceof MainActivity){
            return true;
        }
        return false;
    }

    public void forwardGeoFence(int transition){
        if(mLastShowActivity instanceof MainActivity){
            sendIntent(transition);
        }else {
            mLastTransition = transition;
            Logger.d(TAG, "forwardGeoFence: MainActivity not show, keep it");
        }
    }

    private void sendIntent(int transition) {
        Intent forwardIntent = new Intent(this, MainActivity.class);
        forwardIntent.setAction(Consts.GEOFENCE);
        forwardIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        forwardIntent.putExtra(Consts.DATA, transition);
        startActivity(forwardIntent);
        mLastTransition = null;
        Logger.d(TAG, "forwardGeoFence: forwarded to MainActivity");
    }

    // region [ActivityLifeCycleCallbacks]
    //=================================================================

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        mLastShowActivity = activity;
        // Sent last saved intent to MainActivity
        if(mLastTransition != null &&
                mLastShowActivity instanceof MainActivity ){
            sendIntent(mLastTransition);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        mLastShowActivity = null;
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }




    //=================================================================
    // endregion [ActivityLifeCycleCallbacks]

}
