package btremote999.geofencetest.utils;

import android.util.Log;

//import com.crashlytics.android.Crashlytics;

/**
 * Created by kklow on 7/1/17.
 */

public class Logger {

    // region standard debug log
    public static void d(String tag, String format, Object... args) {
        try {
            String msg = String.format(format, args);
            Log.d(tag, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void i(String tag, String format, Object... args) {
        try {
            String msg = String.format(format, args);
            Log.i(tag, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void w(String tag, String format, Object... args) {
        try {
            String msg = String.format(format, args);
            Log.w(tag, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void e(String tag, String format, Object... args) {
        try {
            String msg = String.format(format, args);
            Log.e(tag, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion standard debug log



}
