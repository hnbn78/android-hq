package com.desheng.app.toucai.panel;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.desheng.app.toucai.consitance.CommonConsts;
import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.view.PlayFootView;
import com.desheng.app.toucai.view.PlaySnakeView;
import com.desheng.base.model.LotteryPlayJD;
import com.desheng.base.model.LotteryPlayUserInfo;
import com.desheng.base.model.LotteryPlayUserInfoJD;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.act.util.FragmentsHelper;
import com.shark.tc.R;

/**
 * 玩法容器, 处理触摸, 填充各种玩法fragment
 * Created by lee on 2018/3/9.
 */
public class FragLotteryPlayContainerJD extends AbBaseFragment implements ILotteryPlayContainerJD {

    private LotteryPlayUserInfoJD userPlayInfo;
    private String category;
    private LotteryPlayPanelJD lotteryPlayPanel;

    public static FragLotteryPlayContainerJD newIns(int lotteryId, String category, LotteryPlayPanelJD lotteryPlayPanel) {
        FragLotteryPlayContainerJD fragment = new FragLotteryPlayContainerJD();
        Bundle bundle = new Bundle();
        bundle.putInt("lotteryId", lotteryId);
        bundle.putString("category", category);
        fragment.setArguments(bundle);
        fragment.lotteryPlayPanel = lotteryPlayPanel;
        return fragment;
    }

    private ViewGroup expandedView;
    private ViewGroup collapsedView;
    private View vDummyTouch;
    private FrameLayout rlPlayContainer;

    private int mLotteryId;
    private BaseLotteryPlayFragmentJD currentPlayFrag;

    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_container_jd;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void init(View root) {
        mLotteryId = getArguments().getInt("lotteryId");
        category = getArguments().getString("category");

        expandedView = (ViewGroup) root.findViewById(R.id.expanded_view);

        rlPlayContainer = (FrameLayout) root.findViewById(R.id.rlPlayContainer);

        //act中填入的默认直选
       /*
        currentPlayFrag =
        FragmentsHelper.replaceFragments(getChildFragmentManager(), R.id.rlPlayContainer, currentPlayFrag);
        */
        //底部消息
        //vSnakeBar = (PlaySnakeView) root.findViewById(R.id.vSnakeBar);
        //((ActLotteryPlayJD)getActivity()).setDefaultPlay();
    }

    public void setUserPlayInfo(LotteryPlayUserInfoJD userPlayInfo) {
        this.userPlayInfo = userPlayInfo;
        if (currentPlayFrag != null) {
            currentPlayFrag.setCurrentUserPlayInfo(userPlayInfo);
        }
    }


    @Override
    public void setUserPlayInfo(LotteryPlayUserInfo userPlayInfo) {
        //nothing
    }

    @Override
    public FrameLayout getPlayContainer() {
        return rlPlayContainer;
    }

    @Override
    public BaseLotteryPlayFragmentJD getCurrentPlayFrag() {
        return currentPlayFrag;
    }

    @Override
    public void showPlay(String category, String playCode) {
        currentPlayFrag = getPlayFragment(category, playCode);
        if (!isAdded()) return;
        FragmentsHelper.replaceFragments(getChildFragmentManager(), R.id.rlPlayContainer, currentPlayFrag);
    }

    public void removeCurrentPlay() {
        FragmentsHelper.removeFragment(getChildFragmentManager(), currentPlayFrag);
    }

    public PlaySnakeView getSnakeBar() {
        return null;
        //return vSnakeBar;
    }

    @Override
    public PlayFootView getFootView() {
        return null;
    }

    private BaseLotteryPlayFragmentJD getPlayFragment(String category, String playName) {
        BaseLotteryPlayFragmentJD fragment = null;
        LotteryPlayJD config = UserManagerTouCai.getIns().getLotteryPlayJD(category, playName);
        Log.e("FragLotteryPlayContaine", mLotteryId + "");
        switch (config.getType()) {
            case CtxLotteryTouCai.ShowType_DualDigits_JD:
                if (needNewBetUi(mLotteryId)) {
                    fragment = BaseLotteryPlayFragmentJD.newJDIns(FragLotteryPlayFragmentDigitsJDPK10.class, category, playName, mLotteryId, config.getPlayId(), userPlayInfo, lotteryPlayPanel);
                } else {
                    fragment = BaseLotteryPlayFragmentJD.newJDIns(FragLotteryPlayFragmentDigitsJD.class, category, playName, mLotteryId, config.getPlayId(), userPlayInfo, lotteryPlayPanel);
                }
                break;
            case CtxLotteryTouCai.ShowType_SMALL_Digits_JD:
            case CtxLotteryTouCai.ShowType_Digits_JD:
                if (needNewBetUi(mLotteryId)) {
                    fragment = BaseLotteryPlayFragmentJD.newJDIns(FragLotteryPlayFragmentDigitsJDPK10.class, category, playName, mLotteryId, config.getPlayId(), userPlayInfo, lotteryPlayPanel);
                } else {
                    fragment = BaseLotteryPlayFragmentJD.newJDIns(FragLotteryPlayFragmentDigitsJD.class, category, playName, mLotteryId, config.getPlayId(), userPlayInfo, lotteryPlayPanel);
                }
                break;
            case CtxLotteryTouCai.ShowType_Board_JD:
                fragment = BaseLotteryPlayFragmentJD.newJDIns(FragLotteryPlayFragmentBoardJD.class, category, playName, mLotteryId, config.getPlayId(), userPlayInfo, lotteryPlayPanel);
                break;
            case CtxLotteryTouCai.ShowType_NIUNIU_JD:
                fragment = BaseLotteryPlayFragmentJD.newJDIns(FragLotteryPlayFragmentNiuNiuJD.class, category, playName, mLotteryId, config.getPlayId(), userPlayInfo, lotteryPlayPanel);
                break;
            default:
                break;
        }
        fragment.setFragContainer(this);
        return fragment;
    }

    private boolean needNewBetUi(int lotteryId) {
        return CommonConsts.setBetNeedNewUIConfig(LotteryKind.find(lotteryId).getCode());
    }
}
