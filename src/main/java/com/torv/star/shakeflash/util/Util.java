package com.torv.star.shakeflash.util;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by lijian on 6/14/15.
 */
public class Util {

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager magager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : magager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
