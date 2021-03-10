package com.desheng.app.toucai.panel;

import android.view.View;
import android.view.ViewGroup;

import com.ab.util.Strs;
import com.desheng.app.toucai.controller.CtrlPlay_Zodiac_LHC;
import com.desheng.app.toucai.controller.CtrlPlay_Zodiac_Six_LHC;
import com.desheng.app.toucai.controller.LHCZodiacQuickPlayController;
import com.desheng.app.toucai.view.PlayBoardLHCView;
import com.desheng.app.toucai.view.PlayDigitsLHCView;
import com.desheng.app.toucai.view.PlayZodiacLHCView;
import com.pearl.view.ToggleImageButton;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 彩票玩法fragment
 * Created by lee on 2018/3/9.
 */
public class FragLotteryPlayFragmentZodiacLHC extends BaseLotteryPlayFragmentLHC {
    PlayZodiacLHCView boardGroup;

    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_zodiac_lhc;
    }
    @Override
    protected void createCtrlPlay() {
        //配置玩法controller

        if (Strs.isEqual(getPlayCode(), "lhc_SX3")) {
            setCtrlPlay(new CtrlPlay_Zodiac_Six_LHC(getActivity(), this, getLotteryInfo(), getUiConfig(), new LHCZodiacQuickPlayController(this)));
        } else {
            setCtrlPlay(new CtrlPlay_Zodiac_LHC(getActivity(), this, getLotteryInfo(), getUiConfig(), new LHCZodiacQuickPlayController(this)));
        }
    }

    @Override
    public void createPlayModel(View root) {
        boardGroup = (PlayZodiacLHCView) root.findViewById(R.id.vgDigit0);
        boardGroup.setVisibility(View.VISIBLE);
        boardGroup.init();
    }


    public PlayBoardLHCView getBoardGroup() {
        return null;
    }

    @Override
    public void syncSelection() {
        boardGroup.syncSelection();
    }

    @Override
    public ArrayList<PlayDigitsLHCView> getListBallGroupsLHC() {
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
    public ArrayList<ToggleImageButton> getPowerBtnList() {
        return null;
    }

    public PlayZodiacLHCView getPlayZodiacLHCView() { return boardGroup;};
}
