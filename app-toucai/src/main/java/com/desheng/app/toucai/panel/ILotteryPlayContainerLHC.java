package com.desheng.app.toucai.panel;

import android.widget.FrameLayout;

import com.desheng.app.toucai.view.PlayFootView;
import com.desheng.app.toucai.view.PlaySnakeView;
import com.desheng.base.model.LotteryPlayUserInfo;

/**
 * Created by lee on 2018/4/17.
 */

public interface ILotteryPlayContainerLHC {
    void setUserPlayInfo(LotteryPlayUserInfo userPlayInfo);
    
    FrameLayout getMenuContainer();
    
    FrameLayout getPlayContainer();
    
    BaseLotteryPlayFragmentLHC getCurrentPlayFrag();
    
    void showPlay(String category, String playCode);
    
    void removeCurrentPlay();
    
    PlaySnakeView getSnakeBar();
    
    PlayFootView getFootView();
    
//    NestedScrollView getScrollView();
}
