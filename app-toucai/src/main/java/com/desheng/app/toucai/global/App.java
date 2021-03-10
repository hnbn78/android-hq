package com.desheng.app.toucai.global;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ab.global.AbDevice;
import com.ab.global.AbException;
import com.ab.global.Config;
import com.ab.global.ENV;
import com.ab.global.Global;
import com.ab.http.AbHttpAO;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.http.HttpModule;
import com.ab.module.MM;
import com.ab.util.AbAppUtil;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.AwardPoolDetailoMode;
import com.desheng.app.toucai.model.BonusPoolWinJsonDataMode;
import com.desheng.app.toucai.model.ContactsBackMsgMode;
import com.desheng.app.toucai.model.DajiangPushEventModel;
import com.desheng.app.toucai.model.MsgReadHuizhiMode;
import com.desheng.app.toucai.model.NewUserMission;
import com.desheng.app.toucai.model.eventmode.BonusWinJsonMode;
import com.desheng.app.toucai.panel.ActContacts;
import com.desheng.app.toucai.panel.ActLoading;
import com.desheng.app.toucai.panel.ActLoginPasswordTouCai;
import com.desheng.app.toucai.panel.ActMain;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.util.NotificationUtil;
import com.desheng.app.toucai.util.NotificationUtils;
import com.desheng.app.toucai.util.UIHelperTouCai;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.controller.FloatActionController;
import com.desheng.base.global.AppBase;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.growingio.android.sdk.collection.Configuration;
import com.growingio.android.sdk.collection.GrowingIO;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.pearl.act.AbActManager;
import com.pearl.act.ActModule;
import com.pearl.act.util.UIHelper;
import com.pearl.db.DBModule;
import com.pearl.push.IPushUmeng;
import com.pearl.push.PushModule;
import com.pearl.view.rmondjone.locktableview.DisplayUtil;
import com.pearl.webview.WebViewModule;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.shark.tc.BuildConfig;
import com.shark.tc.R;
import com.tencent.bugly.crashreport.CrashReport;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by lee on 2017/10/2.
 */

public class App extends AppBase {
    public static final String TAG = App.class.getName();
    private Handler handler;
    private int mFinalCount;
    public boolean actmainCreate = false;
    public boolean needUpdateTip = true;
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new ClassicsHeader(context);
            }
        });
    }

    private Socket mSocket;
    private Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();
        whenInMaintenance();//平台维护中...
        loadConfig(ConfigTouCai.class);
        Config.custom_flag = BuildConfig.FLAVOR;
        MultiDex.install(this);
        if (Config.isEmulatorDebug()) {
            ENV.curr = ENV.LOCAL;
        } else if (Config.isDevelopDebug()) {
            ENV.curr = ENV.TEST;
        } else {
            ENV.curr = ENV.PUBLISH;
        }
        if (BaseConfig.FLAG_FEIYU.equals(Config.custom_flag)) {
            CrashReport.initCrashReport(getApplicationContext(), "dc21750300", false);//TODO 正式打包时方开使用
        } else {
            CrashReport.initCrashReport(getApplicationContext(), "9e8869b9db", false);//TODO 正式打包时方开使用
        }

        //logger日志配置
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .tag(BuildConfig.APPLICATION_ID)
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });

        //全局变量初始化
        Global.init(this);
        ZXingLibrary.initDisplayOpinion(this);

        //各模块初始化
        String channel = "";
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(AbDevice.appPackageName, PackageManager.GET_META_DATA);
            channel = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (Strs.isEmpty(channel)) {
            channel = "gm";
        }

        MM.getIns().configModules(new HttpModule(new AppHttpHandler()),
                new ActModule(false, "", R.mipmap.ic_launcher),
                new WebViewModule(new AppWebViewUrlFilter()),
                new DBModule(new AppDBHandler()));

        //根据不同的渠道，需要在友盟后台添加渠道包名信息，然后拿到友盟提供的appId和secret
        if (BaseConfig.FLAG_FEIYU.equals(Config.custom_flag)) {
            MM.getIns().configModules(new PushModule(channel, "5b05379cf29d985ad10000d4",
                    "1205e51c9268c57668224ea99294a277", new AppUmengPushHandler()));
        } else {
            MM.getIns().configModules(new PushModule(channel, "5b05379cf29d985ad10000d4",
                    "1205e51c9268c57668224ea99294a277", new AppUmengPushHandler()));
        }

        MM.getIns().initAll();
        Dialogs.setIsFullScreen(true);
        UIHelper.initUiHelper(new UIHelperTouCai());

        //数据管理初始化
        CtxLottery.setIns(new CtxLotteryTouCai());
        UserManager.setIns(new UserManagerTouCai()).init(this);
        if (Strs.isNotEmpty(UserManager.getIns().getHostUserPrefix())) {
            Config.HOST_USER_PREFIX = UserManager.getIns().getHostUserPrefix();
        }

