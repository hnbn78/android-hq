package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.desheng.base.panel.ActWeb;
import com.desheng.base.util.ResUtil;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

public class ActHelpAndFeedback extends AbAdvanceActivity {


    private TextView tvHowModify,tvHowFindBack,tvHowEditInfo,tvHowBind;

    private LinearLayout layout_all_question,layout_feedback,layout_kefu;

    public static void launcher(Activity activity){
        activity.startActivity(new Intent(activity,ActHelpAndFeedback.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_help_and_feedback;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), ResUtil.getString(R.string.help_and_feedback));
        setStatusBarTranslucentAndLightContentWithPadding();

        tvHowModify = findViewById(R.id.tvHowModify);
        tvHowFindBack = findViewById(R.id.tvHowFindBack);
        tvHowEditInfo = findViewById(R.id.tvHowEditInfo);
        tvHowBind = findViewById(R.id.tvHowBind);
        layout_all_question = findViewById(R.id.layout_all_question);
        layout_feedback = findViewById(R.id.layout_feedback);
        layout_kefu = findViewById(R.id.layout_kefu);

        tvHowModify.setOnClickListener(this);
        tvHowFindBack.setOnClickListener(this);
        tvHowEditInfo.setOnClickListener(this);
        tvHowBind.setOnClickListener(this);
        layout_all_question.setOnClickListener(this);
        layout_feedback.setOnClickListener(this);
        layout_kefu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(tvHowBind==v){

        }else if(tvHowModify==v){

        }else if(tvHowFindBack==v){

        }else if(tvHowEditInfo==v){

        }else if(tvHowBind==v){

        }else if(layout_all_question==v){
            ActAllQuestion.launcher(this);
        }else if(layout_feedback==v){
            ActFeedBackAward.launcher(this);
        }else if(layout_kefu==v){
            ActWeb.launchCustomService(this);
        }
    }
}
