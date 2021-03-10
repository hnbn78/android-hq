package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.callback.AbCallback;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.MD5;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.view.MaterialDialog;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.UserBindStatus;
import com.desheng.base.util.ResUtil;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.util.HashMap;
import java.util.regex.Pattern;

import okhttp3.Request;

import static com.desheng.app.toucai.panel.ActBindBankCardToucai.BIND_OK_RESULT_CODE;

/**
 * 设置资金密码
 */
public class ActSettingFundsPwd extends AbAdvanceActivity {

    private EditText etPwd, etPwdAgain;
    private Button btnSure;
    private boolean isWithdraw;
    public static final int SETTING_FUND_PWD_RESULT_OK = 333;

    /**
     * @param isWithdraw 是否提现操作
     */
    public static void launcher(Activity act, boolean isWithdraw) {
        Intent intent = new Intent(act, ActSettingFundsPwd.class);
        intent.putExtra("isWithdraw", isWithdraw);
        act.startActivityForResult(intent, 0);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_funds_pwd;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), ResUtil.getString(R.string.funds_password));
        setStatusBarTranslucentAndLightContentWithPadding();
        isWithdraw = getIntent().getBooleanExtra("isWithdraw", false);
        etPwd = findViewById(R.id.etPwd);
        etPwdAgain = findViewById(R.id.etPwdAgain);
        btnSure = findViewById(R.id.btnSure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSettingPwd();
            }
        });
        etPwd.addTextChangedListener(new MyTextWatcher());
        etPwdAgain.addTextChangedListener(new MyTextWatcher());
    }

    private void onSettingPwd() {
        String pwd1 = etPwd.getText().toString().trim();
        String pwd2 = etPwdAgain.getText().toString().trim();
        if (TextUtils.isEmpty(pwd1)) {
            Toasts.show(ResUtil.getString(R.string.input_funds_password));
            return;
        }

        Pattern pattern = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,12}$");
        if (!pattern.matcher(pwd1).matches()) {
            Toasts.show(ActSettingFundsPwd.this, "资金密码是由6-12位字母和数字组成", false);
            return;
        }
        if (TextUtils.isEmpty(pwd2)) {
            Toasts.show(ResUtil.getString(R.string.please_input_password_agame));
            return;
        }
        if (!pwd1.equals(pwd2)) {
            Toasts.show("两次输入的密码要保持一致!!!");
            return;
        }

        addFundPwd(pwd1);
    }


    class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (Strs.isNotEmpty(etPwd.getText().toString())
                    && Strs.isNotEmpty(etPwdAgain.getText().toString())
                    && etPwd.getText().toString().equals(etPwdAgain.getText().toString())) {
                btnSure.setEnabled(true);
            } else {
                btnSure.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private void addFundPwd(String fundPwd) {
        fundPwd = MD5.md5(fundPwd).toLowerCase();
        HttpAction.bindFunPwd(null, fundPwd, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    UserManager.getIns().setIsBindWithdrawPassword(true);
                    UserManagerTouCai.getIns().checkUserBindStatus(new UserManager.IUserBindCheckCallback() {
                        @Override
                        public void onBefore() {

                        }

                        @Override
                        public void onUserBindChecked(UserBindStatus status) {
                            if (!status.isBindCard()) {
                                if (isWithdraw) {
                                    showBindCardTip("您暂时未绑定银行卡", "请您先绑定银行卡再实行操作", "前往绑定", status);
                                } else {
                                    ActBindBankCardToucai.launch(ActSettingFundsPwd.this, !status.isBindWithdrawName());
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onUserBindCheckFailed(String msg) {

                        }

                        @Override
                        public void onAfter() {

                        }
                    });
                } else {
                    Toasts.show(ActSettingFundsPwd.this, msg, false);

                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(ActSettingFundsPwd.this, content, true);
                return super.onError(status, content);
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
            }

        });

    }

    private void showBindNameTip(String tip1, String tip2, String right) {
        showBindDialog(ActSettingFundsPwd.this, tip1, tip2, "", right,
                false, new AbCallback<Object>() {
                    @Override
                    public boolean callback(Object obj) {
                        ActEditAccount.launcher(ActSettingFundsPwd.this);
                        DialogsTouCai.hideBindTipDialog();
                        return true;
                    }
                });
    }

    private void showBindCardTip(String tip1, String tip2, String right, UserBindStatus status) {
        showBindDialog(ActSettingFundsPwd.this, tip1, tip2, "", right,
                false, new AbCallback<Object>() {
                    @Override
                    public boolean callback(Object obj) {
                        ActBindBankCardToucai.launch(ActSettingFundsPwd.this, !status.isBindWithdrawName());
                        DialogsTouCai.hideBindTipDialog();
                        return true;
                    }
                });
    }

    private static MaterialDialog tipDialog;

    public static void showBindDialog(Activity act, String tips1, String tips2, String left, String right, boolean cancelTouchOutside, AbCallback<Object> callback) {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.dialog_tip_bind, null);
        ((TextView) viewGroup.findViewById(R.id.tvTips01)).setText(tips1);
        ((TextView) viewGroup.findViewById(R.id.tvTips02)).setText(tips2);


        TextView btnConfirm = viewGroup.findViewById(R.id.btnConfirm);
        TextView btnCancel = viewGroup.findViewById(R.id.btnCancel);

        if (Strs.isNotEmpty(left)) {
            btnCancel.setText(left);
        }

        if (Strs.isNotEmpty(right)) {
            btnConfirm.setText(right);
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != callback) {
                    callback.callback(null);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipDialog.dismiss();
                act.setResult(SETTING_FUND_PWD_RESULT_OK);
                act.finish();
            }
        });
        tipDialog = Dialogs.showCustomDialog(act, viewGroup, cancelTouchOutside);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActWithdrawals.WITHDRAW_OK_RESULT_OK) {
            setResult(resultCode);
            finish();
        } else if (resultCode == BIND_OK_RESULT_CODE) {
            setResult(resultCode);
            finish();
        }
    }

}
