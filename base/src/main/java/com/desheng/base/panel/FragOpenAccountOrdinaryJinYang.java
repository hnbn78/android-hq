package com.desheng.base.panel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Nums;
import com.ab.util.Toasts;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.OpenAccount;
import com.pearl.act.base.AbBaseFragment;

import java.util.HashMap;

/**
 * Created by o on 2018/4/2.
 */

public class FragOpenAccountOrdinaryJinYang extends AbBaseFragment implements View.OnClickListener {
    private Button btOpen;
    private EditText etUsername, etPassword, etReturn;
    private CheckBox ckAgence, ckPlay;
    private int type = 1;
    private TextView tvReturnLab, play_tv;
    private OpenAccount openAccount;

    public static Fragment newIns(OpenAccount openAccount) {
        Fragment fragment = new FragOpenAccountOrdinaryJinYang();
        Bundle bundle = new Bundle();
        bundle.putSerializable("addAccount", openAccount);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_open_account_ordinary;
    }

    @Override
    public void init(View root) {
        etUsername = (EditText) root.findViewById(R.id.etUsername);
        etPassword = (EditText) root.findViewById(R.id.etPassword);
        etReturn = (EditText) root.findViewById(R.id.etReturn);
        btOpen = (Button) root.findViewById(R.id.account_bt);
        btOpen.setOnClickListener(this);
        ckAgence = (CheckBox) root.findViewById(R.id.agency_icon);
        ckAgence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ckPlay.setChecked(false);
                    type = 1;
                    updateLotteryPoinit();
                } else {
                    if (!ckPlay.isChecked()) {
                        ckPlay.setChecked(true);
                    }
                }
            }
        });

        ckPlay = (CheckBox) root.findViewById(R.id.play_icon);
        play_tv = (TextView) root.findViewById(R.id.play_tv);
        ckPlay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ckAgence.setChecked(false);
                    type = 0;
                    updateLotteryPoinit();
                } else {
                    if (!ckAgence.isChecked()) {
                        ckAgence.setChecked(true);
                    }
                }
            }
        });

        tvReturnLab = (TextView) root.findViewById(R.id.tvReturnLab);
        openAccount = (OpenAccount) getArguments().getSerializable("addAccount");
        updateLotteryPoinit();

        if (UserManager.getIns().getMainUserLevel() > 4) {
            ckPlay.setVisibility(View.VISIBLE);
            play_tv.setVisibility(View.VISIBLE);
            ckAgence.setEnabled(true);
        } else {
            ckPlay.setVisibility(View.GONE);
            play_tv.setVisibility(View.GONE);
            ckAgence.setChecked(true);
            ckAgence.setEnabled(false);
        }

    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.account_bt) {
            String userName = etUsername.getText().toString().trim();
            if (TextUtils.isEmpty(userName)) {
                Toasts.show(getActivity(), "请输入用户名", false);
                return;
            }

            String password = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                Toasts.show(getActivity(), "请输入正确的密码", false);
                return;
            }

            if (password.trim().length() < 6) {
                Toasts.show(getActivity(), "密码长度至少6位", false);
                return;
            }

            if (!checkPwd(password)) {
                Toasts.show(getActivity(), "请输入正确的密码格式");
                return;
            }

            String lotteryReturn = etReturn.getText().toString();
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
            if (point < 0 || point < min || point > max) {
                Toasts.show(getActivity(), "请输入正确返点", false);
                return;
            }

            openAccount(userName, password, lotteryReturn);
        }
    }

    private boolean checkPwd(String password) {
        boolean pwd_ok = true;
        String start4 = password.substring(0, 4);
        char[] arr = start4.toCharArray();
        for (int j = 0; j < arr.length; j++) {
            boolean b = ('a' <= arr[j] && arr[j] <= 'z') || ('A' <= arr[j] && arr[j] <= 'Z');
            if (!b) {
                pwd_ok = false;
                break;
            }
        }

        return pwd_ok;
    }


    /**
     * 普通开户
     */
    private void openAccount(String userName, String password, String lotteryReturn) {

        HttpAction.addAccount(userName, password, lotteryReturn, type, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", String.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    Toasts.show(getContext(), "开户成功！！！", true);
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


    public void updateLotteryPoinit() {

        /**
         * 第一级股东   		1994			14.7       	14.7 的大主管
         * 第二级大主管   		1994			14.7		14.7 的内部主管
         * 第三级内部主管   	1994			14.7		14.6 的主管
         * 第四级主管 	  	1992			14.6		14.5 的招商
         * 第五级招商   		1990			14.5		0-14.5 的招商跟会员
         */
        if (UserManager.getIns().getMainUserLevel() >= 5) {
            tvReturnLab.setText(String.format("开户区间：%s-%s",
                    Nums.formatDecimal(openAccount.lotteryAgentRange.minPoint, 3),
                    Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint, 3)));
        } else if (UserManager.getIns().getMainUserLevel() == 4) {
            tvReturnLab.setText("开户区间:14.5");
            etReturn.setText("14.5");
            etReturn.setEnabled(false);
        } else if (UserManager.getIns().getMainUserLevel() == 3) {
            tvReturnLab.setText("开户区间:14.6");
            etReturn.setText("14.6");
            etReturn.setEnabled(false);
        } else if (UserManager.getIns().getMainUserLevel() == 2 || UserManager.getIns().getMainUserLevel() == 1) {
            tvReturnLab.setText("开户区间:" + openAccount.lotteryAgentRange.maxPoint);
            etReturn.setText("" + openAccount.lotteryAgentRange.maxPoint);
            etReturn.setEnabled(false);
        }

    }

}
