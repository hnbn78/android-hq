package com.desheng.app.toucai.panel;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.ab.thread.ThreadCollector;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.controller.BaseCtrlPlay;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.view.PlayBoardJDView;
import com.desheng.app.toucai.view.PlayDigitView;
import com.desheng.app.toucai.view.PlayDigitsJDView;
import com.desheng.app.toucai.view.PlayFootView;
import com.desheng.app.toucai.view.PlayHeadView;
import com.desheng.app.toucai.view.PlayLongHuHeView;
import com.desheng.app.toucai.view.PlaySnakeView;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryPlay;
import com.desheng.base.model.LotteryPlayUIConfig;
import com.desheng.base.model.LotteryPlayUserInfo;
import com.google.gson.Gson;
import com.pearl.act.base.AbBaseFragment;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lee on 2018/3/19.
 */

public abstract class BaseLotteryPlayFragment extends AbBaseFragment {
    private BaseCtrlPlay ctrlPlay;
    private int lotteryId;
    private String playCode;
    private ILotteryKind lotteryKind;
    private LotteryPlay play;
    public LotteryPlayUserInfo userPlayInfo;
    private String category;

    private LotteryPlayUIConfig uiConfig;
    private PlayHeadView vHead;
    private PlayFootView vFoot;

    private ILotteryPlayContainer fragContainer;
    private LotteryPlayPanel lotteryPlayPanel;

    public void setFragContainer(ILotteryPlayContainer fragContainer) {
        this.fragContainer = fragContainer;
    }

    public ILotteryPlayContainer getFragContainer() {
        return fragContainer;
    }

    public static <T extends BaseLotteryPlayFragment> BaseLotteryPlayFragment newIns(Class<T> clazz, String category, int lotteryId, String playCode, LotteryPlayUserInfo info, LotteryPlayPanel lotteryPlayPanel) {
        BaseLotteryPlayFragment fragment = null;
        try {
            fragment = clazz.newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        bundle.putInt("lotteryId", lotteryId);
        bundle.putString("playCode", playCode);
        bundle.putString("info", new Gson().toJson(info, LotteryPlayUserInfo.class));
        fragment.setArguments(bundle);
        fragment.lotteryPlayPanel = lotteryPlayPanel;
        return fragment;
    }

    @Override
    public void init(View root) {
        lotteryId = getArguments().getInt("lotteryId");
        lotteryKind = LotteryKind.find(lotteryId);
        playCode = getArguments().getString("playCode");
        category = getArguments().getString("category");
        String playInfoStr = getArguments().getString("info");
        userPlayInfo = new Gson().fromJson(playInfoStr, LotteryPlayUserInfo.class);

        play = UserManagerTouCai.getIns().getLotteryPlay(category, playCode);
        uiConfig = UserManagerTouCai.getIns().getLotteryPlayUIConfigWithCategoryMap().get(category).get(playCode);
        //AbDebug.log(AbDebug.TAG_APP, "initData" );

        vHead = (PlayHeadView) root.findViewById(R.id.vHead);
        vHead.setMoney(UserManagerTouCai.getIns().getLotteryAvailableBalance());
        vFoot = root.findViewById(R.id.vFootView);

        //配置脚
        //LotteryPlayPanel activity = (ActLotteryMain) context;
        PlaySnakeView snakeBar = lotteryPlayPanel.getSnakeBar();
        if (vFoot != null && snakeBar != null) {
            vFoot.setSnakeView(snakeBar);
            if (userPlayInfo != null) {
                vFoot.setLotteryInfo(play, userPlayInfo);
            }
        }

        //AbDebug.log(AbDebug.TAG_APP, "initView" );
        createPlayModel(root);
        createCtrlPlay();
        //启动
        if (ctrlPlay != null) {
            ctrlPlay.start(null);
        }
        //AbDebug.log(AbDebug.TAG_APP, "initCtrl" );
    }


    public abstract void createPlayModel(View root);

    protected abstract void createCtrlPlay();


    @Override
    public void onShow() {
        super.onShow();
        if (uiConfig != null) {
            //getHead().registShake(getActivity(), getCtrlPlay());
            getHead().setWanfaShuomingContent(getCtrlPlay().getUiConfig().getHelp());
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

    public PlayHeadView getHead() {
        return vHead;
    }

    ;

    public PlayFootView getFoot() {
        return vFoot;
    }

    public void setCtrlPlay(BaseCtrlPlay ctrlPlay) {
        this.ctrlPlay = ctrlPlay;
    }

    public BaseCtrlPlay getCtrlPlay() {
        return ctrlPlay;
    }

    ;

    public LotteryPlay getLotteryPlay() {
        return play;
    }


    public String getPlayId() {
        return playCode;
    }

    ;

    public String getPlayShortName() {
        return play.name;
    }

    public String getPlayFullName() {
        return play.showName;
    }


    public LotteryPlayUIConfig getUiConfig() {
        return uiConfig;
    }

    public LotteryPlayUserInfo getUserPlayInfo() {
        return userPlayInfo;
    }

    public void setCurrentUserPlayInfo(final Object userPlayInfo) {
        if (userPlayInfo instanceof LotteryPlayUserInfo) {
            this.userPlayInfo = (LotteryPlayUserInfo) userPlayInfo;
//            if (vFoot != null) {
//                vFoot.setLotteryInfo(play,  this.userPlayInfo);
//            }
        }
        if (getCtrlPlay() != null) {
            getCtrlPlay().refreshCurrentUserPlayInfo(userPlayInfo);
        }
    }

    public LotteryPlayPanel getLotteryPlayPanel() {
        return lotteryPlayPanel;
    }

    public abstract ArrayList<PlayDigitView> getListBallGroups();

    public abstract ArrayList<PlayLongHuHeView> getListLHHGroups();

    public abstract ArrayList<PlayDigitsJDView> getListBallGroupsJD();

    public abstract ArrayList<PlayBoardJDView> getListBoardGroupsJD();

    public abstract HashMap<String, View> getInputGroup();

    public abstract ArrayList<ViewGroup> getPowerGroup();

    public abstract ArrayList<CheckedTextView> getPowerBtnList();

    public abstract int getMode();
}
