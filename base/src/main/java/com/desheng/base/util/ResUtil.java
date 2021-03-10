package com.desheng.base.util;

import android.graphics.drawable.Drawable;
import android.support.annotation.BoolRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.content.res.AppCompatResources;

import com.ab.global.Global;

public class ResUtil {

    public static String getString(int res) {
        return Global.app.getString(res);
    }

    public static String getString(int res, Object... arg) {
        return Global.app.getString(res, arg);
    }

    public static Drawable getDrawable(int res) {
        return AppCompatResources.getDrawable(Global.app, res);
    }

    public static int getColor(@ColorRes int color) {
        return ContextCompat.getColor(Global.app, color);
    }


    public static float getDimension(@DimenRes int id) {
        return Global.app.getResources().getDimension(id);
    }

    public static int getDimensionPixel(@DimenRes int res) {
        return Global.app.getResources().getDimensionPixelSize(res);
    }

    public static boolean getBoolean(@BoolRes int bool) {
        return Global.app.getResources().getBoolean(bool);
    }
}
