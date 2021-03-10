package com.desheng.app.toucai.panel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ab.callback.AbCallback;
import com.ab.util.Toasts;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.UserBindStatus;
import com.desheng.base.panel.ActChangeFundPassword;
import com.desheng.base.util.FingerprintUtil;
import com.desheng.base.util.ResUtil;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

public class ActSecuritySetting extends AbAdvanceActivity {

    private View vgFundsPwd;
    private TextView tvAddFundsPwd, tvFundsInfo;
    private ToggleButton tgFinger;

    public static void launcher(Activity activity) {
        activity.startActivity(new Intent(activity, ActSecuritySetting.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_security_setting;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), ResUtil.getString(R.string.security_setting));
        setStatusBarTranslucentAndLightContentWithPadding();
        findViewById(R.id.vgLoginPwd).setOnClickListener(v -> ActChangePassword.launcher(ActSecuritySetting.this));
        vgFundsPwd = findViewById(R.id.vgFundsPwd);
        tgFinger = findViewById(R.id.finger_switch);
        tvAddFundsPwd = findViewById(R.id.tvAddFundsPwd);
        tvFundsInfo = findViewById(R.id.tvFundsInfo);
        //这里只允许修改密码，不允许设置密码
        tvAddFundsPwd.setText(UserManager.getIns().getIsBindWithdrawPassword() ? "修改资金密码" : "检查绑定信息");
        tvAddFundsPwd.setOnClickListener(v -> {
            if (UserManager.getIns().getIsBindWithdrawPassword()) {
                ActChangeFundPassword.launch(ActSecuritySetting.this);
            } else {
                //ActSettingFundsPwd.launcher(ActSecuritySetting.this, false);
                showBindTip("您暂时未绑定银行卡相关信息", "请您先绑定银行卡，真实姓名，资金密码，再实行操作", "前往绑定");
            }
        });
        tgFinger.setChecked(UserManager.getIns().isUserFingerPrint());
        registerFingerToggle(true);

        UserManager.getIns().checkUserBindStatus(new UserManager.IUserBindCheckCallback() {

            @Override
            public void onBefore() {

            }

            @Override
            public void onUserBindChecked(UserBindStatus status) {

            }

            @Override
            public void onUserBindCheckFailed(String msg) {

            }

            @Override
            public void onAfter() {
                checkUserBindStatus();
            }
        });
    }

    private void showBindTip(String tip1, String tip2, String right) {
        DialogsTouCai.showBindDialog(this, tip1, tip2, "", right, true, new AbCallback<Object>() {
            @Override
            public boolean callback(Object obj) {
                ActBindBankCardToucai.launch(ActSecuritySetting.this, !UserManager.getIns().getIsBindWithdrawName());
                DialogsTouCai.hideBindTipDialog();
                return true;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void registerFingerToggle(boolean isNeed) {
        if (isNeed) {
            tgFinger.setOnCheckedChangeListener((buttonView, isChecked) -> onFingerprintClick(isChecked));
        } else {
            tgFinger.setOnCheckedChangeListener(null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onFingerprintClick(final boolean isChecked) {
        FingerprintUtil.callFingerPrint(new FingerprintUtil.OnCallBackListenr() {
            AlertDialog dialog;

            @Override
            public void onSupportFailed() {
                registerFingerToggle(false);
                tgFinger.setChecked(!isChecked);
                registerFingerToggle(true);
                Toasts.show("当前设备不支持指纹");
            }

            @Override
            public void onInsecurity() {
                registerFingerToggle(false);
                tgFinger.setChecked(!isChecked);
                registerFingerToggle(true);
                Toasts.show("请设置指纹解锁");
            }

            @Override
            public void onEnrollFailed() {
                registerFingerToggle(false);
                tgFinger.setChecked(!isChecked);
                registerFingerToggle(true);
                Toasts.show("请到设置中设置指纹");
            }

            @Override
            public void onAuthenticationStart() {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActSecuritySetting.this);
                View view = LayoutInflater.from(ActSecuritySetting.this).inflate(com.desheng.base.R.layout.layout_fingerprint, null);
                initfinget(view);
                builder.setView(view);
                builder.setCancelable(false);
                builder.setNegativeButton("取消", (dialog, which) -> {
                    handler.removeMessages(0);
                    registerFingerToggle(false);
                    tgFinger.setChecked(!isChecked);
                    registerFingerToggle(true);
                    FingerprintUtil.cancel();
                });

                dialog = builder.create();
                dialog.show();
            }

            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                Toasts.show(errString.toString(), false);
                registerFingerToggle(false);
                tgFinger.setChecked(!isChecked);
                registerFingerToggle(true);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    handler.removeMessages(0);
                }
                FingerprintUtil.cancel();
            }

            @Override
            public void onAuthenticationFailed() {
                registerFingerToggle(false);
                tgFinger.setChecked(!isChecked);
                registerFingerToggle(true);
                Toasts.show("设置失败", false);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    handler.removeMessages(0);
                }
                FingerprintUtil.cancel();
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                Toasts.show(helpString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                Toasts.show("设置成功", true);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    handler.removeMessages(0);
                }
                UserManager.getIns().setUserFingerPrint(isChecked);
            }
        });
    }

    TextView[] tv = new TextView[5];
    private int position;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                int i = position % 5;
                if (i == 0) {
                    tv[4].setBackgroundResource(0);
                    tv[i].setBackgroundColor(getResources().getColor(com.desheng.base.R.color.colorPrimary));
                } else {
                    tv[i].setBackgroundColor(getResources().getColor(com.desheng.base.R.color.colorPrimary));
                    tv[i - 1].setBackgroundResource(0);
                }
                position++;
                handler.sendEmptyMessageDelayed(0, 100);
            }
        }
    };

    private void initfinget(View view) {
        position = 0;
        tv[0] = view.findViewById(com.desheng.base.R.id.tv_1);
        tv[1] = view.findViewById(com.desheng.base.R.id.tv_2);
        tv[2] = view.findViewById(com.desheng.base.R.id.tv_3);
        tv[3] = view.findViewById(com.desheng.base.R.id.tv_4);
        tv[4] = view.findViewById(com.desheng.base.R.id.tv_5);
        handler.sendEmptyMessageDelayed(0, 100);
    }

    private void checkUserBindStatus() {
        if (UserManager.getIns().getIsBindWithdrawPassword()) {
            tvFundsInfo.setText(ResUtil.getString(R.string.change_funds_password_info));
        } else {
            tvFundsInfo.setText(ResUtil.getString(R.string.funds_password_use_info) + "\n※(检测到你暂未绑定资金密码，请您先绑定真实姓名，资金密码，银行卡等信息再实行操作)");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode > 0) {
            checkUserBindStatus();
        }
    }
}
