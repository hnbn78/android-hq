package com.desheng.app.toucai.panel;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.desheng.app.toucai.controller.CtrlPlay_DigitsAllInOne;
import com.desheng.app.toucai.controller.CtrlPlay_Dummy;
import com.desheng.app.toucai.view.PlayBoardJDView;
import com.desheng.app.toucai.view.PlayDigitView;
import com.desheng.app.toucai.view.PlayDigitsJDView;
import com.desheng.app.toucai.view.PlayLongHuHeView;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 彩票玩法fragment
 * Created by lee on 2018/3/9.
 */
public class FragLotteryPlayFragmentDigitsAllInOne extends BaseLotteryPlayFragment {

    private int[] arrDefGroup = new int[]{R.id.vgDigit0, R.id.vgDigit1, R.id.vgDigit2, R.id.vgDigit3, R.id.vgDigit4};
    private int[] arrDevider = new int[]{R.id.devider0, R.id.devider1, R.id.devider2, R.id.devider3};
    private ArrayList<PlayDigitView> listBallGroups = new ArrayList<>();


    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_digits;
    }

    @Override
    protected void createCtrlPlay() {
        //配置玩法controller
        if (((LotteryPlayPanel) getActivity()).getLotteryPlayUserInfo() == null)
            setCtrlPlay(new CtrlPlay_Dummy(getActivity(), this, getLotteryInfo(), super.getUiConfig(), super.getLotteryPlay()));
        else
            setCtrlPlay(new CtrlPlay_DigitsAllInOne(getActivity(), this, getLotteryInfo(), getUiConfig(), getLotteryPlay()));
    }

    @Override
    public void createPlayModel(View root) {
        if (((LotteryPlayPanel) getActivity()).getLotteryPlayUserInfo() == null)
            return;

        //配置号码组
        listBallGroups = new ArrayList<>();
        for (int i = 0; i < getUiConfig().getLayout().size(); i++) {
            PlayDigitView digitGroup = (PlayDigitView) root.findViewById(arrDefGroup[i]);
            digitGroup.setVisibility(View.VISIBLE);
            if (i > 0 && i < getUiConfig().getLayout().size()) {
                root.findViewById(arrDevider[i - 1]).setVisibility(View.VISIBLE);
            }
            digitGroup.init();
            digitGroup.setConfig(getUiConfig().getLayout().get(i));
            listBallGroups.add(digitGroup);
        }
    }

    @Override
    public void setCurrentUserPlayInfo(Object userPlayInfo) {
        super.setCurrentUserPlayInfo(userPlayInfo);

        if (listBallGroups == null) {
            getCtrlPlay().stop();
            createPlayModel(getContentView());
            setCtrlPlay(new CtrlPlay_DigitsAllInOne(getActivity(), this, getLotteryInfo(), getUiConfig(), getLotteryPlay()));
            getCtrlPlay().start(null);
        }
    }

    @Override
    public ArrayList<PlayDigitView> getListBallGroups() {
        return listBallGroups;
    }

    @Override
    public ArrayList<PlayLongHuHeView> getListLHHGroups() {
        return null;
    }

    @Override
    public ArrayList<PlayDigitsJDView> getListBallGroupsJD() {
        return null;
    }

    @Override
    public ArrayList<PlayBoardJDView> getListBoardGroupsJD() {
        return null;
    }

    @Override
    public HashMap<String, View> getInputGroup() {
        return null;
    }

    @Override
    public ArrayList<ViewGroup> getPowerGroup() {
        return null;
    }

    @Override
    public ArrayList<CheckedTextView> getPowerBtnList() {
        return null;
    }

    @Override
    public int getMode() {
        return 0;
    }


}
