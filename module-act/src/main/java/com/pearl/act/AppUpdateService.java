package com.pearl.act;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.widget.RemoteViews;

import com.ab.debug.AbDebug;
import com.ab.global.AbDevice;
import com.ab.global.Config;
import com.ab.global.Global;
import com.ab.thread.ThreadCollector;
import com.ab.thread.ValueRunnable;
import com.pearl.act.download.DownloadConfig;
import com.pearl.act.download.DownloadManager;
import com.pearl.act.download.entity.DownloadEntry;
import com.pearl.act.download.notify.DataWatcher;

import java.io.File;

public class AppUpdateService extends Service {
    private static final String ACTION_UPDATE = BuildConfig.APPLICATION_ID + ".service.action.getUpdate";
    private static final String ACTION_STOP = BuildConfig.APPLICATION_ID + ".service.action.stopUpdate";
    
    public static final String EVENT_PROGRESS = BuildConfig.APPLICATION_ID + ".service.event.progress";
    
    private static final String EXTRA_FILE_NAME = BuildConfig.APPLICATION_ID + ".service.extra.file.name";
    private static final String EXTRA_URL = BuildConfig.APPLICATION_ID + ".service.extra.url";
    
    private Notification updateNotification;
    private NotificationManager updateNotificationManager;
    private PendingIntent updatePendingIntent;
    
    private int retryCount = 0;
    private static final int RETRY_MAX = 10;
    
    public static void startUpdate(Context context, String version, String updateUrl) {
        Intent intent = new Intent(context, AppUpdateService.class);
        intent.setAction(ACTION_UPDATE);
        intent.putExtra(EXTRA_FILE_NAME, version);
        intent.putExtra(EXTRA_URL, updateUrl);
        context.startService(intent);
    }
    
    public static void stopUpdate(Context context, String version, String updateUrl) {
        Intent intent = new Intent(context, AppUpdateService.class);
        intent.setAction(ACTION_STOP);
        intent.putExtra(EXTRA_FILE_NAME, version);
        intent.putExtra(EXTRA_URL, updateUrl);
        context.startService(intent);
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_UPDATE.equals(intent.getAction())) {
            DownloadManager.getInstance(this).addObserver(dataWatcher);
            String fileName = intent.getStringExtra(EXTRA_FILE_NAME);
            String updateUrl = intent.getStringExtra(EXTRA_URL);
            startDownLoad(fileName, updateUrl);
        } else if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopDownLoad(intent.getStringExtra(EXTRA_FILE_NAME), intent.getStringExtra(EXTRA_URL));
        }
        return super.onStartCommand(intent, flags, startId);
    }
    
    private void startDownLoad(String version, String updateUrl) {
        initRemoteView();
        startDownloadApk(version, updateUrl);
    }
    
    
    @SuppressLint("NewApi")
    private void initRemoteView() {
        try {
            updateNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            this.updateNotification = new Notification.Builder(this).setTicker("版本更新下载").setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.ic_download_ing).build();
            this.updatePendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, RemoteViews.class), 0);
            this.updateNotification.contentIntent = this.updatePendingIntent;
            this.updateNotification.contentView = new RemoteViews(getApplication().getPackageName(), R.layout.view_download_progress);
            this.updateNotification.contentView.setImageViewResource(R.id.imageView1, ActModule.icLuncherResId);
            this.updateNotification.contentView.setProgressBar(R.id.progressBar1, 100, 0, false);
            this.updateNotification.contentView.setTextViewText(R.id.textView1, "0%");
            this.updateNotificationManager.notify(0, this.updateNotification);
        } catch (Exception e) {
            AbDebug.error(AbDebug.TAG_APP, Thread.currentThread(), e);
        }
    }

   
    @SuppressLint("NewApi")
    private DataWatcher dataWatcher = new DataWatcher() {
        @Override
        public void onDataChanged(DownloadEntry data) {
            //正在下载
            if (DownloadEntry.DownloadStatus.downloading.equals(data.status)) {
                if (updateNotification.contentView != null) {
                    updateNotification.contentView.setProgressBar(R.id.progressBar1, 100, data.percent, false);
                    updateNotification.contentView.setTextViewText(R.id.textView1, data.percent + "%");
                    updateNotification.contentIntent = updatePendingIntent;
                    updateNotificationManager.notify(0, updateNotification);
                }
                
                //发布进度广播
                Intent itt = new Intent(EVENT_PROGRESS);
                itt.putExtra("progress", data.percent);
                sendBroadcast(itt);
            } else if (DownloadEntry.DownloadStatus.done.equals(data.status)) {
                try {
                    Intent itt = new Intent(EVENT_PROGRESS);
                    itt.putExtra("progress", 100);
                    sendBroadcast(itt);
                    //下载完成
                    installApk(DownloadConfig.DOWNLOAD_PATH + data.name);
                    updateNotificationManager.cancel(0);
                } catch (Exception ex) {
                    AbDebug.error(AbDebug.TAG_APP, Thread.currentThread(), ex);
                }
            } else if (DownloadEntry.DownloadStatus.error.equals(data.status)) {
                try {
                    Notification notification = new Notification.Builder(AppUpdateService.this).setContentTitle(getString(R.string.app_name)).setContentText("下载失败").setSmallIcon(R.drawable.download).build();
                    updateNotificationManager.notify(0, notification);
                    if (retryCount < RETRY_MAX) {
                        ThreadCollector.getIns().postDelayOnUIThread(3000, new ValueRunnable(data) {
                            @Override
                            public void run() {
                                DownloadManager.getInstance(AppUpdateService.this).resume((DownloadEntry) getValue());
                            }
                        });
                    } else {
                        DownloadManager.getInstance(AppUpdateService.this).cancel(data);
                    }
                    retryCount++;
                } catch (Exception ex) {
                    AbDebug.error(AbDebug.TAG_APP, Thread.currentThread(), ex);
                }
            }
        }
    };

    /*private void installApk(String filePath) {
        try {
            Uri uri = Uri.fromFile(new File(filePath));
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
            startActivity(installIntent);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }*/
    
    private void installApk(String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(Global.app, AbDevice.appPackageName + ".fileProvider", new File(filePath));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
        }
        Global.app.startActivity(intent);
    }
    
    private void startDownloadApk(String version, String downloadUrl) {
        DownloadEntry entry = new DownloadEntry(downloadUrl, getFileName(version));
        DownloadManager.getInstance(AppUpdateService.this).add(entry);
    }
    
    private void stopDownLoad(String version, String downloadUrl) {
        DownloadEntry entry = new DownloadEntry(downloadUrl, getFileName(version));
        DownloadManager.getInstance(AppUpdateService.this).cancel(entry);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        DownloadManager.getInstance(this).removeObserver(dataWatcher);
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    
    public String getFileName(String version) {
        return "app_" + Config.custom_flag.replace("flag_", "") + "_" + version + ".apk";
    }
}
