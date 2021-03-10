package com.pearl.push;

import android.app.Activity;
import android.content.Context;

import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

public abstract class IUmengPushCallback extends UmengMessageHandler {
    /**
     * 自定义消息的回调方法
     */
    public abstract void dealWithCustomMessage(final Context context, final UMessage msg);
    
    public abstract void doWithCustomMsg(Context context, String msgJson);
    
    public abstract void registActivity(Activity activity);
    
    public abstract void unregist();
    
    public abstract void doMsg(Object msg);
}
