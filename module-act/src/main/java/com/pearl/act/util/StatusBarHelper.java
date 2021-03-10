package com.pearl.act.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.ab.util.Strs;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 工具类
 * Created by SmileXie on 2017/6/29.
 */

public class StatusBarHelper {
    public static int screenWidth;
    public static int screenHeight;
    public static int navigationHeight = 0;

    private static DisplayMetrics mMetrics;
    public static final String HOME_CURRENT_TAB_POSITION = "HOME_CURRENT_TAB_POSITION";

    /**
     * 通过反射的方式获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取底部导航栏高度
     *
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        if(navigationHeight == 0 && checkDeviceHasNavigationBar(context)){
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics dm = new DisplayMetrics();
            try {
                @SuppressWarnings("rawtypes")
                Class c = Class.forName("android.view.Display");
                @SuppressWarnings("unchecked")
                Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
                method.invoke(display, dm);
                navigationHeight = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
       
        return navigationHeight;
    }

    //获取是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }

    /**
     * @param activity
     * @param useThemestatusBarColor   是否要状态栏的颜色，不设置则为透明色
     * @param withoutUseStatusBarColor 是否不需要使用状态栏为暗色调
     */
    public static void setStatusBar(Activity activity, boolean useThemestatusBarColor, boolean withoutUseStatusBarColor) {
        int  softInputMode = activity.getWindow().getAttributes().softInputMode;
       
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            if (useThemestatusBarColor) {
                activity.getWindow().setStatusBarColor(activity.getResources().getColor(android.R.color.white));
            } else {
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !withoutUseStatusBarColor) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    
        activity.getWindow().setSoftInputMode(softInputMode);
    }
    
    
    /**
     * @param activity
     * @param withoutUseStatusBarColor 是否不需要使用状态栏为暗色调
     */
    public static void setStatusBar(Activity activity, int bgColor, boolean withoutUseStatusBarColor) {
        int  softInputMode = activity.getWindow().getAttributes().softInputMode;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            if (bgColor != -1) {
                activity.getWindow().setStatusBarColor(activity.getResources().getColor(bgColor));
            } else {
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !withoutUseStatusBarColor) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        
        activity.getWindow().setSoftInputMode(softInputMode);
    }

    public static void reMeasure(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        mMetrics = new DisplayMetrics();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealMetrics(mMetrics);
        } else {
            display.getMetrics(mMetrics);
        }

