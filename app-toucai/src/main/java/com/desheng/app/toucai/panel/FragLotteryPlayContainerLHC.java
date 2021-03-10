package com.desheng.app.toucai.panel;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ab.util.Strs;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.view.PlayFootView;
import com.desheng.app.toucai.view.PlaySnakeView;
import com.desheng.base.model.LotteryPlayLHCUI;
import com.desheng.base.model.LotteryPlayUserInfo;
import com.desheng.base.model.LotteryPlayUserInfoLHC;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.act.util.FragmentsHelper;
import com.shark.tc.R;

/**
 * 玩法容器, 处理触摸, 填充各种玩法fragment
 * Created by lee on 2018/3/9.
 */
public class FragLotteryPlayContainerLHC extends AbBaseFragment implements  ILotteryPlayContainerLHC {
    
    private LotteryPlayUserInfoLHC userPlayInfo;
    private LotteryPlayPanelLHC lotteryPlayPanel;

    public static FragLotteryPlayContainerLHC newIns(int lotteryId, LotteryPlayPanelLHC lotteryPlayPanel) {
        FragLotteryPlayContainerLHC fragment = new FragLotteryPlayContainerLHC();
        Bundle bundle = new Bundle();
        bundle.putInt("lotteryId", lotteryId);
        fragment.setArguments(bundle);
        fragment.lotteryPlayPanel = lotteryPlayPanel;
        return fragment;
    }
    
    private ViewGroup expandedView;
    private ViewGroup collapsedView;
    private View vDummyTouch;
    private FrameLayout rlPlayContainer;
    private FrameLayout rlMenuContainer;
    //暂不使用
    //private PlaySnakeView vSnakeBar;
  
    private int lotteryId;
    private BaseLotteryPlayFragmentLHC currentPlayFrag;

    @Override
    public int getLayoutId() {
        return R.layout.frag_lottery_play_container_lhc;
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    
    @Override
    public void init(View root) {
        lotteryId = getArguments().getInt("lotteryId");

        expandedView = (ViewGroup)root.findViewById(R.id.expanded_view);

        rlPlayContainer = (FrameLayout) root.findViewById(R.id.rlPlayContainer);
        rlMenuContainer = (FrameLayout) root.findViewById(R.id.rlMenuContainer);

        //act中填入的默认直选
       /*
        currentPlayFrag =
        FragmentsHelper.replaceFragments(getChildFragmentManager(), R.id.rlPlayContainer, currentPlayFrag);
        */
        //底部消息
        //vSnakeBar = (PlaySnakeView) root.findViewById(R.id.vSnakeBar);
        //填入菜单
        rlMenuContainer.setVisibility(View.GONE);
        //FragmentsHelper.swapFragments(getChildFragmentManager(), R.id.rlMenuContainer, FragLotteryPlayMenuLHC.newIns(category));
        lotteryPlayPanel.setDefaultPlay();
    }
    
    public void setUserPlayInfo(LotteryPlayUserInfoLHC userPlayInfo){
        this.userPlayInfo = userPlayInfo;
        if (currentPlayFrag != null) {
            currentPlayFrag.setCurrentUserPlayInfo(userPlayInfo);
        }
    }
    
    
    @Override
    public void setUserPlayInfo(LotteryPlayUserInfo userPlayInfo) {
        //nothing
    }
    
    public FrameLayout getMenuContainer(){
        return rlMenuContainer;
    }
    
    @Override
    public FrameLayout getPlayContainer(){
        return rlPlayContainer;
    }
    
    @Override
    public BaseLotteryPlayFragmentLHC getCurrentPlayFrag() {
        return currentPlayFrag;
    }
    
    @Override
    public void showPlay(String category, String playCode){
        currentPlayFrag = getPlayFragment(category, playCode);
        FragmentsHelper.replaceFragments(getChildFragmentManager(), R.id.rlPlayContainer, currentPlayFrag);
    }
    
    public void removeCurrentPlay(){
        FragmentsHelper.removeFragment(getChildFragmentManager(), currentPlayFrag);
    }
    
    public PlaySnakeView getSnakeBar(){
        return null;
        //return vSnakeBar;
    }
    
    @Override
    public PlayFootView getFootView() {
        return null;
    }
//
//    public NestedScrollView getScrollView(){
//        return nsvContent;
//    }
    
  
    private BaseLotteryPlayFragmentLHC getPlayFragment(String category, String playCode){
        BaseLotteryPlayFragmentLHC fragment = null;
        LotteryPlayLHCUI config = UserManagerTouCai.getIns().getLotteryPlayLHCMap().get(playCode);
        // FIXME: 2018/7/12
//        switch (config.getType()){
//            case CtxLotteryTouCai.ShowType_DualDigits_LHC:
//                fragment = BaseLotteryPlayFragmentLHC.newLHCIns(FragLotteryPlayFragmentDigitsLHC.class, category, playName, lotteryId, config.getPlayId(), userPlayInfo);
//                break;
//            case CtxLotteryTouCai.ShowType_Digits_LHC:
//                fragment = BaseLotteryPlayFragmentLHC.newLHCIns(FragLotteryPlayFragmentDigitsLHC.class, category, playName, lotteryId, config.getPlayId(), userPlayInfo);
//                break;
//            case CtxLotteryTouCai.ShowType_Board_LHC:
        // FIXME: 2018/7/12
        if (Strs.isEqual(category, "特码")) {
            fragment = BaseLotteryPlayFragmentLHC.newLHCIns(FragLotteryPlayFragmentDigitsLHC.class, category, lotteryId, playCode, userPlayInfo, lotteryPlayPanel);
        } else if (Strs.isEqual(category, "正码")) {
            fragment = BaseLotteryPlayFragmentLHC.newLHCIns(FragLotteryPlayFragmentDigitsLHC.class, category, lotteryId, playCode, userPlayInfo, lotteryPlayPanel);
        } else if (Strs.isEqual(category, "半波")) {
            fragment = BaseLotteryPlayFragmentLHC.newLHCIns(FragLotteryPlayFragmentBoardLHC.class, category, lotteryId, playCode, userPlayInfo, lotteryPlayPanel);
        } else if (Strs.isEqual(category, "生肖")) {
            fragment = BaseLotteryPlayFragmentLHC.newLHCIns(FragLotteryPlayFragmentZodiacLHC.class, category, lotteryId, playCode, userPlayInfo, lotteryPlayPanel);
        } else if (Strs.isEqual(category, "尾数")) {
            fragment = BaseLotteryPlayFragmentLHC.newLHCIns(FragLotteryPlayFragmentBoardLHC.class, category, lotteryId, playCode, userPlayInfo, lotteryPlayPanel);
        } else if (Strs.isEqual(category, "总分")) {
            fragment = BaseLotteryPlayFragmentLHC.newLHCIns(FragLotteryPlayFragmentScoreLHC.class, category, lotteryId, playCode, userPlayInfo, lotteryPlayPanel);
        } else if (Strs.isEqual(category, "不中")) {
            fragment = BaseLotteryPlayFragmentLHC.newLHCIns(FragLotteryPlayFragmentDigitsLHC.class, category, lotteryId, playCode, userPlayInfo, lotteryPlayPanel);
        }


        fragment.setFragContainer(this);
        return fragment;
    };
}
