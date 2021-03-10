package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ab.module.MM;
import com.ab.util.Dialogs;
import com.ab.util.MD5;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.controller.FloatActionController;
import com.desheng.base.manager.UserManager;
import com.desheng.base.panel.ActWeb;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.ToggleImageButton;
import com.shark.tc.R;
import com.shark.tc.databinding.ActLoginPasswordBinding;

/**
 * 登录页面A
 * Created by Administrator on 2017/6/29 0029.
 */
@Deprecated
public class ActLoginPassword extends AbAdvanceActivity<ActLoginPasswordBinding> implements View.OnClickListener {
    
    public static boolean isShowing;
    private boolean needVerifyCode = false;
    private boolean isRemeberPassword = true;
    private String inputPassword;
    
    public static void launch(Activity act) {
        Intent itt = new Intent(act, ActLoginPassword.class);
        act.startActivity(itt);
    }
    
    public static void launch(Activity act, boolean needVerifyCode) {
        Intent itt = new Intent(act, ActLoginPassword.class);
        itt.putExtra("needVerifyCode", needVerifyCode);
        act.startActivity(itt);
    }
    
    @Override
    protected int getBaseLayoutId() {
        return R.layout.ab_activity_base_overlay_toolbar_front;
    }
    
    @Override
    public int getLayoutId() {
        return R.layout.act_login_password;
    }
    
    
    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarLeftBackAndRightTitle(this, getToolbar(), "首页");
        UIHelper.setToolbarRightButtonGroupClickListener(getToolbar(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActMain.mainKillSwitch = false;
                launchOther(ActMain.class);
            }
        });
        
        setStatusBarTranslucentAndLightContentWithPadding();
        if (isShowing || ActLoginPassword.isShowing) {
            finish();
        }
        isShowing = true;
    
        String inputUserName = getIntent().getStringExtra("userName");
        if (Strs.isNotEmpty(inputUserName)) {
            getBinding().etAccount.setText(inputUserName);
        }
    
        inputPassword = getIntent().getStringExtra("password");
        if (Strs.isNotEmpty(inputPassword)) {
            getBinding().etPwd.setText(CtxLottery.PWD_MOCK);
        }
    
        /**
         * 是否需要验证码
         */
        getBinding().vgVerify.setVisibility(View.GONE);
        needVerifyCode = getIntent().getBooleanExtra("needVerifyCode", false);
        if (needVerifyCode) {
            getBinding().vgVerify.setVisibility(View.VISIBLE);
            UserManager.getIns().getVerifyImage(this,getBinding().ivVercifyCode);
        } else {
            getBinding().vgVerify.setVisibility(View.GONE);
        }
        getBinding().ivVercifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagerTouCai.getIns().getVerifyImage(ActLoginPassword.this,getBinding().ivVercifyCode);
            }
        });
        
        getBinding().tvContact.setOnClickListener(this);
    
        
        if(UserManagerTouCai.getIns().isRememberedPassword()){
            if(Strs.isNotEmpty(UserManagerTouCai.getIns().getAccount())){
                getBinding().etAccount.setText(UserManagerTouCai.getIns().getAccount());
                getBinding().etPwd.setText(CtxLottery.PWD_MOCK);
            }
        }
        //记住密码
        getBinding().cbRemBtn.setOnCheckedChangeListener(new ToggleImageButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ToggleImageButton buttonView, boolean isChecked) {
                isRemeberPassword = isChecked;
                if(!isChecked){
                    UserManagerTouCai.getIns().setRememberedPassword(false);
                    getBinding().etPwd.setText("");
                }
            }
        });
        
        getBinding().ivLoginBtn.setOnClickListener(this);
        getBinding().ivPwdClear.setOnClickListener(this);
        getBinding().tvRegist.setOnClickListener(this);
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
            case R.id.ivLoginBtn:
                String account = Views.getText(getBinding().etAccount);
                if (!Views.checkTextAndToast(ActLoginPassword.this, account, "请输入用户名!")) {
                    return;
                }
                
                String pwd = Views.getText(getBinding().etPwd);
                if (!Views.checkTextAndToast(ActLoginPassword.this, pwd, "请输入密码!")) {
                    return;
                }
                
                String verifyCode =  Views.getText(getBinding().etVerify);
                if(getBinding().vgVerify.getVisibility() == View.VISIBLE &&
                        !Views.checkTextAndToast(ActLoginPassword.this, verifyCode, "请输入校验码!")){
                    return;
                }
                
                loginWithPassword(account, pwd, verifyCode);
                break;
            case R.id.tvContact:
                ActWeb.launchCustomService(this);
                break;
            case R.id.ivPwdClear:
                getBinding().etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                getBinding().etPwd.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getBinding().etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                }, 3000);
                break;
            case R.id.tvRegist:
                isShowing = false;
                ActRegistTouCai.launch(this);
                break;
        }
    }
    
    
    @Override
    protected void onStart() {
        super.onStart();
        if(UserManagerTouCai.getIns().isLogined() && !(getIntent().getBooleanExtra("isFromCheck", false))){ //注册完成后退出
            finish();
        }
        String account = Views.getText(getBinding().etAccount);
        String pwd = Views.getText(getBinding().etPwd);
        if(Strs.isNotEmpty(account) && Strs.isNotEmpty(pwd)
                && UserManager.getIns().isUserFingerPrint()){
            UserManager.getIns().fingerPrint(this, new UserManager.IUserFingerPrintCallback() {
                @Override
                public void onSuccess() {
                    getBinding().ivLoginBtn.performClick();
                }
            
                @Override
                public void onFail(String msg) {
                    Toasts.show(ActLoginPassword.this, msg, false);
                }
            });
        }
        FloatActionController.checkAndShowFloat(this);
    }
    

    
    //登陆
    private void loginWithPassword(final String account, String pwd, @Nullable String capchaCode) {
        String pwdStr = MD5.md5(pwd).toLowerCase();
        if(UserManagerTouCai.getIns().isRememberedPassword() && Strs.isNotEmpty(UserManagerTouCai.getIns().getPassword())
                && CtxLottery.PWD_MOCK.equals(pwd)){
            pwdStr = UserManagerTouCai.getIns().getPassword();
        }
        if (UserManager.getIns().isRememberedPassword() && Strs.isNotEmpty(inputPassword)
                && CtxLottery.PWD_MOCK.equals(pwd)) {
            pwdStr = inputPassword;
        }
        
        final String finalPwdStr = pwdStr;
        UserManagerTouCai.getIns().loginWithPassword(account, pwdStr, capchaCode, new UserManagerTouCai.IUserLoginCallback() {
            @Override
            public void onBefore() {
                DialogsTouCai.showProgressDialog(ActLoginPassword.this, "登录中...");
            }
            
            @Override
            public void onUserLogined(boolean isDataInited) {
                if (isDataInited) {  //已同步到用户数据
                    if(isRemeberPassword){
                        UserManagerTouCai.getIns().setAccount(account);
                        UserManagerTouCai.getIns().setPassword(finalPwdStr);
                        UserManagerTouCai.getIns().setRememberedPassword(true);
                    }
                    /*隐藏软键盘*/
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(ActLoginPassword.this.getCurrentFocus().getWindowToken(), 0);
                    }
                    String toWhere = getIntent().getStringExtra("to");
                    if (toWhere != null) {
                        if ("lottery".equals(toWhere) && getIntent().getStringExtra("lotteryId") != null) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ILotteryKind kind = LotteryKind.find(Strs.parse("lotteryId", 0));
                                    CtxLotteryTouCai.launchLotteryPlay(ActLoginPassword.this, kind);
                                    finish();
                                }
                            }, 300);
                        }else{
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ActMain.mainKillSwitch = false;
                                    ActMain.navigatePage = 0;
                                    finish();
                                }
                            }, 300);
                        }
                    }else{
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ActMain.mainKillSwitch = false;
                                ActMain.navigatePage = 0;
                                finish();
                            }
                        }, 300);
                    }
                }else{
                    Dialogs.updateProgressDialog("获取数据中...");
                }
            }
            
            @Override
            public void onUserNeedValidateCode() {
                DialogsTouCai.hideProgressDialog(ActLoginPassword.this);
                getBinding().vgVerify.setVisibility(View.VISIBLE);
                UserManagerTouCai.getIns().getVerifyImage(ActLoginPassword.this,getBinding().ivVercifyCode);
            }
            
            @Override
            public void onUserLoginFailed(String msg) {
                getBinding().tvLoginInfo.setText(msg);
                UserManagerTouCai.getIns().getVerifyImage(ActLoginPassword.this,getBinding().ivVercifyCode);
            }
            
            @Override
            public void onAfter() {
                DialogsTouCai.hideProgressDialog(ActLoginPassword.this);
            }
        });
    }
    
    @Override
    public void onBackPressed() {
        DialogsTouCai.hideProgressDialog(this);
        if (Strs.isNotEmpty(inputPassword)) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("你已主动退出登录, 继续退出将清除暂存的用户名密码, 是否继续??")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        
                        }
                    })
                    .show();
        }else{
            finish();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isShowing = false;
        MM.http.cancellAllByTag(UserManagerTouCai.getIns());
        DialogsTouCai.hideProgressDialog(this);
    }
    
    
}
