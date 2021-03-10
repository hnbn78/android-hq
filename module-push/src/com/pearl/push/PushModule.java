package com.pearl.push;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.ab.debug.AbDebug;
import com.ab.global.Config;
import com.ab.global.Global;
import com.ab.module.ModuleBase;
import com.ab.push.AbPushMessageHandler;
import com.ab.push.IPushCallback;
import com.ab.thread.ThreadCollector;
import com.ab.util.Strs;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

/**
 * Created by lee on 2017/10/17.
 */

public class PushModule extends ModuleBase implements IPushUmeng{
    
    public static final String NAME = "推送模块";
    public static final int VERSION = 1;
    public static final String DESC = "友盟推送";
    public static final String UPDATE_STATUS_ACTION = "com.umeng.message.example.action.UPDATE_STATUS";
    private static final String TAG = AbDebug.TAG_PUSH;
    private String channel;
    private String appKey;
    private String appSecret;
    private IUmengPushCallback callback;
    private Handler handler;
    private PushAgent mPushAgent;
    private String deviceToken;
    private boolean isInited;
    
    public PushModule(String channel, String appId, String secret, IUmengPushCallback callback) {
        super(NAME, VERSION, DESC);
        this.channel = channel;
        this.appKey = appId;
        this.appSecret = secret;
        this.callback = callback;
    }
    
    @Override
    public boolean isAllProcessInit() {
        return true;
    }
    
    @Override
    public void onPreConfig() {
        UMConfigure.init(Global.app, appKey, channel, UMConfigure.DEVICE_TYPE_PHONE, appSecret);
        UMConfigure.setLogEnabled(Config.isDebug());
    }
    
    @Override
    public void onCreate(Application app) {
        initUpush(app);
    }
    
    @Override
    public void onPostConfig() {
        PushAgent.getInstance(Global.app).onAppStart();
    }
    
    @Override
    public void onDestroy() {
    
    }
    
    @Override
    public void bindAccount(String account, String groupName, final IPushCallback outerCallback) {
        if(Strs.isNotEmpty(deviceToken)){
            outerCallback.onSuccess(null, null, deviceToken);
        }else{
            ThreadCollector.getIns().startAppThread("注册推送循环", new Runnable() {
                @Override
                public void run() {
                    int i = 0;
                    while (i < 10){
                        if (Strs.isNotEmpty(deviceToken)) {
                            ThreadCollector.getIns().runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    outerCallback.onSuccess(null, null, deviceToken);
                                }
                            });
                            break;
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        i++;
                    }
                }
            });
        }
    }
    
    /**
     * 处理tag/alias相关操作的点击
     */
    public void setTagAlias(int action, boolean isAliasAction, String... values) {
    
    }
    
    private void initUpush(Application app) {
        mPushAgent = PushAgent.getInstance(app);
        handler = new Handler(app.getMainLooper());
        
        //sdk开启通知声音
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        // sdk关闭通知声音
//		mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
        // 通知声音由服务端控制
//		mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER);
//		mPushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
//		mPushAgent.setNotificationPlayVibrate(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
        
        
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            /**
             * 自定义消息的回调方法
             */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                if (callback != null) {
                    callback.dealWithCustomMessage(context, msg);
                }
            }
            
            /**
             * 自定义通知栏样式的回调方法
             */
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                if (callback != null) {
                    return callback.getNotification(context, msg);
                } else {
                    return null;
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);
        
        /**
         * 自定义行为的回调处理，参考文档：高级功能-通知的展示及提醒-自定义通知打开动作
         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
        };
        
        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知，参考http://bbs.umeng.com/thread-11112-1-1.html
        //CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    
        //注册推送服务 每次调用register都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                AbDebug.log(AbDebug.TAG_PUSH, "device token: " + deviceToken);
                PushModule.this.deviceToken = deviceToken;
            }
        
            @Override
            public void onFailure(String s, String s1) {
                AbDebug.log(AbDebug.TAG_PUSH,  "register failed: " + s + " " + s1);
                PushModule.this.deviceToken = null;
            }
        });
        
        isInited = true;
    }
    
    @Override
    public boolean isInited() {
        return isInited;
    }
    
    @Override
    public AbPushMessageHandler getPushMessageHandler() {
        return null;
    }
    
    @Override
    public IUmengPushCallback getUmengPushCallback() {
        return callback;
    }
    
    @Override
    public void unBindAccount(String account, String groupName, IPushCallback outerCallback) {
    
    }
    
  
}
