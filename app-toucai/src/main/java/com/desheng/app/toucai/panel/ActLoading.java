package com.desheng.app.toucai.panel;


import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.debug.AbDebug;
import com.ab.global.Config;
import com.ab.global.ENV;
import com.ab.http.AbHttpAO;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.module.MM;
import com.ab.util.Strs;
import com.ab.view.MaterialDialog;
import com.bumptech.glide.Glide;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.global.App;
import com.desheng.app.toucai.global.AppUmengPushHandler;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.MissionPushContentMode;
import com.desheng.app.toucai.model.UpdateContentList;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.FloatPermissionManager;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.VersionUpdateInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbBaseActivity;
import com.shark.tc.R;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 启动页面
 */
public class ActLoading extends AbBaseActivity {
    CountDownTimer timer;
    boolean noNeedRedict = false;
    boolean isDestroyed = false;
    boolean isNetworkDone = false;
    ImageView ivLogo;
    private TextView tvCountdown;
    private MaterialDialog alertDialog;
    private long issueTime;
    private String buildDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_loading);
        AbDebug.log(AbDebug.TAG_PUSH, "设置推送开始");
        try {
            AppUmengPushHandler.CustomMsg jo = (AppUmengPushHandler.CustomMsg) getIntent().getSerializableExtra("pushCustomMsg");
            if (jo != null) {
                ((UserManagerTouCai) UserManager.getIns()).setLatestPushMsg(jo);
                AbDebug.log(AbDebug.TAG_PUSH, "设置推送" + jo.toString());
            }
        } catch (Exception e) {
            AbDebug.error(AbDebug.TAG_PUSH, Thread.currentThread(), e);
        }

        TextView testLab = (TextView) findViewById(R.id.tvTestLab);
        ivLogo = (ImageView) findViewById(R.id.ivLogo);

        if (Config.isDebug()) {
            testLab.setVisibility(View.VISIBLE);
            ApplicationInfo ai = null;
            try {
                ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                if (ai != null) {
                    buildDate = String.valueOf(ai.metaData.get("BUILD_DATE"));
                }
                testLab.setText("验收环境, 请勿发布, 打包日期: " + buildDate);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            testLab.setVisibility(View.GONE);
        }
        tvCountdown = findViewById(R.id.tvCountdown);
        tvCountdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                    alertDialog = null;
                }
                noNeedRedict = true;
                isNetworkDone = true;
                if (UserManager.getIns().getHideGuide()) {
                    gotoMain();
                } else {
                    ActGuidePageTouCai.launchAndFinish(ActLoading.this);
                }
            }
        });

        //引导页测试
        setBgPic();
        mPushContentModeStr = getIntent().getStringExtra("pushContentMode");
    }

    String mPushContentModeStr;

    private void doPushIntend(String pushContentModeStr) {
        if (Strs.isEmpty(pushContentModeStr)) {
            ActMain.launch(this);
            finish();
            return;
        }
        MissionPushContentMode pushContentMode = new Gson().fromJson(pushContentModeStr, MissionPushContentMode.class);
        if (pushContentMode == null || pushContentMode.getLinkUrl() == null) {
            ActMain.launch(this);
            finish();
            return;
        }
        if (pushContentMode.getLinkUrl().getType() == 1) {
            ActMain.launch(this, 3);
            finish();
        } else if (pushContentMode.getLinkUrl().getType() == 2) {
            ActMain.launch(this, pushContentMode.getLinkUrl().getLinkUrl());
            finish();
        } else {
            ActMain.launch(this);
            finish();
        }
    }

    private void setBgPic() {
        String splashOnlineBg = UserManagerTouCai.getIns().getSplashOnlineBg();
        if (Strs.isNotEmpty(splashOnlineBg)) {
            //加载启动图，同时要处理异常情况 异常或者加载失败都要显示默认图
            Glide.with(ActLoading.this).load(splashOnlineBg)
                    .asBitmap().placeholder(R.mipmap.bg_loading_1)
                    .into(ivLogo);
        } else {
            ivLogo.setBackgroundResource(R.mipmap.bg_loading_1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        RxPermissions rxPermissions = new RxPermissions(this);
        //读取手机状态和身份
        //读取您的SD卡中的内容
        //修改或删除您的SD卡中的内容
        rxPermissions.request(Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean value) {
                        if (!value) {
                            final MaterialDialog materialDialog = new MaterialDialog(ActLoading.this);
                            materialDialog.setTitle("");
                            materialDialog.setMessage(String.format(getResources().getString(R.string.permission_deny), getResources().getString(R.string.app_name)));
                            materialDialog.setPositiveButton(R.string.confirm, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    materialDialog.dismiss();
                                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_SETTINGS));
                                }
                            });
                            materialDialog.show();
                            return;
                        }

                        noNeedRedict = false;

                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        //启动时间定时, 超时弹出直接进入提示
                        timer = new CountDownTimer(5000, 300) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                if (!ActLoading.this.isFinishing() && !isDestroyed) {

                                    long left = (long) Math.ceil(millisUntilFinished / 1000.0);
                                    if (left <= 1) {
                                        tvCountdown.setVisibility(View.GONE);
                                    } else {
                                        tvCountdown.setVisibility(View.VISIBLE);
                                        tvCountdown.setText((left - 2) + "s跳过");
                                    }

                                }
                            }


                            @Override
                            public void onFinish() {
                                if (!ActLoading.this.isFinishing() && !isDestroyed && !isNetworkDone) {
//                                    alertDialog = new MaterialDialog(ActLoading.this);
//                                    alertDialog.setTitle("提示");
//                                    alertDialog.setMessage("网络连接较慢, 请检查网络, 是否继续?");
//                                    alertDialog.setPositiveButton(R.string.confirm, new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            alertDialog.dismiss();
//                                            if (UserManager.getIns().getHideGuide()) {
//                                                gotoMain();
//                                            } else {
//                                                ActGuidePageTouCai.launchAndFinish(ActLoading.this, guidePics);
//                                            }
//                                        }
//                                    });
//                                    alertDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            alertDialog.dismiss();
//                                            onStart();
//                                        }
//                                    });
//                                    alertDialog.show();
//                                    return;
                                }
                                if (!noNeedRedict) {
                                    noNeedRedict = true;
                                    if (UserManager.getIns().getHideGuide()) {
                                        gotoMain();
                                    } else {
                                        ActGuidePageTouCai.launchAndFinish(ActLoading.this);
                                    }

                                    //强制进入
//                                    if (alertDialog != null) {
//                                        alertDialog.dismiss();
//                                        alertDialog = null;
//                                    }
                                }
                            }
                        };
                        timer.start();
                        issueTime = System.currentTimeMillis();
                        UserManager.getIns().checkOnlineConfig(ActLoading.this, new UserManager.IConfigUpdateCallback() {

                            @Override
                            public void onGetConfig(VersionUpdateInfo info) {

                                //UserManager.getIns().getGlobleSkinSettings(this);
                                UserManagerTouCai.getIns().setBgPic();
                                //UserManagerTouCai.getIns().getChangwanData();

                                Log.d("ActLoading", "偏好线路--------------host: " + UserManager.getIns().getUrlFevorite());
                                Log.d("ActLoading", "ENV.curr.host--------------host: " + ENV.curr.host);

                                Log.d("ActLoading", "Account: " + UserManagerTouCai.getIns().getAccount());
                                Log.d("ActLoading", "Password: " + UserManagerTouCai.getIns().getPassword());
                                if (Strs.isNotEmpty(UserManagerTouCai.getIns().getAccount()) && Strs.isNotEmpty(UserManagerTouCai.getIns().getPassword())) {
                                    Log.e("ActLoading", "自动登录登录----->");
                                    //已登录过, 重登录, 通过进入主页面, 不通过进登录页
                                    UserManagerTouCai.getIns().validateLogin(new UserManagerTouCai.IUserLoginCallback() {
                                        @Override
                                        public void onBefore() {

                                        }

                                        @Override
                                        public void onUserLogined(boolean isDataInited) {
                                            if (isDataInited) {  //已同步到用户数据
                                                long delta = System.currentTimeMillis() - issueTime;
                                                isNetworkDone = true;
                                                if (delta < 1000) {
                                                    //nothing 让timer逻辑执行
                                                }
                                                //UserManagerTouCai.getIns().getChangwanData();
                                                //Log.e("ActLoading", "-----actloading------getSessionID-------: " + getSessionID());
                                            }
                                        }

                                        @Override
                                        public void onUserNeedValidateCode() {
                                        }

                                        @Override
                                        public void onUserLoginFailed(String msg) {
                                        }

                                        @Override
                                        public void onAfter() {
                                        }
                                    });
                                } else { //未登录也进入主页
                                    Log.e("ActLoading", "未登录 也进入主页----->");
                                    isNetworkDone = true;
                                    UserManagerTouCai.getIns().setAccount("");
                                }
                            }

                            @Override
                            public void onFail() {
                                BuglyLog.e("ActLoading", "线路json获取失败 --> 时间:" + System.currentTimeMillis());
                                Log.d("ActLoading", "线路json获取失败 --> 时间:" + System.currentTimeMillis());
                                CrashReport.postCatchedException(new Throwable("ActLoading----线路获取失败"));
                                String urlFevorite = UserManager.getIns().getUrlFevorite();
                                if (Strs.isNotEmpty(urlFevorite)) {
                                    MM.http.setHost(urlFevorite);
                                    ENV.curr.host = urlFevorite;
                                    Log.d("ActLoading", "urlFevorite--作为备用域名----host:" + urlFevorite);
                                } else if (Strs.isNotEmpty(BaseConfig.ForceHost)) {//线路json获取失败时（被dns攻击时可能会遇到json无法访问的情况） 使用写死的默认域名进行访问
                                    MM.http.setHost(BaseConfig.ForceHost);
                                    ENV.curr.host = BaseConfig.ForceHost;
                                    Log.d("ActLoading", "写死的ForceHost--作为备用域名----host:" + ENV.curr.host);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void checkFloatingPermission() {
        boolean isPermission = FloatPermissionManager.getInstance().applyFloatWindow(this);
        if (isPermission) {
            FloatPermissionManager.getInstance();
        } else {

        }
    }

    //跳转主页
    public void gotoMain() {
        ActMain.mainKillSwitch = false;
        //ActLoading.this.launchOtherAndFinish(ActMain.class);
        doPushIntend(mPushContentModeStr);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        noNeedRedict = true;
        /*if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }*/

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
        if (ivLogo != null) {
            ivLogo.setBackgroundResource(0);
        }
    }
}
