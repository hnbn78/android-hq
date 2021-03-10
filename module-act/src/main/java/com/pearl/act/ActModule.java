package com.pearl.act;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.ab.module.IAct;
import com.ab.module.ModuleBase;
import com.pearl.act.base.AbBaseFragment;

/**
 * Created by lee o 2017/9/21.
 */

public  class ActModule extends ModuleBase implements IAct {
    public static final String NAME = ActModule.class.getSimpleName();
    public static final int VERSION = 100;
    public static final String DESC = "管理activity与fragment, 集成友盟";
    public static  boolean isAnalyticsEnable;
    public static String analyticsAppKey;
    public static int icLuncherResId;
    
    public ActModule(boolean isAnalyticsEnable, String analyticsAppKey, int icLuncherResId) {
        super(NAME, VERSION, DESC);
        ActModule.isAnalyticsEnable = isAnalyticsEnable;
        ActModule.analyticsAppKey = analyticsAppKey;
        ActModule.icLuncherResId = icLuncherResId;
    }
    
    @Override
    public void onPreConfig() {
    
    }
    
    @Override
    public void onPostConfig() {
        /**
         * 设置组件化的Log开关
         * 参数: boolean 默认为false，如需查看LOG设置为true
         */
        if(ActModule.isAnalyticsEnable){
        
        }
    }
    
    @Override
    public void onCreate(Application app) {
        AbActManager.init();
        AbFragManager.init();
      
        //统计初始化
        if(ActModule.isAnalyticsEnable) {
        
        }
    }
    
    
    @Override
    public void onDestroy() {
        AbActManager.destroy();
        AbFragManager.destroy();
    }
    
    @Override
    public int getActCount() {
        return AbActManager.getCount();
    }
    
    @Override
    public void addAct(Activity act) {
        AbActManager.add(act);
    }
    
    @Override
    public void removeAct(Activity act) {
        AbActManager.remove(act);
    }
    
    @Override
    public Activity findActByFullName(String actName) {
        return AbActManager.findActByFullName(actName);
    }
    
    @Override
    public boolean isContainActFullName(String targetClassName) {
        return AbActManager.isContainActFullName(targetClassName);
    }
    
    @Override
    public void finishActByFullName(String targetClassName) {
        AbActManager.finishActByFullName(targetClassName);
    }
    
    @Override
    public void clearAndFinishAll() {
        AbActManager.clearAndFinishAll();
    }
    
    @Override
    public void launchAct(Context ctx, String className, Bundle optBundle) {
        AbActManager.launchAct(ctx, className, optBundle);
    }
    
    @Override
    public void launchAct(Context ctx, Class<Activity> clazz, Bundle optBundle) {
        AbActManager.launchAct(ctx, clazz, optBundle);
    }
    
    @Override
    public void exitAndKill() {
        AbActManager.exitAndKill();
    }
    
    @Override
    public void exitWithoutKillProcess() {
        AbActManager.exitWithoutKillProcess();
    }
    
    @Override
    public void exitAndRestart() {
        AbActManager.exitAndRestart();
    }
    
    @Override
    public void createFragGroup(String groupName, int containerResId) {
        AbFragManager.createGroup(groupName, containerResId);
    }
    
    @Override
    public void putIntoFragGroup(FragmentManager manager, String groupName, String fragToken, Fragment fragment) {
        AbFragManager.putIntoGroup(manager, groupName, fragToken, (AbBaseFragment) fragment);
    }
    
    @Override
    public void putIntoGroup(FragmentManager manager, String groupName, int index, Fragment fragment) {
        AbFragManager.putIntoGroup(manager, groupName, index, (AbBaseFragment) fragment);
    }
    
    @Override
    public void showOneFromGroup(FragmentManager manager, String groupName, String fragToken) {
        AbFragManager.showOneFromGroup(manager, groupName, fragToken);
    }
    
    @Override
    public void showOneFromGroup(FragmentManager manager, String groupName, int index) {
        AbFragManager.showOneFromGroup(manager, groupName, index);
    }
    
    @Override
    public void reportError(String report) {
        AbActManager.sendErrorReport(report);
    }
    
    @Override
    public void updateOnlineConfig() {
        if(isAnalyticsEnable){
            AbActManager.updateOnlineConfig();
        }
    }
    
    @Override
    public String getOnlineConfigParam(String key) {
        if(isAnalyticsEnable){
           return AbActManager.getOnlineConfigParam(key);
        }
        return null;
    }
    
    @Override
    public void onProfileSignIn(String account) {
        if(isAnalyticsEnable) {
            AbActManager.onProfileSignIn(account);
        }
    }
    
    @Override
    public void onProfileSignOff() {
        if(isAnalyticsEnable) {
            AbActManager.onProfileSignOff();
        }
    }
    
    @Override
    public void onEvent(String eventId, String... params) {
        if(isAnalyticsEnable) {
            AbActManager.onEvent(eventId, params);
        }
    }
    
    @Override
    public void onValueEvent(String eventId, int value, String... params) {
        if(isAnalyticsEnable) {
            AbActManager.onValueEvent(eventId, value, params);
        }
    }
}
