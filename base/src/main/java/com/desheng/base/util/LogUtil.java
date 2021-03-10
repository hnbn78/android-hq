package com.desheng.base.util;

import android.util.Log;

public class LogUtil {
    private static final String MY_TAG=">>>>>>>>:";

    public static void logE(String spTag,String msg){
        Log.e(MY_TAG+spTag,msg);
    }

}
