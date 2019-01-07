package btremote999.geofencetest.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private static AtomicInteger id;
    private static boolean isInited = false;
    private static void init(){
        id = new AtomicInteger(0);
        isInited = true;
    }
    public static int newId(){
        if(!isInited) init();
        return id.incrementAndGet();
    }
}
