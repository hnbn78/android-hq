package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.ab.callback.AbCallback;
import com.ab.global.ENV;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.module.MM;
import com.ab.thread.ThreadCollector;
import com.ab.util.MD5;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.event.DajiangPushMode;
import com.desheng.app.toucai.global.App;
import com.desheng.app.toucai.global.ConfigTouCai;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.UpdateContentList;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.manager.UserManager;
import com.desheng.base.panel.ActWeb;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.ToggleImageButton;
import com.shark.tc.R;
import com.shark.tc.databinding.ActRegistTouCaiBinding;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.regex.Pattern;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * 注册
 * Created by Administrator on 2017/6/29 0029.
 */
public class ActRegistTouCai extends AbAdvanceActivity<ActRegistTouCaiBinding> implements View.OnClickListener {

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

    private int registType = 1;
    private boolean isAccountExist = false;

    public static void launch(Activity act) {
        Intent itt = new Intent(act, ActRegistTouCai.class);
        act.startActivity(itt);
    }

    @Override
    protected int getBaseLayoutId() {
        return R.layout.ab_activity_base_overlay_toolbar_front;
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_regist_tou_cai;
    }


    @Override
    protected void init() {
        setToolbarLeftBtn(UIHelper.BACK_WHITE_CIRCLE_ARROW);
        setToolbarBgColor(R.color.transparent);
        setToolbarButtonRightText("立即登录");
        setToolbarButtonRightTextSize(15);
        setToolbarButtonRightTextColor(R.color.white);
        setToolbarRightButtonGroupClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActLoginPasswordTouCai.launch(Act);
                finish();
            }
        });

        setStatusBarTranslucentAndLightContentWithPadding();
        if (isShowing) {
            finish();
        }
        isShowing = true;

        userManager = UserManager.getIns(UserManagerTouCai.class);
        setBgFromWeb();
        /**
         * 是否需要验证码
         */
        B.vgVerify.setVisibility(View.GONE);
        needVerifyCode = getIntent().getBooleanExtra("needVerifyCode", false);
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
                userManager.getVerifyImage(ActRegistTouCai.this, B.ivVercifyCode);
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


        B.ivRegistType.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                registType = (registType + 1) % 2;

                B.vgUserName.setVisibility(View.GONE);
                B.vgPassword.setVisibility(View.GONE);
                B.vgRePassword.setVisibility(View.GONE);
                B.vgPhone.setVisibility(View.GONE);
                B.vgSmsCode.setVisibility(View.GONE);
                B.vgVerify.setVisibility(View.GONE);

                if (registType == UserManager.REGIST_TYPE_ACCOUNT) {
                    B.vgUserName.setVisibility(View.VISIBLE);
                    B.vgUserName.post(() -> {
                        B.vgUserName.requestFocus();
                    });
                    B.vgPassword.setVisibility(View.VISIBLE);
                    B.vgRePassword.setVisibility(View.VISIBLE);
                    B.vgVerify.setVisibility(View.VISIBLE);
                    UserManager.getIns().getVerifyImage(ActRegistTouCai.this, B.ivVercifyCode);
                    B.ivRegistType.setImageResource(R.mipmap.ic_regist_type_phone);
                    B.tvRegistType.setText("手机注册");
                } else if (registType == UserManager.REGIST_TYPE_PHONE) {
                    B.vgPhone.setVisibility(View.VISIBLE);
                    B.vgUserName.post(() -> {
                        B.vgPhone.requestFocus();
                    });
                    B.vgPassword.setVisibility(View.VISIBLE);
                    B.vgSmsCode.setVisibility(View.VISIBLE);

                    B.ivRegistType.setImageResource(R.mipmap.ic_regist_type_account);
                    B.tvRegistType.setText("账号注册");
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
                isAccountExist = false;
                String account = s.toString();
                if (Strs.isNotEmpty(account)) {
                    B.ivAccountDelete.setVisibility(View.VISIBLE);
                } else {
                    B.ivAccountDelete.setVisibility(View.INVISIBLE);
                }

                //6-10位字符，只能包含英文字母、数字、下划线
                boolean isValide = userManager.isValideAccount(account);

                B.ivAccountValidate.setVisibility(isValide ? View.VISIBLE : View.INVISIBLE);
                B.etAccount.setTextColor(Views.fromColors(isValide ? R.color.text_login : R.color.text_login_error));

                checkBtnEnable();
            }
        });
        B.etAccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String str = Views.getText(B.etAccount);
                if (!hasFocus && userManager.isValideAccountOrPhone(str)) {
                    checkPhone(B.etAccount);
                }
            }
        });

        B.ivPhoneDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                B.etPhone.setText("");
            }
        });
        B.etPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    checkPhone(B.etPhone);
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
                B.ivPhoneValidate.setVisibility(isValide ? View.VISIBLE : View.INVISIBLE);
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
        B.ivPwdShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isShow = (Boolean) B.ivPwdShow.getTag();
                if (isShow == null) {
                    isShow = false;
                }
                isShow = !isShow;
                if (isShow) {
                    B.ivPwdShow.setImageResource(R.mipmap.ic_pwd_show);
                    tempEtIndex = B.etPwd.getSelectionStart();
                    B.etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    B.etPwd.setSelection(tempEtIndex);
                } else {
                    B.ivPwdShow.setImageResource(R.mipmap.ic_pwd_hide);
                    tempEtIndex = B.etPwd.getSelectionStart();
                    B.etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    B.etPwd.setSelection(tempEtIndex);
                }
                B.ivPwdShow.setTag(isShow);
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

                boolean isValide = UserManagerTouCai.getIns(UserManagerTouCai.class).isValidePwdForRegist(pwd);

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
        B.etPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Strs.isNotEmpty(Views.getText(B.etRePwd))) {
                        if (!Views.getText(B.etRePwd).equals(Views.getText(B.etPwd))) {
                            Toasts.show(Act, "两次输入密码不一致");
                        }
                    }
                }
            }
        });

        B.ivRePwdDelete.setOnClickListener((view) -> {
            B.etRePwd.setText("");
        });
        B.ivRePwdShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isShow = (Boolean) B.ivRePwdShow.getTag();
                if (isShow == null) {
                    isShow = false;
                }
                isShow = !isShow;
                if (isShow) {
                    B.ivRePwdShow.setImageResource(R.mipmap.ic_pwd_show);
                    tempEtIndex = B.etRePwd.getSelectionStart();
                    B.etRePwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    B.etRePwd.setSelection(tempEtIndex);
                } else {
                    B.ivRePwdShow.setImageResource(R.mipmap.ic_pwd_hide);
                    tempEtIndex = B.etRePwd.getSelectionStart();
                    B.etRePwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    B.etRePwd.setSelection(tempEtIndex);
                }
                B.ivRePwdShow.setTag(isShow);
            }
        });
        B.etRePwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String rePwd = s.toString();

                boolean isValide = userManager.isValidePwdForRegist(rePwd)
                        && rePwd.equals(Views.getText(B.etPwd));

                B.ivRePwdValidate.setVisibility(isValide ? View.VISIBLE : View.INVISIBLE);
                B.etRePwd.setTextColor(Views.fromColors(isValide ? R.color.text_login : R.color.text_login_error));

                if (Strs.isNotEmpty(s.toString())) {
                    B.ivRePwdDelete.setVisibility(View.VISIBLE);
                } else {
                    B.ivRePwdDelete.setVisibility(View.INVISIBLE);
                }

                checkBtnEnable();
            }
        });

        B.etRePwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Strs.isNotEmpty(Views.getText(B.etPwd))) {
                        if (!Views.getText(B.etPwd).equals(Views.getText(B.etRePwd))) {
                            Toasts.show(Act, "两次输入密码不一致");
                        }
                    }
                }
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


        //开户协议
        String str = getResources().getString(R.string.regist_xieyi_tips);
        B.tvAgreement.setText(Html.fromHtml(str));
        B.tvAgreement.setOnClickListener((view) -> {
            ActWeb.launch(ActRegistTouCai.this, ENV.curr.host + "/registerLogin/index.html#/protocol_app");
        });

        //统一协议
        B.cbAgreement.setOnCheckedChangeListener(new ToggleImageButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ToggleImageButton buttonView, boolean isChecked) {
                isRemeberPassword = isChecked;
                checkBtnEnable();
            }
        });

        B.ivOneKey.setOnClickListener(this);
        B.tvOneKey.setOnClickListener(this);
        B.tvSmsCode.setOnClickListener(this);
        B.tvRegistBtn.setOnClickListener(this);

        checkBtnEnable();
    }

    /**
     * 设置皮肤问题
     */
    private void setBgFromWeb() {
        HttpActionTouCai.getUpdateContentList(this, Consitances.contentManager.BG_REGISTER, new AbHttpResult() {
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
                    Glide.with(ActRegistTouCai.this).load(ENV.curr.host + list.getList().get(0).getMobilePictureUrl())
                            .asBitmap().listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            B.ivBg.setBackgroundResource(R.mipmap.ic_regist_bg);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    }).into(B.ivBg);

                } else {
                    B.ivBg.setBackgroundResource(R.mipmap.ic_regist_bg);
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                B.ivBg.setBackgroundResource(R.mipmap.ic_regist_bg);
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

    private String phoneNumber = "";

    private void checkPhone(EditText editText) {
        String number = editText.getText().toString();
        if (editText == B.etPhone && number.length() != 11) return;
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
                if (isExist)
                    Toasts.show(editText == B.etPhone ? "手机号已注册" : "该用户名已注册", false);
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show("content ====>  " + content);
                return super.onError(status, content);
            }
        });
    }

    private void checkBtnEnable() {
        String pwd = Views.getText(B.etPwd);
        if (registType == UserManager.REGIST_TYPE_ACCOUNT) {
            String rePwd = Views.getText(B.etRePwd);
            if (userManager.isValideAccount(Views.getText(B.etAccount))
                    && userManager.isValidePwdForRegist(pwd)
                    && userManager.isValidePwdForRegist(rePwd)
                    && pwd.equals(rePwd)
                    && B.cbAgreement.isChecked()) {

                //判断是否有图形验证码
                if (B.vgVerify.getVisibility() == View.VISIBLE) {
                    B.tvRegistBtn.setEnabled(userManager.isValidePhotoVerifyCode(Views.getText(B.etVerify)));
                } else {
                    B.tvRegistBtn.setEnabled(true);
                }
            } else {
                B.tvRegistBtn.setEnabled(false);
            }
        } else if (registType == UserManager.REGIST_TYPE_PHONE) {
            if (userManager.isValidePhone(Views.getText(B.etPhone))
                    && userManager.isValidePwdForRegist(pwd)
                    && userManager.isValideSmsVerifyCode(Views.getText(B.etSmsCode))
                    && B.cbAgreement.isChecked()) {
                if (B.vgVerify.getVisibility() == View.VISIBLE) {
                    B.tvRegistBtn.setEnabled(userManager.isValidePhotoVerifyCode(Views.getText(B.etVerify)));
                } else {
                    B.tvRegistBtn.setEnabled(true);
                }
            } else {
                B.tvRegistBtn.setEnabled(false);
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvRegistBtn:

                String pwd = Views.getText(B.etPwd);
                if (!Views.checkTextAndToast(Act, pwd, "请输入密码!")) {
                    return;
                }
                Pattern pattern1 = Pattern.compile("[0-9A-Z_a-z]{6,24}");
                if (!pattern1.matcher(pwd).matches()) {
                    Toasts.show(this, "密码为6到24位字母和数字组合", false);
                    return;
                }
//                Pattern pattern2 = Pattern.compile("[0-9]+"); //必须
//                if (!pattern2.matcher(pwd).find()) {
//                    Toasts.show(this, "密码为6到24位字母和数字组合, 必须同时包含字符,数字", false);
//                    return;
//                }

                String rePwd = Views.getText(B.etRePwd);
                if (B.vgRePassword.getVisibility() == View.VISIBLE && !Views.checkTextAndToast(Act, rePwd, "请输入密码!")) {
                    return;
                }

                if (B.vgRePassword.getVisibility() == View.VISIBLE && !pwd.equals(rePwd)) {
                    Toasts.show(this, "两次输入密码不一致!", false);
                    return;
                }

                //推荐码（非必填）
                String inviteCode = Views.getText(B.etInviteCode);

                String verifyCode = Views.getText(B.etVerify);
                if (B.vgVerify.getVisibility() == View.VISIBLE &&
                        !Views.checkTextAndToast(Act, verifyCode, "请输入图形校验码!")) {
                    return;
                }

                if (!B.cbAgreement.isChecked()) {
                    Toasts.show(this, "请先阅读与同意用户协议!", false);
                    return;
                }

                if (registType == UserManager.REGIST_TYPE_ACCOUNT) {
                    String account = Views.getText(B.etAccount);
                    if (!Views.checkTextAndToast(Act, account, "请输入用户名!")) {
                        return;
                    }
                    if (!UserManagerTouCai.getIns().isValideAccount(account)) {
                        Toasts.show(this, "用户名由6-10位字母或数字组成", false);
                        return;
                    }
                    regist(UserManager.REGIST_TYPE_ACCOUNT, "", account, verifyCode, "", pwd, inviteCode);
                } else {
                    String phone = Views.getText(B.etPhone);
                    if (!Views.checkPhoneAndToast(Act, phone, "请输入正确手机号码!")) {
                        return;
                    }
                    String smsVerifyCode = Views.getText(B.etSmsCode);
                    if (!Views.checkTextAndToast(Act, smsVerifyCode, "请输入短信验证码!")) {
                        return;
                    }
                    regist(UserManager.REGIST_TYPE_PHONE, phone, "", verifyCode, smsVerifyCode, pwd, inviteCode);
                }

                break;
            case R.id.tvAgreement:
                ActWeb.launchCustomService(this);
                break;
            case R.id.ivOneKey:
            case R.id.tvOneKey:
                registOneKey();
                break;
            case R.id.tvSmsCode:
                String phone = Views.getText(B.etPhone);
                if (!Views.checkTextAndToast(Act, phone, "请输入正确手机手机验证码!")) {
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
                                    B.tvSmsCode.setOnClickListener(Act);
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
                            DialogsTouCai.hideVerifyPhotoDialog(Act);
                            UserManager.getIns(UserManagerTouCai.class).getSmsCodeWithTimer(Act, true, phone, smsPhotoVerify, successCallback, failureCallback);
                        }
                        B.tvSmsCode.setOnClickListener(Act);
                        return true;
                    }
                };

                UserManager.getIns(UserManagerTouCai.class).getSmsCodeWithTimer(Act, true, phone, null, successCallback, failureCallback);

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
            B.tvSmsCode.setOnClickListener(Act);
            //B.tvSmsCode.setBackgroundResource(R.drawable.sh_bd_rec_red_white_oval);
        }
    }

    private void registOneKey() {
        ActWeb.launchCustomService(Act);
    }

    //注册
    //registUser(Object tag, int registType, final String phoneNum, String name, String yanzCode, final String phoneYzCode, final String pwd, final IUserRegistCallback registCallback, final IUserLoginCallback loginCallback)
    private void regist(final int type, final String phoneNum, final String name, String yanzCode, final String phoneYzCode, final String pwd, String inviteCode) {
        final UserManager userManager = UserManagerTouCai.getIns();
        userManager.registUser(Act, type, phoneNum, name, yanzCode, phoneYzCode, pwd,
                new UserManager.IUserRegistCallback() {
                    @Override
                    public void onBefore() {
                        DialogsTouCai.showProgressDialog(Act, "");
                    }

                    @Override
                    public void onUserRegisted() {

                    }

                    @Override
                    public void onUserRegistFailed(String msg) {
                        DialogsTouCai.hideProgressDialog(Act);
                        Toasts.show(Act, msg, false);
                        if (type == UserManager.REGIST_TYPE_ACCOUNT) {
                            UserManagerTouCai.getIns().getVerifyImage(ActRegistTouCai.this, B.ivVercifyCode);
                        }
                    }

                    @Override
                    public void onAfter() {

                    }
                },
                new UserManager.IUserLoginCallback() {
                    @Override
                    public void onBefore() {

                    }

                    @Override
                    public void onUserLogined(boolean isDataInited) {
                        if (isDataInited) {
                            if (type == 1) { //手机注册
                                userManager.setAccount(phoneNum);
                                userManager.setLastAccount(phoneNum);
                            } else if (type == 0) {
                                userManager.setAccount(name);
                                userManager.setLastAccount(name);
                            }
                            userManager.setClearTextPassword(pwd);
                            userManager.setPassword(MD5.md5(pwd).toLowerCase());
                            userManager.setLastPassword(MD5.md5(pwd).toLowerCase());
                            userManager.setLastRememberCheck(true);

                            //常玩数据
                            //UserManagerTouCai.getIns().getChangwanData();
                            ((App) getApplication()).resetAdvPopShownFlags();
                            EventBus.getDefault().post(new DajiangPushMode(0));
                            ThreadCollector.getIns().postDelayOnUIThread(300, new Runnable() {
                                @Override
                                public void run() {
                                    ActRegistTouCaiSuccess.launchAndFinish(Act);
                                }
                            });
                        }
                    }

                    @Override
                    public void onUserNeedValidateCode() {
                        //UserManagerTouCai.getIns().getVerifyImage(B.ivVercifyCode);
                    }

                    @Override
                    public void onUserLoginFailed(String msg) {
                        Toasts.show(Act, msg, false);
                    }

                    @Override
                    public void onAfter() {
                        DialogsTouCai.hideProgressDialog(Act);
                    }
                }, inviteCode);
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


    private void toMain() {
        ActMain.mainKillSwitch = false;
        launchOther(ActMain.class);
    }
}
