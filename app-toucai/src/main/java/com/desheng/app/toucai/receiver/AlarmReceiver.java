package com.desheng.app.toucai.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.desheng.app.toucai.model.eventmode.BonusPoolEventMode;

import org.greenrobot.eventbus.EventBus;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //可以执行一个有趣的操作，我们这里只是显示当前时间
        EventBus.getDefault().post(new BonusPoolEventMode());
    }
}
