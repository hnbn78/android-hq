package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.desheng.base.util.ResUtil;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

public class ActAllQuestion extends AbAdvanceActivity {


    private TextView tvAccount,tvDeposit,tvLottery;

    public static void launcher(Activity activity){
        activity.startActivity(new Intent(activity,ActAllQuestion.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_all_question;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), ResUtil.getString(R.string.help_and_feedback));
        setStatusBarTranslucentAndLightContentWithPadding();

        tvAccount = findViewById(R.id.tvAccount);
        tvDeposit = findViewById(R.id.tvDeposit);
        tvLottery = findViewById(R.id.tvLottery);


        tvAccount.setOnClickListener(this);
        tvDeposit.setOnClickListener(this);
        tvLottery.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(tvAccount==v){

        }else if(tvDeposit==v){

        }else if(tvLottery==v){

        }
    }
}
