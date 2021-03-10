package com.desheng.app.toucai.panel;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.desheng.app.toucai.controller.CtrlPlay_Board_JD;
import com.desheng.app.toucai.view.PlayBoardJDView;
import com.desheng.app.toucai.view.PlayDigitsJDView;
import com.pearl.view.ToggleImageButton;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 彩票玩法fragment
 * Created by lee on 2018/3/9.
 */
public class FragLotteryPlayFragmentBoardJD extends BaseLotteryPlayFragmentJD {

    private int[] arrDefGroup = new int[]{R.id.vgDigit0, R.id.vgDigit1, R.id.vgDigit2, R.id.vgDigit3, R.id.vgDigit4,
            R.id.vgDigit5, R.id.vgDigit6, R.id.vgDigit7, R.id.vgDigit8, R.id.vgDigit9};

    private ArrayList<PlayBoardJDView> listBoardGroupsJD = new ArrayList<>();

    private int mode = 0;

    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_board_jd;
    }

    @Override
    protected void createCtrlPlay() {
        //配置玩法controller
        setCtrlPlay(new CtrlPlay_Board_JD(getActivity(), this, getLotteryInfo(), getUiConfig()));
    }

    @Override
    public void createPlayModel(View root) {

        //配置号码组
        listBoardGroupsJD = new ArrayList<>();
        for (int i = 0; i < getUiConfig().getLayout().size(); i++) {
            PlayBoardJDView boardGroup = (PlayBoardJDView) root.findViewById(arrDefGroup[i]);
            boardGroup.setVisibility(View.VISIBLE);
            boardGroup.init();
            listBoardGroupsJD.add(boardGroup);
            if (i % 2 == 1) {
                ((View) boardGroup.getParent()).findViewById(R.id.v_divider).setVisibility(View.VISIBLE);
                ((LinearLayout) boardGroup.getParent()).setWeightSum(2);

                LinearLayout.LayoutParams pa = (LinearLayout.LayoutParams) ((View) boardGroup.getParent().getParent()).getLayoutParams();
                pa.weight = 2;
                ((View) boardGroup.getParent().getParent()).setLayoutParams(pa);
                boardGroup.showTiger();
            } else {
                boardGroup.showDragon();
            }
        }
    }


    @Override
    public ArrayList<PlayDigitsJDView> getListBallGroupsJD() {
        return null;
    }

    @Override
    public ArrayList<PlayBoardJDView> getListBoardGroupsJD() {
        return listBoardGroupsJD;
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

    @Override
    public int getMode() {
        return 0;
    }

    @Override
    public void syncSelection() {
        for (PlayBoardJDView v : listBoardGroupsJD) {
            v.syncSelection();
        }
    }

}
