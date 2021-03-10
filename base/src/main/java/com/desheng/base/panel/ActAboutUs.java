package com.desheng.base.panel;

import com.desheng.base.R;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;

/**
 * 关于我们的界面
 */

public class ActAboutUs extends AbAdvanceActivity {
    
    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "关于我们");
        setStatusBarTranslucentAndLightContentWithPadding();
        
        initView();
    }
    
    @Override
    protected int getLayoutId() {
        return R.layout.act_about_us;
    }
    
    private void initView() {
    
    }
    
}
