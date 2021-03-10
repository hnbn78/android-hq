package com.desheng.base.global;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.ab.global.Config;
import com.ab.global.Global;
import com.uuzuche.lib_zxing.ZApplication;

import java.util.List;

/**
 * Created by lee on 16/9/11.
 */
public abstract class AppBase extends ZApplication {

    @Override
    public void onCreate() {
        super.onCreate();

    }
    
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    
    @Override
    public void onTerminate() {
        Global.destroy();
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        System.gc();
        super.onLowMemory();
    }
    
    /**
     * @return null may be returned if the specified process not found
     */
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }
    
    protected void loadConfig(Class<? extends Config> clazz){
        //让各子类加载, 执行父类配置修改.
        try {
            clazz.newInstance();
            //保留, 默认不初始化listEventLogs, 完善时加入更多调试信息
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
