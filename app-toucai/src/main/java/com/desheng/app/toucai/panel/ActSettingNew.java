package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import com.ab.global.AbDevice;
import com.ab.global.Config;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.event.DajiangPushMode;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.base.manager.UserManager;
import com.desheng.base.panel.ActWeb;
import com.desheng.base.util.ResUtil;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import org.greenrobot.eventbus.EventBus;

public class ActSettingNew extends AbAdvanceActivity {

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, ActSettingNew.class);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_setting;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), ResUtil.getString(R.string.setting));
        setStatusBarTranslucentAndLightContentWithPadding();
        setToolbarRightButtonImg(R.mipmap.ic_customer_service, 48, 48, v -> ActWeb.launchCustomService(ActSettingNew.this));
        findViewById(R.id.btnExit).setOnClickListener(v -> logout());
        findViewById(R.id.tvAccountSetting).setOnClickListener(v -> ActAccountSetting.launcher(this));
        findViewById(R.id.tvSecuritySetting).setOnClickListener(v -> ActSecuritySetting.launcher(ActSettingNew.this));
        findViewById(R.id.tvAboutApp).setOnClickListener(v -> {
//            ActWeb.launch(ActSettingNew.this, ENV.curr.host+"/registerLogin/index.html#/aboutUs",true,"关于我们");
            ActAboutToucai.launcher(ActSettingNew.this);
        });
        findViewById(R.id.tvHelp).setOnClickListener(v -> {
            // ActHelpAndFeedback.launcher(ActSettingNew.this)
            //ActWeb.launch(ActSettingNew.this, ENV.curr.host + "/registerLogin/index.html#/help", true, "帮助与反馈");
            ActWebX5.launch(ActSettingNew.this, "帮助与反馈", "/nologinaboutUs", true);
        });
        findViewById(R.id.tvNewNotice).setOnClickListener(v -> ActNoticeSetting.launcher(ActSettingNew.this));
//        findViewById(R.id.vgVersion).setOnClickListener(v -> getVersionUpdate());
        ((TextView) findViewById(R.id.tvVersion)).setText("当前版本号：" + AbDevice.appVersionName);

    }

    private void logout() {
        String pwdStr = "";
        if (UserManager.getIns().isRememberedPassword() && Strs.isNotEmpty(UserManager.getIns().getPassword())) {
            pwdStr = UserManager.getIns().getPassword();
        }
        final String finalPwd = pwdStr;
        UserManager.getIns().logout(new UserManager.IUserLogoutCallback() {
            @Override
            public void onBefore() {
                EventBus.getDefault().post(new DajiangPushMode(1));
                Dialogs.showProgressDialog(ActSettingNew.this, "");
            }

            @Override
            public void onUserLogouted() {
                //清除保存在本地的用户常玩彩种数据
                UserManagerTouCai.getIns().setUserChangwanGameData("[]");
                if (Strs.isNotEmpty(finalPwd)) {
                    UserManager.getIns().reLogin(ActSettingNew.this, UserManager.getIns().getMainUserName(), UserManager.getIns().getPassword());
                } else {
                    UserManager.getIns().reLogin(ActSettingNew.this, UserManager.getIns().getMainUserName());
                }

                finish();
            }

            @Override
            public void onUserLogoutFailed(String msg) {
                Toasts.show(ActSettingNew.this, msg, false);
            }

            @Override
            public void onAfter() {
                Dialogs.hideProgressDialog(ActSettingNew.this);
            }
        });
    }

    /**
     * 版本更新
     */
    public void getVersionUpdate() {
        if (!Config.custom_flag.contains("demo")) {
            UserManager.getIns().checkLatestVersion(ActSettingNew.this, new UserManager.IVersionUpdateCallback() {
                @Override
                public void noNeedUpdate() {
                    Toasts.show("已是最新版本!", true);
                }

                @Override
                public void notTipUpdate() {

                }
            });
        }
    }
}
