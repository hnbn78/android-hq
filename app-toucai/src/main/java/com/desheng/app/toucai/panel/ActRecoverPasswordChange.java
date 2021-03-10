package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.callback.AbCallback;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.ab.view.MaterialDialog;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.manager.UserManager;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.util.HashMap;

/**
 * Created by user on 2018/3/5.
 */

public class ActRecoverPasswordChange extends AbAdvanceActivity implements View.OnClickListener {
    private EditText current_password_et, new_password_et, aggin_et;
    String currentpwd;
    String newpwd;
    String againPassword;
    private TextView save_tv;
    private ImageView imageView;
    private EditText etPassword;
    private EditText etPasswordAgain;
    private View ivNextBtn;
    private MaterialDialog successDialog;
    
    public static void launch(Activity act, String name, String verify) {
        Intent itt = new Intent(act, ActRecoverPasswordChange.class);
        itt.putExtra("name", name);
        itt.putExtra("verify", verify);
        act.startActivity(itt);
        act.finish();
    }
    
    @Override
    protected int getLayoutId() {
        return R.layout.act_recover_password_change;
    }

    @Override
    public void init() {
        UIHelper.getIns().simpleToolbar(getToolbar(), "找回密码");
        
        setStatusBarTranslucentAndLightContentWithPadding();
        setToolbarLeftBtn(UIHelper.BACK_WHITE_CIRCLE_ARROW);
        
        initView();

    }

    private void initView (){
        etPassword =(EditText) findViewById(R.id.etPassword);
        etPasswordAgain =(EditText) findViewById(R.id.etPasswordAgain);
        ivNextBtn = findViewById(R.id.ivNextBtn);
        ivNextBtn.setOnClickListener(this);
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                findViewById(R.id.layout_error_hint).setVisibility(View.INVISIBLE);
            }
        
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            
            }
        
            @Override
            public void afterTextChanged(Editable s) {
                checkBtnEnable(s.toString(), Views.getText(etPasswordAgain));
            }
        });
        etPasswordAgain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                findViewById(R.id.layout_error_hint).setVisibility(View.INVISIBLE);
            }
        
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            
            }
        
            @Override
            public void afterTextChanged(Editable s) {
                checkBtnEnable(Views.getText(etPassword), s.toString());
            }
        });
    }
    
    private void checkBtnEnable(String pwd, String pwdAgain) {
        
        if (Strs.isEmpty(pwd)) {
            ((TextView) findViewById(R.id.tv_error_hint)).setText("密码不能为空");
            findViewById(R.id.layout_error_hint).setVisibility(View.VISIBLE);
            ivNextBtn.setEnabled(false);
            return;
        }
    
        if (!Strs.isEqual(pwd, pwdAgain)) {
            ((TextView) findViewById(R.id.tv_error_hint)).setText("两次密码输入不一致");
            findViewById(R.id.layout_error_hint).setVisibility(View.VISIBLE);
            ivNextBtn.setEnabled(false);
            return;
        }
    
        if (!UserManager.getIns(UserManagerTouCai.class).isValidePwdForRegist(pwd)) {
            ((TextView) findViewById(R.id.tv_error_hint)).setText("请输入6-24位数字字母组合");
            findViewById(R.id.layout_error_hint).setVisibility(View.VISIBLE);
            ivNextBtn.setEnabled(false);
            return;
        }
        if (UserManager.getIns(UserManagerTouCai.class).isValidePwdForRegist(pwd) && Strs.isNotEmpty(pwdAgain) && pwdAgain.equals(pwd)) {
            ivNextBtn.setEnabled(true);
        }else{
            ivNextBtn.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivNextBtn:
                String pwd = Views.getText(etPassword);
                String pwdAgain = Views.getText(etPasswordAgain);

               

                String name = getIntent().getStringExtra("name");
                String verify = getIntent().getStringExtra("verify");
                
                HttpActionTouCai.resetPwd(this, name, verify, pwd, new AbHttpResult(){
                    @Override
                    public void setupEntity(AbHttpRespEntity entity) {
                        entity.putField("data", Object.class);
                    }
    
                    @Override
                    public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                        if (code == 0 && error == 0) {
                            DialogsTouCai.showChangPwdSuccessDialog(ActRecoverPasswordChange.this, new AbCallback() {
                                @Override
                                public boolean callback(Object obj) {
                                    UserManager.getIns().redirectToLogin(ActRecoverPasswordChange.this);
                                    finish();
                                    return true;
                                }
                            });
                        }else{
                            Toasts.show(ActRecoverPasswordChange.this, msg, false);
                        }
                        return true;
                    }
                });
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
