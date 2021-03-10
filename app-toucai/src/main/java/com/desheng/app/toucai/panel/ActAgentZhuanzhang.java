package com.desheng.app.toucai.panel;

import android.view.View;
import android.widget.TextView;

import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

public class ActAgentZhuanzhang extends AbAdvanceActivity {

    private TextView tv_title;

    @Override
    protected int getLayoutId() {
        return R.layout.act_agent_zhuanzhang;
    }

    @Override
    protected void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();
        tv_title = ((TextView) headRoot.findViewById(R.id.tv_title));
        tv_title.setText("转账");
        initView();
    }

    private void initView() {

    }
}
