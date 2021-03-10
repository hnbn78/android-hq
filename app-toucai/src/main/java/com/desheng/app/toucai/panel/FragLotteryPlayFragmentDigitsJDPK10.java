package com.desheng.app.toucai.panel;

import android.view.View;
import android.view.ViewGroup;

import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.controller.CtrlPlay_Digits_JD;
import com.desheng.app.toucai.controller.CtrlPlay_Digits_JDpk10;
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
public class FragLotteryPlayFragmentDigitsJDPK10 extends BaseLotteryPlayFragmentJD {
    public static final int MODE_Ball_1 = 1; // rect
    public static final int MODE_Dual_2 = 2; // long bar(2 column)
    public static final int MODE_Small = 3;  // short bar(3 column)

    private int[] arrDefGroup = new int[]{R.id.vgDigit0, R.id.vgDigit1, R.id.vgDigit2, R.id.vgDigit3, R.id.vgDigit4,
            R.id.vgDigit5, R.id.vgDigit6, R.id.vgDigit7, R.id.vgDigit8, R.id.vgDigit9, R.id.vgDigit10};
    private int[] arrDevider = new int[]{R.id.devider0, R.id.devider1, R.id.devider2, R.id.devider3, R.id.devider4,
            R.id.devider5, R.id.devider6, R.id.devider7, R.id.devider8, R.id.devider9};
    private ArrayList<PlayDigitsJDView> listBallGroups = new ArrayList<>();

    protected int mode = 0;

    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_digits_jd_jdpk10;
    }

    @Override
    protected void createCtrlPlay() {
        //配置玩法controller
        setCtrlPlay(new CtrlPlay_Digits_JDpk10(getActivity(), this, getLotteryInfo(), getUiConfig()));
    }


    @Override
    public void createPlayModel(View root) {
        if (getHead() != null) {
            getHead().setTvInstructionStatus(View.GONE);
        }
        if (getUiConfig().getType() == CtxLotteryTouCai.ShowType_DualDigits_JD) {
            mode = MODE_Dual_2;
        } else if (getUiConfig().getType() == CtxLotteryTouCai.ShowType_Digits_JD) {
            mode = MODE_Ball_1;
        } else if (getUiConfig().getType() == CtxLotteryTouCai.ShowType_SMALL_Digits_JD) {
            mode = MODE_Small;
        }

        //配置号码组
        listBallGroups = new ArrayList<>();
        for (int i = 0; i < getUiConfig().getLayout().size(); i++) {
            PlayDigitsJDView digitGroup = (PlayDigitsJDView) root.findViewById(arrDefGroup[i]);
            digitGroup.setVisibility(View.VISIBLE);
            if (i > 0 && i < getUiConfig().getLayout().size()) {
                root.findViewById(arrDevider[i - 1]).setVisibility(View.VISIBLE);
            }
            digitGroup.init(getLotteryInfo().getId());
            listBallGroups.add(digitGroup);
        }
    }


    @Override
    public ArrayList<PlayDigitsJDView> getListBallGroupsJD() {
        return listBallGroups;
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
    public ArrayList<ToggleImageButton> getPowerBtnList() {
        return null;
    }

    @Override
    public int getMode() {
        return mode;
    }

    @Override
    public void syncSelection() {
        for (PlayDigitsJDView v : listBallGroups) {
            v.syncSelection();
        }
    }
}
