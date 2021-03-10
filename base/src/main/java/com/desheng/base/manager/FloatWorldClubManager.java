package com.desheng.base.manager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.ab.util.Toasts;
import com.desheng.base.R;
import com.desheng.base.panel.ActThirdGameForAll;
import com.desheng.base.util.Constants_LM;
import com.desheng.base.util.SpUtil;
import com.desheng.base.view.FloatLayout;

import java.lang.ref.WeakReference;


/**
 * Author:xishuang
 * Date:2017.08.01
 * Des:悬浮窗统一管理，与悬浮窗交互的真正实现
 */
public class FloatWorldClubManager {
    /**
     * 上下文引用
     */
    private static WeakReference<Activity> ctxRef;

    /**
     * 悬浮窗
     */
    private static FloatLayout mFloatLayout;
    private static WindowManager mWindowManager;
    private static WindowManager.LayoutParams wmParams;

    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void createFloatWindow(Context context) {
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = getWindowManager(context);
        mFloatLayout = new FloatLayout(context);

        mFloatLayout.setImage(R.mipmap.ic_world_club_button);
        if (Build.VERSION.SDK_INT >= 24) { /*android7.0不能用TYPE_TOAST*/
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else { /*以下代码块使得android6.0之后的用户不必再去手动开启悬浮窗权限*/
            String packname = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", packname));
            if (permission) {
                wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
        }

        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.START | Gravity.TOP;

        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        FloatWorldClubManager.mWindowManager.getDefaultDisplay().getMetrics(dm);
        //窗口的宽度
        int screenWidth = dm.widthPixels;
        //窗口高度
        int screenHeight = dm.heightPixels;
        //以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = screenWidth;
        wmParams.y = screenHeight;
        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //mWindowManager.addView(mFloatLayout, wmParams);
        //是否展示小红点展示
        checkRedDot(context);
    }

    /**
     * 移除悬浮窗
     */
    public static void removeFloatWindowManager() {
        //移除悬浮窗口
        boolean isAttach = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isAttach = mFloatLayout.isAttachedToWindow();
        }
        if (mFloatLayout.getParent() != null && isAttach && mWindowManager != null)
            mWindowManager.removeView(mFloatLayout);
    }

    /**
     * 返回当前已创建的WindowManager。
     */
    private static WindowManager getWindowManager(Context context) {
        if (context == null) {
            return null;
        }
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return mWindowManager;
    }

    /**
     * 小红点展示
     */
    public static void checkRedDot(Context context) {
        if (mFloatLayout == null) return;
        //是否展示小红点展示
        int num = getObtainNumber(context);
        if (num > 0) {
            mFloatLayout.setDragFlagViewVisibility(View.VISIBLE);
            mFloatLayout.setDragFlagViewText(getObtainNumber(context));
        } else {
            mFloatLayout.setDragFlagViewVisibility(View.GONE);
        }
    }

    /**
     * 添加小红点
     */
    public static void addObtainNumer(Context context) {
        int number = (int) SpUtil.get(context, Constants_LM.OBTAIN_NUMBER, 0);
        if (number < 0) {
            number = 0;
        }
        number = number + 1;
        SpUtil.put(context, Constants_LM.OBTAIN_NUMBER, number);
        if (mFloatLayout != null) {
            mFloatLayout.setDragFlagViewVisibility(View.VISIBLE);
            mFloatLayout.setDragFlagViewText(number);
        }
    }

    /**
     * 获取小红点展示的数量
     */
    private static int getObtainNumber(Context context) {
        return 0;
        //return (int) SpUtil.get(context, Constants_LM.OBTAIN_NUMBER, 0);
    }

    /**
     * 设置小红点数字
     */
    public static void setObtainNumber(Context context, int number) {
        if (number < 0) {
            number = 0;
        }
        SpUtil.put(context, Constants_LM.OBTAIN_NUMBER, number);
        FloatWorldClubManager.checkRedDot(context);
    }

    /**
     * 隐藏对话栏，是否小红点
     */
    public static void updataRedAndDialog(Context context) {
        mFloatLayout.setDragFlagViewVisibility(View.VISIBLE);
        //是否展示小红点展示
        checkRedDot(context);
    }

    public static void hide() {
        if (mFloatLayout.getParent() != null) {
            mWindowManager.removeViewImmediate(mFloatLayout);
            getWindowManager(null);
        }
    }

    public static void show(Activity act) {
        if (mFloatLayout.getParent() == null) {
            getWindowManager(act);
            ctxRef = new WeakReference<Activity>(act);
            //重点，类型设置为dialog类型,可无视权限!
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
            //重点,必须设置此参数，用于窗口机制验证
            IBinder windowToken = act.getWindow().getDecorView().getWindowToken();
            wmParams.token = windowToken;
            mFloatLayout.setParams(wmParams);
            mWindowManager.addView(mFloatLayout, wmParams);

            mFloatLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ctxRef != null && ctxRef.get() != null) {
                        toImSports(ctxRef.get());
                    }
                }
            });
        }
    }

    //toImSports();
    private static void toImSports(final Activity ctxr) {
        ActThirdGameForAll.launchOutside(ctxr, 6);
    }

}
