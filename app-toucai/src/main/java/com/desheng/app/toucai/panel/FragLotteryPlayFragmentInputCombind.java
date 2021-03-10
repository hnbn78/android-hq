package com.desheng.app.toucai.panel;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;

import com.desheng.app.toucai.controller.CtrlPlay_InputCombind;
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
public class FragLotteryPlayFragmentInputCombind extends BaseLotteryPlayFragment {
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
 
    public static FragLotteryPlayFragmentInputCombind newIns(String playCode) {
        FragLotteryPlayFragmentInputCombind fragment = new FragLotteryPlayFragmentInputCombind();
        Bundle bundle = new Bundle();
        bundle.putString("playCode", playCode);
        fragment.setArguments(bundle);
        return fragment;
    }
    
    private EditText etInput;
    private HashMap<String, View> mapInputGroup;
    private ArrayList<ViewGroup> listVgPower = new ArrayList<>();
    private ArrayList<CheckedTextView> listTextGroups = new ArrayList<>();
    
    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_input_combind;
    }
    
    @Override
    protected void createCtrlPlay() {
        setCtrlPlay(new CtrlPlay_InputCombind(getActivity(), this, getLotteryInfo() , getUiConfig(), getLotteryPlay()));
    }
    
    @Override
    public void createPlayModel(View root) {
        //选位
        for (int i = 0; i < arrVgPower.length; i++) {
            listVgPower.add((ViewGroup) root.findViewById(arrVgPower[i]));
            listTextGroups.add(root.findViewById(arrTextIds[i]));
        }
    
        for (int i = 0; i < getUiConfig().getValue().size(); i++) {
            listTextGroups.get(i).setVisibility(View.VISIBLE);
            listTextGroups.get(i).setText(getUiConfig().getValue().get(i));
        }
        
        //输入
        etInput = (EditText) root.findViewById(R.id.etInput);
        mapInputGroup = new HashMap<>();
        mapInputGroup.put("etInput", etInput);
        //mapInputGroup.put("", );
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etInput.clearFocus();
            }
        });
    }
    
  
    @Override
    public ArrayList<PlayDigitView> getListBallGroups() {
        return null;
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
        return mapInputGroup;
    }
 
    @Override
    public ArrayList<ViewGroup> getPowerGroup(){
        return listVgPower;
    }
    
    @Override
    public ArrayList<CheckedTextView> getPowerBtnList(){
        return listTextGroups;
    }
    
    @Override
    public int getMode() {
        return 0;
    }
}
