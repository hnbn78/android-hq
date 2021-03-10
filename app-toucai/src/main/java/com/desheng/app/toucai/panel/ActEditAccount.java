package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import com.ab.callback.AbCallback;
import com.ab.http.AbHttpResult;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.panel.ActWeb;
import com.desheng.base.util.DeviceUtil;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.util.HashMap;

import okhttp3.Request;

import static com.desheng.app.toucai.panel.ActBindBankCardToucai.BIND_OK_RESULT_CODE;

public class ActEditAccount extends AbAdvanceActivity {

    private Button btnSure;
    private EditText et_real_name;

    public static final int RESULT_CODE = 111;
    private boolean isWithdraw;

    public static void launcher(Activity activity) {
        Intent intent = new Intent(activity, ActEditAccount.class);
        activity.startActivityForResult(intent, 0);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_edit_real_name;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "账号设置");
        setStatusBarTranslucentAndLightContentWithPadding();
        setToolbarRightButtonImg(R.mipmap.ic_customer_service, 48, 48, v -> ActWeb.launchCustomService(ActEditAccount.this));
        isWithdraw = getIntent().getBooleanExtra("isWithdraw", false);
        btnSure = findViewById(R.id.btnSure);
        btnSure = findViewById(R.id.btnSure);
        et_real_name = findViewById(R.id.et_real_name);
        btnSure.setOnClickListener(v -> {
            String realName = et_real_name.getText().toString();
            if (Strs.isEmpty(realName)) {
                Toasts.show("姓名格式不正确！");
                return;
            }
            if (realName.length() < 2 || realName.length() > 20) {
                Toasts.show("真实姓名为2到20位中文！");
                return;
            }
            if (!Strs.isAllChineseWithDot(realName)) {
                Toasts.show("姓名必须是中文格式！");
                return;
            }
            submitRealName(realName);
        });
    }

    private void submitRealName(String realname) {
        HttpAction.bindTrueName(null, realname, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
            }


            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                Toasts.show(msg, true);

                if (code == 0 && error == 0) {
                    UserManager.getIns().setIsBindWithdrawName(true);
                    UserManager.getIns().setWithDrawName(realname);
                    DeviceUtil.hideInputKeyboard(ActEditAccount.this);
                    if (!UserManager.getIns().getIsBindWithdrawPassword()) {
                        DialogsTouCai.showBindDialog(ActEditAccount.this, "您暂时未绑定资金密码",
                                "请前往绑定资金密码", "返回", "前往绑定",
                                false, new AbCallback<Object>() {

                                    @Override
                                    public boolean callback(Object obj) {
                                        ActEditAccount.this.finish();
                                        return false;
                                    }
                                }, new AbCallback<Object>() {
                                    @Override
                                    public boolean callback(Object obj) {
                                        ActSettingFundsPwd.launcher(ActEditAccount.this, isWithdraw);
                                        return false;
                                    }
                                });
                    } else if (!UserManager.getIns().getIsBindCard()) {
                        if (isWithdraw) {
                            DialogsTouCai.showBindDialog(ActEditAccount.this, "您暂时未绑定银行卡",
                                    "请您先绑定银行卡再实行操作", "返回", "前往绑定",
                                    false, new AbCallback<Object>() {

                                        @Override
                                        public boolean callback(Object obj) {
                                            ActEditAccount.this.finish();
                                            return false;
                                        }
                                    }, new AbCallback<Object>() {
                                        @Override
                                        public boolean callback(Object obj) {
                                            ActBindBankCardToucai.launch(ActEditAccount.this, !UserManager.getIns().getIsBindWithdrawName());
                                            return false;
                                        }
                                    });

                        } else
                            ActBindBankCardToucai.launch(ActEditAccount.this, !UserManager.getIns().getIsBindWithdrawName());

                    } else {
                        ActEditAccount.this.finish();
                    }
                }


                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
            }
        });
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
