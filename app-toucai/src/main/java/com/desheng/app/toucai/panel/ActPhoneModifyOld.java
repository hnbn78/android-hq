package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.global.ENV;
import com.ab.http.AbHttpResult;
import com.ab.module.MM;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.desheng.app.toucai.dialog.PictureCodeDialog;
import com.desheng.app.toucai.global.ConfigTouCai;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.AbCodeCallBack;
import com.desheng.base.manager.UserManager;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.util.HashMap;

import okhttp3.Request;

/**
 * 绑定用户手机
 * Created by Administrator on 2017/6/29 0029.
 */
public class ActPhoneModifyOld extends AbAdvanceActivity implements View.OnClickListener {
    private String verifyCode, phoneSms;
    private CountDownTimer smsTimer;

    public static void launch(Activity act) {
        Intent itt = new Intent(act, ActPhoneModifyOld.class);
        act.startActivityForResult(itt,0);
    }


    @Override
    public int getLayoutId() {
        return R.layout.act_bind_phone;
    }

    private TextView tvSendMsg;
    private TextView ivRegistBtn;
    private EditText etPhone,etSmsVerify;

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "绑定手机号");
        setStatusBarTranslucentAndLightContentWithPadding();
        ivRegistBtn = findViewById(R.id.ivRegistBtn);
        tvSendMsg = findViewById(R.id.tvSendMsg);
        etSmsVerify = findViewById(R.id.etSmsVerify);
        etPhone = findViewById(R.id.etPhone);
        ivRegistBtn.setOnClickListener(this);
        tvSendMsg.setOnClickListener(this);
        if (Strs.isNotEmpty(UserManagerTouCai.getIns().getUrlFevorite())) {
            UserManagerTouCai.getIns().setUrlFevorite("");
            if (ConfigTouCai.isDebug()) {
                ENV.curr.host = ConfigTouCai.HOST_TEST;
            } else {
                ENV.curr.host = ConfigTouCai.HOST_PUBLISH;
            }
            MM.http.setHost(ENV.curr.host);
        }
    }


    @Override
    public void onKeyBoadHide() {

    }

    @Override
    public void onKeyBoadShow() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivRegistBtn:
                String phone = Views.getText(etPhone);
                if (!Views.checkTextAndToast(ActPhoneModifyOld.this, phone, "请输入正确手机号码!")) {
                    return;
                }
                String smsVerifyCode = Views.getText(etSmsVerify);
                if (!Views.checkTextAndToast(ActPhoneModifyOld.this, smsVerifyCode, "请输入短信验证码!")) {
                    return;
                }
                updatePhone(phone, smsVerifyCode);
                break;
            case R.id.tvSendMsg:
                phoneSms = Views.getText(etPhone);
                if (!Views.checkTextAndToast(ActPhoneModifyOld.this, phoneSms, "请输入正确手机号码!")) {
                    return;
                }
                onSendCode();
                break;
        }
    }

    private void onSendCode() {
        UserManager.getIns().getSmsCode2(ActPhoneModifyOld.this, phoneSms, verifyCode, new AbCodeCallBack() {
            @Override
            public void onSendSuccessfully() {
                Toasts.show("短信发送成功", true);
                tvSendMsg.setText("60秒后重新发送");
                tvSendMsg.setBackgroundResource(R.drawable.sh_bd_gray_gray_oval);
                if (smsTimer == null) {
                    smsTimer = new CountDownTimer(60 * 1000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            tvSendMsg.setText(millisUntilFinished / 1000 + "秒后重新发送");
                            tvSendMsg.setTextColor(Color.parseColor("#A8A8A8"));
                            tvSendMsg.setOnClickListener(null);
                        }

                        @Override
                        public void onFinish() {
                            tvSendMsg.setText("获取验证码");
                            tvSendMsg.setTextColor(Views.fromColors(R.color.colorPrimary));
                            tvSendMsg.setOnClickListener(Act);
                            tvSendMsg.setBackgroundResource(R.drawable.sh_bd_rec_red_white_oval);
                            smsTimer = null;
                        }

                    }.start();
                }

            }

            @Override
            public void onNeedImageCode() {
                showPicCodeDialog();
            }

            @Override
            public void onSendCodeField(String content) {
                Toasts.show(content, false);
            }
        });
    }

    private void showPicCodeDialog() {
        PictureCodeDialog.Builder builder = new PictureCodeDialog.Builder(this);
        builder.setTitle("请输入验证码")
                .setMessage("验证完成后继续进行操作")
                .setOnTouchOutsideCancel(false)
                .setPositiveButton("确认", new PictureCodeDialog.OnPositiveClickListener() {
                    @Override
                    public void onPositive(PictureCodeDialog dialog, String code) {
                        dialog.dismiss();
                        verifyCode = code;
                        onSendCode();
                    }
                }).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 绑定手机号
     */
    private void updatePhone(String phone, String pwd) {
        HttpAction.updatePhone(null, phone, pwd, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    UserManager.getIns().setIsBindPhone(true);
                    UserManager.getIns().setInfoCellphone(phone);
                    setResult(ActEditAccount.RESULT_CODE);
                    finish();
                }

                Toasts.show(ActPhoneModifyOld.this, msg);
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(ActPhoneModifyOld.this, content);
                return super.onError(status, content);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogsTouCai.hideProgressDialog(this);
    }


}
