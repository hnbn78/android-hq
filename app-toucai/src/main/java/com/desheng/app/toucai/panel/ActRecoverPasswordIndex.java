package com.desheng.app.toucai.panel;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ab.util.Views;
import com.desheng.base.manager.UserManager;
import com.desheng.base.panel.ActWebX5;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;
import com.shark.tc.databinding.ActRecoverPasswordIndexBinding;

/**
 * Created by user on 2018/4/20.
 */

public class ActRecoverPasswordIndex extends AbAdvanceActivity<ActRecoverPasswordIndexBinding> implements View.OnClickListener{
    
    public static void launch(Context act, String phoneNum) {
        Intent itt = new Intent(act, ActRecoverPasswordIndex.class);
        itt.putExtra("phoneNum", phoneNum);
        act.startActivity(itt);
    }
    
    private boolean isPhoneBinded = false;
    
    @Override
    protected int getLayoutId() {
        return R.layout.act_recover_password_index;
    }
    
    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "找回密码");
        setToolbarLeftBtn(UIHelper.BACK_WHITE_CIRCLE_ARROW);
        setStatusBarTranslucentAndLightContentWithPadding();
        initView();
    }
    
    private void initView() {
        if(getIntent().getStringExtra("phoneNum").isEmpty()){
            B.tvPhone.setTextColor(Views.fromColors(R.color.textColorSecondary));
            B.ivPhoneArrow1.setVisibility(View.INVISIBLE);
            B.tvBindStatus.setVisibility(View.VISIBLE);
        }else{
            B.ivPhoneArrow1.setVisibility(View.VISIBLE);
            B.tvBindStatus.setVisibility(View.INVISIBLE);
            B.vgPhone.setOnClickListener(this);
        }
        B.vgUser.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vgUser:
                ActWebX5.launch(ActRecoverPasswordIndex.this, UserManager.getIns().getCustomServiceLink());
                break;
            case R.id.vgPhone:
                ActRecoverPasswordPhone.launch(ActRecoverPasswordIndex.this, getIntent().getStringExtra("phoneNum"));
                break;
        }
    }
    
}
