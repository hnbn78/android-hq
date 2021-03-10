package com.desheng.app.toucai.panel;

import com.desheng.app.toucai.view.PlayFootView;
import com.desheng.app.toucai.view.PlaySnakeView;
import com.desheng.base.model.LotteryPlayUserInfo;

/**
 * Created by lee on 2018/4/6.
 */

public interface LotteryPlayPanel extends LotteryPlayPanelBase {

    void setCurrentUserPlayInfo(Object userPlayInfo);

    LotteryPlayUserInfo getLotteryPlayUserInfo();

    BaseLotteryPlayFragment getCurrentPlay();

    PlaySnakeView getSnakeBar();

    PlayFootView getFootView();
}
