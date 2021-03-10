package com.desheng.base.panel;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.ab.global.ENV;
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

public class FragOpenAccountLinkJinYang extends AbBaseFragment implements View.OnClickListener {
    private Button btBuildLink;
    private EditText etReturn;
    private ImageView ivAgency, ivPlay;
    private RadioGroup rgLink;
    private RadioButton rbOne, rbSeven, rbThirty, rbUmilt;
    private String lotteryPoint;
    private CheckBox checkBoxagence, checkBoxplay;
    private RadioButton rbLink;
    private RadioGroup rgType;
    private CheckBox cbAgent;
    private CheckBox cbPlayer;
    private int type = 1;
    private int day = 1;
    private TextView tvReturnLab,play_tv;
    private OpenAccount openAccount;

    public static Fragment newIns(OpenAccount openAccount) {
        Fragment fragment = new FragOpenAccountLinkJinYang();
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
        play_tv = (TextView) root.findViewById(R.id.play_tv);
        cbAgent = (CheckBox) root.findViewById(R.id.cbAgent);
        cbAgent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    type = 1;
                    cbPlayer.setChecked(false);
                    updateLotteryPoinit();
                } else {
                    if (!cbPlayer.isChecked()) {
                        cbPlayer.setChecked(true);
                    }
                }
            }
        });
        cbPlayer = (CheckBox) root.findViewById(R.id.cbPlayer);
        cbPlayer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    type = 0;
                    cbAgent.setChecked(false);
                    updateLotteryPoinit();
                } else {
                    if (!cbAgent.isChecked()) {
                        cbAgent.setChecked(true);
                    }
                }
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
        updateLotteryPoinit();

        if (UserManager.getIns().getMainUserLevel() > 4) {
            cbPlayer.setVisibility(View.VISIBLE);
            play_tv.setVisibility(View.VISIBLE);
            cbAgent.setEnabled(true);
        } else {
            cbPlayer.setVisibility(View.GONE);
            play_tv.setVisibility(View.GONE);
            cbAgent.setChecked(true);
            cbAgent.setEnabled(false);
        }
    }

    public void updateLotteryPoinit() {

        /**
         * 第一级股东   		1994			14.7       	14.7 的大主管
         * 第二级大主管   		1994			14.7		14.7 的内部主管
         * 第三级内部主管   	1994			14.7		14.6 的主管
         * 第四级主管 	  	1992			14.6		14.5 的招商
         * 第五级招商   		1990			14.5		0-14.5 的招商跟会员
         */
        if(UserManager.getIns().getMainUserLevel()>=5){
            tvReturnLab.setText(String.format("开户区间：%s-%s",
                    Nums.formatDecimal(openAccount.lotteryAgentRange.minPoint, 3),
                    Nums.formatDecimal(openAccount.lotteryAgentRange.maxPoint, 3)));
        }else if(UserManager.getIns().getMainUserLevel()==4){
            tvReturnLab.setText("开户区间:14.5");
            etReturn.setText("14.5");
            etReturn.setEnabled(false);
        }else if(UserManager.getIns().getMainUserLevel()==3){
            tvReturnLab.setText("开户区间:14.6");
            etReturn.setText("14.6");
            etReturn.setEnabled(false);
        }else if(UserManager.getIns().getMainUserLevel()==2||UserManager.getIns().getMainUserLevel()==1){
            tvReturnLab.setText("开户区间:"+openAccount.lotteryAgentRange.maxPoint);
            etReturn.setText(""+openAccount.lotteryAgentRange.maxPoint);
            etReturn.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btBuildlink) {
            lotteryPoint = etReturn.getText().toString();
            double point = Nums.parse(lotteryPoint, 0.0);
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
                Toast.makeText(getContext(), "请输入正确返点", Toast.LENGTH_LONG).show();
                return;
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
                    // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
                    ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cm.setText(ENV.curr.host + getFieldObject(extra, "data", String.class));
                    Toasts.show(getContext(), "开户成功链接生成成功，并已复制链接!", true);
                } else {
                    Toasts.show(getContext(), msg);
                }
                return true;
            }

            ;

            @Override
            public boolean onError(int status, String content) {
                return true;
            }
        });


    }


}
