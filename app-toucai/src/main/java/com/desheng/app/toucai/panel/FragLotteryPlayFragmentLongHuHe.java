package com.desheng.app.toucai.panel;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import com.desheng.app.toucai.controller.CtrlPlay_LongHuHe;
import com.desheng.app.toucai.view.PlayBoardJDView;
import com.desheng.app.toucai.view.PlayDigitView;
import com.desheng.app.toucai.view.PlayDigitsJDView;
import com.desheng.app.toucai.view.PlayLongHuHeView;
import com.desheng.base.model.LotteryPlayUIConfig;
import com.desheng.base.model.LotteryPlayUserInfo;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 彩票玩法fragment
 * Created by lee on 2018/3/9.
 */
public class FragLotteryPlayFragmentLongHuHe extends BaseLotteryPlayFragment {

    private int[] arrDefGroup = new int[]{R.id.vgDigit0, R.id.vgDigit1, R.id.vgDigit2, R.id.vgDigit3, R.id.vgDigit4,
            R.id.vgDigit5, R.id.vgDigit6, R.id.vgDigit7, R.id.vgDigit8, R.id.vgDigit9};
    private ArrayList<PlayLongHuHeView> listBallGroups = new ArrayList<>();
    private String[] lhhBalls = {"龙", "虎", "和"};
    
    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_longhuhe;
    }
    
    @Override
    protected void createCtrlPlay() {
         setCtrlPlay(new CtrlPlay_LongHuHe(getActivity(), this, getLotteryInfo() , getUiConfig(), getLotteryPlay()));
    }
    
    @Override
    public void createPlayModel(View root) {
        //配置号码组
        listBallGroups = new ArrayList<>();
        for (int i = 0; i < getUiConfig().getLayout().size(); i++) {
            PlayLongHuHeView digitGroup = (PlayLongHuHeView) root.findViewById(arrDefGroup[i]);
            digitGroup.setVisibility(View.VISIBLE);
            digitGroup.init();

            LotteryPlayUIConfig.LayoutBean layoutBean = getUiConfig().getLayout().get(i);
            boolean isNewLongHu = getUiConfig().getLotteryID() > 1111 && getUiConfig().getLotteryID() <= 1121;
            if (isNewLongHu) {
                List<String> balls = new ArrayList<>();
                for (int j = 0; j < 3; j++) {
                    LotteryPlayUserInfo.MathodBean methodBean = getUserPlayInfo().getMethod().get(layoutBean.getBallCode().get(j));
                    if (methodBean != null) {
                        balls.add(lhhBalls[j]);
                    }
                }
                layoutBean.setBalls(balls);
            }
            digitGroup.setConfig(layoutBean);

            listBallGroups.add(digitGroup);
            if (i % 2 == 1) {
                ((View) digitGroup.getParent()).findViewById(R.id.v_divider).setVisibility(View.VISIBLE);
                ((LinearLayout) digitGroup.getParent()).setWeightSum(2);

                LinearLayout.LayoutParams pa = (LinearLayout.LayoutParams) ((View) digitGroup.getParent().getParent()).getLayoutParams();
                pa.weight = 2;
                ((View) digitGroup.getParent().getParent()).setLayoutParams(pa);
                digitGroup.showTiger();
            } else {
                digitGroup.showDragon();
            }
        }
    }
    
    @Override
    public ArrayList<PlayDigitView> getListBallGroups() {
        return null;
    }

    @Override
    public ArrayList<PlayLongHuHeView> getListLHHGroups() {
        return listBallGroups;
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
