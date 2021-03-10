package com.desheng.app.toucai.panel;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.AESUtil;
import com.ab.util.MD5;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.fragment.BasePageFragment;
import com.desheng.app.toucai.model.AddAccountBean;
import com.desheng.base.action.HttpAction;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.model.OpenAccount;
import com.google.gson.Gson;
import com.noober.background.view.BLRadioButton;
import com.noober.background.view.BLRadioGroup;
import com.noober.background.view.BLTextView;
import com.shark.tc.R;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FragCommonOpenAccount extends BasePageFragment implements View.OnClickListener {


    private EditText etUsername, etPassword, etReturn;
    private BLTextView createAccount;
    private BLRadioGroup accountType;
    private OpenAccount openAccount;
    private int type = 0;
    private BLRadioButton typePlayer;
    private BLRadioButton typeAgent;

    public static FragCommonOpenAccount newInstance(OpenAccount openAccountInfo) {
        FragCommonOpenAccount fragment = new FragCommonOpenAccount();
        Bundle bundle = new Bundle();
        bundle.putSerializable("addAccount", openAccountInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int setContentView() {
        return R.layout.layout_frag_common_openaccount;
    }

    @Override
    public void fetchData() {
    }

    @Override
    protected void initView(View rootview) {
        etUsername = ((EditText) rootview.findViewById(R.id.username));
        etPassword = ((EditText) rootview.findViewById(R.id.password));
        etReturn = ((EditText) rootview.findViewById(R.id.fandian));
        createAccount = ((BLTextView) rootview.findViewById(R.id.createAccount));
        accountType = ((BLRadioGroup) rootview.findViewById(R.id.accountType));
        typePlayer = ((BLRadioButton) rootview.findViewById(R.id.typePlayer));
        typeAgent = ((BLRadioButton) rootview.findViewById(R.id.typeAgent));
        openAccount = (OpenAccount) getArguments().getSerializable("addAccount");
        createAccount.setOnClickListener(this);
        accountType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.typeAgent:
                        String minPointAgentStr = Nums.formatDecimal(openAccount.lotteryAgentRange.minPoint, 1);
                        String maxPointAgentStr = Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint, 1);
                        if (Strs.isEqual(minPointAgentStr, maxPointAgentStr)) {
                            etReturn.setText(minPointAgentStr);
                            etReturn.setEnabled(false);
                        } else {
                            etReturn.setHint("可分配范围 " + minPointAgentStr + " ~ " + maxPointAgentStr);
                        }
                        type = 1;
                        break;
                    case R.id.typePlayer://默认为玩家
                        String minPointPlayerStr = Nums.formatDecimal(openAccount.lotteryPlayerRange.minPoint, 1);
                        String maxPointPlayerStr = Nums.formatDecimal(openAccount.lotteryPlayerRange.maxPoint, 1);
                        if (Strs.isEqual(minPointPlayerStr, maxPointPlayerStr)) {
                            etReturn.setText(minPointPlayerStr);
                            etReturn.setEnabled(false);
                        } else {
                            etReturn.setHint("可分配范围 " + minPointPlayerStr + " ~ " + maxPointPlayerStr);
                        }
                        type = 0;
                        break;
                    default:
                }
            }
        });

        if (openAccount != null) {
            typePlayer.setVisibility(openAccount.allowAddUser ? View.VISIBLE : View.GONE);
            type = openAccount.allowAddUser ? 0 : 1;
            typeAgent.setVisibility(openAccount.allowAddAgent ? View.VISIBLE : View.GONE);
            typePlayer.setChecked(openAccount.allowAddUser ? true : false);
            typeAgent.setChecked((!openAccount.allowAddUser && openAccount.allowAddAgent) ? true : false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createAccount:
                String userName = etUsername.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    Toasts.show(getActivity(), "请输入用户名", false);
                    return;
                }

                Pattern pattern = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,10}$");
                Matcher matcher = pattern.matcher(userName);
                if (!matcher.matches()) {
                    Toasts.show(getActivity(), "用户名由6-10位字母和数字组成", false);
                    return;
                }

                String password = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    Toasts.show(getActivity(), "请输入密码", false);
                    return;
                }

                if (password.length() < 6) {
                    Toasts.show(getActivity(), "密码长度不够", false);
                    return;
                }

                pattern = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,12}$");
                matcher = pattern.matcher(password);
                if (!matcher.matches()) {
                    Toasts.show(getActivity(), "密码由6-12位字母和数字组成", false);
                    return;
                }

                String lotteryReturn = etReturn.getText().toString();

                String[] numParts = lotteryReturn.split("\\.");
                if (numParts.length == 2 && numParts[1].length() > 1) {
                    Toasts.show(getActivity(), "返点只能输入一位小数", false);
                    return;
                }

                double point = Nums.parse(lotteryReturn, 0.0);

                double min = 0.0;
                double max = 0.0;

                if (type == 1) {
                    min = openAccount.lotteryAgentRange.minPoint;
                    max = openAccount.lotteryAgentRange.maxPoint;
                } else {
                    min = openAccount.lotteryPlayerRange.minPoint;
                    max = openAccount.lotteryPlayerRange.maxPoint;
                }
                if (point < 0 || point - min < -0.00001 || point - max > 0.00001) {
                    Toasts.show(getActivity(), "请输入正确返点", false);
                    return;
                }

                //总代返点固定
                if (((ActAgentOpenAccountCenter) getActivity()).isHideLink() && point != openAccount.lotteryAgentRange.maxPoint) {
                    Toasts.show(getActivity(), "请输入正确返点", false);
                    return;
                }

                openAccount(userName, password, lotteryReturn);
                break;
        }
    }


    /**
     * 普通开户
     */
    private void openAccount(String userName, String password, String lotteryReturn) {
        if (BaseConfig.USE_AES_OPEN_ACCOUNT) {
            String pwdMd5 = MD5.md5(password).toLowerCase();
            AddAccountBean accountBean = new AddAccountBean(userName, pwdMd5, lotteryReturn, type);
            try {
                String j = new Gson().toJson(accountBean);
                //Log.e("FragOpenAccountOrdinary", "加密前："+j);
                String json = AESUtil.aesPKCS5PaddingEncrypt(j, BaseConfig.AES_OPEN_ACCOUNT_KEY);
                HttpAction.addAccountAes(json, new AbHttpResult() {
                    @Override
                    public void setupEntity(AbHttpRespEntity entity) {
                        entity.putField("data", String.class);
                    }

                    @Override
                    public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                        if (code == 0 && error == 0) {
                            Toasts.show(getContext(), "开户成功！！！", true);
                            etUsername.setText("");
                            etPassword.setText("");
                            if (etReturn.isEnabled()) {
                                etReturn.setText("");
                            }
                        } else {
                            Toasts.show(getContext(), msg, false);
                        }
                        return true;
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            HttpAction.addAccount(userName, password, lotteryReturn, type, new AbHttpResult() {
                @Override
                public void setupEntity(AbHttpRespEntity entity) {
                    entity.putField("data", String.class);
                }

                @Override
                public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                    if (code == 0 && error == 0) {
                        Toasts.show(getContext(), "开户成功！！！", true);
                        etUsername.setText("");
                        etPassword.setText("");
                        if (etReturn.isEnabled()) {
                            etReturn.setText("");
                        }
                    } else {
                        Toasts.show(getContext(), msg, false);
                    }
                    return true;
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }
            });
        }
    }
}
