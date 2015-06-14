package com.torv.star.shakeflash.util;

import android.util.Log;

/**
 * Created by lijian on 6/14/15.
 */
public class Lg {

    public static void e(String tag, String msg){
        if(Constants.CONSTANT_PRINT_LOG){
            Log.e(tag, msg);
        }
    }

    public static void d(String tag, String msg){
        if(Constants.CONSTANT_PRINT_LOG){
            Log.d(tag, msg);
        }
    }
}