        screenWidth = mMetrics.widthPixels;
        screenHeight = mMetrics.heightPixels;
    }

    /**
     * 改变魅族的状态栏字体为黑色，要求FlyMe4以上
     */
    private static void processFlyMe(boolean isLightStatusBar, Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        try {
            Class<?> instance = Class.forName("android.view.WindowManager$LayoutParams");
            int value = instance.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON").getInt(lp);
            Field field = instance.getDeclaredField("meizuFlags");
            field.setAccessible(true);
            int origin = field.getInt(lp);
            if (isLightStatusBar) {
                field.set(lp, origin | value);
            } else {
                field.set(lp, (~value) & origin);
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    /**
     * 改变小米的状态栏字体颜色为黑色, 要求MIUI6以上  lightStatusBar为真时表示黑色字体
     */
    private static void processMIUI(boolean lightStatusBar, Activity activity) {
        float versionCode = 0;
        try {
            versionCode = Strs.parse(BuildProperties.newInstance().getProperty(KEY_MIUI_VERSION_CODE, null), 7.0f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(versionCode >= 7){
            if (lightStatusBar) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            } else {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        }else{
            Class<? extends Window> clazz = activity.getWindow().getClass();
            try {
                int darkModeFlag;
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags",int.class,int.class);
                extraFlagField.invoke(activity.getWindow(), lightStatusBar? darkModeFlag : 0, darkModeFlag);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
        
    }

    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    /**
     * 判断手机是否是小米
     * @return
     */
    public static boolean isMIUI() {
        try {
            final BuildProperties prop = BuildProperties.newInstance();
            return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } catch (final IOException e) {
            return false;
        }
    }

    /**
     * 判断手机是否是魅族
     * @return
     */
    public static boolean isFlyme() {
        try {
            // Invoke Build.hasSmartBar()
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * 设置状态栏文字色值为深色调
     * @param useDart 是否使用深色调
     * @param activity
     */
    public static void setStatusTextColor(boolean useDart, Activity activity) {
        if (isFlyme()) {
            processFlyMe(useDart, activity);
        } else if (isMIUI()) {
            processMIUI(useDart, activity);
        } else {
            if (useDart) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            } else {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
            //activity.getWindow().getDecorView().findViewById(android.R.id.content).setPadding(0, 0, 0, StatusBarHelper.navigationHeight);
        }
    }
    
    /**
     * 解决键盘档住输入框
     * Created by SmileXie on 2017/4/3.
     */
    
    public static class SoftHideKeyBoardUtil {
        public static void assistActivity (Activity activity) {
            new SoftHideKeyBoardUtil(activity);
        }
        private View mChildOfContent;
        private int usableHeightPrevious;
        //private FrameLayout.LayoutParams frameLayoutParams;
        //为适应华为小米等手机键盘上方出现黑条或不适配
        private int contentHeight;//获取setContentView本来view的高度
        private boolean isfirst = true;//只用获取一次
        private  int statusBarHeight;//状态栏高度
        private Activity activity;
        private  int navBarHeight;//状态栏高度
        private boolean isShowingKeyboard = false;
        private SoftHideKeyBoardUtil(Activity activity) {
            this.activity = activity;
            statusBarHeight = getStatusBarHeight(activity);
            if(checkDeviceHasNavigationBar(activity)){
                navBarHeight = getNavigationBarHeight(activity);
            }
            //1､找到Activity的最外层布局控件，它其实是一个DecorView,它所用的控件就是FrameLayout
            FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
            //2､获取到setContentView放进去的View
            mChildOfContent = content.getChildAt(0);
            //3､给Activity的xml布局设置View树监听，当布局有变化，如键盘弹出或收起时，都会回调此监听
            mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                //4､软键盘弹起会使GlobalLayout发生变化
                public void onGlobalLayout() {
                    if (isfirst) {
                        contentHeight = mChildOfContent.getHeight();//兼容华为等机型
                        usableHeightPrevious = contentHeight;
                        isfirst = false;
                    }
                    //5､当前布局发生变化时，对Activity的xml布局进行重绘
                    possiblyResizeChildOfContent();
                }
            });
            
        }
        
        // 获取界面可用高度，如果软键盘弹起后，Activity的xml布局可用高度需要减去键盘高度
        private void possiblyResizeChildOfContent() {
            //1､获取当前界面可用高度，键盘弹起后，当前界面可用布局会减少键盘的高度
            int usableHeightNow = computeUsableHeight();
            //6､获取到Activity的xml布局的放置参数
            FrameLayout.LayoutParams frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
            //2､如果当前可用高度和原始值不一样
            if (usableHeightNow != usableHeightPrevious) {
                //3､获取Activity中xml中布局在当前界面显示的高度
                int usableHeightSansKeyboard = mChildOfContent.getHeight();
                //4､Activity中xml布局的高度-当前可用高度
                int heightDifference = usableHeightSansKeyboard - usableHeightNow;
                //5､高度差大于屏幕1/4时，说明键盘弹出
                if (heightDifference > (usableHeightSansKeyboard/4)) {
                    // 6､键盘弹出了，Activity的xml布局高度应当减去键盘高度
                    isShowingKeyboard = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                         frameLayoutParams.height = usableHeightSansKeyboard - heightDifference ;
                    } else {
                        frameLayoutParams.height = usableHeightSansKeyboard - heightDifference ;
                    }
                    ((IOnKeyBoadListener)activity).onKeyBoadShow();
                } else {
                    frameLayoutParams.height = contentHeight;
                    if(isShowingKeyboard){
                        isShowingKeyboard = false;
                        mChildOfContent.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((IOnKeyBoadListener)activity).onKeyBoadHide();
                            }
                        }, 200);
                    }
                }
                //7､ 重绘Activity的xml布局
                mChildOfContent.setLayoutParams(frameLayoutParams);
                usableHeightPrevious = usableHeightNow;
            }
        }
        private int computeUsableHeight() {
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;
            Rect r = new Rect();
            mChildOfContent.getWindowVisibleDisplayFrame(r);
    
            //这个判断是为了解决19之后的版本在弹出软键盘时，键盘和推上去的布局（adjustResize）之间有黑色区域的问题
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                return (r.bottom - r.top)+statusBarHeight;
            }
            return (r.bottom - r.top);
        }
    }
}
