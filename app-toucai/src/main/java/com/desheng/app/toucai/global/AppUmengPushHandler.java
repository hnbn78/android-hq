package com.desheng.app.toucai.global;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.ab.debug.AbDebug;
import com.ab.global.Global;
import com.ab.module.MM;
import com.ab.thread.ThreadCollector;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.view.ActMaterialDialog;
import com.desheng.app.toucai.consitance.CommonConsts;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.MissionPushContentMode;
import com.desheng.app.toucai.model.ZhongjiangMode;
import com.desheng.app.toucai.panel.ActLoading;
import com.desheng.app.toucai.panel.ActMain;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.manager.UserManager;
import com.desheng.base.panel.ActWeb;
import com.desheng.base.panel.ActWebX5;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.pearl.act.AbActManager;
import com.pearl.push.IPushUmeng;
import com.pearl.push.IUmengPushCallback;
import com.shark.tc.R;
import com.umeng.message.entity.UMessage;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AppUmengPushHandler extends IUmengPushCallback {
    private View view;
    private WindowManager wm;
    private boolean showWm = true;//默认是应该显示悬浮通知栏
    private WindowManager.LayoutParams params;
    private int NotificationId = 0;
    /**
     * 上下文引用
     */
    private static WeakReference<Activity> ctxRef;

    public AppUmengPushHandler() {
        initWindowManager();
        //设置全局弹窗dialog
        ActMaterialDialog.ACTION_SHOW = "com.ab.action.toucai.showDialog";
        ActMaterialDialog.ACTION_HIDE = "com.ab.action.toucai.hideDialog";
    }

    @Override
    public void dealWithCustomMessage(final Context context, final UMessage msg) {
        ThreadCollector.getIns().runOnUIThread(new Runnable() {

            @Override
            public void run() {
                doWithCustomMsg(context, msg.custom);
            }
        });
    }

    @Override
    public void doWithCustomMsg(Context context, String joson) {
        Log.d("AppUmengPushHandler", joson);
        Gson gson = new Gson();
        CustomMsg jo = gson.fromJson(joson, CustomMsg.class);
        if (Strs.isNotEmpty(jo.content)) {
            jo.setContent(jo.content.replace("<br/>", "\n"));
        }

        String ticker = "";
        switch (jo.code) {
            case 0:
                ticker = "充值消息";
                UserManagerTouCai.getIns().refreshUserData();
                break;
            case 1:
                ticker = "提现消息";
                UserManagerTouCai.getIns().refreshUserData();
                break;
            case 2:
                ticker = "派奖消息";
                UserManagerTouCai.getIns().refreshUserData();
                break;
            case 3:
                ticker = "大奖全局公告";
                //UserManagerTouCai.getIns().refreshUserData();
                break;
            case 4:
                ticker = "推送活动链接";
                UserManagerTouCai.getIns().refreshUserData();
                jo.setContent(jo.getContent().replace("&quot;", "\"")
                        .replace("\"{", "{")
                        .replace("}\"", "}"));
                break;
        }

        Log.d("AppUmengPushHandler", "ticker:" + ticker + "---jo.code:" + jo.code + "---content:" + jo.content);

        boolean isBackground = AbActManager.isBackground();

        String contenPushStr = "";

        if (!isBackground && ctxRef != null) {  //应用在前台, 弹悬浮框
            Activity activity = ctxRef.get();
            if (activity != null) {
                switch (jo.code) {
                    case 0:
                        DialogsTouCai.showDialog(activity, "温馨提醒", "恭喜您,充值成功", v -> {
                            UserManagerTouCai.getIns().sendBroadcaset(context, UserManagerTouCai.EVENT_USER_DEPOSIT_OK, null);

                        }, false);
                        contenPushStr = jo.content;
                        break;
                    case 2:
                        try {
                            ZhongjiangMode zhongjiangMode = gson.fromJson(jo.content, ZhongjiangMode.class);

                            if (zhongjiangMode == null) {
                                return;
                            }

                            String content = "";
                            String caizhong = "";

                            caizhong = zhongjiangMode.getLottery();
                            String qihao = zhongjiangMode.getIssue();
                            String awad = Nums.formatDecimal(String.valueOf(zhongjiangMode.getBonus()), 3);
                            int skinSetting = UserManager.getIns().getSkinSetting();

                            if (skinSetting == 0) {
                                content = "在" + caizhong + " 第" + qihao + "期\n" + "喜中" + "<font color='#FEEA3A'><small>¥ </small><big>" + awad + "</big></font>" + " 彩金";
                            } else if (skinSetting == 1) {
                                content = "在" + caizhong + " 第" + qihao + "期\n" + "喜中" + "<font color='red'><small>¥ </small><big>" + awad + "</big></font>" + " 彩金";
                            }

                            contenPushStr = "在" + caizhong + " 第" + qihao + "期 " + "喜中 " + awad + " 彩金";

                            String finalCaizhong = caizhong;
                            DialogsTouCai.showAwardDialog(activity, content, CommonConsts.setAwadDialogResId(skinSetting), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (UserManager.getIns().isLogined()) {
                                        if (Strs.isNotEmpty(finalCaizhong)) {
                                            CtxLotteryTouCai.launchLotteryPlay(activity, zhongjiangMode.getLotteryId());
                                        }
                                    } else {
                                        UserManager.getIns().redirectToLogin(activity);
                                    }
                                }
                            });
                        } catch (JsonParseException e) {

                        }
                        break;
                    case -1:
                        DialogsTouCai.showDialog(activity, "温馨提醒", "尊敬的会员，您申请的提现因流水不足或其他原因审批不通过，了解详情请联系在线客服",
                                "联系客服", "确定", v -> {
                                    ActWeb.launchCustomService(activity);
                                }, false);
                        return;
                }
            }

            if (jo.code != 3) {
                if (jo.code == 4) {
                    try {
                        MissionPushContentMode pushContentMode = gson.fromJson(jo.getContent(), MissionPushContentMode.class);
                        createFloatView(jo, pushContentMode, contenPushStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    createFloatView(jo, contenPushStr);
                }
            }
        }

        if (jo.code == 4) {
            Log.d("AppUmengPushHandler", jo.getContent());
            try {
                MissionPushContentMode pushContentMode = gson.fromJson(jo.getContent(), MissionPushContentMode.class);
                sendNotificationWithRemote(context, jo, pushContentMode, ticker);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            sendNotification(context, jo, contenPushStr, ticker);
        }
    }

    public void sendNotification(Context context, CustomMsg jo, String content, String ticker) {
        //简单启动
        /*Intent intent = new Intent(context, ActLoading.class);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);*/

        //判定状态启动
        Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
        broadcastIntent.putExtra("pushCustomMsg", jo);
        PendingIntent pendingIntent = PendingIntent.
                getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification notification1 = null;

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText(Strs.isEmpty(content) ? jo.content : content);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel mChannel = new NotificationChannel(channelId, channename, NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(mChannel);

            notification1 = new Notification.Builder(context)
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setOngoing(false)
                    .setWhen(System.currentTimeMillis())
                    .setTicker(ticker)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .setContentTitle(jo.title)
                    .setContentText(Strs.isEmpty(content) ? jo.content : content)
                    //.setStyle(style)
                    .setSmallIcon(R.mipmap.ic_launcher).build();
        } else {

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channename)
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setOngoing(false)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setWhen(System.currentTimeMillis())
                    .setTicker(ticker)
                    .setAutoCancel(true)
                    .setContentTitle(jo.title)
                    .setContentText(Strs.isEmpty(content) ? jo.content : content)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setStyle(style)
                    .setOngoing(true);
            notification1 = notificationBuilder.build();
        }

        manager.notify(NotificationId++, notification1);
    }

    String channelId = "my_channel_01";
    String channename = "活动消息推送";

    public void sendNotificationWithRemote(Context context, CustomMsg jo, MissionPushContentMode pushContentMode, String ticker) {

        Log.e("AppUmengPushHandler", "isAppRunning(context):" + isAppRunning(context));

//        Intent intent = new Intent(context, ActLoading.class);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.setAction(Intent.ACTION_MAIN);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("pushContentMode", jo.getContent());
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
        broadcastIntent.putExtra("pushContentMode", jo.getContent());
        PendingIntent pendingIntent = PendingIntent.
                getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //----------------------------------------

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification notification1 = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel mChannel = new NotificationChannel(channelId, channename, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);

            notification1 = new Notification.Builder(context)
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setOngoing(true)
                    .setWhen(System.currentTimeMillis())
                    .setTicker(ticker)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .setContentTitle(jo.title)
                    .setContentText(pushContentMode.getContent())
                    .setSmallIcon(R.mipmap.ic_launcher).build();
        } else {

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channename)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setWhen(System.currentTimeMillis())
                    .setTicker(ticker)
                    .setAutoCancel(true)
                    .setContentTitle(jo.title)
                    .setContentText(pushContentMode.getContent())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true);
            notification1 = notificationBuilder.build();
        }
        notificationManager.notify(jo.code, notification1);
    }

    @Override
    public Notification getNotification(Context context, UMessage msg) {
        switch (msg.builder_id) {
            case 1:
                Notification.Builder builder = new Notification.Builder(context);
                RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
                myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
                builder.setContent(myNotificationView)
                        .setSmallIcon(getSmallIconId(context, msg))
                        .setTicker(msg.ticker)
                        .setAutoCancel(true);

                return builder.getNotification();
            default:
                //默认为0，若填写的builder_id并不存在，也使用默认。
                return super.getNotification(context, msg);
        }
    }

    private void initWindowManager() {
        wm = (WindowManager) Global.app.getSystemService(
                Context.WINDOW_SERVICE);
        //注意是TYPE_SYSTEM_ERROR而不是TYPE_SYSTEM_ALERT
        //前面有SYSTEM才可以遮挡状态栏，不然的话只能在状态栏下显示通知栏

        //重点，类型设置为dialog类型,可无视权限!

        params = new WindowManager.LayoutParams(1, 1,
                WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        //设置必须触摸通知栏才可以关掉
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        // 设置通知栏的长和宽
        params.width = wm.getDefaultDisplay().getWidth();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP;
    }

    @Override
    public void registActivity(Activity activity) {
        ctxRef = new WeakReference<Activity>(activity);
    }

    @Override
    public void unregist() {
        if (ctxRef != null) {
            ctxRef.clear();
            ctxRef = null;
        }
        if (wm != null) {
            if (view != null) {
                try {
                    wm.removeViewImmediate(view);
                } catch (Exception e) {
                    AbDebug.error(AbDebug.TAG_PUSH, Thread.currentThread(), e);
                }
            }
            showWm = true;
        }
    }

    @Override
    public void doMsg(Object msg) {

    }

    private void createFloatView(CustomMsg msg, String content) {

        Log.d("AppUmengPushHandler", content);

        if (ctxRef == null || ctxRef.get() == null) {
            return;
        }
        Activity act = ctxRef.get();

        //重点,必须设置此参数，用于窗口机制验证
        IBinder windowToken = act.getWindow().getDecorView().getWindowToken();
        params.token = windowToken;

        if (!showWm) {
            if (wm != null && view != null) {
                wm.removeViewImmediate(view);
            }
            view = null;
        }

        view = LayoutInflater.from(act).inflate(R.layout.view_float_notice, null);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText(msg.getTitle());
        TextView tvContent = view.findViewById(R.id.tvContent);
        tvContent.setText(Strs.isEmpty(content) ? msg.getContent() : content);

        view.setTag(msg);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeViewImmediate(view);
                view = null;
                showWm = true;
                doMsg(v.getTag());
            }
        });

        //在这里你可以解析你的自定义的布局成一个View
        wm.addView(view, params);
        showWm = false;
        new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (wm != null && view != null) {
                    try {
                        wm.removeViewImmediate(view);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                view = null;
            }
        }.start();
    }

    private void createFloatView(CustomMsg msg, MissionPushContentMode pushContentMode, String content) {

        Log.d("AppUmengPushHandler", content);

        if (ctxRef == null || ctxRef.get() == null) {
            return;
        }
        Activity act = ctxRef.get();

        if (act == null) {
            return;
        }
        //重点,必须设置此参数，用于窗口机制验证
        IBinder windowToken = act.getWindow().getDecorView().getWindowToken();
        params.token = windowToken;

        if (!showWm) {
            if (wm != null && view != null) {
                wm.removeViewImmediate(view);
            }
            view = null;
        }

        view = LayoutInflater.from(act).inflate(R.layout.view_float_notice, null);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText(msg.getTitle());
        TextView tvContent = view.findViewById(R.id.tvContent);
        tvContent.setText(pushContentMode.getContent());

        view.setTag(msg);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeViewImmediate(view);
                view = null;
                showWm = true;
                doMsg(v.getTag());
            }
        });

        //在这里你可以解析你的自定义的布局成一个View
        wm.addView(view, params);
        showWm = false;
        new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (wm != null && view != null) {
                    try {
                        wm.removeViewImmediate(view);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                view = null;
            }
        }.start();
    }


    public static class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            AbDebug.log(AbDebug.TAG_PUSH, "push NotificationReceiver onReceive............");
            CustomMsg msg = (CustomMsg) intent.getSerializableExtra("pushCustomMsg");
            String pushContent = intent.getStringExtra("pushContentMode");
            //ActMain.mainKillSwitch, 有了说明app正在运行
            if (!ActMain.mainKillSwitch) {
                /*Intent mainIntent = new Intent(context, ActMain.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                mainIntent.putExtra("pushCustomMsg", msg);
                context.startActivity(mainIntent);*/

                if (msg != null) {
                    Intent mainIntent = new Intent(context, ActMain.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mainIntent.putExtra("toTabIndex", 1);
                    context.startActivity(mainIntent);
                }

                if (Strs.isNotEmpty(pushContent)) {
                    MissionPushContentMode pushContentMode = new Gson().fromJson(pushContent, MissionPushContentMode.class);
                    //将MainAtivity的launchMode设置成SingleTask, 或者在下面flag中加上Intent.FLAG_CLEAR_TOP,
                    //如果Task栈中有MainActivity的实例，就会把它移到栈顶，把在它之上的Activity都清理出栈，
                    //如果Task栈不存在MainActivity实例，则在栈顶创建
                    Intent mainIntent = new Intent(context, ActMain.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (pushContentMode.getLinkUrl().getType() == 1) {
                        mainIntent.putExtra("toTabIndex", 3);
                    } else if (pushContentMode.getLinkUrl().getType() == 2) {
                        mainIntent.putExtra("pushContentMode", pushContentMode.getLinkUrl().getLinkUrl());
                    }
                    context.startActivity(mainIntent);
                }

                //显示弹框
                ((IPushUmeng) MM.push).getUmengPushCallback().doMsg(msg);
            } else {
                //如果app进程已经被杀死，先重新启动app，将LoadingActivity的启动参数写入全局文件,到actmain中检测执行
                try {
                    Intent startIntent = new Intent(context, ActLoading.class);
                    startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    startIntent.setAction(Intent.ACTION_MAIN);
                    startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    startIntent.putExtra("pushCustomMsg", intent.getSerializableExtra("pushCustomMsg"));
                    if (Strs.isNotEmpty(pushContent)) {
                        startIntent.putExtra("pushContentMode", pushContent);
                    }
                    context.startActivity(startIntent);
                    AbDebug.log(AbDebug.TAG_PUSH, "push start ActLoading............");
                } catch (Exception e) {
                    AbDebug.error(AbDebug.TAG_PUSH, Thread.currentThread(), e);
                }
            }
        }
    }


    public static class CustomMsg implements Serializable {
        //"充值消息"
        public static final int CODE_CHARGE = 0;

        //"提现消息"
        public static final int CODE_WITHDRAW = 1;

        //"派奖消息"
        public static final int CODE_DISPATCH_MONEY = 2;

        private int code = -1;
        private String title;
        private String content;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "CustomMsg{" +
                    "code=" + code +
                    ", title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    public boolean isAppRunning(Context context) {
        String mainProcessName = context.getPackageName();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (list.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.baseActivity.getPackageName().equals(mainProcessName)) {
                return true;
            }
        }
        return false;
    }
}
