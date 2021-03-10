package com.desheng.base.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.ab.thread.ThreadCollector;
import com.desheng.base.service.FloatWorldClubService;
import com.desheng.base.view.FloatCallBack;

import java.lang.ref.WeakReference;


/**
 * Author:xishuang
 * Date:2017.08.01
 * Des:与悬浮窗交互的控制类，真正的实现逻辑不在这
 */
public class FloatWorldClubActionController {

    private FloatWorldClubActionController() {
    }
    
    public static FloatWorldClubActionController getInstance() {
        return LittleMonkProviderHolder.sInstance;
    }
    
    // 静态内部类
    private static class LittleMonkProviderHolder {
        private static final FloatWorldClubActionController sInstance = new FloatWorldClubActionController();
    }
    
    private FloatCallBack mFloatCallBack;
    

    public static void showWorldClub(final Activity act) {
        final WeakReference<Activity> actRef = new WeakReference<Activity>(act);
        ThreadCollector.getIns().postDelayOnUIThread(100, new Runnable() {
            @Override
            public void run() {
                FloatWorldClubActionController.getInstance().hide();
                if(actRef != null && actRef.get() != null && !actRef.get().isFinishing()){
                    FloatWorldClubActionController.getInstance().show(act);
                }
            }
        });
    }

    /**
     * 开启世界杯服务悬浮窗
     */
    public void startWorldClubServer(Context context) {
        Intent intent = new Intent(context, FloatWorldClubService.class);
        context.startService(intent);
    }

    /**
     * 关闭世界杯悬浮窗
     */
    public void stopWorldClubServer(Context context) {
        Intent intent = new Intent(context, FloatWorldClubService.class);
        context.stopService(intent);
    }
    
    /**
     * 注册监听
     */
    public void registerWorldClubListener(FloatCallBack floatCallBack) {
        mFloatCallBack = floatCallBack;
    }
    
    /**
     * 调用引导的方法
     */
    public void callGuide(int type) {
        if (mFloatCallBack == null) {
            return;
        }
        mFloatCallBack.guideUser(type);
    }
    
    /**
     * 悬浮窗的显示
     */
    public void show(Activity act) {
        if (mFloatCallBack == null) {
            return;
        }
        mFloatCallBack.show(act);
    }
    
    /**
     * 悬浮窗的隐藏
     */
    public void hide() {
        if (mFloatCallBack == null) {
            return;
        }
        mFloatCallBack.hide();
    }
    
    /**
     * 增加数量
     */
    public void addObtainNumer() {
        if (mFloatCallBack == null) {
            return;
        }
        mFloatCallBack.addObtainNumer();
    }
    
    /**
     * 设置数量
     */
    public void setObtainNumber(int number) {
        if (mFloatCallBack == null) {
            return;
        }
        mFloatCallBack.setObtainNumber(number);
    }
}
