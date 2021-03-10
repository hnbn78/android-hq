package com.ab.util;

import android.app.Activity;
import android.view.View;

import com.ab.core.R;
import com.ab.global.Global;
import com.github.mrengineer13.snackbar.SnackBar;

/**
 * Created by lee on 2018/4/5.
 */

public class Snackbars {
    
    public static void show(Activity ctx, String str) {
        new SnackBar.Builder(ctx)
                .withMessage(str)
                .withTextColorId(R.color.black)
                .withBackgroundColorId(R.color.white)
                .withDuration((short) 300)
                .show();
    }
    
    public static void showShort(View root, String str) {
        new SnackBar.Builder(Global.app, root)
                .withMessage(str)
               /* .withTextColorId(R.color.black)
                .withBackgroundColorId(R.color.white)*/
                .withClearQueued()
                .withDuration((short) 1000)
                .show();
    }
    
    public static void showLong(View root, String str) {
        new SnackBar.Builder(Global.app, root)
                .withMessage(str)
               /* .withTextColorId(R.color.black)
                .withBackgroundColorId(R.color.white)*/
                .withDuration((short) 3000)
                .withClearQueued()
                .show();
    }
}
