package com.desheng.app.toucai.panel;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.desheng.app.toucai.context.CtxLotteryTouCai;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.view.PlayFootView;
import com.desheng.app.toucai.view.PlaySnakeView;
import com.desheng.base.model.LotteryPlayUIConfig;
import com.desheng.base.model.LotteryPlayUserInfo;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.act.util.FragmentsHelper;
import com.shark.tc.R;

/**
 * 玩法容器, 处理触摸, 填充各种玩法fragment
 * Created by lee on 2018/3/9.
 */
public class FragLotteryPlayContainer extends AbBaseFragment implements ILotteryPlayContainer {

    private LotteryPlayUserInfo userPlayInfo;
    private String category;
    private LotteryPlayPanel lotteryPlayPanel;

    public static FragLotteryPlayContainer newIns(int lotteryId, String category, LotteryPlayPanel lotteryPlayPanel) {
        FragLotteryPlayContainer fragment = new FragLotteryPlayContainer();
        Bundle bundle = new Bundle();
        bundle.putInt("lotteryId", lotteryId);
        bundle.putString("category", category);
        fragment.setArguments(bundle);
        fragment.lotteryPlayPanel = lotteryPlayPanel;
        return fragment;
    }


    private FrameLayout rlPlayContainer;


    private int lotteryId;
    private BaseLotteryPlayFragment currentPlayFrag;

    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_container;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void init(View root) {
        lotteryId = getArguments().getInt("lotteryId");
        category = getArguments().getString("category");

        rlPlayContainer = (FrameLayout) root.findViewById(R.id.rlPlayContainer);

        //act中填入的默认直选
       /*
        currentPlayFrag =
        FragmentsHelper.replaceFragments(getChildFragmentManager(), R.id.rlPlayContainer, currentPlayFrag);
        */
    }

    @Override
    public void setUserPlayInfo(LotteryPlayUserInfo userPlayInfo) {
        this.userPlayInfo = userPlayInfo;
        if (currentPlayFrag != null) {
            currentPlayFrag.setCurrentUserPlayInfo(userPlayInfo);
        }
    }


    @Override
    public FrameLayout getPlayContainer() {
        return rlPlayContainer;
    }

    @Override
    public BaseLotteryPlayFragment getCurrentPlayFrag() {
        return currentPlayFrag;
    }

    @Override
    public void showPlay(String category, String playCode) {
        currentPlayFrag = getPlayFragment(category, playCode, userPlayInfo);
        if (!isAdded()) return;
        FragmentsHelper.replaceFragments(getChildFragmentManager(), R.id.rlPlayContainer, currentPlayFrag);
    }


    @Override
    public void removeCurrentPlay() {
        FragmentsHelper.removeFragment(getChildFragmentManager(), currentPlayFrag);
    }

//    @Override
//    public PlayFootView getFootView() {
//        if (getCurrentPlayFrag() != null) {
//            return getCurrentPlayFrag().getFoot();
//        } else {
//            return null;
//        }
//    }

    private BaseLotteryPlayFragment getPlayFragment(String category, String playCode, LotteryPlayUserInfo userPlayInfo) {

        BaseLotteryPlayFragment fragment = null;
        if (UserManagerTouCai.getIns().getLotteryPlayUIConfigWithCategoryMap() == null) {
            return null;
        }

        if (UserManagerTouCai.getIns().getLotteryPlayUIConfigWithCategoryMap().get(category) == null) {
            return null;
        }

        LotteryPlayUIConfig config = UserManagerTouCai.getIns().getLotteryPlayUIConfigWithCategoryMap().get(category).get(playCode);
        if (config == null) {
            return null;
        }


        switch (config.getShowType()) {
        case CtxLotteryTouCai.ShowType_Digis_K3:
            fragment = BaseLotteryPlayFragment.newIns(FragLotteryPlayFragmentDigitsK3.class, category, lotteryId, playCode, userPlayInfo, lotteryPlayPanel);
            break;
        case CtxLotteryTouCai.ShowType_Digis:
            fragment = BaseLotteryPlayFragment.newIns(FragLotteryPlayFragmentDigits.class, category, lotteryId, playCode, userPlayInfo, lotteryPlayPanel);
            break;
        case CtxLotteryTouCai.ShowType_DetailShowTypeWri:
            fragment = BaseLotteryPlayFragment.newIns(FragLotteryPlayFragmentInput.class, category, lotteryId, playCode, userPlayInfo, lotteryPlayPanel);
            break;
        case CtxLotteryTouCai.ShowType_DetailShowTypeWriAndNum:
            fragment = BaseLotteryPlayFragment.newIns(FragLotteryPlayFragmentInputCombind.class, category, lotteryId, playCode, userPlayInfo, lotteryPlayPanel);
            break;
        case CtxLotteryTouCai.ShowType_DetailShowTypeBitAndNum:
            fragment = BaseLotteryPlayFragment.newIns(FragLotteryPlayFragmentDigitsCombind.class, category, lotteryId, playCode, userPlayInfo, lotteryPlayPanel);
            break;
        case CtxLotteryTouCai.ShowType_DetailShowTypeLongHU:
            fragment = BaseLotteryPlayFragment.newIns(FragLotteryPlayFragmentLongHuHe.class, category, lotteryId, playCode, userPlayInfo, lotteryPlayPanel);
            break;
        case CtxLotteryTouCai.ShowType_DetailShowTypeLongHU_AllInOne:
            fragment = BaseLotteryPlayFragment.newIns(FragLotteryPlayFragmentLongHuHeAllInOne.class, category, lotteryId, playCode, userPlayInfo, lotteryPlayPanel);
            break;
        case CtxLotteryTouCai.ShowType_DetailShowTypeDaXiaoDanShuang_AllInOne:
            fragment = BaseLotteryPlayFragment.newIns(FragLotteryPlayFragmentDigitsAllInOne.class, category, lotteryId, playCode, userPlayInfo, lotteryPlayPanel);
            break;
        default:
            break;
        }
        fragment.setFragContainer(this);
        return fragment;
    }
}
