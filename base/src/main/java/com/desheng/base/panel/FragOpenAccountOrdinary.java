package com.desheng.base.panel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.Config;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Nums;
import com.ab.util.Toasts;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.OpenAccount;
import com.pearl.act.base.AbBaseFragment;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by o on 2018/4/2.
 */

public class FragOpenAccountOrdinary extends AbBaseFragment implements View.OnClickListener {
    private Button btOpen;
    private EditText etUsername, etPassword, etReturn;
    private CheckBox ckAgence, ckPlay;
    private TextView play_tv;
    private int type = 1;
    private TextView tvReturnLab;
    private OpenAccount openAccount;

    public static Fragment newIns(OpenAccount openAccount) {
        Fragment fragment = new FragOpenAccountOrdinary();
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
        play_tv = (TextView) root.findViewById(R.id.play_tv);
        ckAgence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ckPlay.setChecked(false);
                    type = 1;
                    updateLotteryPoint(type);

                } else {
                    if (!ckPlay.isChecked()) {
                        ckPlay.setChecked(true);
                    }
                }
            }
        });


        etReturn.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable edt) {

            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                ((ActOpenAccount) getActivity()).inputLimit(s, etReturn);
            }
        });


        ckPlay = (CheckBox) root.findViewById(R.id.play_icon);
        ckPlay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ckAgence.setChecked(false);
                    type = 0;
                    updateLotteryPoint(type);

                } else {
                    if (!ckAgence.isChecked()) {
                        ckAgence.setChecked(true);
                    }
                }
            }
        });

        tvReturnLab = (TextView) root.findViewById(R.id.tvReturnLab);
        openAccount = (OpenAccount) getArguments().getSerializable("addAccount");

        updateLotteryPoint(type);
    }


    public void updateLotteryPoint(int agent1player0) {
        if (Config.custom_flag.equals(BaseConfig.FLAG_HETIANXIA)) {
            _updateLotteryPointOther(agent1player0, 5, 0);
        } else if (Config.custom_flag.equals(BaseConfig.FLAG_JINDU)) {
            if (UserManager.getIns().getMainUserLevel() == 5) {
                _updateLotteryPointOther(agent1player0, 10, 0.1f);
            } else {
                _updateLotteryPointOther(agent1player0, 10, 0f);
            }

        } else {
            _updateLotteryPoint(agent1player0);
        }
    }

    private void _updateLotteryPoint(int agent1player0) {
        if (agent1player0 == 1) {
            tvReturnLab.setText(String.format("开户区间：%s-%s",
                    Nums.formatDecimal(openAccount.lotteryAgentRange.minPoint, 1),
                    Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint, 1)));
        } else {
            tvReturnLab.setText(String.format("开户区间：%s-%s",
                    Nums.formatDecimal(openAccount.lotteryPlayerRange.minPoint, 1),
                    Nums.formatDecimal(openAccount.lotteryPlayerRange.maxPoint, 1)));

        }

        if (((ActOpenAccount) getActivity()).isHideLink()) {
            ckPlay.setVisibility(View.INVISIBLE);
            play_tv.setVisibility(View.INVISIBLE);
            ckAgence.setChecked(true);
            ckAgence.setEnabled(false);
            tvReturnLab.setText(String.format("开户区间：%s",
                    Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint, 1)));

            if (Config.custom_flag.equals(BaseConfig.FLAG_ZHONGXIN) || Config.custom_flag.equals(BaseConfig.FLAG_CAISHIJI)) {
                etReturn.setText("" + Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint, 1));
            } else {
                etReturn.setText("" + Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint, 3));
            }
            etReturn.setEnabled(false);
        } else {
            ckPlay.setVisibility(View.VISIBLE);
            play_tv.setVisibility(View.VISIBLE);
            ckAgence.setEnabled(true);
            etReturn.setEnabled(true);
        }

        if (((ActOpenAccount) getActivity()).isHidePlayer()) {
            ckPlay.setVisibility(View.GONE);
            play_tv.setVisibility(View.GONE);
            ckAgence.setChecked(true);
            ckAgence.setEnabled(false);
            if (agent1player0 == 1) {
                tvReturnLab.setText(String.format("开户区间：%s",
                        Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint, 1)));
            } else {
                tvReturnLab.setText(String.format("开户区间：%s",
                        Nums.formatDecimal(openAccount.lotteryPlayerRange.maxPoint, 1)));
            }

            if (Config.custom_flag.equals(BaseConfig.FLAG_ZHONGXIN) || Config.custom_flag.equals(BaseConfig.FLAG_CAISHIJI)) {
                etReturn.setText("" + Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint, 1));
            } else {
                etReturn.setText("" + Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint, 3));
            }
            etReturn.setEnabled(false);
        } else {
            ckPlay.setVisibility(View.VISIBLE);
            play_tv.setVisibility(View.VISIBLE);
            ckAgence.setEnabled(true);
            etReturn.setEnabled(true);
        }
    }

    private void _updateLotteryPointOther(int agent1player0, int plusNum, float sub_num) {
        if (agent1player0 == 1) {
            tvReturnLab.setText(String.format("开户区间：%s-%s",
                    Nums.formatDecimal(openAccount.lotteryAgentRange.minPoint + plusNum, 1),
                    Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint + plusNum - sub_num, 1)));
        } else {
            tvReturnLab.setText(String.format("开户区间：%s-%s",
                    Nums.formatDecimal(openAccount.lotteryPlayerRange.minPoint + plusNum, 1),
                    Nums.formatDecimal(openAccount.lotteryPlayerRange.maxPoint + plusNum - sub_num, 1)));

        }

        if (((ActOpenAccount) getActivity()).isHideLink()) {
            ckPlay.setVisibility(View.INVISIBLE);
            play_tv.setVisibility(View.INVISIBLE);
            ckAgence.setChecked(true);
            ckAgence.setEnabled(false);
            tvReturnLab.setText(String.format("开户区间：%s",
                    Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint + plusNum, 1)));
            etReturn.setText("" + Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint + plusNum, 1));
            etReturn.setEnabled(false);
        } else {
            ckPlay.setVisibility(View.VISIBLE);
            play_tv.setVisibility(View.VISIBLE);
            ckAgence.setEnabled(true);
            etReturn.setEnabled(true);
        }

        if (((ActOpenAccount) getActivity()).isHidePlayer()) {
            ckPlay.setVisibility(View.INVISIBLE);
            play_tv.setVisibility(View.INVISIBLE);
            ckAgence.setChecked(true);
            ckAgence.setEnabled(false);
            if (agent1player0 == 1) {
                tvReturnLab.setText(String.format("开户区间：%s",
                        Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint + plusNum, 1)));
            } else {
                tvReturnLab.setText(String.format("开户区间：%s",
                        Nums.formatDecimal(openAccount.lotteryPlayerRange.maxPoint + plusNum, 1)));
            }
            String returnStr = Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint + plusNum, 1);
            etReturn.setText(returnStr);
            etReturn.setEnabled(false);
        } else {
            ckPlay.setVisibility(View.VISIBLE);
            play_tv.setVisibility(View.VISIBLE);
            ckAgence.setEnabled(true);
            etReturn.setEnabled(true);
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
//            if(userName.length()<8||userName.length()>12){
//                Toasts.show(getActivity(), "用户名长度8-12位");
//                return;
//            }

            Pattern pattern = Pattern.compile("^[0-9]*$");
            Matcher matcher = pattern.matcher(userName);
            if (matcher.matches()) {
                Toasts.show(getActivity(), "用户名不能为纯数字", false);
                return;
            }

            if (Config.custom_flag.equals(BaseConfig.FLAG_ZHONGXIN)
                    || Config.custom_flag.equals(BaseConfig.FLAG_HETIANXIA)
                    || Config.custom_flag.equals(BaseConfig.FLAG_JINDU)) {
                pattern = Pattern.compile("^[0-9a-zA-Z]*$");
                matcher = pattern.matcher(userName);
                if (!matcher.matches()) {
                    Toasts.show(getActivity(), "用户名不能包含特殊字符", false);
                    return;
                }
            }

            String password = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                Toasts.show(getActivity(), "请输入密码", false);
                return;

            }

            if (password.length() < 4) {
                Toasts.show(getActivity(), "密码长度不够", false);
                return;
            }

            if (Config.custom_flag.equals(BaseConfig.FLAG_JINDU)) {
                pattern = Pattern.compile("^[a-zA-Z]{2}[a-zA-Z0-9]*");
                matcher = pattern.matcher(password);
                if (!matcher.matches()) {

                    Toasts.show(getActivity(), "密码前2位必须为字母", false);
                    return;
                }
            } else {
                pattern = Pattern.compile("^[a-zA-Z]{4}[a-zA-Z0-9]*");
                matcher = pattern.matcher(password);
                if (!matcher.matches()) {

                    Toasts.show(getActivity(), "密码前4位必须为字母", false);
                    return;
                }
            }



            String lotteryReturn = etReturn.getText().toString();

            String[] numParts = lotteryReturn.split("\\.");
            if (numParts.length == 2 && numParts[1].length() > 1) {
                Toasts.show(getActivity(), "返点只能输入一位小数", false);
                return;
            }

            double point = Nums.parse(lotteryReturn, 0.0);

            if (Config.custom_flag.equals(BaseConfig.FLAG_HETIANXIA)) {
                point -= 5;
                lotteryReturn = Nums.formatDecimal(point, 1);
            } else if (Config.custom_flag.equals(BaseConfig.FLAG_JINDU)) {
                point -= 10;
                lotteryReturn = Nums.formatDecimal(point, 1);
            }

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

            if (Config.custom_flag.equals(BaseConfig.FLAG_JINDU)&&UserManager.getIns().getMainUserLevel()==5) {
                if (point < 0 || point < min  || point > max-0.1) {
                    Toast.makeText(getContext(), "请输入正确返点", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            //总代返点固定
            if (((ActOpenAccount) getActivity()).isHideLink() && point != openAccount.lotteryAgentRange.maxPoint) {
                Toasts.show(getActivity(), "请输入正确返点", false);
                return;
            }

            openAccount(userName, password, lotteryReturn);
        }
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
                    etUsername.setText("");
                    etPassword.setText("");

                    if (etReturn.isEnabled())
                        etReturn.setText("");

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
