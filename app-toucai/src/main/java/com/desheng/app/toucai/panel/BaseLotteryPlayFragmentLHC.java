package com.desheng.app.toucai.panel;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.ab.thread.ThreadCollector;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.controller.BaseCtrlPlayLHC;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.view.PlayBoardLHCView;
import com.desheng.app.toucai.view.PlayDigitsLHCView;
import com.desheng.app.toucai.view.PlayFootView;
import com.desheng.app.toucai.view.PlayHeadViewLHC;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryPlayLHCUI;
import com.desheng.base.model.LotteryPlayUserInfoLHC;
import com.google.gson.Gson;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.ToggleImageButton;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lee on 2018/3/19.
 */

public abstract class BaseLotteryPlayFragmentLHC extends AbBaseFragment {
    private BaseCtrlPlayLHC ctrlPlay;
    private ILotteryKind lotteryKind;
    private LotteryPlayUserInfoLHC userPlayInfoLHC;
    protected int lotteryId;
    protected String category;
    protected String playCode;

    private LotteryPlayLHCUI uiConfigLHC;
    private PlayHeadViewLHC vHead;
    private PlayFootView vFoot;

    private ILotteryPlayContainerLHC fragContainer;
    private LotteryPlayPanelLHC lotteryPlayPanel;
    public RecyclerView recycleViewWanfaDetail;

    public void setFragContainer(ILotteryPlayContainerLHC fragContainer) {
        this.fragContainer = fragContainer;
    }

    public ILotteryPlayContainerLHC getFragContainer() {
        return fragContainer;
    }


    public static <T extends BaseLotteryPlayFragmentLHC> BaseLotteryPlayFragmentLHC newLHCIns(Class<T> clazz, String category, int lotteryId, String playCode, LotteryPlayUserInfoLHC info, LotteryPlayPanelLHC lotteryPlayPanel) {
        BaseLotteryPlayFragmentLHC fragment = null;
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
        bundle.putString("code", playCode);
        bundle.putString("infoLHC", new Gson().toJson(info, LotteryPlayUserInfoLHC.class));
        fragment.setArguments(bundle);
        fragment.lotteryPlayPanel = lotteryPlayPanel;
        return fragment;
    }

    @Override
    public void init(View root) {
        lotteryId = getArguments().getInt("lotteryId");
        lotteryKind = LotteryKind.find(lotteryId);
        playCode = getArguments().getString("code");
        category = getArguments().getString("category");
        String playInfoStr = getArguments().getString("infoLHC");
        userPlayInfoLHC = new Gson().fromJson(playInfoStr, LotteryPlayUserInfoLHC.class);

        uiConfigLHC = UserManagerTouCai.getIns().getLotteryPlayLHCMap().get(playCode);
        //AbDebug.log(AbDebug.TAG_APP, "initData" );

        vHead = root.findViewById(R.id.vHead);
        vHead.setMoney(UserManagerTouCai.getIns().getLotteryAvailableBalance());
        vFoot = fragContainer.getFootView();

        recycleViewWanfaDetail = root.findViewById(R.id.recycleViewWanfaDetail);
        //配置脚
//        LotteryPlayPanelLHC activity = (LotteryPlayPanelLHC) getActivity();
//        if (activity.getSnakeBar() != null && vFoot != null) {
//            vFoot.setSnakeView(activity.getSnakeBar());
//        }

        //AbDebug.log(AbDebug.TAG_APP, "initView" );
        createPlayModel(root);
        createCtrlPlay();
        //启动
        ctrlPlay.start(null);
        //AbDebug.log(AbDebug.TAG_APP, "initCtrl" );
        if (mPlayviewLoadcomplete != null) {
            mPlayviewLoadcomplete.onPlayviewLoadcomplete();
        }
    }

    IPlayviewLoadcomplete mPlayviewLoadcomplete;

    public void setmPlayviewLoadcomplete(IPlayviewLoadcomplete mPlayviewLoadcomplete) {
        this.mPlayviewLoadcomplete = mPlayviewLoadcomplete;
    }

    public interface IPlayviewLoadcomplete {
        void onPlayviewLoadcomplete();
    }


    public abstract void createPlayModel(View root);

    protected abstract void createCtrlPlay();


    @Override
    public void onShow() {
        super.onShow();
        if (uiConfigLHC != null) {
            getHead().setShakeVisible(false);
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

    public PlayHeadViewLHC getHead() {
        return vHead;
    }

    ;
    
   /* public PlayFootView getFoot() {
        return vFoot;
    }*/

    public void setCtrlPlay(BaseCtrlPlayLHC ctrlPlay) {
        this.ctrlPlay = ctrlPlay;
    }

    public BaseCtrlPlayLHC getCtrlPlay() {
        return ctrlPlay;
    }

    ;

    public LotteryPlayLHCUI getLotteryPlay() {
        return uiConfigLHC;
    }


    public String getPlayShortName() {
        // FIXME: 2018/7/12 
        return "short name";
    }

    public String getPlayFullName() {
        // FIXME: 2018/7/12 
        return "full name";
    }

    public String getPlayCode() {
        return playCode;
    }

    public String getCategory() {
        return category;
    }

    public LotteryPlayUserInfoLHC getUserPlayInfoLHC() {
        return userPlayInfoLHC;
    }

    public LotteryPlayLHCUI getUiConfig() {
        return uiConfigLHC;
    }

    public void setCurrentUserPlayInfo(final Object userPlayInfo) {
        if (userPlayInfo instanceof LotteryPlayUserInfoLHC) {
            this.userPlayInfoLHC = (LotteryPlayUserInfoLHC) userPlayInfo;
        }
        if (getCtrlPlay() != null) {
            getCtrlPlay().refreshCurrentUserPlayInfo(userPlayInfo);
        } else { //极端情况, 接口快速返回, ctrlPlay未来得及初始话
            ThreadCollector.getIns().postDelayOnUIThread(300, new Runnable() {
                @Override
                public void run() {
                    getCtrlPlay().refreshCurrentUserPlayInfo(userPlayInfo);
                }
            });
        }
    }

    public LotteryPlayPanelLHC getLotteryPlayPanel() {
        return lotteryPlayPanel;
    }

    public abstract ArrayList<PlayDigitsLHCView> getListBallGroupsLHC();

    public abstract HashMap<String, View> getInputGroup();

    public abstract ArrayList<ViewGroup> getPowerGroup();

    public abstract ArrayList<ToggleImageButton> getPowerBtnList();

    public abstract PlayBoardLHCView getBoardGroup();

    public abstract void syncSelection();
}
