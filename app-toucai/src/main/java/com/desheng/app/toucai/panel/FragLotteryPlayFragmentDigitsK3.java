package com.desheng.app.toucai.panel;

import android.view.View;

import com.ab.util.Strs;
import com.desheng.app.toucai.controller.CtrlPlay_Digits;
import com.desheng.app.toucai.view.PlayDigitK3View;
import com.desheng.app.toucai.view.PlayDigitView;
import com.shark.tc.R;

/**
 * 彩票玩法fragment
 * Created by lee on 2018/3/9.
 */
public class FragLotteryPlayFragmentDigitsK3 extends FragLotteryPlayFragmentDigits {


    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_digits_k3;
    }

    @Override
    protected void createCtrlPlay() {
        //配置玩法controller
        setCtrlPlay(new CtrlPlay_Digits(getActivity(), this, getLotteryInfo(), getUiConfig(), getLotteryPlay()));
    }

    @Override
    public void createPlayModel(View root) {
        super.createPlayModel(root);
        for (PlayDigitView view : getListBallGroups()) {
            try {
                PlayDigitK3View k3 = (PlayDigitK3View) view;
                k3.setMode(getMode());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getMode() {
        if (Strs.isEqual(getUiConfig().getLotteryName(), "sthtx")
                || Strs.isEqual(getUiConfig().getLotteryName(), "sthdx")
                || Strs.isEqual(getUiConfig().getLotteryName(), "sbthdx")
                || Strs.isEqual(getUiConfig().getLotteryName(), "ebthdx")
                || Strs.isEqual(getUiConfig().getLotteryName(), "ethdx")
                || Strs.isEqual(getUiConfig().getLotteryName(), "ethfx")
                || Strs.isEqual(getUiConfig().getLotteryName(), "bdw_bdw_fs")
                || Strs.isEqual(getUiConfig().getLotteryName(), "slhtx"))
            return 1; // 色子
        else
            return 0; // 数字
    }


}
