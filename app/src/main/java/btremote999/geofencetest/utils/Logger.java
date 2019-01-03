package pollyfun.tvboxlib.util;

import android.util.Log;
import com.crashlytics.android.Crashlytics;

/**
 * Created by kklow on 7/1/17.
 */

public class Logger {

    public interface IInterceptor {
        void onLog(int logLevel, String tag, String msg);
    }

    private static IInterceptor sInterceptor = null;
    public static void setInterceptor(IInterceptor interceptor){
        sInterceptor = interceptor;
    }

    // region standard debug log
    public static void d(String tag, String format, Object... args) {
        try {
            String msg = String.format(format, args);
            Crashlytics.log(Log.DEBUG, tag, msg);
            intercept(Log.DEBUG, tag, msg);
        } catch (Exception e) {
            Crashlytics.log(Log.ERROR, tag, "Exception during create log. " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void intercept(int logLevel, String tag, String msg) {
        if(sInterceptor != null)
            sInterceptor.onLog(logLevel, tag, msg);

    }

    public static void i(String tag, String format, Object... args) {
        try {
            String msg = String.format(format, args);
            Crashlytics.log(Log.INFO, tag, msg);
            intercept(Log.INFO, tag, msg);

        } catch (Exception e) {
            Crashlytics.log(Log.ERROR, tag, "Exception during create log. " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void w(String tag, String format, Object... args) {
        try {
            int priority = Log.WARN;
            String msg = String.format(format, args);
            Crashlytics.log(priority, tag, msg);
            intercept(priority, tag, msg);
//            Log.w(tag, String.format(format, args));
        } catch (Exception e) {
            Crashlytics.log(Log.ERROR, tag, "Exception during create log. " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void e(String tag, String format, Object... args) {
        try {
            int priority = Log.ERROR;
            String msg = String.format(format, args);
            Crashlytics.log(priority, tag, msg);
            intercept(priority, tag, msg);
        } catch (Exception e) {
            Crashlytics.log(Log.ERROR, tag, "Exception during create log. " + e.getMessage());
            e.printStackTrace();
        }
    }
    //endregion standard debug log

    public static void logException(Exception exception) {
        Crashlytics.logException(exception);
        int priority = Log.ASSERT;
        String msg = exception.getMessage();
        intercept(priority, "Exception", msg);

        exception.printStackTrace();
    }
    public static String logLvlToString(int level){
        switch(level) {
            case Log.DEBUG:
                return "D";
            case Log.INFO:
                return "I";
            case Log.WARN:
                return "W";
            case Log.ERROR:
                return "E";
            case Log.ASSERT:
                return "A";
            default:
                return "U";
        }

    }

}
