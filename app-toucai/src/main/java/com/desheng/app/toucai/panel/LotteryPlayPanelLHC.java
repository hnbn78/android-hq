package com.desheng.app.toucai.panel;

import android.view.ViewGroup;
import android.widget.EditText;

import com.desheng.app.toucai.view.PlaySnakeViewLHC;
import com.desheng.base.model.LotteryPlayLHCUI;

/**
 * Created by lee on 2018/4/6.
 */

public interface LotteryPlayPanelLHC extends LotteryPlayPanelBase {

    void setCurrentUserPlayInfo(Object userPlayInfo);

    BaseLotteryPlayFragmentLHC getCurrentPlay();

    LotteryPlayLHCUI getSelectedLotteryPlay();

    PlaySnakeViewLHC getSnakeBar();

    ViewGroup getQuickPlay();

    EditText getMoneyInput();
}
