package com.ab.util;

import android.app.Activity;
import android.view.WindowManager;

/**
 * Created by Leo on 16/7/11.
 */
public class AbWindows {
    public static void initStatusBar(Activity act){
        act.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        act.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static void hideStatusBar(Activity act) {
        WindowManager.LayoutParams attrs = act.getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        act.getWindow().setAttributes(attrs);
    }

    public static  void showStatusBar(Activity act) {
        WindowManager.LayoutParams attrs =act.getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        act.getWindow().setAttributes(attrs);
    }
}
