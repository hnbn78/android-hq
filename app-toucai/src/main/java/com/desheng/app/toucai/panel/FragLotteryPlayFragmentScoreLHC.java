package com.desheng.app.toucai.panel;

import android.view.View;
import android.view.ViewGroup;

import com.desheng.app.toucai.controller.CtrlPlay_Score_LHC;
import com.desheng.app.toucai.view.PlayBoardLHCView;
import com.desheng.app.toucai.view.PlayDigitsLHCView;
import com.desheng.app.toucai.view.PlayScoreLHCView;
import com.pearl.view.ToggleImageButton;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 彩票玩法fragment
 * Created by lee on 2018/3/9.
 */
public class FragLotteryPlayFragmentScoreLHC extends BaseLotteryPlayFragmentLHC {
    PlayScoreLHCView boardGroup;

    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_score_lhc;
    }
    @Override
    protected void createCtrlPlay() {
        //配置玩法controller
        setCtrlPlay(new CtrlPlay_Score_LHC(getActivity(), this, getLotteryInfo(), getUiConfig(), null));
    }

    @Override
    public void createPlayModel(View root) {
        boardGroup =  root.findViewById(R.id.vgDigit0);
        boardGroup.setVisibility(View.VISIBLE);
        boardGroup.init();
    }

    @Override
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

    public PlayScoreLHCView getPlayScoreLHCView() {return boardGroup;}
}
