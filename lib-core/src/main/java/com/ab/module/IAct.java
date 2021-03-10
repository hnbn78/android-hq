package com.ab.module;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by lee on 2017/9/27.
 */

public interface IAct {
    int getActCount();
    void addAct(Activity act);
    void removeAct(Activity act);
    Activity findActByFullName(String actName);
    boolean isContainActFullName(String targetClassName);
    void finishActByFullName(String targetClassName);
    void clearAndFinishAll();
    void launchAct(Context ctx, String className, Bundle optBundle);
    void launchAct(Context ctx, Class<Activity> clazz, Bundle optBundle);
    void exitAndKill();
    void exitWithoutKillProcess();
    void exitAndRestart();
    
    void createFragGroup(String groupName, int containerResId);
    void putIntoFragGroup(FragmentManager manager, String groupName, String fragToken, Fragment fragment);
    void putIntoGroup(FragmentManager manager, String groupName, int index, Fragment fragment);
    void showOneFromGroup(FragmentManager manager, String groupName, String fragToken);
    void showOneFromGroup(FragmentManager manager, String groupName, int index);
    
    void reportError(String report);
    void updateOnlineConfig();
    String getOnlineConfigParam(String key);
    
    void onProfileSignIn(String account);
    void onProfileSignOff();
    
    void onEvent(String eventId, String ... params);
    void onValueEvent(String eventId, int value, String ... params);
}
