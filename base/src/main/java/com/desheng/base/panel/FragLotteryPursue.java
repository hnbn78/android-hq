package com.desheng.base.panel;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.util.Maps;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.desheng.base.R;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.util.DeviceUtil;
import com.pearl.act.base.AbBaseFragment;

import java.util.HashMap;

/**
 * 彩票追号页面
 */
public class FragLotteryPursue extends AbBaseFragment implements View.OnClickListener{
    public static final String TYPE_FanBei = "TYPE_FanBei";
    public static final String TYPE_TongBei = "TYPE_TongBei";
    public static final String TYPE_TongLiRen = "TYPE_TongLiRen";

    private ViewGroup vgHead;
    private EditText etIssueCount;
    private EditText etInit;
    private TextView tvCountIntervalLab1;
    private EditText etCountInterval;
    private TextView tvCountIntervalLab2;
    private EditText etMax;
    private TextView tvMaxBei;
    private TextView tvMultiBtn;
    private TextView tvPlusBtn;
    private EditText etMulti;
    private TextView tvMultiLab3;
    private EditText etPercent;
    private TextView tvBuild;

    private String type;
    private String lotteryCode;
    private String sign = "multi";
    private TextView tvMaxLab;
    private TextView tvPercentLab1;
    private TextView tvPercentLab2;
    private FragLotteryPursue.IBuildListener buildListener;

    public static FragLotteryPursue newIns(String type, String lotteryCode) {
        FragLotteryPursue fragment = new FragLotteryPursue();
        Bundle bundle = new Bundle();
        bundle.putString("lotteryCode", lotteryCode);
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_purse;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        buildListener = (IBuildListener) context;
    }

