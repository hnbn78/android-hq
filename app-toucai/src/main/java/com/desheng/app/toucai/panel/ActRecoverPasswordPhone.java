package com.desheng.app.toucai.panel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.ab.callback.AbCallback;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.manager.UserManager;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;
import com.shark.tc.databinding.ActRecoverPasswordPhoneBinding;

import java.util.HashMap;

/**
 * Created by user on 2018/4/20.
 */

public class ActRecoverPasswordPhone extends AbAdvanceActivity<ActRecoverPasswordPhoneBinding> implements View.OnClickListener{
    
    private CountDownTimer smsTimer;
    private AbCallback<String> successCallback;
    private AbCallback<String> failureCallback;
    private String smsPhotoVerify;
    
    public static void launch(Context act, String phone) {
        Intent itt = new Intent(act, ActRecoverPasswordPhone.class);
        itt.putExtra("phone", phone);
        act.startActivity(itt);
    }
    
    private boolean isPhoneBinded = false;
    private String phone;

    @Override
    protected int getLayoutId() {
        return R.layout.act_recover_password_phone;
    }
    
    @Override
    protected void init() {
        phone = getIntent().getStringExtra("phone");
        if(Strs.isNotEmpty(phone) && phone.startsWith("86")){
            phone = phone.substring(2);
        }
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "手机找回");
        setStatusBarTranslucentAndLightContentWithPadding();
        setToolbarLeftBtn(UIHelper.BACK_WHITE_CIRCLE_ARROW);

        if (!Strs.isEmpty(phone) && phone.length() > 8) {
            EditText ac = findViewById(R.id.etAccount);
            
            ac.setText(phone.substring(0, 3) + "******" + phone.substring(9, phone.length()));
            ac.setEnabled(false);
        }

        initView();
    }
    
    private void initView() {
        B.etAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        
            }
    
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
        
            }
    
            @Override
            public void afterTextChanged(Editable s) {
                checkBtnEnable(s.toString(), Views.getText(B.etVerify));
            }
        });
        
        B.ivSmsDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                B.etVerify.setText("");
            }
        });
        B.etVerify.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        
            }
    
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
        
            }
    
            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                boolean isValide = UserManager.getIns(UserManagerTouCai.class).isValideSmsVerifyCode(string);
                B.ivSmsCodeValidate.setVisibility(isValide ? View.VISIBLE : View.INVISIBLE);
                B.etVerify.setTextColor(Views.fromColors(isValide ? R.color.text_login : R.color.text_login_error));
                if(Strs.isNotEmpty(string)){
                    B.ivSmsDelete.setVisibility(View.VISIBLE);
                }else{
                    B.ivSmsDelete.setVisibility(View.INVISIBLE);
                }
                
                checkBtnEnable(Views.getText(B.etAccount), s.toString());
            }
        });
        
        
        getBinding().ivNextBtn.setOnClickListener(this);
    
        B.tvSmsCode.setOnClickListener(this);
    }
    
    private void checkBtnEnable(String name, String code) {
        if (UserManager.getIns(UserManagerTouCai.class).isValideSmsVerifyCode(code)) {
            B.ivNextBtn.setEnabled(true);
        }else{
            B.ivNextBtn.setEnabled(false);
        }
    }
    
    @Override
    public void onClick(View v) {
        String account = Views.getText(B.etAccount);
        if(account != null && !account.contains("*")){
            phone = account;
        }
        switch (v.getId()) {
            case R.id.ivNextBtn:
                
                String verify = Views.getText(B.etVerify);
    
                HttpActionTouCai.verificationPhoneCode(this, phone, verify, new AbHttpResult(){
                    @Override
                    public void setupEntity(AbHttpRespEntity entity) {
                        entity.putField("data", String.class);
                    }
         
                    @Override
                    public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                        if (code == 0 && error == 0) {
                            String data = getField(extra, "data", "");
                            Toasts.show(ActRecoverPasswordPhone.this, data, true);
                            ActRecoverPasswordChange.launch(ActRecoverPasswordPhone.this, phone, verify);
                        }else{
                            Toasts.show(ActRecoverPasswordPhone.this, msg, false);
                        }
                        return true;
                    }
                });
                break;
            case R.id.tvSmsCode:
                successCallback = new AbCallback<String>() {
                    @Override
                    public boolean callback(String obj) {
                        B.tvSmsCode.setText("60秒后重新发送");
                        //B.tvSmsCode.setBackgroundResource(R.drawable.sh_bd_gray_gray_oval);
                        if (smsTimer == null) {
                            smsTimer = new CountDownTimer(60 * 1000, 1000) {
                                
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    B.tvSmsCode.setText(millisUntilFinished / 1000 + "秒后重新发送");
                                    B.tvSmsCode.setTextColor(Color.parseColor("#A8A8A8"));
                                    B.tvSmsCode.setOnClickListener(null);
                                }
                    
                                @Override
                                public void onFinish() {
                                    B.tvSmsCode.setText("获取验证码");
                                    //B.tvSmsCode.setTextColor(Views.fromColors(R.color.colorPrimary));
                                    B.tvSmsCode.setOnClickListener(ActRecoverPasswordPhone.this);
                                    //B.tvSmsCode.setBackgroundResource(R.drawable.sh_bd_rec_red_white_oval);
                                    smsTimer = null;
                                }
                                
                            }.start();
                        }
                        return true;
                    }
                };
    
                failureCallback = new AbCallback<String>() {
                    @Override
                    public boolean callback(String obj) {
                        //下次使用
                        if (obj != null) {
                            smsPhotoVerify = obj;
                            DialogsTouCai.hideVerifyPhotoDialog(ActRecoverPasswordPhone.this);
                            UserManager.getIns(UserManagerTouCai.class).getSmsCodeWithTimer(ActRecoverPasswordPhone.this, false, phone, smsPhotoVerify, successCallback, failureCallback);
                        }
                        B.tvSmsCode.setOnClickListener(ActRecoverPasswordPhone.this);
                        return true;
                    }
                };
    
                UserManager.getIns(UserManagerTouCai.class).getSmsCodeWithTimer(ActRecoverPasswordPhone.this, false, phone, null, successCallback, failureCallback);
                break;
        }
    }
    
    
    @Override
    protected void onStop() {
        super.onStop();
        if (smsTimer != null) {
            smsTimer.cancel();
            smsTimer = null;
            B.tvSmsCode.setText("获取验证码");
            //B.tvSmsCode.setTextColor(Views.fromColors(R.color.colorPrimary));
            B.tvSmsCode.setOnClickListener(ActRecoverPasswordPhone.this);
            //B.tvSmsCode.setBackgroundResource(R.drawable.sh_bd_rec_red_white_oval);
        }
    }
    
}
