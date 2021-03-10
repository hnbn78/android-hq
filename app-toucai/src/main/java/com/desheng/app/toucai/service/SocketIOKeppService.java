package com.desheng.app.toucai.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ab.http.AbHttpAO;
import com.ab.util.Strs;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.event.DajiangPushMode;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.ContactsBackMsgMode;
import com.desheng.app.toucai.model.ContactsMessageMode;
import com.desheng.app.toucai.model.DajiangPushEventModel;
import com.desheng.app.toucai.model.MessageBackInfo;
import com.desheng.app.toucai.model.MessageInfo;
import com.desheng.app.toucai.model.MsgReadHuizhiMode;
import com.desheng.app.toucai.model.eventmode.BonusPoolEventMode;
import com.desheng.app.toucai.model.eventmode.BonusPoolFreshEventMode;
import com.desheng.app.toucai.model.eventmode.BonusWinJsonMode;
import com.desheng.app.toucai.panel.ActLotteryMain;
import com.desheng.app.toucai.panel.ActMain;
import com.desheng.app.toucai.util.Utils;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.pearl.act.AbActManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketIOKeppService extends Service {

    private static final String TAG = "SocketIOKeppService";
    /**
     * id不可设置为0,否则不能设置为前台service
     */
    private static final int NOTIFICATION_DOWNLOAD_PROGRESS_ID = 0x0001;
    DajiangPushEventModel dajiangPushEventModel;
    long interval = 30 * 1000;
    Handler mhandler = new Handler();
    private Socket mSocket;
    private Gson gson;


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mSocket != null) {
                Logger.t(TAG).d("SocketId:" + mSocket.id() + "\n--------------------连接成功----------------------");
            }
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Logger.t(TAG).d("--SocketId:" + mSocket.id() + "\n--------------------断开连接--------------------");
            if (mSocket != null) {
                Log.e(TAG, "==========" + "SocketId:" + mSocket.id() + "------断开连接-----");
                Logger.t(TAG).e("SocketId:" + mSocket.id() + "尝试连接");
                if (UserManager.getIns().isLogined()) {
                    if (mISocketDisconnect != null) {
                        mISocketDisconnect.onSocketDisconnect(mSocket);
                    }
                    //flagonDisconnect = false;
                }
            } else {
                initMyIm();
                Log.e(TAG, "==========" + "------mSocket为空-------");
            }
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Logger.t(TAG).d("连接异常--");
            if (mSocket != null) {
                Log.e(TAG, "==========" + "SocketId:" + mSocket.id() + "------断开连接-----");
                Logger.t(TAG).e("SocketId:" + mSocket.id() + "尝试连接");
                if (UserManager.getIns().isLogined()) {
                    if (mISocketDisconnect != null) {
                        mISocketDisconnect.onSocketDisconnect(mSocket);
                    }
                    //flagonDisconnect = false;
                }
            } else {
                initMyIm();
                Log.e(TAG, "==========" + "------mSocket为空-------");
            }
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            if (data == null || gson == null) {
                return;
            }
            if (mSocket != null) {
                Logger.t(TAG).d("--SocketId:" + mSocket.id() + "---新消息：" + data.toString());
            }
            dajiangPushEventModel = gson.fromJson(data.toString(), DajiangPushEventModel.class);
            if (dajiangPushEventModel == null) {
                return;
            }
            int messageType = Integer.parseInt(dajiangPushEventModel.getMessageType());

            switch (messageType) {
                case Consitances.ImassageType.MASSAGE_TYPE_DAJIANG:
                    EventBus.getDefault().post(dajiangPushEventModel);
                    break;
                case Consitances.ImassageType.MASSAGE_TYPE_BONUS:
                    String content = dajiangPushEventModel.getContent();
                    if (Strs.isNotEmpty(content)) {
                        EventBus.getDefault().post(new BonusWinJsonMode(content));
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        gson = new Gson();
    }

    public String getSessionID() {
        String sso_session_uid = "";
        if (Strs.isNotEmpty(AbHttpAO.getIns().getLastCookie())) {
            String[] cookies = AbHttpAO.getIns().getLastCookie().split(";");
            for (String cookie : cookies) {
                if (cookie.contains("sso_session_uid") && !cookie.contains("sso_session_uid_sign")) {
                    sso_session_uid = cookie.split("=")[1];
                }
            }
            Logger.t(TAG).d("获取最新_session_uid:" + sso_session_uid);
            return sso_session_uid;
        }
        return null;
    }


    public void initMyIm() {
        String sessionID = getSessionID();
        if (Strs.isEmpty(sessionID)) {
            return;
        }
        //socket.io实现计时通讯，推送等
        try {
            String urlFevorite = UserManagerTouCai.getIns().getUrlFevorite();
            mSocket = IO.socket(urlFevorite + "/message/chat?sessionId=" + sessionID);//http://tc508.com

            if (mSocket != null) {
                mSocket.on(Socket.EVENT_CONNECT, onConnect);
                mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
                mSocket.on("notice", onNewMessage);
                mSocket.on("message", sendMessage);
                mSocket.on("message_read_notice", message_read_notice);
                mSocket.connect();
            }

        } catch (URISyntaxException e) {
            Logger.t(TAG).d("IO.socket 创建失败!!!", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mhandler.postDelayed(runnable, interval);
            if (!AbActManager.isBackground()) {
                Activity peelActivity = AbActManager.getPeelActivity();
                if (peelActivity instanceof ActMain || peelActivity instanceof ActLotteryMain) {
                    EventBus.getDefault().post(new BonusPoolEventMode());
                    Logger.t(TAG).d("执行任务");
                }
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bonusPoolFreshEventBus(BonusPoolFreshEventMode bonusPoolFreshEventMode) {
        if (bonusPoolFreshEventMode == null) {
            return;
        }
        if (!bonusPoolFreshEventMode.ismIsopen()) {
            Logger.t(TAG).d("奖池活动未开启，移除定时刷新任务");
            if (mhandler != null) {
                mhandler.removeCallbacks(runnable);
            }
        } else {
            Logger.t(TAG).d("奖池活动已开启，启动定时刷新任务");
            doBonusPoolfresh();
        }
    }

    //输入框发出的聊天消息--发送给服务端
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEventBus(MessageInfo messageInfo) {
        if (messageInfo == null) {
            return;
        }

        //组装json,发送给服务端
        String msgjson = gson.toJson(new ContactsMessageMode(String.valueOf(System.currentTimeMillis()), messageInfo.getInviteCode(),
                messageInfo.getToParent(), 0, messageInfo.getContent()));

        Logger.t(TAG).i("消息准备完毕：---->准备发送服务器时间：" +
                Utils.getDate(System.currentTimeMillis(), "MM月dd日 HH:mm:ss") + "\n---内容：" + msgjson);
        try {
            JSONObject obj = new JSONObject(msgjson);

            if (mSocket != null && Strs.isNotEmpty(msgjson)) {
                mSocket.emit("message", obj, new Ack() {

                    @Override
                    public void call(Object... args) {
                        Logger.t(TAG).i("发送成功后的回执：" + args[0].toString());
                        Logger.t(TAG).i("消息发送成功--->收到回执时间：" +
                                Utils.getDate(System.currentTimeMillis(), "MM月dd日 HH:mm:ss"));
                        EventBus.getDefault().post(new MessageBackInfo(messageInfo));
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //发送聊天消息
    private Emitter.Listener sendMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            if (data == null) {
                return;
            }

            Logger.t(TAG).i("接收聊天消息：" + data.toString());

            if (gson != null) {
                ContactsBackMsgMode backMsgMode = gson.fromJson(data.toString(), ContactsBackMsgMode.class);
                //存储联系人列表重要信息
                UserManagerTouCai.getIns().updateNotReadMsgPool(backMsgMode);
                EventBus.getDefault().post(backMsgMode);

            }
        }
    };

    //接收聊天消息
    private Emitter.Listener message_read_notice = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            if (data == null) {
                return;
            }

            Logger.t(TAG).i("接收对方已读回执：" + data.toString());

            if (gson != null) {
                MsgReadHuizhiMode msgReadHuizhiMode = gson.fromJson(data.toString(), MsgReadHuizhiMode.class);
                EventBus.getDefault().post(msgReadHuizhiMode);
            }
        }
    };

    //定义处理接收的方法,处理大奖推送的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(DajiangPushMode dajiangPushMode) {
        if (mSocket != null && dajiangPushMode != null) {
            if (dajiangPushMode.getStatus() == 0) {
                Logger.t(TAG).i("登录成功后----重建socket");
                mSocket.disconnect();
                mSocket.off(Socket.EVENT_CONNECT, onConnect);
                mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
                mSocket.off("notice", onNewMessage);
                mSocket.off("message", sendMessage);
                mSocket = null;
                initMyIm();
            } else {
                Logger.t(TAG).i("退出登录----断开socket");
                mSocket.disconnect();
                mSocket.off(Socket.EVENT_CONNECT, onConnect);
                mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
                mSocket.off("notice", onNewMessage);
                mSocket.off("message", sendMessage);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logger.t(TAG).e("---------SocketIOKeepService, killed-------------");

        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT, onConnect);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.off("notice", onNewMessage);
            mSocket.off("message", sendMessage);
            mSocket = null;
        }

        EventBus.getDefault().unregister(this);

        if (runnable != null) {
            mhandler.removeCallbacks(runnable);
        }
    }

    private void doBonusPoolfresh() {
        if (mhandler != null) {
            mhandler.removeCallbacks(runnable);
            mhandler.postDelayed(runnable, interval);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mSocketConnectBinder;
    }

    private SocketConnectBinder mSocketConnectBinder = new SocketConnectBinder();

    public class SocketConnectBinder extends Binder {

        public void reConnect() {
            if (Strs.isEqual(BaseConfig.custom_flag, BaseConfig.FLAG_TIANMAO) ||
                    Strs.isEqual(BaseConfig.custom_flag, BaseConfig.FLAG_HERO)) {
                doBonusPoolfresh();
            } else if (Strs.isEqual(BaseConfig.custom_flag, BaseConfig.FLAG_FEIYU)) {
                initMyIm();
            } else {
                initMyIm();
                doBonusPoolfresh();
            }
            Log.e(TAG, "==========" + "SocketConnectBinder--reConnect--连接");
        }

        public void disConnect() {
            if (mSocket != null) {
                mSocket.disconnect();
                mSocket.off(Socket.EVENT_CONNECT, onConnect);
                mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
                mSocket.off("message", sendMessage);
                mSocket.off("message_read_notice", message_read_notice);
                mSocket = null;
            }
        }

        public SocketIOKeppService getService() {
            return SocketIOKeppService.this;
        }
    }

    private ISocketDisconnect mISocketDisconnect;

    public void setmISocketDisconnect(ISocketDisconnect mISocketDisconnect) {
        this.mISocketDisconnect = mISocketDisconnect;
    }

    public interface ISocketDisconnect {
        void onSocketDisconnect(Socket socket);
    }
}
