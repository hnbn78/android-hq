package com.desheng.base.panel;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ab.global.Config;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Toasts;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.OpenAccount;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;

import java.util.HashMap;

import okhttp3.Request;


/**
 * 开户中心
 * Created by user on 2018/2/27.
 */

public class ActOpenAccount extends AbAdvanceActivity implements View.OnClickListener {

    public static void launch(Activity act) {
        simpleLaunch(act, ActOpenAccount.class);
    }

    private RadioGroup rgType;
    private RadioButton rbOrdinary, rbLink;
    private Button btnLinkManage;
    private ViewGroup vgContainer1;
    private ViewGroup vgContainer2;
    private OpenAccount openAccountInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.act_open_account;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "开户中心");
        setStatusBarTranslucentAndLightContentWithPadding();

        initView();

        HttpAction.prepareAddAccount(this, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActOpenAccount.this, "");
                    }
                });
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", OpenAccount.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && extra.get("data") != null) {
                    openAccountInfo = getFieldObject(extra, "data", null);
                    if (BaseConfig.FLAG_JINYANG.equals(Config.custom_flag)) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.vgContainer1, FragOpenAccountOrdinaryJinYang.newIns(openAccountInfo))
                                .commit();

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.vgContainer2, FragOpenAccountLinkJinYang.newIns(openAccountInfo))
                                .commit();
                    } else {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.vgContainer1, FragOpenAccountOrdinary.newIns(openAccountInfo))
                                .commit();

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.vgContainer2, FragOpenAccountLink.newIns(openAccountInfo))
                                .commit();
                    }
                } else {
                    Toasts.show(ActOpenAccount.this, msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideProgressDialog(ActOpenAccount.this);
                    }
                }, 400);
            }
        });
    }

    public void inputLimit(CharSequence s,EditText etReturn) {
        if (s.toString().contains(".")) {
            if (s.length() - 1 - s.toString().indexOf(".") > 1) {
                s = s.toString().subSequence(0,
                        s.toString().indexOf(".") + 2);
                etReturn.setText(s);
                etReturn.setSelection(s.length());
            }
        }
        if (s.toString().trim().substring(0).equals(".")) {
            s = "0" + s;
            etReturn.setText(s);
            etReturn.setSelection(2);
        }

        if (s.toString().startsWith("0")
                && s.toString().trim().length() > 1) {
            if (!s.toString().substring(1, 2).equals(".")) {
                etReturn.setText(s.subSequence(0, 1));
                etReturn.setSelection(1);
                return;
            }
        }
    }

    private void initView() {
        vgContainer1 = (ViewGroup) findViewById(R.id.vgContainer1);
        vgContainer2 = (ViewGroup) findViewById(R.id.vgContainer2);

        vgContainer2.setVisibility(View.GONE);

        rgType = (RadioGroup) findViewById(R.id.rgType);
        rbOrdinary = (RadioButton) findViewById(R.id.rbOrdinary);
        rbLink = (RadioButton) findViewById(R.id.rbLink);
        btnLinkManage = (Button) findViewById(R.id.btnLinkManage);

        if (isHideLink()) {
            rbLink.setVisibility(View.GONE);
            btnLinkManage.setVisibility(View.GONE);
        } else {
            rbLink.setVisibility(View.VISIBLE);
            btnLinkManage.setVisibility(View.VISIBLE);
        }

        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rbOrdinary.setTextColor(Color.BLACK);
                rbLink.setTextColor(Color.BLACK);
                vgContainer1.setVisibility(View.GONE);
                vgContainer2.setVisibility(View.GONE);
                if (checkedId == R.id.rbOrdinary) {
                    rbOrdinary.setTextColor(Color.WHITE);
                    vgContainer1.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.rbLink) {
                    rbLink.setTextColor(Color.WHITE);
                    vgContainer2.setVisibility(View.VISIBLE);
                }
            }
        });

        btnLinkManage.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == btnLinkManage) {
            ActLinkManage.launch(this);
        }
    }

    public boolean isHideLink() {
        long mainUserLevel = UserManager.getIns().getMainUserLevel();
        if (BaseConfig.Org != null) {
            return !BaseConfig.Org.canOpenLink(mainUserLevel);
        }
        return false;
    }

    public boolean isHidePlayer() {
        long mainUserLevel = UserManager.getIns().getMainUserLevel();
        if (BaseConfig.Org != null) {
            return !BaseConfig.Org.canOpenUser(mainUserLevel);
        }
        return false;
    }

}
