package com.desheng.base.panel;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.Config;
import com.ab.global.ENV;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.ClipboardUtils;
import com.ab.util.Nums;
import com.ab.util.Toasts;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.OpenAccount;
import com.pearl.act.base.AbBaseFragment;

import java.util.HashMap;

/**
 * Created by o on 2018/4/2.
 */

public class FragOpenAccountLink extends AbBaseFragment implements View.OnClickListener {
    private Button btBuildLink;
    private EditText etReturn;
    private ImageView ivAgency, ivPlay;
    private RadioGroup rgLink;
    private RadioButton rbOne, rbSeven, rbThirty, rbUmilt;
    private String lotteryPoint;
    private RadioButton rbLink;
    private RadioGroup rgType;
    private CheckBox cbAgent;
    private CheckBox cbPlayer;
    private TextView play_tv;
    private int type = 1;
    private int day = 1;
    private TextView tvReturnLab;
    private OpenAccount openAccount;

    public static Fragment newIns(OpenAccount openAccount) {
        Fragment fragment = new FragOpenAccountLink();
        Bundle bundle = new Bundle();
        bundle.putSerializable("addAccount", openAccount);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_open_account_link;
    }

    @Override
    public void init(View root) {
        btBuildLink = (Button) root.findViewById(R.id.btBuildlink);
        btBuildLink.setOnClickListener(this);
        etReturn = (EditText) root.findViewById(R.id.etInputAmount);

        cbAgent = (CheckBox) root.findViewById(R.id.cbAgent);
        cbAgent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    type = 1;
                    cbPlayer.setChecked(false);
                    updateLotteryPoint(type);
                } else {
                    if (!cbPlayer.isChecked()) {
                        cbPlayer.setChecked(true);
                    }
                }
            }
        });
        cbPlayer = (CheckBox) root.findViewById(R.id.cbPlayer);
        play_tv = (TextView) root.findViewById(R.id.play_tv);
        cbPlayer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    type = 0;
                    cbAgent.setChecked(false);
                    updateLotteryPoint(type);
                } else {
                    if (!cbAgent.isChecked()) {
                        cbAgent.setChecked(true);
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

        rgType = (RadioGroup) root.findViewById(R.id.rgType);
        rbOne = (RadioButton) root.findViewById(R.id.rbOne);
        rbSeven = (RadioButton) root.findViewById(R.id.rbSeven);
        rbThirty = (RadioButton) root.findViewById(R.id.rbThirty);
        rbUmilt = (RadioButton) root.findViewById(R.id.rbUmilt);
        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rbOne.setTextColor(Color.BLACK);
                rbSeven.setTextColor(Color.BLACK);
                rbThirty.setTextColor(Color.BLACK);
                rbUmilt.setTextColor(Color.BLACK);

                if (checkedId == R.id.rbOne) {
                    rbOne.setTextColor(Color.WHITE);
                    day = 1;
                } else if (checkedId == R.id.rbSeven) {
                    rbSeven.setTextColor(Color.WHITE);
                    day = 7;
                } else if (checkedId == R.id.rbThirty) {
                    rbThirty.setTextColor(Color.WHITE);
                    day = 30;
                } else if (checkedId == R.id.rbUmilt) {
                    rbUmilt.setTextColor(Color.WHITE);
                    day = -1;
                }
            }
        });
        tvReturnLab = (TextView) root.findViewById(R.id.tvReturnLab);
        openAccount = (OpenAccount) getArguments().getSerializable("addAccount");


        if (Config.custom_flag.equals(BaseConfig.FLAG_HETIANXIA)
                || Config.custom_flag.equals(BaseConfig.FLAG_ZHONGXIN) || Config.custom_flag.equals(BaseConfig.FLAG_JINDU)) {
            rbUmilt.setChecked(true);
        }

        updateLotteryPoint(type);

    }

    public void updateLotteryPoint(int agent1player0) {
        if (Config.custom_flag.equals(BaseConfig.FLAG_HETIANXIA)) {
            _updateLotteryPointOther(agent1player0, 5,0);
        } else if (Config.custom_flag.equals(BaseConfig.FLAG_JINDU)) {
            if(UserManager.getIns().getMainUserLevel()==5){
                _updateLotteryPointOther(agent1player0,10,0.1f);
            }else{
                _updateLotteryPointOther(agent1player0,10,0f);
            }
        } else {
            _updateLotteryPoinit(agent1player0);
        }
    }

    private void _updateLotteryPoinit(int agent1player0) {

        if (agent1player0 == 1) {
            tvReturnLab.setText(String.format("开户区间：%s-%s",
                    Nums.formatDecimal(openAccount.lotteryAgentRange.minPoint, 1),
                    Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint, 1)));
        } else {
            tvReturnLab.setText(String.format("开户区间：%s-%s",
                    Nums.formatDecimal(openAccount.lotteryPlayerRange.minPoint, 1),
                    Nums.formatDecimal(openAccount.lotteryPlayerRange.maxPoint, 1)));

        }

        if (((ActOpenAccount) getActivity()).isHidePlayer()) {
            cbPlayer.setVisibility(View.GONE);
            play_tv.setVisibility(View.GONE);
            cbAgent.setChecked(true);
            cbAgent.setEnabled(false);

            if (agent1player0 == 1) {
                tvReturnLab.setText(String.format("开户区间：%s",
                        Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint, 1)));
            } else {
                tvReturnLab.setText(String.format("开户区间：%s",
                        Nums.formatDecimal(openAccount.lotteryPlayerRange.maxPoint, 1)));
            }
            boolean isOne = Config.custom_flag.equals(BaseConfig.FLAG_ZHONGXIN) || Config.custom_flag.equals(BaseConfig.FLAG_CAISHIJI);
            String returnStr = Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint, isOne ? 1 : 3);
            etReturn.setText(returnStr);
            etReturn.setEnabled(false);
        } else {
            cbPlayer.setVisibility(View.VISIBLE);
            play_tv.setVisibility(View.VISIBLE);
            cbAgent.setEnabled(true);
            etReturn.setEnabled(true);
        }
    }

    private void _updateLotteryPointOther(int agent1player0, int plusNum,float sub_num) {

        if (agent1player0 == 1) {
            tvReturnLab.setText(String.format("开户区间：%s-%s",
                    Nums.formatDecimal(openAccount.lotteryAgentRange.minPoint + plusNum, 1),
                    Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint + plusNum-sub_num, 1)));
        } else {
            tvReturnLab.setText(String.format("开户区间：%s-%s",
                    Nums.formatDecimal(openAccount.lotteryPlayerRange.minPoint + plusNum, 1),
                    Nums.formatDecimal(openAccount.lotteryPlayerRange.maxPoint + plusNum-sub_num, 1)));

        }

        if (((ActOpenAccount) getActivity()).isHidePlayer()) {
            cbPlayer.setVisibility(View.INVISIBLE);
            play_tv.setVisibility(View.INVISIBLE);
            cbAgent.setChecked(true);
            cbAgent.setEnabled(false);

            if (agent1player0 == 1) {
                tvReturnLab.setText(String.format("开户区间：%s",
                        Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint + plusNum, 1)));
            } else {
                tvReturnLab.setText(String.format("开户区间：%s",
                        Nums.formatDecimal(openAccount.lotteryPlayerRange.maxPoint + plusNum, 1)));
            }
            boolean isOne = Config.custom_flag.equals(BaseConfig.FLAG_CAISHIJI);
            String returnStr = Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint + plusNum, isOne ? 1 : 3);
            etReturn.setText(returnStr);
            etReturn.setEnabled(false);
        } else {
            cbPlayer.setVisibility(View.VISIBLE);
            play_tv.setVisibility(View.VISIBLE);
            cbAgent.setEnabled(true);
            etReturn.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btBuildlink) {
            lotteryPoint = etReturn.getText().toString();

            String[] numParts = lotteryPoint.split("\\.");
            if (numParts.length == 2 && numParts[1].length() > 1) {
                Toasts.show(getActivity(), "返点只能输入一位小数", false);
                return;
            }

            double point = Nums.parse(lotteryPoint, 0.0);

            if (Config.custom_flag.equals(BaseConfig.FLAG_HETIANXIA)) {
                point -= 5;
                lotteryPoint = Nums.formatDecimal(point, 1);
            } else if (Config.custom_flag.equals(BaseConfig.FLAG_JINDU)) {
                point -= 10;
//                if(UserManager.getIns().getMainUserLevel()==5){
//                    point=point-0.1;
//                }
                lotteryPoint = Nums.formatDecimal(point, 1);

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
                Toast.makeText(getContext(), "请输入正确返点", Toast.LENGTH_LONG).show();
                return;
            }

            if (Config.custom_flag.equals(BaseConfig.FLAG_JINDU)&&UserManager.getIns().getMainUserLevel()==5) {
                if (point < 0 || point < min  || point > max-0.1) {
                    Toast.makeText(getContext(), "请输入正确返点", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            getLink();

        }


    }


    /**
     * 链接开户
     */
    public void getLink() {
        HttpAction.addRegistLink(type, day, lotteryPoint, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", String.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    // 将文本内容放到系统剪贴板里。
                    String data = getFieldObject(extra, "data", String.class);
                    if (data.endsWith(".html")) {
                        data = data.replace(".html", "");
                    }
                    ClipboardUtils.copyText(getActivity(), ENV.curr.host + data);
                    Toasts.show(getContext(), "开户成功链接生成成功，并已复制链接!", true);
                } else {
                    Toasts.show(getContext(), msg);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                return true;
            }
        });


    }


}
