package com.desheng.app.toucai.panel;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.ab.util.Toasts;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

/**
 * Created by user on 2018/4/20.
 */

public class ActFundsManagment extends AbAdvanceActivity {
    
    private View vDevider;
    
    public static void launch(Context act, boolean isShowTrans) {
        Intent itt = new Intent(act, ActFundsManagment.class);
        itt.putExtra("isShowTrans", isShowTrans);
        act.startActivity(itt);
    }
    
    
    private RelativeLayout rlDeposit, rlWithDrawals, rlTransfer;
    
    @Override
    protected int getLayoutId() {
        return R.layout.act_funds_managment;
    }
    
    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "资金管理");
        setStatusBarTranslucentAndLightContentWithPadding();
        initView();
    }
    
    
    private void initView() {
        rlDeposit = (RelativeLayout) findViewById(R.id.rlBindcard);
        rlDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ActFundsManagment.this, ActDeposit.class);
                startActivity(intent);
            }
        });
        rlWithDrawals = (RelativeLayout) findViewById(R.id.rlUserLayout);
        rlWithDrawals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActWithdrawals.launch(ActFundsManagment.this);
            }
        });
        rlTransfer = (RelativeLayout) findViewById(R.id.rlBindNumberLayout);
        vDevider = findViewById(R.id.vDevider);
        boolean isShowTrans = getIntent().getBooleanExtra("isShowTrans", false);
        if (isShowTrans) {
            rlTransfer.setVisibility(View.VISIBLE);
            vDevider.setVisibility(View.VISIBLE);
        }else{
            rlTransfer.setVisibility(View.INVISIBLE);
            vDevider.setVisibility(View.INVISIBLE);
        }
        rlTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasts.show(ActFundsManagment.this, "即将推出使用，敬请期待");
            }
        });
        
    }
    
   
}
