package com.desheng.base.util;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

/**
 * Created by user on 2018/3/26.
 */

public class QikuUtils {
    private static final String TAG = "QikuUtils";

    /**
     * 检测 360 悬浮窗权限
     */
    public static boolean checkFloatWindowPermission(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class clazz = AppOpsManager.class;
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int)method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        } else {
            Log.e("", "Below API 19 cannot invoke!");
        }
        return false;
    }

    /**
     * 去360权限申请页面
     */
    public static void applyPermission(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings$OverlaySettingsActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            intent.setClassName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
            if (isIntentAvailable(intent, context)) {
                context.startActivity(intent);
            } else {
                Log.e(TAG, "can't open permission page with particular name, please use " +
                        "\"adb shell dumpsys activity\" command and tell me the name of the float window permission page");
            }
        }
    }

    private static boolean isIntentAvailable(Intent intent, Context context) {
        if (intent == null) {
            return false;
        }
        return context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }


    /**
     * Created by user on 2018/3/26.
     */

    public static class RomUtils {

        private static final String TAG = "RomUtils";

        /**
         * 获取 emui 版本号
         * @return
         */
        public static double getEmuiVersion() {
            try {
                String emuiVersion = getSystemProperty("ro.build.version.emui");
                String version = emuiVersion.substring(emuiVersion.indexOf("_") + 1);
                return Double.parseDouble(version);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 4.0;
        }

        /**
         * 获取小米 rom 版本号，获取失败返回 -1
         *
         * @return miui rom version code, if fail , return -1
         */
        public static int getMiuiVersion() {
            String version = getSystemProperty("ro.miui.ui.version.name");
            if (version != null) {
                try {
                    return Integer.parseInt(version.substring(1));
                } catch (Exception e) {
                    Log.e(TAG, "get miui version code error, version : " + version);
                }
            }
            return -1;
        }

        public static String getSystemProperty(String propName) {
            String line;
            BufferedReader input = null;
            try {
                Process p = Runtime.getRuntime().exec("getprop " + propName);
                input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
                line = input.readLine();
                input.close();
            } catch (IOException ex) {
                Log.e(TAG, "Unable to read sysprop " + propName, ex);
                return null;
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Exception while closing InputStream", e);
                    }
                }
            }
            return line;
        }
        public static boolean checkIsHuaweiRom() {
            return Build.MANUFACTURER.contains("HUAWEI");
        }

        /**
         * check if is miui ROM
         */
        public static boolean checkIsMiuiRom() {
            return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
        }

        public static boolean checkIsMeizuRom() {
            //return Build.MANUFACTURER.contains("Meizu");
            String meizuFlymeOSFlag  = getSystemProperty("ro.build.display.id");
            if (TextUtils.isEmpty(meizuFlymeOSFlag)){
                return false;
            }else if (meizuFlymeOSFlag.contains("flyme") || meizuFlymeOSFlag.toLowerCase().contains("flyme")){
                return  true;
            }else {
                return false;
            }
        }

        public static boolean checkIs360Rom() {
            //fix issue https://github.com/zhaozepeng/FloatWindowPermission/issues/9
            return Build.MANUFACTURER.contains("QiKU")
                    || Build.MANUFACTURER.contains("360");
        }


    }
    
}
