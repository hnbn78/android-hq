package com.ab.global;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ab.callback.AbCallback;
import com.ab.debug.AbDebug;
import com.ab.util.PermissionUtils;


/**
 * 管理设备相关信息
 * @author Leo
 *
 */
public class AbDevice {
    public static final String NET_TYPE_2G = "2G";
    public static final String NET_TYPE_3G = "3G";
    public static final String NET_TYPE_4G = "4G";
    public static String appPackageName = null;
    public static String appVersionName = null;
    public static int appTargetSdkVersion;
    public static int appVersionCode = 0;
    
    
    //移动设备国际识别码，移动设备的唯一识别号码
    public static String IMEI = null;
    //国际移动用户识别码储存在SIM卡中
    public static String IMSI = null;
    public static String ANDROID_ID = null;
    //用户手机号码
    public static String PHONE_NUMBER = null;
    //手机型号
    public static String MOBILE_MODEL = null;
    //终端类型
    public static String PLATFORM = "android";
    //显示属性
    public static DisplayMetrics DM = null;
    //屏幕宽高
    public static int SCREEN_WIDTH_PX = 0; // 屏幕宽（像素，如：480px）
    public static int SCREEN_HEIGHT_PX = 0; // 屏幕宽（像素，如：480px）
    public static int SCREEN_WIDTH_DP = 0; // 屏幕宽（像素，如：480px）
    public static int SCREEN_HEIGHT_DP = 0; // 屏幕宽（像素，如：480px）
    
    //系统版本级别
    public static int OS_LEVEL = 0;
    public static boolean isExternalStorageAvailable = false;
    public static boolean isExternalStorageWriteable = false;
    public static Application app;
    
    public static void init(Application app) {
        AbDevice.app = app;

        ANDROID_ID = Settings.System.getString(app.getContentResolver(), Settings.System.ANDROID_ID);
        
        MOBILE_MODEL = Build.MODEL;
        MOBILE_MODEL = MOBILE_MODEL != null ? MOBILE_MODEL : "";
        
        OS_LEVEL = android.os.Build.VERSION.SDK_INT;
        
        String state = Environment.getExternalStorageState();
        
        //检查外部存储
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            isExternalStorageAvailable = isExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            isExternalStorageAvailable = true;
            isExternalStorageWriteable = false;
        } else {
            isExternalStorageAvailable = isExternalStorageWriteable = false;
        }
        
        initDisplay();
        
        initAppInfo();
        
