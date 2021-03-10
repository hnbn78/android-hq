package com.desheng.app.toucai.panel;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.ab.thread.ThreadCollector;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.controller.BaseCtrlPlayJD;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.view.PlayBoardJDView;
import com.desheng.app.toucai.view.PlayDigitsJDView;
import com.desheng.app.toucai.view.PlayFootView;
import com.desheng.app.toucai.view.PlayHeadView;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryPlayJD;
import com.desheng.base.model.LotteryPlayUserInfoJD;
import com.google.gson.Gson;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.ToggleImageButton;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lee on 2018/3/19.
 */

public abstract class BaseLotteryPlayFragmentJD extends AbBaseFragment  {
    private BaseCtrlPlayJD ctrlPlay;
    private int lotteryId;
    private String playCode;
    private ILotteryKind lotteryKind;
    private LotteryPlayUserInfoJD userPlayInfoJD;
    private String category;
    private String playNameJD;

    private LotteryPlayJD uiConfigJD;
    private PlayHeadView vHead;
    private PlayFootView vFoot;

    private ILotteryPlayContainerJD fragContainer;
    private LotteryPlayPanelJD lotteryPlayPanel;

    public void setFragContainer(ILotteryPlayContainerJD fragContainer){
        this.fragContainer = fragContainer;
    }

    public ILotteryPlayContainerJD getFragContainer() {
        return fragContainer;
    }


    public static <T extends BaseLotteryPlayFragmentJD> BaseLotteryPlayFragmentJD newJDIns(Class<T> clazz, String category, String playNameJD, int lotteryId, String playId, LotteryPlayUserInfoJD info, LotteryPlayPanelJD lotteryPlayPanel) {
        BaseLotteryPlayFragmentJD fragment = null;
        try {
            fragment = clazz.newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        bundle.putString("playNameJD", playNameJD);
        bundle.putString("playId", playId);
        bundle.putInt("lotteryId", lotteryId);
        bundle.putString("infoJD", new Gson().toJson(info, LotteryPlayUserInfoJD.class));
        fragment.setArguments(bundle);
        fragment.lotteryPlayPanel = lotteryPlayPanel;
        return fragment;
    }

    @Override
    public void init(View root) {
        lotteryId = getArguments().getInt("lotteryId");
        lotteryKind = LotteryKind.find(lotteryId);
        playCode = getArguments().getString("playCode");
        playNameJD = getArguments().getString("playNameJD");
        category = getArguments().getString("category");
        String playInfoStr = getArguments().getString("info");
        playInfoStr = getArguments().getString("infoJD");
        userPlayInfoJD = new Gson().fromJson(playInfoStr, LotteryPlayUserInfoJD.class);

        uiConfigJD = UserManagerTouCai.getIns().getLotteryPlayJD(category, playNameJD);
        //AbDebug.log(AbDebug.TAG_APP, "initData" );

        vHead = (PlayHeadView) root.findViewById(R.id.vHead);
        vHead.setMoney(UserManagerTouCai.getIns().getLotteryAvailableBalance());
        vFoot = fragContainer.getFootView();

        //配置脚
//        LotteryPlayPanelJD activity = (LotteryPlayPanelJD) getActivity();
//        if (activity.getSnakeBar() != null && vFoot != null) {
//            vFoot.setSnakeView(activity.getSnakeBar());
//        }

        //AbDebug.log(AbDebug.TAG_APP, "initView" );
        createPlayModel(root);
        createCtrlPlay();
        //启动
        ctrlPlay.start(null);
        //AbDebug.log(AbDebug.TAG_APP, "initCtrl" );
    }


    public abstract void createPlayModel(View root);

    protected abstract void createCtrlPlay();


    @Override
    public void onShow() {
        super.onShow();
        if(uiConfigJD != null){
            getHead().setShakeVisible(true);
            //getHead().registShake(getActivity(), getCtrlPlay());
            getHead().setOnInstructionClickListener(getCtrlPlay());
        }

    }


    @Override
    public void onHide() {
        super.onHide();
        //vHead.unregistShake(getActivity());
    }

    @Override
    public void onDestroy() {
        if (ctrlPlay != null)
            ctrlPlay.stop();

        super.onDestroy();
    }

    public LotteryInfo getLotteryInfo() {
        return lotteryKind.getLotteryInfo();
    }

    public PlayHeadView getHead(){
        return vHead;
    };
    
   /* public PlayFootView getFoot() {
        return vFoot;
    }*/

    public void setCtrlPlay(BaseCtrlPlayJD ctrlPlay) {
        this.ctrlPlay = ctrlPlay;
    }

    public BaseCtrlPlayJD getCtrlPlay(){
        return ctrlPlay;
    };

    public LotteryPlayJD getLotteryPlay() {
        return uiConfigJD;
    }


    public String getPlayId() {
        return playCode;
    };

    public String getPlayShortName() {
        return uiConfigJD.getShowName();
    }

    public String getPlayFullName() {
        return uiConfigJD.getShowName();
    }

    public LotteryPlayUserInfoJD getUserPlayInfoJD() {
        return userPlayInfoJD;
    }

    public LotteryPlayJD getUiConfig() {return uiConfigJD;}

    public void setCurrentUserPlayInfo(final Object userPlayInfo){
        if(userPlayInfo instanceof LotteryPlayUserInfoJD){
            this.userPlayInfoJD = (LotteryPlayUserInfoJD) userPlayInfo;
        }
        if(getCtrlPlay() != null){
            getCtrlPlay().refreshCurrentUserPlayInfo(userPlayInfo);
        }
    }

    public LotteryPlayPanelJD getLotteryPlayPanel() {
        return lotteryPlayPanel;
    }

    public abstract ArrayList<PlayDigitsJDView> getListBallGroupsJD();
    public abstract ArrayList<PlayBoardJDView> getListBoardGroupsJD();

    public abstract HashMap<String, View> getInputGroup();

    public abstract ArrayList<ViewGroup> getPowerGroup();

    public abstract ArrayList<ToggleImageButton> getPowerBtnList();

    public abstract int getMode();

    public abstract void syncSelection();
}
