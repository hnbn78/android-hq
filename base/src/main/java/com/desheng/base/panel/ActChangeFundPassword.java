package com.desheng.base.panel;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.thread.ThreadCollector;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * 修改资金密码
 * Created by user on 2018/2/27.
 */

public class ActChangeFundPassword extends AbAdvanceActivity {

    public static void launch(Activity act) {
        Intent itt = new Intent(act, ActChangeFundPassword.class);
        act.startActivity(itt);
    }

    private TextView tvAccount;
    private EditText newEdittext, oldEdittext, againEdittext;
    private String currentpwd;
    private String newpwd;
    private String againpwd;
    private TextView save_tv;
    private ImageView imageView;
    private Button btnSure;

    @Override
    protected int getLayoutId() {
        return R.layout.act_change_fund_password;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "修改资金密码");
        setStatusBarTranslucentAndLightContentWithPadding();

        initView();
    }

    private void initView() {
        oldEdittext = findViewById(R.id.current_password);
        newEdittext = findViewById(R.id.new_password);
        againEdittext = findViewById(R.id.again_et);
        tvAccount = findViewById(R.id.tvAccount);
        btnSure = findViewById(R.id.btnSure);
        btnSure.setOnClickListener(this);

        oldEdittext.addTextChangedListener(new MyTextWatcher());
        newEdittext.addTextChangedListener(new MyTextWatcher());
        againEdittext.addTextChangedListener(new MyTextWatcher());

        tvAccount.setText(UserManager.getIns().getCN());


    }

    class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (Strs.isNotEmpty(oldEdittext.getText().toString())
                    && Strs.isNotEmpty(newEdittext.getText().toString())
                    && Strs.isNotEmpty(againEdittext.getText().toString())) {
                btnSure.setEnabled(true);
            } else {
                btnSure.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (btnSure == v) {
            currentpwd = oldEdittext.getText().toString();
            if (Strs.isEmpty(currentpwd)) {
                Toast.makeText(ActChangeFundPassword.this, "请输入当前密码", Toast.LENGTH_LONG).show();
                return;
            }

            newpwd = newEdittext.getText().toString();
            if (Strs.isEmpty(newpwd)) {
                Toast.makeText(ActChangeFundPassword.this, "请输入新密码", Toast.LENGTH_LONG).show();
                return;
            }

            Pattern pattern = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,12}$");
            if (!pattern.matcher(newpwd).matches()) {
                Toasts.show(ActChangeFundPassword.this, "资金密码是由8-12位字母和数字组成", false);
                return;
            }

            againpwd = againEdittext.getText().toString();
            if (Strs.isEmpty(againpwd)) {
                Toast.makeText(ActChangeFundPassword.this, "请再次输入密码", Toast.LENGTH_LONG).show();
                return;

            }
            if (!newpwd.equals(againpwd)) {
                Toast.makeText(ActChangeFundPassword.this, "两此密码必须一致！！！", Toast.LENGTH_LONG).show();
                return;
            }
            modifyFundsPassword(currentpwd, newpwd);
        }
    }

    /**
     * 修改资金密码
     *
     * @return
     */
    private void modifyFundsPassword(String oldpassword, final String newpassword) {
        HttpAction.modifyFundsPassword(oldpassword, newpassword, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                super.setupEntity(entity);
            }


            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    Toasts.show(ActChangeFundPassword.this, "资金密码修改成功!", true);
                    ThreadCollector.getIns().postDelayOnUIThread(1000, new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                } else {
                    Toasts.show(ActChangeFundPassword.this, msg, false);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                return super.onError(status, content);
            }
        });

    }

}
