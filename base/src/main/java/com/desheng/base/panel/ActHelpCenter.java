package com.desheng.base.panel;

import android.widget.ImageView;

import com.desheng.base.R;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;

/**
 * Created by user on 2018/3/21.
 */

public class ActHelpCenter extends AbAdvanceActivity {
    private ImageView Back_icon;

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "帮助中心");
        setStatusBarTranslucentAndLightContentWithPadding();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_help_center;
    }


}
