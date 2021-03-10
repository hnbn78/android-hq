package com.desheng.app.toucai.panel;

import android.view.View;
import android.view.ViewGroup;

import com.ab.util.Strs;
import com.desheng.app.toucai.controller.CtrlPlay_Board_LHC;
import com.desheng.app.toucai.controller.LHCBanBoQuickPlayController;
import com.desheng.app.toucai.controller.LHCQuickPlayController;
import com.desheng.app.toucai.controller.LHCWeiShuQuickPlayController;
import com.desheng.app.toucai.view.PlayBoardLHCView;
import com.desheng.app.toucai.view.PlayDigitsLHCView;
import com.pearl.view.ToggleImageButton;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 彩票玩法fragment
 * Created by lee on 2018/3/9.
 */
public class FragLotteryPlayFragmentBoardLHC extends BaseLotteryPlayFragmentLHC {
    PlayBoardLHCView boardGroup;

    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_board_lhc;
    }
    @Override
    protected void createCtrlPlay() {
        //配置玩法controller
        LHCQuickPlayController controller = null;

        if (Strs.isEqual(category, "半波")) {
            controller = new LHCBanBoQuickPlayController(this);
        } else  if (Strs.isEqual(category, "尾数")) {
            controller = new LHCWeiShuQuickPlayController(this);
        }

        setCtrlPlay(new CtrlPlay_Board_LHC(getActivity(), this, getLotteryInfo(), getUiConfig(), controller));
    }

    @Override
    public void createPlayModel(View root) {
        boardGroup = (PlayBoardLHCView) root.findViewById(R.id.vgDigit0);
        boardGroup.setVisibility(View.VISIBLE);
        boardGroup.init();
    }

    @Override
    public PlayBoardLHCView getBoardGroup() {
        return boardGroup;
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

}
