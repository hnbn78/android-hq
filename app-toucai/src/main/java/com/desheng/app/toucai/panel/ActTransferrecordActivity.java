package com.desheng.app.toucai.panel;

import android.widget.ImageView;

import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

/**
 * 第三方转账记录
 */


public class ActTransferrecordActivity extends AbAdvanceActivity {
     private ImageView Back_icon;




    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "转账记录");
        setStatusBarTranslucentAndLightContentWithPadding();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_transferrecord;
    }


    private void initView (){



  }


}
