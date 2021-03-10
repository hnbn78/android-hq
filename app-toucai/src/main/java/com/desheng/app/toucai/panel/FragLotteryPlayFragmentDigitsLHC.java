package com.desheng.app.toucai.panel;

import android.view.View;
import android.view.ViewGroup;

import com.ab.util.Strs;
import com.desheng.app.toucai.controller.CtrlPlay_Digits_Buzhong_LHC;
import com.desheng.app.toucai.controller.CtrlPlay_Digits_LHC;
import com.desheng.app.toucai.controller.LHCQuickPlayController;
import com.desheng.app.toucai.controller.LHCZhengMaQuickPlayController;
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
public class FragLotteryPlayFragmentDigitsLHC extends BaseLotteryPlayFragmentLHC {
    public static final int MODE_Ball_1 = 1;
    public static final int MODE_Dual_2 = 2;

    private int[] arrDefGroup = new int[]{R.id.vgDigit0, R.id.vgDigit1, R.id.vgDigit2, R.id.vgDigit3, R.id.vgDigit4,
            R.id.vgDigit5, R.id.vgDigit6, R.id.vgDigit7, R.id.vgDigit8, R.id.vgDigit9, R.id.vgDigit10};
    private int[] arrDevider = new int[]{R.id.devider0, R.id.devider1, R.id.devider2, R.id.devider3, R.id.devider4,
            R.id.devider5, R.id.devider6, R.id.devider7, R.id.devider8, R.id.devider9};
    private ArrayList<PlayDigitsLHCView> listBallGroups = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_digits_lhc;
    }

    @Override
    protected void createCtrlPlay() {
        //配置玩法controller
        LHCQuickPlayController controller = null;

        if (Strs.isEqual(category, "特码")
                || Strs.isEqual(category, "正码")) {
            controller = new LHCZhengMaQuickPlayController(this);
        }

        if (Strs.isEqual(category, "不中")) {
            setCtrlPlay(new CtrlPlay_Digits_Buzhong_LHC(getActivity(), this, getLotteryInfo(), getUiConfig(), controller));
        } else {
            setCtrlPlay(new CtrlPlay_Digits_LHC(getActivity(), this, getLotteryInfo(), getUiConfig(), controller));
        }
    }


    @Override
    public void createPlayModel(View root) {
        //配置号码组
        listBallGroups = new ArrayList<>();
        if (getUiConfig()==null) {
            return;
        }

        for (int i = 0; i < getUiConfig().getCellUI().size(); i++) {
            PlayDigitsLHCView digitGroup = (PlayDigitsLHCView) root.findViewById(arrDefGroup[i]);
            if (digitGroup == null) {
                return;
            }
            digitGroup.setVisibility(View.VISIBLE);
            if (i > 0 && i < getUiConfig().getCellUI().size()) {
                root.findViewById(arrDevider[i - 1]).setVisibility(View.VISIBLE);
            }
            digitGroup.init();
            listBallGroups.add(digitGroup);
        }
    }

    @Override
    public void onShow() {
        super.onShow();
        getHead().setShakeVisible(true);
        //getHead().registShake(getContext(), getCtrlPlay());
    }

    @Override
    public ArrayList<PlayDigitsLHCView> getListBallGroupsLHC() {
        return listBallGroups;
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
    public PlayBoardLHCView getBoardGroup() {
        return null;
    }

    @Override
    public void syncSelection() {
        for (PlayDigitsLHCView view : listBallGroups) {
            view.syncSelection();
        }
    }
}
