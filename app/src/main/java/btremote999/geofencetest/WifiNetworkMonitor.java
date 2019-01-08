package btremote999.geofencetest;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.support.annotation.NonNull;

import btremote999.geofencetest.utils.Logger;

/**
 *  Wifi Network Monitor
 */
public class WifiNetworkMonitor {
    public final static int WIFI_CONNECTED = 1;
    public final static int WIFI_DISCONNECTED = 2;

    public interface IWifiStatusCallback{
        void onWifiStateChange(int WifiState);
    }

    private static final String TAG = "WifiNetworkMonitor";

    private IWifiStatusCallback mCallback;

    public void startWifiMonitor(@NonNull Context context, @NonNull IWifiStatusCallback callback){
        Logger.d(TAG, "startWifiMonitor: ");
        mCallback = callback;

        if(!mRegisteredNetwork) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkRequest nr = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .build();

            if (mMyNetworkCallback == null) {
                mMyNetworkCallback = new MyNetworkCallback();
            }
            cm.registerNetworkCallback(nr, mMyNetworkCallback);
            mRegisteredNetwork = true;
        }

    }

    public void stopWifiMonitor(Context context){
        Logger.d(TAG, "stopWifiMonitor: ");

        if(mRegisteredNetwork) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            cm.unregisterNetworkCallback(mMyNetworkCallback);
            mRegisteredNetwork = false;
        }


    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class MyNetworkCallback extends ConnectivityManager.NetworkCallback {

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            Logger.d(TAG, "onLost: ");
            if(mCallback != null)
                mCallback.onWifiStateChange(WIFI_DISCONNECTED);
        }

        @Override
        public void onUnavailable() {
            super.onUnavailable();
            Logger.d(TAG, "onUnavailable: ");
            if(mCallback != null)
                mCallback.onWifiStateChange(WIFI_DISCONNECTED);
        }

        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            Logger.d(TAG, "onAvailable: ");
            if(mCallback != null)
                mCallback.onWifiStateChange(WIFI_CONNECTED);

        }
    }

    private MyNetworkCallback mMyNetworkCallback;
    private boolean mRegisteredNetwork;
}
