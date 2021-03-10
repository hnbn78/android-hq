package com.desheng.app.toucai.panel;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.desheng.app.toucai.controller.CtrlPlay_DigitsCombind;
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
public class FragLotteryPlayFragmentDigitsCombind extends BaseLotteryPlayFragment {
    public static final int [] arrVgPower = new int[]{
            R.id.vgMPower,
            R.id.vgKPower,
            R.id.vgBPower,
            R.id.vgSPower,
            R.id.vgGPower,};

    public static final int [] arrTextIds = new int[]{
            R.id.tvMDegitBtn,
            R.id.tvKDegitBtn,
            R.id.tvBDegitBtn,
            R.id.tvSDegitBtn,
            R.id.tvGDegitBtn,};
    
    
    private int [] arrDefGroup = new int[]{R.id.vgDigit0, R.id.vgDigit1};
    private ArrayList<PlayDigitView> listBallGroups = new ArrayList<>();
    private ArrayList<ViewGroup> listVgPower = new ArrayList<>();
    private ArrayList<CheckedTextView> listTextGroups = new ArrayList<>();
    
    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_digits_combind;
    }
    
    @Override
    protected void createCtrlPlay() {
        setCtrlPlay(new CtrlPlay_DigitsCombind(getActivity(), this, getLotteryInfo() , getUiConfig(), getLotteryPlay()));
    }
    
    
    @Override
    public void createPlayModel(View root) {
        //选位
        for (int i = 0; i < arrVgPower.length; i++) {
            listVgPower.add((ViewGroup) root.findViewById(arrVgPower[i]));
            listTextGroups.add(root.findViewById(arrTextIds[i]));
        }
        //设置位文字
        for (int i = 0; i < getUiConfig().getValue().size(); i++) {
            listTextGroups.get(i).setVisibility(View.VISIBLE);
            listTextGroups.get(i).setText(getUiConfig().getValue().get(i));
        }
        
        //配置号码组
        listBallGroups = new ArrayList<>();
        for (int i = 0; i < getUiConfig().getLayout().size(); i++) {
            PlayDigitView digitGroup = (PlayDigitView) root.findViewById(arrDefGroup[i]);
            digitGroup.setVisibility(View.VISIBLE);
            digitGroup.init();
            digitGroup.setConfig(getUiConfig().getLayout().get(i));
            listBallGroups.add(digitGroup);
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
        return listVgPower;
    }
    
    @Override
    public ArrayList<CheckedTextView> getPowerBtnList() {
        return listTextGroups;
    }
    
    @Override
    public int getMode() {
        return 0;
    }
    
}
