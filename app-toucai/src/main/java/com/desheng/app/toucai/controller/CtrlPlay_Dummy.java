package com.desheng.app.toucai.controller;

import android.content.Context;

import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.panel.BaseLotteryPlayFragment;
import com.desheng.app.toucai.view.PlayDigitView;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryPlay;
import com.desheng.base.model.LotteryPlayUIConfig;
import com.desheng.base.model.LotteryPlayUserInfo;

import java.util.ArrayList;

/**
 * 空实现
 */
public class CtrlPlay_Dummy extends BaseCtrlPlay implements PlayDigitView.OnBallsChangeListener {


    public CtrlPlay_Dummy(Context ctx, BaseLotteryPlayFragment frag, LotteryInfo info, LotteryPlayUIConfig uiConfig, LotteryPlay play) {
        super(ctx, frag, info, uiConfig, play);

    }

    //设置号码组
    @Override
    public void setContentGroup() {

    }

    public void onBallsChanged(String power, String[] arrAllValue, ArrayList<String> selectedValue, boolean[] arrBallSelected, int ballCount) {

    }


    @Override
    protected void clearAllSelected() {

    }

    @Override
    public void reset() {
        clearAllSelected();
    }

    @Override
    public void autoGenerate() {

    }

    @Override
    public boolean isAllGroupReady() {
        return false;
    }

    @Override
    public void refreshCurrentUserPlayInfo(Object userPlayInfo) {
        //一般玩法无刷新
        //getLotteryPanel().getFootView().setLotteryInfo(UserManagerTouCai.getIns().getLotteryPlay(getLotteryPlay().category, "allInOne_longhu"), (LotteryPlayUserInfo) userPlayInfo);
    }

    public String getNumFromCell() {
        return "";
    }

    private boolean hasRequestText() {
        return !"".equals(getNumFromCell());
    }

    @Override
    public String getRequestText() {
        return "";
    }

    @Override
    public boolean canSubmitOrder() {
        return true;
    }


}
