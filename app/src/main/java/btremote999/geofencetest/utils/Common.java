package btremote999.geofencetest.utils;

import android.content.res.Resources;

/**
 * Common utilities function
 */
public class Common {

    /**
     * Convert Dp to Px
     * @param dp
     * @return
     */
    public static int dpToPx(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        if(density != 3.5) {
            return (int) (dp * density);
        }
        else {
            // patch case for samsung s7 with xxxhdp
            return (int) (dp * 4);
        }
    }
}
