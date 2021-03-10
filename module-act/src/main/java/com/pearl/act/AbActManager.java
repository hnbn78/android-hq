package com.pearl.act;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.ab.debug.AbDebug;
import com.ab.global.Global;
import com.ab.util.Strs;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Activity 管理者
 *
 * @author ChenLei
 */
public class AbActManager {
    private static List<WeakReference<Activity>> activitys;

    private AbActManager() {
        super();
    }

    public static int getCount() {
        return activitys.size();
    }

    public static void init() {
        activitys = new ArrayList<>();
    }

    public static void destroy() {
        activitys.clear();
        activitys = null;
    }


    /**
     * 添加到管理器
     *
     * @param act
     */
    public static void add(Activity act) {
        if (getActRef(act) == null) {
            activitys.add(new WeakReference<Activity>(act));
            AbDebug.log(AbDebug.TAG_UI, "activity 加入列表: " + act.getClass().getSimpleName() + "!\n");
        }
    }

    /**
     * 移除
     *
     * @param act
     */
    public static void remove(Activity act) {
        WeakReference<Activity> ref = getActRef(act);
        if (ref != null) {
            activitys.remove(ref);
            AbDebug.log(AbDebug.TAG_UI, "activity 从列表移除:" + act.getClass().getSimpleName() + "!\n");
        }
    }

    /**
     * @param actName 必须为全限定class name.
     * @return
     */
    public static Activity findActByFullName(String actName) {
        if (Strs.isEmpty(actName)) {
            return null;
        }
        Activity act = null;
        for (int i = activitys.size() - 1; i >= 0; i--) {
            act = activitys.get(i).get();
            if (act != null && act.getClass().getName().equals(actName)) {
                return act;
            }
        }
        return null;
    }

    /**
     * 是否已经存在, 只判断一个
     *
     * @param targetClassName
     * @return
     */
    public static boolean isContainActFullName(String targetClassName) {
        for (WeakReference<Activity> item : activitys) {
            Activity activity = item.get();
            if (activity != null && activity.getClass().getName().equals(targetClassName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 返回引用.
     *
     * @return
     */
    private static WeakReference<Activity> getActRef(Activity act) {
        Activity activity = null;
        for (WeakReference<Activity> item : activitys) {
            activity = item.get();
            if (activity != null && activity == act) {
                return item;
            }
        }
        return null;
    }

    /**
     * 移除结束所有此类名的activity
     *
     * @param targetClassName 必须为全限定class name.
     */
    public static void finishActByFullName(String targetClassName) {
        WeakReference<Activity>[] arrRefRemoved = new WeakReference[activitys.size()];
        WeakReference<Activity> reference = null;
        Activity act = null;
        for (int i = 0; i < activitys.size(); i++) {
            reference = activitys.get(i);
            act = reference.get();
            if (act != null && act.getClass().getName().equals(targetClassName)) {
                arrRefRemoved[i] = reference;
            }
        }
        for (int i = 0; i < arrRefRemoved.length; i++) {
            reference = arrRefRemoved[i];
            if (reference != null) {
                act = reference.get();
                if (act != null) {
                    act.finish();
                    act = null;
                }
                activitys.remove(reference);
                reference = null;
            }
        }
    }

    public static void clearAndFinishAll() {
        for (int i = 0; i < activitys.size(); i++) {
            Activity act = activitys.get(i).get();
            if (act != null) {
                act.finish();
            }
        }
        activitys.clear();
    }

    public static void launchAct(Context ctx, String className, Bundle optBundle) {
        if (ctx == null || Strs.isEmpty(className)) {
            return;
        }
        try {
            Class clazz = ctx.getClassLoader().loadClass(className);
            launchAct(ctx, clazz, optBundle);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void launchAct(Context ctx, Class<Activity> clazz, Bundle optBundle) {
        Intent itt = new Intent(ctx, clazz);
        if (optBundle != null) {
            itt.putExtras(optBundle);
        }
        ctx.startActivity(itt);
    }


    /**
     * 退出,kill掉进程
     */
    public static void exitAndKill() {
        clearAndFinishAll();
        if (ActModule.isAnalyticsEnable) {
            //统计退出
        }
        System.exit(0);
    }

    public static void info() {
        StringBuilder builder = new StringBuilder();

        for (WeakReference<Activity> ref : activitys
                ) {
            String name = (ref.get() != null ? ref.get().getClass().getSimpleName() : "null");
            if (builder.toString().isEmpty()) {
                builder.append("[" + name);
            } else {
                builder.append(", " + name);
            }
        }
        builder.append("]");

        AbDebug.log(AbDebug.TAG_UI, "ActManager 当前数量:" + getCount() + "详细:" + builder.toString());
    }

    /**
     * 退出，进程不Kill
     */
    public static void exitWithoutKillProcess() {
        clearAndFinishAll();
    }

    public static void exitAndRestart() {
        if (ActModule.isAnalyticsEnable) {
            //统计退出
        }
        exitWithoutKillProcess();
        final Intent intent = Global.app.getPackageManager().getLaunchIntentForPackage(Global.app.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Global.app.startActivity(intent);
    }

    /**
     * 用户速登录统计
     *
     * @param account
     */
    public static void onProfileSignIn(String account) {

    }

    /**
     * 用户退出统计
     */
    public static void onProfileSignOff() {

    }

    /**
     * 计数事件
     *
     * @param eventId
     * @param params
     */
    public static void onEvent(String eventId, String... params) {
        if (Strs.isEmpty(eventId)) {
            return;
        }

    }

    /**
     * 数值事件统计
     *
     * @param eventId
     * @param value
     * @param params
     */
    public static void onValueEvent(String eventId, int value, String... params) {
        if (Strs.isEmpty(eventId)) {
            return;
        }
    }

    /**
     * 手动发送错误日志
     *
     * @param str
     */
    public static void sendErrorReport(String str) {

    }

    /**
     * 更新在线参数
     */
    public static void updateOnlineConfig() {

    }

    /**
     * 提取在线参数
     *
     * @param key
     * @return
     */
    public static String getOnlineConfigParam(String key) {
        return "";
    }

    public interface IManagable {
        void addSelf();

        void removeSelf();
    }

    /**
     * 判断应用是否是在后台
     */
    public static boolean isBackground() {
        Context context = Global.app;
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (TextUtils.equals(appProcess.processName, context.getPackageName())) {
                boolean isBackground = (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE);
                boolean isLockedState = keyguardManager.inKeyguardRestrictedInputMode();
                return isBackground || isLockedState;
            }
        }
        return false;
    }

    public static Activity getPeelActivity() {
        Activity currentActivity = null;
        if (activitys != null && activitys.size() > 0 && activitys.get(0) != null) {
            if (getCount() > 0) {
                currentActivity = activitys.get(getCount() - 1).get();
            }
        }
        return currentActivity;
    }

}
