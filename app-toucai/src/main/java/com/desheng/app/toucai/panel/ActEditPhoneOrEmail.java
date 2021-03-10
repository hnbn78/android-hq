package com.desheng.app.toucai.panel;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.desheng.app.toucai.dialog.PictureCodeDialog;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.AbCodeCallBack;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.PersonInfo;
import com.desheng.base.panel.ActWeb;
import com.desheng.base.util.ResUtil;
import com.desheng.base.view.GetCodeDialog;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

import java.util.HashMap;

import okhttp3.Request;

/**
 * 修改手机或者邮箱
 *
 * @Deprecated 下期才做目前不用
 */
public class ActEditPhoneOrEmail extends AbAdvanceActivity {

    private boolean isEditPhone;
    private View vgTop;
    private TextView tvTitle, tvYour, tvContact, tvChangeInfo, tvServer;
    private Button btnChange;
    private GetCodeDialog dialog;
    private PersonInfo mInfo;
    private String code;
    private Button mBtnGetCode;
    private CountDownTimer smsTimer;
    private String mEmailAdress;
    private EditText etInputCode;
    private String mEmailCode;
    private TextView tvKefu;

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, ActEditPhoneOrEmail.class);
        activity.startActivityForResult(intent, 0);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_edit_phone;
    }

    @Override
    protected void init() {
        hideToolbar();
        setStatusBarTranslucentCompatWithPadding();
        setStatusBarTranslucentAndDarkContent();
        mEmailAdress = UserManager.getIns().getEmail();
        findViewById(R.id.imgBack).setOnClickListener(v -> finish());
        findView();
        mBtnGetCode = ((Button) findViewById(R.id.btnGetCode));
        StringBuilder sb = new StringBuilder();
        int index = mEmailAdress.indexOf("@");
        for (int i = 0; i < mEmailAdress.length(); i++) {
            if (i > (index - 3) && i < index) {
                sb.append("*");
            } else {
                sb.append(String.valueOf(mEmailAdress.charAt(i)));
            }
        }

        tvContact.setText(sb.toString());

        btnChange.setOnClickListener(this);
        mBtnGetCode.setOnClickListener(this);
        tvKefu.setOnClickListener(this);

        etInputCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString().trim();
                boolean isValide = Strs.isNotEmpty(string);
                //更新下方确认按钮状态
                if (isValide) {
                    btnChange.setEnabled(true);
                } else {
                    btnChange.setEnabled(false);
                }
            }
        });
    }

    private void showCodeDialog() {
        DialogsTouCai.showDialog(this, "温馨提示", "您的邮箱已经解绑,需要绑定新的邮箱吗?", ResUtil.getString(R.string.go_to_bind)
                , ResUtil.getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        ActBindEmail.launcher(ActEditPhoneOrEmail.this);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }, false);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGetCode:
                getEmailCode(mEmailAdress, mVerifyCode);
                break;
            case R.id.btnChange:
                //验证码
                mEmailCode = etInputCode.getText().toString().trim();
                if (Strs.isEmpty(mEmailCode)) {
                    Toasts.show("请输入邮箱验证码", false);
                    return;
                }
                unBindEmail(mEmailAdress, mEmailCode);
                break;
            case R.id.tvKefu:
                ActWeb.launchCustomService(this);
                break;
        }
    }

    public void unBindEmail(String email, String phYzCode) {
        HttpAction.unBindMail(ActEditPhoneOrEmail.this, email, phYzCode, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
                btnChange.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActEditPhoneOrEmail.this, "");
                    }
                });
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0) {
                    UserManager.getIns().setIsBindEmail(false);
                    UserManager.getIns().setEmail("");
                    showCodeDialog();
                } else {
                    Toasts.show(msg, false);
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(content, false);
                return super.onError(status, content);
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                btnChange.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideProgressDialog(ActEditPhoneOrEmail.this);
                    }
                }, 400);
            }
        });
    }

    /**
     * 点击按钮，请求服务器给 填写的邮箱发送一个验证码
     */
    private void getEmailCode(String email, String verifyCode) {
        UserManager.getIns().getEmailCode(ActEditPhoneOrEmail.this, email, verifyCode, new AbCodeCallBack() {
            @Override
            public void onSendSuccessfully() {
                Toasts.show("邮件发送成功，请查收", true);
                mBtnGetCode.setText("120秒后重新发送");
                mBtnGetCode.setBackgroundResource(R.drawable.sh_bd_gray_gray_oval);
                if (smsTimer == null) {
                    smsTimer = new CountDownTimer(120 * 1000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            mBtnGetCode.setText(millisUntilFinished / 1000 + "秒后重新发送");
                            mBtnGetCode.setTextColor(Color.parseColor("#A8A8A8"));
                            mBtnGetCode.setOnClickListener(null);
                        }

                        @Override
                        public void onFinish() {
                            mBtnGetCode.setText("获取验证码");
                            mBtnGetCode.setTextColor(Views.fromColors(R.color.colorPrimary));
                            mBtnGetCode.setOnClickListener(Act);
                            mBtnGetCode.setBackgroundResource(R.drawable.sh_bd_rec_red_white_oval);
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

    /**
     * 弹出图片验证码框
     */
    private void showPicCodeDialog() {
        PictureCodeDialog.Builder builder = new PictureCodeDialog.Builder(this);
        builder.setTitle("请输入验证码")
                .setMessage("验证完成后继续进行操作")
                .setOnTouchOutsideCancel(false)
                .setPositiveButton("确认", new PictureCodeDialog.OnPositiveClickListener() {
                    @Override
                    public void onPositive(PictureCodeDialog dialog, String code) {
                        dialog.dismiss();
                        mVerifyCode = code;
                        getEmailCode(mEmailAdress, code);
                    }
                }).show();
    }

    private String mVerifyCode;

    private void findView() {
        vgTop = findViewById(R.id.vgTop);
        tvTitle = findViewById(R.id.tvTitle);
        tvYour = findViewById(R.id.tvYour);
        tvContact = findViewById(R.id.tvContact);
        tvChangeInfo = findViewById(R.id.tvChangeInfo);
        tvServer = findViewById(R.id.tvServer);
        btnChange = findViewById(R.id.btnChange);
        etInputCode = findViewById(R.id.etInputCode);
        tvKefu = findViewById(R.id.tvKefu);
    }

    @Override
    protected void onDestroy() {
        if (dialog != null) {
            dialog.cancel();
        }
        super.onDestroy();
    }
}
