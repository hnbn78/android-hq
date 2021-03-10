package com.desheng.app.toucai.panel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ab.callback.AbCallback;
import com.ab.global.AbDevice;
import com.ab.global.ENV;
import com.ab.http.AbHttpAO;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.module.MM;
import com.ab.util.AESUtil;
import com.ab.util.Dialogs;
import com.ab.util.MD5;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.consitance.CommonConsts;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.event.DajiangPushMode;
import com.desheng.app.toucai.global.App;
import com.desheng.app.toucai.global.ConfigTouCai;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.Event;
import com.desheng.app.toucai.model.UpdateContentList;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.controller.FloatActionController;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.FloatWindowManager;
import com.desheng.base.manager.UserManager;
import com.desheng.base.panel.ActWeb;
import com.desheng.base.panel.ActWebX5;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.view.ToggleImageButton;
import com.shark.tc.R;
import com.shark.tc.databinding.ActLoginPasswordToucaiBinding;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * 登录页面A
 * Created by Administrator on 2017/6/29 0029.
 */
public class ActLoginPasswordTouCai extends AbAdvanceActivity<ActLoginPasswordToucaiBinding> implements View.OnClickListener {

    public static final String DEFAULT_PASSWORD = CtxLottery.PWD_MOCK;
    public static boolean isShowing;
    private boolean needVerifyCode = false;
    private boolean isRemeberPassword = true;
    private String inputPassword;
    private int tempEtIndex;
    private String smsPhotoVerify;
    private CountDownTimer smsTimer;
    private Subject<Boolean> loginFailCount = PublishSubject.create();
    private AbCallback<String> failureCallback;
    private AbCallback<String> successCallback;
    private UserManagerTouCai userManager;
    private boolean isOpenEye;
    private String clearTextPwd;//明文密码
    private String clearTextPassword = "";
    private String mPushUrl;

    public static void launch(Activity act) {
        Intent itt = new Intent(act, ActLoginPasswordTouCai.class);
        act.startActivity(itt);
    }

    public static void launch(Activity act, boolean needVerifyCode) {
        Intent itt = new Intent(act, ActLoginPasswordTouCai.class);
        itt.putExtra("needVerifyCode", needVerifyCode);
        act.startActivity(itt);
    }

    public static void launch(Activity act, String pushUrl) {
        Intent itt = new Intent(act, ActLoginPasswordTouCai.class);
        itt.putExtra("pushUrl", pushUrl);
        act.startActivity(itt);
    }


    @Override
    public int getLayoutId() {
        return R.layout.act_login_password_toucai;
    }


