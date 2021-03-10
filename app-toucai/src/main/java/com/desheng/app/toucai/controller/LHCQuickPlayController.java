package com.desheng.app.toucai.controller;

import android.view.ViewGroup;

import com.desheng.app.toucai.panel.BaseLotteryPlayFragmentLHC;
import com.shark.tc.R;

public abstract class LHCQuickPlayController {
    ViewGroup layout;
    ViewGroup content;
    BaseLotteryPlayFragmentLHC frag;

    public LHCQuickPlayController(BaseLotteryPlayFragmentLHC fragment) {
        this.frag = fragment;
    }

    public void onQuickPlayShow() {}
    public void onQuickPlayHide() {}

    public abstract void onCancel();

    public abstract boolean onConfirm();

    public void setLayout(ViewGroup layout) {
        this.layout = layout;
        content = layout.findViewById(R.id.fl_quick_play_content);

        buildUI(content);
    }

    public abstract void buildUI(ViewGroup parent);
}
