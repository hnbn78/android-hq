package com.desheng.app.toucai.panel;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.desheng.app.toucai.model.LotteryOpenNext;

/**
 * Created by lee on 2018/4/6.
 */

public interface LotteryPlayPanelBase {
    Context getContext();

    long getCurrentHit();


    String getCurrentModel();

    int getCurrentPoint();

    boolean hasCurrentRequestText();

    String getCurrentRequestText();

    String getCurrentPlayId();

    String getCurrentPlayShortName();

    String getCurrentPlayFullName();

    LotteryOpenNext getNextIssue();

    void setDefaultMenu();

    void setDefaultPlay();

    void showLoading();

    void hideLoading();

    void showMenu();

    void hideMenu();

    NestedScrollView getScrollView();


    void showPlay();

    void hidePlay();

    void showPlayAndHideLotteryMenu(String category, String selectedPlayCode);

    ViewGroup getBottomGroup();

    EditText getMoneyInput();

    TextView getBottomBuyBtn();

    int getLotteryId();

    int getCurrentNumTimes();

    double getAward();
}
