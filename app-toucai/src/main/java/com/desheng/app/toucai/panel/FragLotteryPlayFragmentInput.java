package com.desheng.app.toucai.panel;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.controller.CtrlPlay_Input;
import com.desheng.app.toucai.view.PlayBoardJDView;
import com.desheng.app.toucai.view.PlayDigitView;
import com.desheng.app.toucai.view.PlayDigitsJDView;
import com.desheng.app.toucai.view.PlayLongHuHeView;
import com.noober.background.view.BLTextView;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 彩票玩法fragment
 * Created by lee on 2018/3/9.
 */
public class FragLotteryPlayFragmentInput extends BaseLotteryPlayFragment {
    
    public static FragLotteryPlayFragmentInput newIns(String playCode) {
        FragLotteryPlayFragmentInput fragment = new FragLotteryPlayFragmentInput();
        Bundle bundle = new Bundle();
        bundle.putString("playCode", playCode);
        fragment.setArguments(bundle);
        return fragment;
    }
    
    private BLTextView tvPaste;
    private EditText etInput;
    
    private HashMap<String, View> mapInputGroup;
    private ClipboardManager mClipboardManager;
    
    
    
    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_input;
    }
    
    
    @Override
    protected void createCtrlPlay() {
            setCtrlPlay(new CtrlPlay_Input(getActivity(), this, getLotteryInfo() , getUiConfig(), getLotteryPlay()));
    }
    
    
    @Override
    public void createPlayModel(View root) {
        tvPaste = (BLTextView) root.findViewById(R.id.tvPaste);
        mClipboardManager = (ClipboardManager) getContext().getSystemService(Service.CLIPBOARD_SERVICE);
        etInput = root.findViewById(R.id.etInput);

        tvPaste.setOnClickListener(new View.OnClickListener() {
            public ClipData mClipData;

            @Override
            public void onClick(View v) {
                //GET贴板是否有内容
                mClipData = mClipboardManager.getPrimaryClip();
                //获取到内容
                if(mClipData != null){
                    ClipData.Item item = mClipData.getItemAt(0);
                    if (item != null && Strs.isNotEmpty(item.getText().toString())) {
                        String text = item.getText().toString();
                        etInput.setText(text);
                        Toasts.show(context, "粘贴成功!", true);
                    }else{
                        Toasts.show(context, "无可粘贴内容!", true);
                    }
                }else{
                    Toasts.show(context, "无可粘贴内容!", true);
                }
            }
        });
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