//        //全局线程异常处理
//        AbException.setGlobalExceptionCaught();
//        AbException.setErrorReportHandler(new AbException.ErrorReportHandler() {
//            @Override
//            public void onGetErrorReport(final StringBuffer report) {
//                if (BaseConfig.FLAG_TOUCAI.equals(Config.custom_flag)) {
//                    //MobclickAgent.reportError(App.this, report.toString());
//                }
//            }
//        });

        //读取域名
        String processName = AbAppUtil.getProcessName(Global.app, android.os.Process.myPid());
        if (processName != null) {
            boolean defaultProcess = processName.equals(Global.appPackageName);
            if (defaultProcess) {
                Log.d(TAG, "BaseConfig.ForceHost-----" + BaseConfig.ForceHost);
                if (Strs.isNotEmpty(BaseConfig.ForceHost)) {
                    MM.http.setHost(BaseConfig.ForceHost);
                    ENV.curr.host = BaseConfig.ForceHost;
                }
                //在线配置初始化
                initFloat();
                initTableUtil();
            }
        }

        //growing io
        GrowingIO.startWithConfiguration(this, new Configuration()
                .trackAllFragments()
                .setChannel(channel)
        );

        //registPush();
    }

    private void whenInMaintenance() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                mFinalCount++;
                //如果mFinalCount ==1，说明是从后台到前台
                Log.d("onActivityStarted", mFinalCount + "");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (activity instanceof ActMain) {
                    String weiHutime = UserManagerTouCai.getIns().getWeiHutime();
                    boolean downtime = UserManagerTouCai.getIns().isDowntime();
                    if (downtime && Strs.isNotEmpty(weiHutime)) {
                        showDialogOk(activity, weiHutime);
                    } else {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                    }
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                mFinalCount--;
                //如果mFinalCount ==0，说明是前台到后台
                Log.i("onActivityStopped", mFinalCount + "");
                if (mFinalCount == 0) {
                    //说明从前台回到了后台
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    Dialog mDialog = null;

    public void showDialogOk(Context context, String content) {
        mDialog = new Dialog(context, R.style.custom_dialog_style);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_msg_weihuing, null);
        TextView container = root.findViewById(R.id.tvTimeWeihu);
        container.setText(content);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.setContentView(root);
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void initTableUtil() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        DisplayUtil.density = dm.density;
        DisplayUtil.densityDPI = dm.densityDpi;
        DisplayUtil.screenWidthPx = dm.widthPixels;
        DisplayUtil.screenhightPx = dm.heightPixels;
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(getApplicationContext(), dm.widthPixels);
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(getApplicationContext(), dm.heightPixels);
    }

    public void initFloat() {
        FloatActionController.getInstance().startMonkServer(App.this);
    }


    private void registPush() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                ((IPushUmeng) MM.push).getUmengPushCallback().registActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                ((IPushUmeng) MM.push).getUmengPushCallback().unregist();
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    @Override
    public void onTerminate() {
        Global.destroy();
        MM.getIns().destroyAll();
        super.onTerminate();
    }

    public boolean[][] advPopShownFlags = {{true, true, true}, {true, true, true},
            {true, true, true}, {true, true, true}, {true, true, true}, {true, true, true}};

    public void resetAdvPopShownFlags() {
        advPopShownFlags = new boolean[][]{{true, true, true}, {true, true, true},
                {true, true, true}, {true, true, true}, {true, true, true}, {true, true, true}};
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        destroedSoeckt();
    }

    private static WeakReference<Activity> ctxRef;

//    //定义处理接收的方法,处理大奖推送的消息
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void bonusPoolWinEvent(BonusWinJsonMode bonusWinJsonMode) {
//        if (bonusWinJsonMode == null) {
//            return;
//        }
//        boolean isBackground = AbActManager.isBackground();
//
//        if (!isBackground) {  //应用在前台, 弹悬浮框
//            try {
//                //Toast.makeText(this, "奖池推送：" + bonusWinJsonMode.getJson(), Toast.LENGTH_SHORT).show();
//
//                Activity peelActivity = AbActManager.getPeelActivity();
//                //Log.e(TAG, "peelActivity:" + peelActivity);
//                if (peelActivity == null || peelActivity instanceof ActLoading || peelActivity instanceof ActLoginPasswordTouCai) {
//                    return;
//                }
//
//                BonusPoolWinJsonDataMode bonusPoolWinJsonDataMode = new Gson().fromJson(bonusWinJsonMode.getJson(), BonusPoolWinJsonDataMode.class);
//                if (bonusPoolWinJsonDataMode == null) {
//                    return;
//                }
//                if (bonusPoolWinJsonDataMode.isHasPrize()) {
//                    DialogsTouCai.showBonusPoolWin(peelActivity, bonusWinJsonMode.getJson());
//                } else {
//                    getMissionList(peelActivity, bonusWinJsonMode.getJson());
//                }
//
//            } catch (Exception e) {
//                Log.e(TAG, e.getMessage());
//                e.printStackTrace();
//            }
//        }
//    }

    private void getMissionList(Activity peelActivity, String json) {
//        HttpActionTouCai.getMissionList(this, Consitances.ImissionTypeConfig.AWARD_POOL, new AbHttpResult() {
//            @Override
//            public void setupEntity(AbHttpRespEntity entity) {
//                entity.putField("data", new TypeToken<ArrayList<NewUserMission>>() {
//                }.getType());
//            }
//
//            @Override
//            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
//                if (code == 0 && error == 0) {
//                    List<NewUserMission> listMission = getField(extra, "data", null);
//                    if (listMission.size() > 0) {
//                        NewUserMission finalMission = listMission.get(0);
//                        String id = finalMission.id;
//                        if (Strs.isNotEmpty(id)) {
//                            getAwardPoolMissionData(id, peelActivity, json);
//                        }
//                    }
//                }
//                return true;
//            }
//
//            @Override
//            public boolean onError(int status, String content) {
//                return true;
//            }
//        });
    }

//    private void getAwardPoolMissionData(String activityId, Activity peelActivity, String json) {
//
//        HttpActionTouCai.getBonusPoolActivityDetail(this, activityId, new AbHttpResult() {
//
//            @Override
//            public void setupEntity(AbHttpRespEntity entity) {
//                entity.putField("data", new TypeToken<AwardPoolDetailoMode>() {
//                }.getType());
//            }
//
//            @Override
//            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
//                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
//                    AwardPoolDetailoMode poolDetailo = getField(extra, "data", null);
//                    DialogsTouCai.showBonusPoolNotWin(peelActivity, json, poolDetailo.getPoolBonus());
//                }
//                return super.onSuccessGetObject(code, error, msg, extra);
//            }
//        });
//    }


    public void initMyIm(String sessionID) {
        boolean soketIOopen = UserManager.getIns().isSoketIOopen();
        if (!soketIOopen) {
            return;
        }
        gson = new Gson();
        if (Strs.isEmpty(sessionID)) {
            return;
        }
        //socket.io实现计时通讯，推送等
        if (mSocket == null) {
            try {
                String soketIOUrl = UserManager.getIns().getSoketIOUrl();
                mSocket = IO.socket(soketIOUrl + "/message/chat?sessionId=" + sessionID);//http://tc508.com

                mSocket.on(Socket.EVENT_CONNECT, onConnect);
                mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
                mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
                mSocket.on(Socket.EVENT_RECONNECTING, onReConnecting);
                mSocket.on("notice", onNewMessage);
                mSocket.on("message", sendMessage);
                mSocket.on("message_read_notice", message_read_notice);
                mSocket.connect();
            } catch (URISyntaxException e) {
                Log.e("acttalk", "IO.socket 创建失败!!  :" + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }


    //接受消息
    private Emitter.Listener sendMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            if (data == null) {
                return;
            }

            Log.e("acttalk", "接收聊天消息：" + data.toString());

            if (gson != null) {
                ContactsBackMsgMode backMsgMode = gson.fromJson(data.toString(), ContactsBackMsgMode.class);
                //存储联系人列表重要信息
                UserManagerTouCai.getIns().updateNotReadMsgPool(backMsgMode);
                //MessageEventBus(backMsgMode);
                EventBus.getDefault().post(backMsgMode);
                // 初始化通知类
                NotificationUtils notificationUtils = NotificationUtils.getInstance();
                notificationUtils.init(getApplicationContext());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // 8.0以上创建通道
                    // 当然这里是具体按照项目需要设定的通道类型创建
                    notificationUtils.createNotificationChannel("chat", "上下级聊天", NotificationManager.IMPORTANCE_HIGH);
                }
                // 设置Intent相关数据
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), ActContacts.class);
                notificationUtils.sendNotification("chat", "您有新的消息", "" + (backMsgMode.getMsgType() == 1 ? "图片消息" : backMsgMode.getText()), intent);
            }
        }
    };

    private int reconectCishu = 0;
    private int reBuildConectCishu = 0;
    DajiangPushEventModel dajiangPushEventModel;

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            if (data == null || gson == null) {
                return;
            }
            if (mSocket != null) {
                Log.e("acttalk", "--SocketId:" + mSocket.id() + "---新消息：" + data.toString());
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
            Log.e("acttalk", "接收对方已读回执：" + data.toString());
            if (gson != null) {
                MsgReadHuizhiMode msgReadHuizhiMode = gson.fromJson(data.toString(), MsgReadHuizhiMode.class);
                EventBus.getDefault().post(msgReadHuizhiMode);
                //MessageEventBus(msgReadHuizhiMode);
            }
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mSocket != null) {
                Log.e("acttalk", "SocketId:" + mSocket.id() + "-----连接断开-----");
                if (UserManager.getIns().isLogined() && reconectCishu < 5) {
                    mSocket.connect();
                    reconectCishu++;
                }
            } else {
                if (reBuildConectCishu < 5) {
                    Log.e("acttalk", "onDisconnect" + "-----连接断开---->initMyIm()");
                    initMyIm(getSessionID());
                }
            }
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mSocket != null) {
                Log.e("acttalk", "SocketId:" + mSocket.id() + "-----连接异常-----");
                if (UserManager.getIns().isLogined() && reconectCishu < 5) {
                    mSocket.connect();
                    reconectCishu++;
                }
            } else {
                if (reBuildConectCishu < 5) {
                    Log.e("acttalk", "onConnectError" + "-----连接异常---->initMyIm()");
                    initMyIm(getSessionID());
                }
            }
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mSocket != null) {
                Log.e("acttalk", "SocketId:" + mSocket.id() + "-----连接成功-----");
                reconectCishu = 0;
                reBuildConectCishu = 0;
            }
        }
    };


    private Emitter.Listener onReConnecting = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mSocket != null) {
                Log.e("acttalk", "SocketId:" + mSocket.id() + "-----重新连接中-----");
            }
        }
    };

    public String getSessionID() {
        String sso_session_uid = "";
        if (Strs.isNotEmpty(AbHttpAO.getIns().getLastCookie())) {
            String[] cookies = AbHttpAO.getIns().getLastCookie().split(";");
            for (String cookie : cookies) {
                if (cookie.contains("sso_session_uid") && !cookie.contains("sso_session_uid_sign")) {
                    sso_session_uid = cookie.split("=")[1];
                }
            }
            Log.e("acttalk", "获取最新_session_uid:" + sso_session_uid);
            return sso_session_uid;
        }
        return null;
    }

    public Socket getSocketClient() {
        return mSocket;
    }


    public void destroedSoeckt() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT, onConnect);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.off(Socket.EVENT_RECONNECTING, onReConnecting);
            mSocket.off("notice", onNewMessage);
            mSocket.off("message", sendMessage);
            mSocket.off("message_read_notice", message_read_notice);
            mSocket = null;
        }
    }
}
