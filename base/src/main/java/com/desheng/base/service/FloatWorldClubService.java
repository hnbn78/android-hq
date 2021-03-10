package com.desheng.base.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.desheng.base.controller.FloatWorldClubActionController;
import com.desheng.base.manager.FloatWorldClubManager;
import com.desheng.base.receiver.HomeWatcherReceiver;
import com.desheng.base.view.FloatCallBack;


/**
 * 悬浮窗在服务中创建，通过暴露接口FloatCallBack与Activity进行交互
 */
public class FloatWorldClubService extends Service implements FloatCallBack {
    /**
     * home键监听
     */
    private HomeWatcherReceiver mHomeKeyReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        FloatWorldClubActionController.getInstance().registerWorldClubListener(this);
        //注册广播接收者
        mHomeKeyReceiver = new HomeWatcherReceiver();
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeKeyReceiver, homeFilter);
        //初始化悬浮窗UI
        initWindowData();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 初始化WindowManager
     */
    private void initWindowData() {
        FloatWorldClubManager.createFloatWindow(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //移除悬浮窗
        FloatWorldClubManager.removeFloatWindowManager();
        //注销广播接收者
        if (null != mHomeKeyReceiver) {
            unregisterReceiver(mHomeKeyReceiver);
        }
    }

    /////////////////////////////////////////////////////////实现接口////////////////////////////////////////////////////
    @Override
    public void guideUser(int type) {
        FloatWorldClubManager.updataRedAndDialog(this);
    }


    /**
     * 悬浮窗的隐藏
     */
    @Override
    public void hide() {
        FloatWorldClubManager.hide();
    }

    /**
     * 悬浮窗的显示
     */
    @Override
    public void show(Activity act) {
        FloatWorldClubManager.show(act);
    }

    /**
     * 添加数量
     */
    @Override
    public void addObtainNumer() {
        FloatWorldClubManager.addObtainNumer(this);
        guideUser(4);
    }

    /**
     * 减少数量
     */
    @Override
    public void setObtainNumber(int number) {
        FloatWorldClubManager.setObtainNumber(this, number);
    }
}
