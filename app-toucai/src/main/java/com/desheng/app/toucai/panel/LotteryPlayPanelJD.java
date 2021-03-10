package com.desheng.app.toucai.panel;

import com.desheng.app.toucai.view.PlaySnakeViewJD;
import com.desheng.base.model.LotteryPlayJD;

/**
 * Created by lee on 2018/4/6.
 */

public interface LotteryPlayPanelJD extends LotteryPlayPanelBase{

    void setCurrentUserPlayInfo(Object userPlayInfo);

    BaseLotteryPlayFragmentJD getCurrentPlay();

    LotteryPlayJD getSelectedLotteryPlay();

    PlaySnakeViewJD getSnakeBar();
}