    @Override
    public void init(View root) {
        type = getArguments().getString("type");
        lotteryCode = getArguments().getString("lotteryCode");

        //初始化头部
        vgHead = (ViewGroup) root.findViewById(R.id.vgHead);
        etIssueCount = (EditText) vgHead.findViewById(R.id.etIssueCount);
        etInit = (EditText) vgHead.findViewById(R.id.etInit);
        tvCountIntervalLab1 = (TextView) vgHead.findViewById(R.id.tvCountIntervalLab1);
        etCountInterval = (EditText) vgHead.findViewById(R.id.etCountInterval);
        tvCountIntervalLab2 = (TextView) vgHead.findViewById(R.id.tvCountIntervalLab2);
        tvMaxLab = (TextView) vgHead.findViewById(R.id.tvMaxLab);
        etMax = (EditText) vgHead.findViewById(R.id.etMax);
        tvMaxBei = (TextView) vgHead.findViewById(R.id.tvMaxBei);
        tvMultiBtn = (TextView) vgHead.findViewById(R.id.tvMultiBtn);
        tvPlusBtn = (TextView) vgHead.findViewById(R.id.tvPlusBtn);
        etMulti = (EditText) vgHead.findViewById(R.id.etMulti);
        tvMultiLab3 = (TextView) vgHead.findViewById(R.id.tvMultiLab3);
        tvPercentLab1 = (TextView) vgHead.findViewById(R.id.tvPercentLab1);
        tvPercentLab2 = (TextView) vgHead.findViewById(R.id.tvPercentLab2);
        etPercent = (EditText) vgHead.findViewById(R.id.etPercent);
        tvBuild = (TextView) vgHead.findViewById(R.id.tvBuild);

        if(type.equals(TYPE_FanBei)){
            tvCountIntervalLab1.setVisibility(View.VISIBLE);
            etCountInterval.setVisibility(View.VISIBLE);
            tvCountIntervalLab2.setVisibility(View.VISIBLE);
            tvMultiBtn.setVisibility(View.VISIBLE);
            tvMultiBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvPlusBtn.setTextColor(0xff333333);
                    tvMultiBtn.setTextColor(0xff333333);
                    if(sign.equals("plus")){
                        tvPlusBtn.setBackgroundResource(R.drawable.sh_bd_rec_gray_white);
                        tvMultiBtn.setBackgroundResource(R.drawable.sh_bd_rect_primary_normal);
                        tvMultiBtn.setTextColor(Color.WHITE);
                        sign = "multi";
                    }
                }
            });
            tvPlusBtn.setVisibility(View.VISIBLE);
            tvPlusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvPlusBtn.setTextColor(0xff333333);
                    tvMultiBtn.setTextColor(0xff333333);
                    if(sign.equals("multi")){
                        tvPlusBtn.setBackgroundResource(R.drawable.sh_bd_rect_primary_normal);
                        tvPlusBtn.setTextColor(Color.WHITE);
                        tvMultiBtn.setBackgroundResource(R.drawable.sh_bd_rec_gray_white);
                        sign = "plus";
                    }
                }
            });
            etMulti.setVisibility(View.VISIBLE);
            tvMultiLab3.setVisibility(View.VISIBLE);

        }else if(type.equals(TYPE_TongBei)){
            //默认
        }else if(type.equals(TYPE_TongLiRen)){
            tvMaxLab.setVisibility(View.VISIBLE);
            etMax.setVisibility(View.VISIBLE);
            tvMaxBei.setVisibility(View.VISIBLE);
            tvPercentLab1.setVisibility(View.VISIBLE);
            tvPercentLab2.setVisibility(View.VISIBLE);
            etPercent.setVisibility(View.VISIBLE);
        }

        tvBuild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int maxMulti = Views.getValue(etMax,0);
                if(maxMulti<=0){
                    Toasts.show(getActivity(), "最大倍数最少1倍!", false);
                    return;
                }
                double percent = Views.getValue(etPercent,0.0);

                int issueCount = Views.getValue(etIssueCount, 0);
                if(issueCount <= 0){
                    Toasts.show(getActivity(), "期数至少选择1期!", false);
                    return;
                }

                int initMulti = Views.getValue(etInit, 0);
                if(initMulti <= 0){
                    Toasts.show(getActivity(), "起始倍数至少选择1倍!", false);
                    return;
                }

                Maps.gen();
                if(TYPE_FanBei.equals(type)){
                    int inteval = Views.getValue(etCountInterval, 0);
                    if(inteval <= 0){
                        Toasts.show(getActivity(), "间隔期数至少选择1期!", false);
                        return;
                    }
                    Maps.put(CtxLottery.Chase.INTEVAL, inteval);

                    Maps.put(CtxLottery.Chase.SIGN, sign);

                    int multi = Views.getValue(etMulti, 0);
                    if(multi <= 0){
                        Toasts.show(getActivity(), "倍数至少选择1倍!", false);
                        return;
                    }
                    Maps.put(CtxLottery.Chase.MULTI, multi);
                }else if(TYPE_TongBei.equals(type)){
                    //nothing
                }else if(TYPE_TongLiRen.equals(type)){
                    //nothing
                }

                buildListener.onBuild(type, issueCount, initMulti, maxMulti,percent,Maps.get(), new IBuildListener.BuildCallback() {
                    @Override
                    public void onResult(int realCount) {
                        setRealIssueCount(realCount);
                    }
                });

                // 隐藏软键盘
                if (getActivity() != null)
                    DeviceUtil.hideInputKeyboard(getActivity());
            }
        });

    }
    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {
        super.onHide();
    }

    @Override
    public void onClick(View v) {

    }

    public void setRealIssueCount(int count) {
        int issueCount = Views.getValue(etIssueCount, 0);

        if (issueCount != count) {
            Toasts.show(getActivity(), String.format(getResources().getString(R.string.lottery_max_chase_count), count));
            etIssueCount.setText(String.valueOf(count));
        }
    }

    public interface IBuildListener{
        void onBuild(String type, int issueCount, int initMulti, int maxMulti,double percent,HashMap<String, Object> map, BuildCallback callback);
        public interface BuildCallback{
            void onResult(int realCount);
        }
    }


}




