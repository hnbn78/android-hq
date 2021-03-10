package com.ab.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ab.core.R;
import com.ab.debug.AbDebug;
import com.ab.global.AbDevice;

/**
 * 真全屏fragment基类, 自带设置R.style.Dialog_FullScreen, DialogFragment.STYLE_NO_FRAME, 沉浸状态栏
 * Created by lee on 2017/10/2.
 */

public abstract class AbBaseFullScreenDialogFragment extends AbDialogFragment{
    private static AbBaseFullScreenDialogFragment ins;
    
    public static boolean isShowingDialog() {
        boolean isShowing;
        if (ins == null) {
            isShowing = false;
        } else {
            isShowing = true;
        }
        return isShowing;
    }
    
    
    protected static void createAndShow(FragmentManager fm, Class<? extends AbBaseFullScreenDialogFragment> clazz, Bundle arguments,
                                        DialogInterface.OnShowListener showListener, DialogInterface.OnDismissListener dismissListener) {
        if (ins != null && ins.isVisible()) {
            ins.dismiss();
            ins = null;
        }
        try {
            ins = clazz.newInstance();
        }  catch (Exception e) {
            e.printStackTrace();
        }
        ins.setOnShowListener(showListener);
        ins.setOnDismissListener(dismissListener);
        ins.setArguments(arguments);
        ins.show(fm, clazz.getSimpleName());
    }
    
    public static AbBaseFullScreenDialogFragment getCurrIns(){
        return ins;
    }
    
    public static void dismissDialog() {
        if (ins != null && ins.isVisible()) {
            ins.dismissAllowingStateLoss();
            ins = null;
        }
    }
    
    /* (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams  params = window.getAttributes();
        params.width = AbDevice.SCREEN_WIDTH_PX;
        params.height = AbDevice.SCREEN_HEIGHT_PX;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        super.onActivityCreated(savedInstanceState);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(R.style.Dialog_FullScreen, DialogFragment.STYLE_NO_FRAME);
    }
 
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
    
    public <T> T getParamFromArgument(String paraName, T def){
        T value = null;
        try {
            value = (T) getArguments().get(paraName);
        }catch (Exception e){
            AbDebug.error(AbDebug.TAG_APP, Thread.currentThread(), e);
        }
        if (value == null) {
            value = def;
        }
        return value;
    }
    
}