        Log.d(AbDebug.strTag, "[Device] Inited! ***********************************************\n" +
                "VersionName:(" + appVersionName + "); VersionCode:(" + appVersionCode + ")\n" +
                "IMEI:(" + IMEI + ") \nIMSI:(" + IMSI + ") \nMOBILE_MODEL;(" + MOBILE_MODEL + ")\n" +
                "ScreenWidth:(" + SCREEN_WIDTH_PX + "); screenHeight;(" + SCREEN_HEIGHT_PX + ");\n" +
                "DisplayMetrics xdpi:(" + DM.xdpi + "); ydpi;(" + DM.ydpi + ");\n" +
                "DisplayMetrics density:(" + DM.density + "); densityDPI:(" + DM.densityDpi + ");\n"
        );
    }
    
    public static void initDisplay() {
        DM = app.getResources().getDisplayMetrics();
        SCREEN_WIDTH_PX = DM.widthPixels; // 屏幕宽（像素，如：480px）
        SCREEN_WIDTH_DP = (int) (DM.widthPixels / DM.density);
        SCREEN_HEIGHT_PX = DM.heightPixels; // 屏幕高（像素，如：800px）
        SCREEN_HEIGHT_DP = (int) (DM.heightPixels / DM.density);
    }
    
    /**
     * 初始化安装包信息。
     */
    public static void initAppInfo() {
        appPackageName = app.getPackageName();
        PackageManager pm = app.getPackageManager();
        PackageInfo info = null;
        try {
            info = pm.getPackageInfo(appPackageName, 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info != null) {
            appVersionName = info.versionName;
            appVersionCode = info.versionCode;
            appTargetSdkVersion = info.applicationInfo.targetSdkVersion;
        }
    }
    
    /**
     * 缓存路径, 优先外部存储
     * @return
     */
    public static String getCachePath() {
        String path = "";
        if (isExternalStorageAvailable && isExternalStorageWriteable && app.getExternalCacheDir() != null) {
            path = app.getExternalCacheDir().getAbsolutePath() + "/";
        } else {
            path = app.getCacheDir().getAbsolutePath() + "/";
        }
        return path;
    }
    
    public static DisplayMetrics getDM() {
        return DM;
    }
    
    
    public static void requestPhoneState(final Activity activity, final AbCallback callback) {
        //**需要 READ_PHONE_STATE 动态权限
        PermissionUtils.requestPermission(activity, PermissionUtils.CODE_READ_PHONE_STATE, new PermissionUtils.PermissionGrant() {
            @Override
            public void onPermissionGranted(int requestCode) {
                if (requestCode == PermissionUtils.CODE_READ_PHONE_STATE) {
                    TelephonyManager tm = (TelephonyManager) app.getSystemService(Context.TELEPHONY_SERVICE);
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    IMEI = tm.getDeviceId();
                    IMEI = IMEI != null ? IMEI : "";
                    IMSI = tm.getSubscriberId();
                    IMSI = IMSI != null ? IMSI : "";
    
                    callback.callback(true);
                }
            }
        });
    }
    
    /**
     * 获取联网状态符
     *
     * @return 状态符
     */
    public static int getConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        int netType = -1; //-1
        if (info == null || !info.isConnected()) {
            netType = -1;
        } else {
            netType = info.getType();
        }
        return netType;
    }
    
    public static boolean isNetworkConnected(){
        return getConnectivity() >= 0;
    }
    
    private static final int NETWORK_TYPE_UNAVAILABLE = -1;
    // private static final int NETWORK_TYPE_MOBILE = -100; 
    private static final int NETWORK_TYPE_WIFI = -101;
    
    private static final int NETWORK_CLASS_WIFI = -101;
    private static final int NETWORK_CLASS_UNAVAILABLE = -1;
    /** Unknown network class. */
    private static final int NETWORK_CLASS_UNKNOWN = 0;
    /** Class of broadly defined "2G" networks. */
    private static final int NETWORK_CLASS_2_G = 1;
    /** Class of broadly defined "3G" networks. */
    private static final int NETWORK_CLASS_3_G = 2;
    /** Class of broadly defined "4G" networks. */
    private static final int NETWORK_CLASS_4_G = 3;
    
    // 适配低版本手机 
    /** Network type is unknown */
    private static final int NETWORK_TYPE_UNKNOWN = 0;
    /** Current network is GPRS */
    private static final int NETWORK_TYPE_GPRS = 1;
    /** Current network is EDGE */
    private static final int NETWORK_TYPE_EDGE = 2;
    /** Current network is UMTS */
    private static final int NETWORK_TYPE_UMTS = 3;
    /** Current network is CDMA: Either IS95A or IS95B */
    private static final int NETWORK_TYPE_CDMA = 4;
    /** Current network is EVDO revision 0 */
    private static final int NETWORK_TYPE_EVDO_0 = 5;
    /** Current network is EVDO revision A */
    private static final int NETWORK_TYPE_EVDO_A = 6;
    /** Current network is 1xRTT */
    private static final int NETWORK_TYPE_1xRTT = 7;
    /** Current network is HSDPA */
    private static final int NETWORK_TYPE_HSDPA = 8;
    /** Current network is HSUPA */
    private static final int NETWORK_TYPE_HSUPA = 9;
    /** Current network is HSPA */
    private static final int NETWORK_TYPE_HSPA = 10;
    /** Current network is iDen */
    private static final int NETWORK_TYPE_IDEN = 11;
    /** Current network is EVDO revision B */
    private static final int NETWORK_TYPE_EVDO_B = 12;
    /** Current network is LTE */
    private static final int NETWORK_TYPE_LTE = 13;
    /** Current network is eHRPD */
    private static final int NETWORK_TYPE_EHRPD = 14;
    /** Current network is HSPA+ */
    private static final int NETWORK_TYPE_HSPAP = 15;
    
    
    public static final String NET_TYPE_WIFI = "Wi-Fi";
    /**
      * 获取网络类型
      *
      * @return
      */
    public static String getCurrentNetworkType() {
        int networkClass = getNetworkClass();
        String type = "";
        switch (networkClass) {
            case NETWORK_CLASS_UNAVAILABLE:
                type = "";
                break;
            case NETWORK_CLASS_WIFI:
                type = NET_TYPE_WIFI;
                break;
            case NETWORK_CLASS_2_G:
                type = NET_TYPE_2G;
                break;
            case NETWORK_CLASS_3_G:
                type = NET_TYPE_3G;
                break;
            case NETWORK_CLASS_4_G:
                type = NET_TYPE_4G;
                break;
            case NETWORK_CLASS_UNKNOWN:
                type = "";
                break;
        }
        return type;
    }
    
    private static int getNetworkClassByType(int networkType) {
        switch (networkType) {
            case NETWORK_TYPE_UNAVAILABLE:
                return NETWORK_CLASS_UNAVAILABLE;
            case NETWORK_TYPE_WIFI:
                return NETWORK_CLASS_WIFI;
            case NETWORK_TYPE_GPRS:
            case NETWORK_TYPE_EDGE:
            case NETWORK_TYPE_CDMA:
            case NETWORK_TYPE_1xRTT:
            case NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case NETWORK_TYPE_UMTS:
            case NETWORK_TYPE_EVDO_0:
            case NETWORK_TYPE_EVDO_A:
            case NETWORK_TYPE_HSDPA:
            case NETWORK_TYPE_HSUPA:
            case NETWORK_TYPE_HSPA:
            case NETWORK_TYPE_EVDO_B:
            case NETWORK_TYPE_EHRPD:
            case NETWORK_TYPE_HSPAP:
                return NETWORK_CLASS_3_G;
            case NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }
        
    private static int getNetworkClass() {
        int networkType = NETWORK_TYPE_UNKNOWN;
        try {
            final NetworkInfo network = ((ConnectivityManager)app
                    .getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();
            if (network != null && network.isAvailable()
                    && network.isConnected()) {
                int type = network.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    networkType = NETWORK_TYPE_WIFI;
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    TelephonyManager telephonyManager = (TelephonyManager)app.getSystemService(
                                    Context.TELEPHONY_SERVICE);
                    networkType = telephonyManager.getNetworkType();
                }
            } else{
                networkType=NETWORK_TYPE_UNAVAILABLE;
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return getNetworkClassByType(networkType);
        
    }
    
    
}