    @Override
    protected void init() {
        hideToolbar();
        B.ivClose.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActMain.mainKillSwitch = false;
                launchOther(ActMain.class);
            }
        }));

        setStatusBarTranslucentAndDarkContentWithPadding();
        if (isShowing) {
            finish();
        }
        isShowing = true;

        userManager = UserManager.getIns(UserManagerTouCai.class);

        setBgFromWeb(userManager.getSkinSetting());

        if (Strs.isEqual(BaseConfig.FLAG_HERO, BaseConfig.custom_flag)
                || Strs.isEqual(BaseConfig.FLAG_FEIYU, BaseConfig.custom_flag)
                || Strs.isEqual(BaseConfig.FLAG_JINFENG_2, BaseConfig.custom_flag)) {
            B.llRegist.setVisibility(View.GONE);
        }

        B.tvVersion.setText("当前版本：" + AbDevice.appVersionName);

        /**
         * 是否需要验证码
         */
        B.vgVerify.setVisibility(View.GONE);
        needVerifyCode = getIntent().getBooleanExtra("needVerifyCode", false);

        mPushUrl = getIntent().getStringExtra("pushUrl");

        if (needVerifyCode) {
            B.vgVerify.setVisibility(View.VISIBLE);
            userManager.getVerifyImage(this, B.ivVercifyCode);
        } else {
            B.vgVerify.setVisibility(View.GONE);
        }
        B.ivVercifyCodeDelete.setOnClickListener((view) -> {
            B.etVerify.setText("");
        });
        B.ivVercifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userManager.getVerifyImage(ActLoginPasswordTouCai.this, B.ivVercifyCode);
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
                String verifyCode = s.toString();
                if (Strs.isNotEmpty(verifyCode)) {
                    B.ivVercifyCodeDelete.setVisibility(View.VISIBLE);
                } else {
                    B.ivVercifyCodeDelete.setVisibility(View.INVISIBLE);
                }
                boolean isValide = userManager.isValidePhotoVerifyCode(verifyCode);

                B.ivVercifyCodeValidate.setVisibility(isValide ? View.VISIBLE : View.INVISIBLE);
                B.etVerify.setTextColor(Views.fromColors(isValide ? R.color.text_login : R.color.text_login_error));

                checkBtnEnable();
            }
        });


        //记住密码
        B.cbRemBtn.setOnCheckedChangeListener(new ToggleImageButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ToggleImageButton buttonView, boolean isChecked) {
                isRemeberPassword = isChecked;
                if (!isChecked) {
                    if (BaseConfig.USE_AES_LOGIN) {
                        userManager.setLastPasswordForAes("");
                    } else {
                        userManager.setLastPassword("");
                    }
                    userManager.setLastRememberCheck(false);
                }
            }
        });


        B.vgTypeCoverLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                B.rgType.check(R.id.rbAccount);
            }
        });
        B.vgTypeCoverRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                B.rgType.check(R.id.rbPhoneNum);
            }
        });
        B.rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                B.rbAccount.setTextColor(Views.fromColors(R.color.radio_login_nomal));
                B.rbAccount.setBackgroundResource(R.drawable.ab_sh_bg_rect_trans);
                B.rbPhoneNum.setTextColor(Views.fromColors(R.color.radio_login_nomal));
                B.rbPhoneNum.setBackgroundResource(R.drawable.ab_sh_bg_rect_trans);

                RadioButton radioButton = group.findViewById(checkedId);
                radioButton.setTextColor(Views.fromColors(R.color.radio_login_selected));
                radioButton.setBackgroundResource(R.drawable.bg_radio_red_triangle_btn);

                B.vgUserName.setVisibility(View.GONE);
                //B.etAccount.setText("");

                B.vgPassword.setVisibility(View.GONE);
                // B.etPwd.setText("");

                B.vgVerify.setVisibility(View.GONE);
                //B.etVerify.setText("");

                B.vgPhoneCode.setVisibility(View.GONE);
                //B.etPhone.setText("");

                B.vgSmsCode.setVisibility(View.GONE);
                //B.etSmsCode.setText("");

                B.vgRemBtn.setVisibility(View.GONE);

                B.vgVerify.setVisibility(View.GONE);
                // B.etVerify.setText("");

                if (checkedId == R.id.rbAccount) {
                    B.vgUserName.setVisibility(View.VISIBLE);
                    B.vgPassword.setVisibility(View.VISIBLE);
                    B.vgRemBtn.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.rbPhoneNum) {
                    B.vgPhoneCode.setVisibility(View.VISIBLE);
                    B.vgSmsCode.setVisibility(View.VISIBLE);
                }

                checkBtnEnable();
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

                //6-10位字符，只能包含英文字母、数字、下划线
                boolean isValide = userManager.isValideAccountOrPhone(account);

                B.ivAccountValidate.setVisibility(isValide ? View.VISIBLE : View.INVISIBLE);
                B.etAccount.setTextColor(Views.fromColors(isValide ? R.color.text_login : R.color.text_login_error));

                checkBtnEnable();
            }
        });

        B.ivPhoneDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                B.etPhone.setText("");
            }
        });

        B.etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                boolean isValide = Strs.isPhoneValid(string) && string.length() >= 11;
                B.ivPhoneCodeValidate.setVisibility(isValide ? View.VISIBLE : View.INVISIBLE);
                B.etPhone.setTextColor(Views.fromColors(isValide ? R.color.text_login : R.color.text_login_error));
                if (Strs.isNotEmpty(string)) {
                    B.ivPhoneDelete.setVisibility(View.VISIBLE);
                } else {
                    B.ivPhoneDelete.setVisibility(View.INVISIBLE);
                }

                checkBtnEnable();
            }
        });

        B.ivPwdDelete.setOnClickListener((view) -> {
            B.etPwd.setText("");
        });
