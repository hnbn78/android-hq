package com.desheng.app.toucai.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtils {
    private Context context;
    private NotificationManager notificationManager;
    private static int id = 0;

    private static class NotificationUtilsHolder {
        public static final NotificationUtils notificationUtils = new NotificationUtils();
    }

    private NotificationUtils(){
    }

    public static NotificationUtils getInstance() {
        return NotificationUtilsHolder.notificationUtils;
    }

    /**
     * 初始化
     * @param context 引用全局上下文
     */
    public void init(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }

    /**
     * 创建通知通道
     * @param channelId 通道id
     * @param channelName 通道名称
     * @param importance 通道级别
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel(String channelId, String channelName, int importance) {

        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * 发送通知
     * @param channelId 通道id
     * @param title 标题
     * @param content 内容
     * @param intent 意图
     */
    public void sendNotification(String channelId, String title, String content, Intent intent) {

        PendingIntent pendingIntent = null;
        if (intent != null) {
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setGroupSummary(true)
                .setContentIntent(pendingIntent)
                .build();

        notificationManager.notify(id++, notification);
    }
}
