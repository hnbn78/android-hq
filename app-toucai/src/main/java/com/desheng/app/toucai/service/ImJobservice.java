package com.desheng.app.toucai.service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.ab.global.ENV;
import com.ab.http.AbHttpAO;
import com.ab.util.Strs;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.event.DajiangPushMode;
import com.desheng.app.toucai.model.DajiangPushEventModel;
import com.desheng.app.toucai.model.eventmode.BonusPoolEventMode;
import com.desheng.app.toucai.model.eventmode.BonusPoolFreshEventMode;
import com.desheng.app.toucai.model.eventmode.BonusWinJsonMode;
import com.desheng.app.toucai.panel.ActLotteryMain;
import com.desheng.app.toucai.panel.ActMain;
import com.google.gson.Gson;
import com.pearl.act.AbActManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ImJobservice extends JobService {

    private static final String TAG = "service.ImJobservice";
    private Socket mSocket;
    private boolean flag = true;
    private Gson gson;
    long interval = 30 * 1000;
    long SocketHelpinterval = 2 * 1000;
    private PendingIntent mAlarmIntent;
    AlarmManager manager;

    @Override
    public boolean onStartJob(JobParameters params) {
        if (flag) {
            doJob();
        }
        doBonusPoolfresh();
        return true;
    }

    Handler mhandler = new Handler();

    private void doBonusPoolfresh() {
        if (mhandler != null) {
            mhandler.removeCallbacks(runnable);
            mhandler.postDelayed(runnable, interval);
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mhandler.postDelayed(runnable, interval);
            if (!AbActManager.isBackground()) {
                Activity peelActivity = AbActManager.getPeelActivity();
                Log.d(TAG, "peelActivity:" + peelActivity + "----AbActManager.getCount():" + AbActManager.getCount());
                if (peelActivity instanceof ActMain || peelActivity instanceof ActLotteryMain) {
                    EventBus.getDefault().post(new BonusPoolEventMode());
                    Log.d(TAG, "执行任务");
                }
            }
        }
    };

    private void doSocketHelp() {
        if (mhandler != null) {
            mhandler.removeCallbacks(SocketHelprunnable);
            mhandler.postDelayed(SocketHelprunnable, SocketHelpinterval);
        }
    }

    Runnable SocketHelprunnable = new Runnable() {
        @Override
        public void run() {
            mhandler.postDelayed(SocketHelprunnable, SocketHelpinterval);
            Log.d(TAG, "尝试重新创建socket连接------");
            if (!AbActManager.isBackground()) {
                initMyIm();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        gson = new Gson();
    }

    private void doJob() {
        Log.d(TAG, "doJob");
        initMyIm();

        if (mSocket == null) {
            Log.d(TAG, "mSocket == null");
            doSocketHelp();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bonusPoolFreshEventBus(BonusPoolFreshEventMode bonusPoolFreshEventMode) {
        if (bonusPoolFreshEventMode == null) {
            return;
        }
        if (!bonusPoolFreshEventMode.ismIsopen()) {
            if (manager != null && mAlarmIntent != null) {
                manager.cancel(mAlarmIntent);
            }
        }
    }

    //定义处理接收的方法,处理大奖推送的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(DajiangPushMode dajiangPushMode) {
        if (mSocket != null && dajiangPushMode != null) {
            if (dajiangPushMode.getStatus() == 0) {
                Log.d(TAG, "登录成功后----重新socket");
                mSocket.disconnect();
                mSocket.off(Socket.EVENT_CONNECT, onConnect);
                mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
                mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
                mSocket.off("notice", onNewMessage);
                mSocket = null;
                initMyIm();
            } else {
                Log.d(TAG, "退出登录----销毁socket");
                mSocket.disconnect();
                mSocket.off(Socket.EVENT_CONNECT, onConnect);
                mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
                mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
                mSocket.off("notice", onNewMessage);
            }
        }
    }

    public void initMyIm() {

        String sso_session_uid = "";
        if (Strs.isNotEmpty(AbHttpAO.getIns().getLastCookie())) {
            String[] cookies = AbHttpAO.getIns().getLastCookie().split(";");
            for (String cookie : cookies) {
                if (cookie.contains("sso_session_uid") && !cookie.contains("sso_session_uid_sign")) {
                    sso_session_uid = cookie.split("=")[1];
                }
            }
            Log.d(TAG, "sso_session_uid:" + sso_session_uid);
            //socket.io实现计时通讯，推送等
            try {
                mSocket = IO.socket(ENV.curr.host + "/message/chat?sessionId=" + sso_session_uid);
            } catch (URISyntaxException e) {
                Log.d(TAG, e.getMessage());
                throw new RuntimeException(e);
            }

            if (mSocket != null) {
                mSocket.on(Socket.EVENT_CONNECT, onConnect);
                mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
                mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
                mSocket.on("notice", onNewMessage);
                mSocket.connect();
            }
        }
        flag = false;
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "--SocketId:" + mSocket.id() + "连接成功--");
            //EventBus.getDefault().post(new SocketConnetStatusMode(1));
            if (mhandler != null) {
                mhandler.removeCallbacks(SocketHelprunnable);
            }
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "--SocketId:" + mSocket.id() + "断开连接--");
            //EventBus.getDefault().post(new SocketConnetStatusMode(0));
            if (mSocket != null) {
                Log.d(TAG, "--SocketId:" + mSocket.id() + "--mSocket.connect()");
                mSocket.connect();
            }
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "连接异常--");
            //EventBus.getDefault().post(new SocketConnetStatusMode(-1));
            if (mSocket != null) {
                mSocket.connect();
            }
        }
    };

    DajiangPushEventModel dajiangPushEventModel;

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            if (data == null || gson == null) {
                return;
            }
            Log.d(TAG, "--SocketId:" + mSocket.id() + "---新消息：" + data.toString());
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
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT, onConnect);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.off("notice", onNewMessage);
            mSocket = null;
        }

        EventBus.getDefault().unregister(this);

        if (manager != null) {
            manager.cancel(mAlarmIntent);
        }

        if (runnable != null) {
            mhandler.removeCallbacks(runnable);
            mhandler.removeCallbacks(SocketHelprunnable);
        }
    }
}
