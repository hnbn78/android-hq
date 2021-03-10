package com.ab.global;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.ab.thread.ThreadCollector;

public class Global {
    public static Application app;
    public static String appPackageName = null;
    public static String appVersionName = null;
    public static int appVersionCode = 0;
    
    public static boolean isInited = false;
    /**
     * 初始化andbase，
     * AbImageLoader初始化， 上传已缓存到本地的图片使用。
     * @param application
     */
    public static void init(Application application){
        if(!isInited){
            app = application;
            AbDevice.init(app);
            initAppInfo();
            ThreadCollector.getIns().init(application);
            isInited = true;
        }
    }
    
    public static void destroy(){
        if(isInited){
            app = null;
            isInited = false;
            ThreadCollector.getIns().clear();
        }
    }
    
    /**
     * 初始化安装包信息。
     */
    public static void initAppInfo(){
        appPackageName = Global.app.getPackageName();
        PackageManager pm = Global.app.getPackageManager();
        PackageInfo info = null;
        try {
            info = pm.getPackageInfo(appPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(info != null) {
            appVersionName = info.versionName;
            appVersionCode= info.versionCode;
        }
    }
    
}
