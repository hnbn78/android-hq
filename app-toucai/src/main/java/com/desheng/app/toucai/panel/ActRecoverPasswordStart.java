package com.desheng.app.toucai.panel;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.thread.ThreadCollector;
import com.ab.util.Maps;
import com.ab.util.Strs;
import com.ab.util.Views;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.manager.UserManager;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;
import com.shark.tc.databinding.ActRecoverPasswordStartBinding;

import java.util.HashMap;

import okhttp3.Request;

/**
 * Created by user on 2018/4/20.
 */

public class ActRecoverPasswordStart extends AbAdvanceActivity<ActRecoverPasswordStartBinding> implements View.OnClickListener {

    public static void launch(Context act) {
        Intent itt = new Intent(act, ActRecoverPasswordStart.class);
        act.startActivity(itt);
    }

    private boolean isPhoneBinded = false;

    @Override
    protected int getLayoutId() {
        return R.layout.act_recover_password_start;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "忘记密码");
        setStatusBarTranslucentAndLightContentWithPadding();
        setToolbarLeftBtn(UIHelper.BACK_WHITE_CIRCLE_ARROW);
        B.ivNextBtn.setEnabled(false);

        B.ivVercifyDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                B.etVerify.setText("");
            }
        });
        B.etVerify.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                B.layoutErrorHint.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isValide = UserManager.getIns(UserManagerTouCai.class).isValidePhotoVerifyCode(s.toString());
                B.ivVercifyValidate.setVisibility(isValide ? View.VISIBLE : View.INVISIBLE);
                B.etVerify.setTextColor(Views.fromColors(isValide ? R.color.text_login : R.color.text_login_error));
                B.ivVercifyDelete.setVisibility(Strs.isNotEmpty(s.toString()) ? View.VISIBLE : View.INVISIBLE);
                checkBtnEnable(Views.getText(B.etAccount), s.toString());
            }
        });

        B.ivAccountDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                B.etAccount.setText("");
            }
        });
        B.etAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                B.layoutErrorHint.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String account = s.toString();
                if (Strs.isNotEmpty(account)) {
                    B.ivAccountDelete.setVisibility(View.VISIBLE);
                } else {
                    B.ivAccountDelete.setVisibility(View.INVISIBLE);
                }

                boolean isValide = UserManager.getIns(UserManagerTouCai.class).isValideAccountOrPhone(account);
                B.ivAccountValidate.setVisibility(isValide ? View.VISIBLE : View.INVISIBLE);
                B.etAccount.setTextColor(Views.fromColors(isValide ? R.color.text_login : R.color.text_login_error));
                checkBtnEnable(s.toString(), Views.getText(B.etVerify));
            }
        });
        getBinding().ivNextBtn.setOnClickListener(this);
        B.ivVercifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManager.getIns().getVerifyImage(ActRecoverPasswordStart.this, B.ivVercifyCode);
            }
        });

    }

    private void checkBtnEnable(String name, String code) {
        if (UserManager.getIns(UserManagerTouCai.class).isValideAccountOrPhone(name)
                && UserManager.getIns(UserManagerTouCai.class).isValidePhotoVerifyCode(code)) {
            B.ivNextBtn.setEnabled(true);
        } else {
            B.ivNextBtn.setEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        UserManager.getIns().getVerifyImage(this, B.ivVercifyCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivNextBtn:
                String name = B.etAccount.getText().toString().trim();
                if (!Views.checkTextAndToast(this, name, "请输入用户名")) {
                    return;
                }
                String verify = B.etVerify.getText().toString().trim();
                if (!Views.checkTextAndToast(this, name, "请输入验证码")) {
                    return;
                }
                HttpActionTouCai.validateResetLoginPwd(this, name, verify, new AbHttpResult() {
                    @Override
                    public void onBefore(Request request, int id, String host, String funcName) {
                        DialogsTouCai.showProgressDialog(ActRecoverPasswordStart.this, "");
                    }

                    @Override
                    public void setupEntity(AbHttpRespEntity entity) {
                        entity.putField("data", new TypeToken<HashMap<String, Object>>() {
                        }.getType());
                    }

                    @Override
                    public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                        HashMap<String, Object> data = getField(extra, "data", null);
                        if (data != null && code == 0 && error == 0) {
                            String phoneNum = Maps.value(data, "phoneNum", "");
                            ActRecoverPasswordIndex.launch(ActRecoverPasswordStart.this, phoneNum);
                        } else {
                            B.tvErrorHint.setText(msg);
                            B.layoutErrorHint.setVisibility(View.VISIBLE);
                            UserManager.getIns().getVerifyImage(ActRecoverPasswordStart.this, B.ivVercifyCode);
                        }
                        return true;
                    }

                    @Override
                    public void onFinish() {
                        ThreadCollector.getIns().postDelayOnUIThread(600, new Runnable() {
                            @Override
                            public void run() {
                                DialogsTouCai.hideProgressDialog(ActRecoverPasswordStart.this);
                            }
                        });
                    }
                });
                break;
        }
    }
}