//        B.ivPwdShow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                isOpenEye = !isOpenEye;
//                if (isOpenEye) {
//                    Log.e("ActLoginPasswordTouCai", "B.etPwd.getText():1---" + B.etPwd.getText());
//                    B.ivPwdShow.setSelected(true);
//                    B.etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    if (B.etPwd.getText().toString().equals(DEFAULT_PASSWORD)) {
//                        String pwd = UserManager.getIns().getClearTextPassword();
//                        B.etPwd.setText(pwd);
//                    }
//                    tempEtIndex = B.etPwd.getSelectionStart();
//                    B.etPwd.setSelection(tempEtIndex);
//                } else {
//                    Log.e("ActLoginPasswordTouCai", "B.etPwd.getText():2---" + B.etPwd.getText());
//                    B.ivPwdShow.setSelected(false);
//                    tempEtIndex = B.etPwd.getSelectionStart();
//                    B.etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    B.etPwd.setSelection(tempEtIndex);
//                }
//            }
//        });


        B.etPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    checkPhone();
            }
        });
        B.etPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pwd = s.toString();
                if (pwd.equals(DEFAULT_PASSWORD)) {
                    return;
                }

                boolean isValide = UserManagerTouCai.getIns(UserManagerTouCai.class).isValidePwd(pwd);

                B.ivPwdValidate.setVisibility(isValide ? View.VISIBLE : View.INVISIBLE);
                B.etPwd.setTextColor(Views.fromColors(isValide ? R.color.text_login : R.color.text_login_error));

                if (Strs.isNotEmpty(s.toString())) {
                    B.ivPwdDelete.setVisibility(View.VISIBLE);
                } else {
                    B.ivPwdDelete.setVisibility(View.INVISIBLE);
                }

                checkBtnEnable();
            }
        });


        B.ivSmsDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                B.etSmsCode.setText("");
            }
        });
        B.etSmsCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                boolean isValide = userManager.isValideSmsVerifyCode(string);
                B.ivSmsCodeValidate.setVisibility(isValide ? View.VISIBLE : View.INVISIBLE);
                B.etSmsCode.setTextColor(Views.fromColors(isValide ? R.color.text_login : R.color.text_login_error));
                if (Strs.isNotEmpty(string)) {
                    B.ivSmsDelete.setVisibility(View.VISIBLE);
                } else {
                    B.ivSmsDelete.setVisibility(View.INVISIBLE);
                }

                checkBtnEnable();
            }
        });


        String inputUserName = userManager.getLastAccount();
        if (Strs.isNotEmpty(inputUserName)) {
            B.etAccount.setText(inputUserName);
            if (Strs.isPhoneValid(inputUserName)) {
                B.etPhone.setText(inputUserName);
            } else {
                B.etPwd.post(new Runnable() {
                    @Override
                    public void run() {
                        B.etPwd.requestFocus();
                    }
                });
            }
        }

        if (BaseConfig.USE_AES_LOGIN) {
            inputPassword = userManager.getLastPasswordForAes();
        } else {
            inputPassword = userManager.getLastPassword();
        }

        clearTextPassword = userManager.getClearTextPassword();

        if (userManager.getLastRememberCheck() && Strs.isNotEmpty(clearTextPassword)) {
            Log.d("clearTextPassword", "B.etPwd.getText():3---" + clearTextPassword);
            B.etPwd.setText(clearTextPassword);
            B.ivLoginBtn.setEnabled(true);
        }

        B.ivPwdShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    B.etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    B.etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        B.cbRemBtn.setChecked(userManager.getLastRememberCheck());

        B.tvSmsCode.setOnClickListener(this);
        B.tvRegist.setOnClickListener(this);
        B.tvForgetPwd.setOnClickListener(this);
        B.ivLoginBtn.setOnClickListener(this);

        subscribeLoginCount5();
        checkBtnEnable();
        B.selectLine.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ((App) getApplication()).destroedSoeckt();
    }

    private String phoneNumber = "";

    private void checkPhone() {
        String number = B.etPhone.getText().toString();
        if (number.length() != 11) return;
        if (!number.equals(phoneNumber)) {
            phoneNumber = number;
        } else {
            return;
        }
        HttpAction.checkPhone(this, number, new AbHttpResult() {

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                super.setupEntity(entity);
                entity.putField("data", Boolean.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                boolean isExist = getFieldObject(extra, "data", null);
                if (!isExist)
                    Toasts.show("账号未注册，请先前往注册", false);
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show("content ====>  " + content);
                return super.onError(status, content);
            }
        });
    }

    @Override
    public void onKeyBoadHide() {

    }

    @Override
    public void onKeyBoadShow() {

    }

    private void checkBtnEnable() {
      
        /*if(Strs.isEmpty(inputPassword) || !CtxLottery.PWD_MOCK.equals(Views.getText(B.etPwd)) || B.rgType.getCheckedRadioButtonId() == R.id.rbPhoneNum){
            if(B.rgType.getCheckedRadioButtonId() == R.id.rbAccount){
                if (userManager.isValideAccountOrPhone(Views.getText(B.etAccount))
                        && userManager.isValidePwd(Views.getText(B.etPwd))) {
                    if(B.vgVerify.getVisibility() == View.VISIBLE){
                        B.ivLoginBtn.setEnabled(userManager.isValidePhotoVerifyCode(Views.getText(B.etVerify)));
                    }else{
                        B.ivLoginBtn.setEnabled(true);
                    }
                }else{
                    B.ivLoginBtn.setEnabled(false);
                }
            } if(CtxLottery.PWD_MOCK.equals(Views.getText(B.etPwd))){
                if (userManager.isValidePhone(Views.getText(B.etPhone))
                        && userManager.isValideSmsVerifyCode(Views.getText(B.etSmsCode))) {
                    if(B.vgVerify.getVisibility()== View.VISIBLE){
                        B.ivLoginBtn.setEnabled(userManager.isValidePhotoVerifyCode(Views.getText(B.etVerify)));
                    }else{
                        B.ivLoginBtn.setEnabled(true);
                    }
                }else{
                    B.ivLoginBtn.setEnabled(false);
                }
            }
        }else if(CtxLottery.PWD_MOCK.equals(Views.getText(B.etPwd)) || B.rgType.getCheckedRadioButtonId() == R.id.rbPhoneNum){
            if(B.rgType.getCheckedRadioButtonId() == R.id.rbAccount){
                if (userManager.isValideAccountOrPhone(Views.getText(B.etAccount))) {
                    if(B.vgVerify.getVisibility() == View.VISIBLE){
                        B.ivLoginBtn.setEnabled(userManager.isValidePhotoVerifyCode(Views.getText(B.etVerify)));
                    }else{
                        B.ivLoginBtn.setEnabled(true);
                    }
                }else{
                    B.ivLoginBtn.setEnabled(false);
                }
            } if(B.rgType.getCheckedRadioButtonId() == R.id.rbPhoneNum){
                if (userManager.isValidePhone(Views.getText(B.etPhone))) {
                    if(B.vgVerify.getVisibility()== View.VISIBLE){
                        B.ivLoginBtn.setEnabled(userManager.isValidePhotoVerifyCode(Views.getText(B.etVerify)));
                    }else{
                        B.ivLoginBtn.setEnabled(true);
                    }
                }else{
                    B.ivLoginBtn.setEnabled(false);
                }
            }
        }
       */

        if (B.rgType.getCheckedRadioButtonId() == R.id.rbAccount) {
            clearTextPassword = userManager.getClearTextPassword();
            //无记住密码, 或 记住密码被修改
            if (Strs.isEmpty(inputPassword) && !clearTextPassword.equals(Views.getText(B.etPwd))) {
                if (userManager.isValideAccountOrPhone(Views.getText(B.etAccount))
                        && userManager.isValidePwd(Views.getText(B.etPwd))) {
                    //判断是否有图形验证码
                    if (B.vgVerify.getVisibility() == View.VISIBLE) {
                        B.ivLoginBtn.setEnabled(userManager.isValidePhotoVerifyCode(Views.getText(B.etVerify)));
                    } else {
                        B.ivLoginBtn.setEnabled(true);
                    }
                } else {
                    B.ivLoginBtn.setEnabled(false);
                }
            } else { //记住密码的情况
                //不在检查密码
                if (userManager.isValideAccountOrPhone(Views.getText(B.etAccount))) {
                    if (B.vgVerify.getVisibility() == View.VISIBLE) {
                        B.ivLoginBtn.setEnabled(userManager.isValidePhotoVerifyCode(Views.getText(B.etVerify)));
                    } else {
                        B.ivLoginBtn.setEnabled(true);
                    }
                } else {
                    B.ivLoginBtn.setEnabled(false);
                }
            }
        } else if (B.rgType.getCheckedRadioButtonId() == R.id.rbPhoneNum) {
            if (userManager.isValidePhone(Views.getText(B.etPhone)) && userManager.isValideSmsVerifyCode(Views.getText(B.etSmsCode))) {
                if (B.vgVerify.getVisibility() == View.VISIBLE) {
                    B.ivLoginBtn.setEnabled(userManager.isValidePhotoVerifyCode(Views.getText(B.etVerify)));
                } else {
                    B.ivLoginBtn.setEnabled(true);
                }
            } else {
                B.ivLoginBtn.setEnabled(false);
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvRegist:
                isShowing = false;
                ActRegistTouCai.launch(this);
                break;
            case R.id.selectLine:
                FloatWindowManager.showPopupWindow(this,null);
                break;
            case R.id.ivLoginBtn:
                if (B.rgType.getCheckedRadioButtonId() == R.id.rbAccount) {
                    String account = Views.getText(B.etAccount);
                    if (!Views.checkTextAndToast(ActLoginPasswordTouCai.this, account, "请输入用户名!")) {
                        return;
                    }

                    String pwd = Views.getText(B.etPwd);
                    clearTextPwd = pwd;
                    if (!Views.checkTextAndToast(ActLoginPasswordTouCai.this, pwd, "请输入密码!")) {
                        return;
                    }

                    String verifyCode = Views.getText(B.etVerify);
                    if (B.vgVerify.getVisibility() == View.VISIBLE &&
                            !Views.checkTextAndToast(ActLoginPasswordTouCai.this, verifyCode, "请输入校验码!")) {
                        return;
                    }

                    loginWithPassword(account, pwd, verifyCode);
                } else if (B.rgType.getCheckedRadioButtonId() == R.id.rbPhoneNum) {
                    String phone = Views.getText(B.etPhone);
                    if (!(userManager.isValidePhone(phone))) {
                        Toasts.show(this, "请输入正确手机号码!");
                        return;
                    }

                    String smsCode = Views.getText(B.etSmsCode);
                    if (!Views.checkTextAndToast(ActLoginPasswordTouCai.this, smsCode, "请输验证码!")) {
                        return;
                    }

                    loginWithPhone(phone, smsCode, "");
                }
                break;
            case R.id.tvForgetPwd://改为联系客服
                //ActRecoverPasswordStart.launch(this);
                ActWeb.launchCustomService(this);
                break;
            case R.id.tvSmsCode:
                String phone = Views.getText(getBinding().etPhone);
                if (!Views.checkTextAndToast(ActLoginPasswordTouCai.this, phone, "请输入正确手机手机验证码!")) {
                    return;
                }

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
                                    B.tvSmsCode.setOnClickListener(ActLoginPasswordTouCai.this);
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
                            DialogsTouCai.hideVerifyPhotoDialog(ActLoginPasswordTouCai.this);
                            userManager.getSmsCodeWithTimer(ActLoginPasswordTouCai.this, false, phone, smsPhotoVerify, successCallback, failureCallback);
                        }
                        B.tvSmsCode.setOnClickListener(ActLoginPasswordTouCai.this);
                        return true;
                    }
                };

                userManager.getSmsCodeWithTimer(ActLoginPasswordTouCai.this, false, phone, null, successCallback, failureCallback);
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (userManager.isLogined() && !(getIntent().getBooleanExtra("isFromCheck", false))) { //注册完成后退出
            finish();
        }
        String account = Views.getText(B.etAccount);
        String pwd = Views.getText(B.etPwd);
        if (Strs.isNotEmpty(account) && Strs.isNotEmpty(pwd)
                && userManager.isUserFingerPrint()) {
            userManager.fingerPrint(this, new UserManager.IUserFingerPrintCallback() {
                @Override
                public void onSuccess() {
                    B.ivLoginBtn.performClick();
                }

                @Override
                public void onFail(String msg) {
                    Toasts.show(ActLoginPasswordTouCai.this, msg, false);
                }
            });
        }
        FloatActionController.checkAndShowFloat(this);

        App application = (App) getApplication();
        if (application != null && application.needUpdateTip) {
            UserManager.getIns().checkLatestVersion(ActLoginPasswordTouCai.this, new UserManager.IVersionUpdateCallback() {
                @Override
                public void noNeedUpdate() {

                }

                @Override
                public void notTipUpdate() {
                    application.needUpdateTip = false;
                }
            });
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
            B.tvSmsCode.setOnClickListener(ActLoginPasswordTouCai.this);
            //B.tvSmsCode.setBackgroundResource(R.drawable.sh_bd_rec_red_white_oval);
        }
        FloatActionController.getInstance().hide();
    }

    //登陆
    private void loginWithPassword(final String account, String pwd, @Nullable String capchaCode) {
        String pwdStr = "";
        clearTextPassword = userManager.getClearTextPassword();
        if (clearTextPassword.equals(Views.getText(B.etPwd)) && Strs.isNotEmpty(inputPassword)) {
            pwdStr = inputPassword;
        } else {
            clearTextPwd = pwd;
            pwdStr = MD5.md5(pwd).toLowerCase();
            if (ConfigTouCai.USE_AES_LOGIN) {
                try {
                    pwdStr = AESUtil.aesPKCS5PaddingEncrypt(pwdStr, ConfigTouCai.AES_LOGIN_KEY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        final String finalPwdStr = pwdStr;
        Log.d("ActLoginPasswordTouCai", "pwdStr:" + pwdStr);
        userManager.loginWithPassword(account, pwdStr, capchaCode, new UserManagerTouCai.IUserLoginCallback() {
            @Override
            public void onBefore() {
                DialogsTouCai.showProgressDialog(ActLoginPasswordTouCai.this, "登录中...");
            }

            @Override
            public void onUserLogined(boolean isDataInited) {
                if (isDataInited) {  //已同步到用户数据
                    userManager.setAccount(account);
                    userManager.setLastAccount(account);
                    if (isRemeberPassword) {
                        userManager.setPassword(finalPwdStr);
                        if (BaseConfig.USE_AES_LOGIN) {
                            userManager.setLastPasswordForAes(finalPwdStr);
                        } else {
                            userManager.setLastPassword(finalPwdStr);
                        }
                        userManager.setLastRememberCheck(true);
                    }
                    userManager.setClearTextPassword(clearTextPwd);
                    /*隐藏软键盘*/
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(ActLoginPasswordTouCai.this.getCurrentFocus().getWindowToken(), 0);
                    }
                    String toWhere = getIntent().getStringExtra("to");
                    if (toWhere != null) {
                        if ("lottery".equals(toWhere) && getIntent().getStringExtra("lotteryId") != null) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ILotteryKind kind = LotteryKind.find(Strs.parse("lotteryId", 0));
                                    CtxLotteryTouCai.launchLotteryPlay(ActLoginPasswordTouCai.this, kind);
                                    finish();
                                }
                            }, 300);
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ActMain.mainKillSwitch = false;
                                    ActMain.navigatePage = 0;
                                    finish();
                                }
                            }, 300);
                        }
                    } else {
                        if (Strs.isNotEmpty(mPushUrl)) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ActWebX5.launch(ActLoginPasswordTouCai.this, mPushUrl, true);
                                    finish();
                                }
                            }, 300);
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ActMain.mainKillSwitch = false;
                                    ActMain.navigatePage = 0;
                                    finish();
                                }
                            }, 300);
                        }
                    }

                    //常玩数据
                    //getChangwanData();
                    ((App) getApplication()).resetAdvPopShownFlags();
                    ((App) getApplication()).initMyIm(getSessionID());
                    EventBus.getDefault().post(new DajiangPushMode(0));
                    EventBus.getDefault().post(new Event.LoginEvent());
                } else {
                    Dialogs.updateProgressDialog("获取数据中...");
                }

                loginFailCount.onNext(true);
            }

            @Override
            public void onUserNeedValidateCode() {
                DialogsTouCai.hideProgressDialog(ActLoginPasswordTouCai.this);
                B.vgVerify.setVisibility(View.VISIBLE);
                userManager.getVerifyImage(ActLoginPasswordTouCai.this, B.ivVercifyCode);
                B.vgVerify.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.hideProgressDialog(ActLoginPasswordTouCai.this);
                    }
                }, 600);
            }

            @Override
            public void onUserLoginFailed(String msg) {
                Toasts.show(ActLoginPasswordTouCai.this, msg);
                userManager.getVerifyImage(ActLoginPasswordTouCai.this, B.ivVercifyCode);
                loginFailCount.onNext(false);
            }

            @Override
            public void onAfter() {
                B.vgVerify.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.hideProgressDialog(ActLoginPasswordTouCai.this);
                    }
                }, 600);
            }
        });
    }


    //手机登陆
    private void loginWithPhone(final String phone, String code, @Nullable String capchaCode) {
        userManager.loginWithPhone(phone, code, capchaCode, new UserManagerTouCai.IUserLoginCallback() {

            @Override
            public void onBefore() {
                DialogsTouCai.showProgressDialog(ActLoginPasswordTouCai.this, "登录中...");
            }

            @Override
            public void onUserLogined(boolean isDataInited) {
                if (isDataInited) {  //已同步到用户数据
                    //用户的账户名
                    userManager.setAccount(phone);
                    userManager.setLastAccount(phone);

                    userManager.setPassword("");
                    if (BaseConfig.USE_AES_LOGIN) {
                        userManager.setLastPasswordForAes("");
                    } else {
                        userManager.setLastPassword("");
                    }

                    /*隐藏软键盘*/
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(ActLoginPasswordTouCai.this.getCurrentFocus().getWindowToken(), 0);
                    }
                    String toWhere = getIntent().getStringExtra("to");
                    if (toWhere != null) {
                        if ("lottery".equals(toWhere) && getIntent().getStringExtra("lotteryId") != null) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ILotteryKind kind = LotteryKind.find(Strs.parse("lotteryId", 0));
                                    CtxLotteryTouCai.launchLotteryPlay(ActLoginPasswordTouCai.this, kind);
                                    finish();
                                }
                            }, 300);
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ActMain.mainKillSwitch = false;
                                    ActMain.navigatePage = 2;
                                    finish();
                                }
                            }, 300);
                        }
                    } else {
                        if (Strs.isNotEmpty(mPushUrl)) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ActWebX5.launch(ActLoginPasswordTouCai.this, mPushUrl, true);
                                    finish();
                                }
                            }, 300);
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ActMain.mainKillSwitch = false;
                                    ActMain.navigatePage = 0;
                                    finish();
                                }
                            }, 300);
                        }
                    }
                    //常玩数据
                    //getChangwanData();
                    ((App) getApplication()).resetAdvPopShownFlags();
                    EventBus.getDefault().post(new DajiangPushMode(0));
                } else {
                    Dialogs.updateProgressDialog("获取数据中...");
                }
            }

            @Override
            public void onUserNeedValidateCode() {
                DialogsTouCai.hideProgressDialog(ActLoginPasswordTouCai.this);
                B.vgVerify.setVisibility(View.VISIBLE);
                userManager.getVerifyImage(ActLoginPasswordTouCai.this, B.ivVercifyCode);
                B.vgVerify.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.hideProgressDialog(ActLoginPasswordTouCai.this);
                    }
                }, 600);
            }

            @Override
            public void onUserLoginFailed(String msg) {
                DialogsTouCai.hideProgressDialog(ActLoginPasswordTouCai.this);
                Toasts.show(ActLoginPasswordTouCai.this, msg);
                userManager.getVerifyImage(ActLoginPasswordTouCai.this, B.ivVercifyCode);
            }

            @Override
            public void onAfter() {
                B.vgVerify.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.hideProgressDialog(ActLoginPasswordTouCai.this);
                    }
                }, 600);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DialogsTouCai.hideProgressDialog(this);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isShowing = false;
        MM.http.cancellAllByTag(userManager);
        DialogsTouCai.hideProgressDialog(this);
    }

    @SuppressLint("CheckResult")
    private void subscribeLoginCount5() {
        loginFailCount.hide()
                .take(5)
                .subscribeWith(new Observer<Boolean>() {
                    Disposable d;
                    boolean last;

                    @Override
                    public void onSubscribe(Disposable d) {
                        this.d = d;
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            d.dispose();
                            subscribeLoginCount5();
                        }
                        last = aBoolean;
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        subscribeLoginCount5();

                        if (!last)
                            DialogsTouCai.showPasswordErrorDialog(ActLoginPasswordTouCai.this, v -> {
                                ActWeb.launchCustomService(ActLoginPasswordTouCai.this);
                            });
                    }
                });
    }

    private void promptOnQuit(View.OnClickListener listener) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("你是否退出登录?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onClick(null);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    /**
     * 设置皮肤问题
     *
     * @param skinSetting
     */
    private void setBgFromWeb(int skinSetting) {
        B.ivClose.setImageResource(CommonConsts.setLoginCancelIcon(skinSetting));
        B.ivAccount.setImageResource(CommonConsts.setLoginUserIocn(skinSetting));
        B.ivPwd.setImageResource(CommonConsts.setLoginUserPwd(skinSetting));
        //B.ivLoginBtn.setBackgroundResource(CommonConsts.setLoginBtn(skinSetting));
        B.tvRegist.setBackgroundResource(CommonConsts.setRejistBtn(skinSetting));
        B.tvRegist.setTextColor(getResources().getColor(CommonConsts.setRejistTextBtn(skinSetting)));
        B.tvForgetPwd.setTextColor(getResources().getColor(CommonConsts.setForgotPwdBtn(skinSetting)));
        B.ivSmsCode.setImageResource(CommonConsts.setLoginCode(skinSetting));
        B.ivPhone.setImageResource(CommonConsts.setPhoneLogin(skinSetting));
        B.ivBgPic.setVisibility(View.GONE);
        Log.d("ActLoginPasswordTouCai", "skinSetting:" + skinSetting);
        if (skinSetting == 0) {
            HttpActionTouCai.getUpdateContentList(this, Consitances.contentManager.BG_LOGIN, new AbHttpResult() {
                @Override
                public void setupEntity(AbHttpRespEntity entity) {
                    entity.putField("data", new TypeToken<UpdateContentList>() {
                    }.getType());
                }

                @Override
                public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                    if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                        UpdateContentList list = getField(extra, "data", null);

                        if (list == null || list.getList() == null || list.getList().size() == 0) {
                            return true;
                        }
                        B.ivBgPic.setVisibility(View.VISIBLE);
                        Glide.with(ActLoginPasswordTouCai.this).load(ENV.curr.host + list.getList().get(0).getMobilePictureUrl()).asBitmap().listener(new RequestListener<String, Bitmap>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                                B.ivBgPic.setVisibility(View.GONE);
                                B.vBg.setBackgroundResource(CommonConsts.setLoginHeadBg(skinSetting));
                                //B.scrllBg.setBackgroundResource(CommonConsts.setLoginScrollBg(skinSetting));
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                return false;
                            }
                        }).into(B.ivBgPic);
                        B.vBg.setBackgroundResource(0);

                    } else {
                        B.ivBgPic.setVisibility(View.GONE);
                        B.vBg.setBackgroundResource(CommonConsts.setLoginHeadBg(skinSetting));
                        //B.scrllBg.setBackgroundResource(CommonConsts.setLoginScrollBg(skinSetting));
                    }
                    return super.onSuccessGetObject(code, error, msg, extra);
                }

                @Override
                public boolean onError(int status, String content) {
                    B.ivBgPic.setVisibility(View.GONE);
                    B.vBg.setBackgroundResource(CommonConsts.setLoginHeadBg(skinSetting));
                    //B.scrllBg.setBackgroundResource(CommonConsts.setLoginScrollBg(skinSetting));
                    return super.onError(status, content);
                }
            });
        } else {
            //当设置默认皮肤时，即skinSetting=0时，取后台动态图片
            B.ivBgPic.setVisibility(View.VISIBLE);
            B.vBg.setBackgroundResource(CommonConsts.setLoginHeadBg(skinSetting));
            B.ivBgPic.setBackgroundResource(CommonConsts.setLoginScrollBg(skinSetting));
            //B.scrllBg.setBackgroundResource(CommonConsts.setLoginScrollBg(skinSetting));
        }
    }

    private void getChangwanData() {
        //用户常玩数据
        HttpActionTouCai.getFrequentlyUsedLottery(this, new AbHttpResult() {

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<String>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (error == 0 && code == 0) {
                    String data = (String) extra.get("data");
                    //存到sp
                    UserManagerTouCai.getIns().setUserChangwanGameData(data);

                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }
        });
    }

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
}
