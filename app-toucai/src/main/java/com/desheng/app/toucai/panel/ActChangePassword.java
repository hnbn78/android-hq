package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.util.ResUtil;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.util.HashMap;
import java.util.regex.Pattern;

public class ActChangePassword extends AbAdvanceActivity {

    private String newPwd, oldPwd;
    private Button btnSure;

    private EditText etOldPwd, etNewPwd, etPwd;
    private ImageButton imgCurEye, imgNewEye, imgAgainEye;

    public static void launcher(Activity activity) {
        Intent intent = new Intent(activity, ActChangePassword.class);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_change_password;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), ResUtil.getString(R.string.login_password));
        setStatusBarTranslucentAndLightContentWithPadding();
        ((TextView) findViewById(R.id.tvAccount)).setText(UserManager.getIns().getCN());
        etOldPwd = findViewById(R.id.etOldPwd);
        etNewPwd = findViewById(R.id.etNewPwd);
        etPwd = findViewById(R.id.etPwd);
        btnSure = findViewById(R.id.btnSure);

        etNewPwd.addTextChangedListener(new MyTextWatcher());

        etOldPwd.addTextChangedListener(new MyTextWatcher());

        etPwd.addTextChangedListener(new MyTextWatcher());

        btnSure.setOnClickListener(v -> onSubmit());
        imgCurEye = findViewById(R.id.imgCurEye);
        imgCurEye.setOnClickListener(this);
        imgNewEye = findViewById(R.id.imgNewEye);
        imgNewEye.setOnClickListener(this);
        imgAgainEye = findViewById(R.id.imgAgainEye);
        imgAgainEye.setOnClickListener(this);
    }

    private void onSubmit() {
        newPwd = etNewPwd.getText().toString().trim();
        oldPwd = etOldPwd.getText().toString().trim();
        String againPwd = etPwd.getText().toString().trim();
        if (TextUtils.isEmpty(oldPwd)) {
            Toasts.show("请输入当前密码");
            return;
        }

        if (TextUtils.isEmpty(newPwd)) {
            Toast.makeText(ActChangePassword.this, "请输入新密码", Toast.LENGTH_LONG).show();
            return;
        }

        Pattern pattern = Pattern.compile("[0-9A-Z_a-z]{6,24}");
        if (!pattern.matcher(newPwd).matches()) {
            Toasts.show(ActChangePassword.this, "密码为6到24位字母和数字组合", false);
            return;
        }
        Pattern pattern2 = Pattern.compile("[0-9]+"); //必须
        if (!pattern2.matcher(newPwd).find()) {
            Toasts.show(ActChangePassword.this, "密码为大小写字母开头的6到24位字母和数字组合, 必须同时包含字符,数字", false);
            return;
        }
        if (TextUtils.isEmpty(againPwd)) {
            Toast.makeText(ActChangePassword.this, "请再次输入密码", Toast.LENGTH_LONG).show();
            return;

        }

        if (!newPwd.equals(againPwd)) {
            Toast.makeText(ActChangePassword.this, "两次输入的密码要保持一致！！！", Toast.LENGTH_LONG).show();
            return;
        }
        modifyLoginPassword();
    }

    private boolean isOpenEye, isOpenEye1, isOpenEye2;

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.imgCurEye:
                isOpenEye = !isOpenEye;
                resetInputVisible(imgCurEye, etOldPwd, isOpenEye);
                break;
            case R.id.imgNewEye:
                isOpenEye1 = !isOpenEye1;
                resetInputVisible(imgNewEye, etNewPwd, isOpenEye1);
                break;
            case R.id.imgAgainEye:
                isOpenEye2 = !isOpenEye2;
                resetInputVisible(imgAgainEye, etPwd, isOpenEye2);
                break;
        }
    }

    private void resetInputVisible(ImageButton imageButton, EditText editText, boolean isOpen) {
        imageButton.setSelected(isOpen);
        if (isOpen)
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        else
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (Strs.isNotEmpty(etNewPwd.getText().toString())
                    && Strs.isNotEmpty(etOldPwd.getText().toString())
                    && Strs.isNotEmpty(etPwd.getText().toString())) {
                btnSure.setEnabled(true);
            } else {
                btnSure.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    /**
     * 修改登录密码
     */
    private void modifyLoginPassword() {
        HttpAction.modifyLoginPassword(oldPwd, newPwd, new AbHttpResult() {

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                super.setupEntity(entity);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    Toasts.show("修改成功!", true);
                    UserManager.getIns().logout(new UserManager.IUserLogoutCallback() {

                        @Override
                        public void onBefore() {

                        }

                        @Override
                        public void onUserLogouted() {
                            UserManager.getIns().reLogin(ActChangePassword.this, UserManager.getIns().getMainUserName());
                            finish();
                        }

                        @Override
                        public void onUserLogoutFailed(String msg) {
                            Toasts.show(ActChangePassword.this, msg, false);
                        }

                        @Override
                        public void onAfter() {

                        }

                    });
                    finish();
                } else {
                    Toasts.show(msg);
                }

                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                return false;
            }

        });
    }

}
