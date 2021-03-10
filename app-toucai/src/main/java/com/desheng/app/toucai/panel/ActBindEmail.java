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
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.http.AbHttpResult;
import com.ab.util.AbStrUtil;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.desheng.app.toucai.dialog.PictureCodeDialog;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.AbCodeCallBack;
import com.desheng.base.manager.UserManager;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.util.HashMap;

import okhttp3.Request;

public class ActBindEmail extends AbAdvanceActivity {

    public static final int RESULT_CODE_FOR_ACT_SETTING = 13;
    private Button mBtnGetCode;
    private CountDownTimer smsTimer;
    private String mVerifyCode;
    private String mEmailAddress;
    private String mEmailCode;
    private ImageView mIvEamilValidate;
    private ImageView mIvEamilDelete;

    public static void launcher(Activity activity) {
        activity.startActivity(new Intent(activity, ActBindEmail.class));
    }

    private EditText mEtInputEmail;
    private EditText mEetInputCode;
    private Button btnSure;
    private TextView tv_tip;
    private TextView tv_phone_email;

    @Override
    protected int getLayoutId() {
        return R.layout.act_bind_email;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "绑定邮箱");
        setStatusBarTranslucentAndLightContentWithPadding();
        mEtInputEmail = findViewById(R.id.etInputEmail);
        mEetInputCode = findViewById(R.id.etInputCode);
        mIvEamilValidate = findViewById(R.id.ivEamilValidate);
        mIvEamilDelete = findViewById(R.id.ivEamilDelete);

        btnSure = findViewById(R.id.btnSure);
        tv_tip = findViewById(R.id.tv_tip);
        mBtnGetCode = ((Button) findViewById(R.id.btnGetCode));
        tv_phone_email = findViewById(R.id.tv_phone_email);

        btnSure.setOnClickListener(this);
        mBtnGetCode.setOnClickListener(this);
        upDateViewState();
    }


    //输入时动态提示性验证
    private void upDateViewState() {

        mEetInputCode.addTextChangedListener(new TextWatcher() {
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
                mEmailAddress = mEtInputEmail.getText().toString().trim();
                if (Strs.isNotEmpty(mEmailAddress) && isValide) {
                    btnSure.setEnabled(true);
                } else {
                    btnSure.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSure:
                //对邮箱进行验证
                mEmailAddress = mEtInputEmail.getText().toString().trim();
                if (Strs.isEmpty(mEmailAddress)) {
                    Toasts.show("邮箱账号不能有空", false);
                    return;
                }

                if (!AbStrUtil.isEmail(mEmailAddress)) {
                    Toasts.show("请输入正确的邮箱账号", false);
                    return;
                }

                //验证码
                mEmailCode = mEetInputCode.getText().toString().trim();
                if (Strs.isEmpty(mEmailCode)) {
                    Toasts.show("请输入邮箱验证码", false);
                    return;
                }
                //绑定邮箱
                bindMail(mEmailAddress, mEmailCode);
                break;
            case R.id.btnGetCode:
                //对邮箱进行验证
                mEmailAddress = mEtInputEmail.getText().toString().trim();

                if (Strs.isEmpty(mEmailAddress)) {
                    Toasts.show("邮箱账号不能有空", false);
                    return;
                }

                if (!AbStrUtil.isEmail(mEmailAddress)) {
                    Toasts.show("请输入正确的邮箱账号", false);
                    return;
                }
                //获取验证码
                getEmailCode(mEmailAddress, mVerifyCode);
                break;
            default:
        }
    }

    /**
     * 点击按钮，请求服务器给 填写的邮箱发送一个验证码
     */
    private void getEmailCode(String email, String verifyCode) {
        UserManager.getIns().getEmailCode(ActBindEmail.this, email, verifyCode, new AbCodeCallBack() {
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
     * 绑定邮箱
     *
     * @param emailadress
     * @param emailCode
     */
    private void bindMail(String emailadress, String emailCode) {
        HttpAction.bindMail(ActBindEmail.this, emailadress, emailCode, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
                btnSure.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActBindEmail.this, "");
                    }
                });
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0) {
                    UserManager.getIns().setIsBindEmail(true);
                    UserManager.getIns().setEmail(emailadress);
                    Toasts.show(msg, true);
                    finish();
                } else {
                    Toasts.show(msg, false);
                    mEetInputCode.setText("");
                    mEmailCode = "";
                }

                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(ActBindEmail.this, content);
                return super.onError(status, content);
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                btnSure.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideProgressDialog(ActBindEmail.this);
                    }
                }, 400);
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
                        getEmailCode(mEmailAddress, code);
                    }
                }).show();
    }

}
