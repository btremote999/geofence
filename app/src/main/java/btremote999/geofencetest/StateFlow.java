package btremote999.geofencetest;

import android.util.SparseArray;

// State of monitor

public class StateFlow {
    public static final int NONE = 0;

    public static final int CHECK_PERMISSION = 100; // app just start -> perform permission check

    public static final int GRANT_PERMISSION = 101; // grant permission state
    public static final int SHOW_REQUEST_PERMISSION_RATIONALE = 102; // show -> rationale
    public static final int GRANT_PERMISSION_DENIED_PERMANENTLY = 103; // user has denied permanently -> app setting
    public static final int PERMISSION_GRANTED = 104;  // permission granted -> prepare for location related setup

    public static final int APP_READY= 201;





    //=================================================================

    private static final SparseArray<String> NAMES = new SparseArray<>();
    static{
        NAMES.put(NONE, "NONE");
        NAMES.put(CHECK_PERMISSION, "CHECK_PERMISSION");
        NAMES.put(GRANT_PERMISSION, "GRANT_PERMISSION");
        NAMES.put(SHOW_REQUEST_PERMISSION_RATIONALE, "SHOW_REQUEST_PERMISSION_RATIONALE");
        NAMES.put(GRANT_PERMISSION_DENIED_PERMANENTLY, "GRANT_PERMISSION_DENIED_PERMANENTLY");
        NAMES.put(PERMISSION_GRANTED, "PERMISSION_GRANTED");
        NAMES.put(APP_READY, "APP_READY");


    }

    public static String toString(int stateFlow){
        String name = NAMES.get(stateFlow);
        if(name == null)
            return Integer.toString(stateFlow);
        return name;
    }



}
