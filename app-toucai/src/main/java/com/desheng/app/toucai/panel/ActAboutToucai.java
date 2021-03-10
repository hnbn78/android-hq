package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

public class ActAboutToucai extends AbAdvanceActivity {

    private TextView tvAbout;
    private TextView tvFunctionIntro;

    public static void launcher(Activity activity) {
        activity.startActivity(new Intent(activity, ActAboutToucai.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_about;
    }

    @Override
    protected void init() {
        setStatusBarTranslucentAndDarkContent();
        hideToolbar();
        findViewById(R.id.imgBack).setOnClickListener(v -> finish());
        tvAbout = findViewById(R.id.tvAbout);
        tvFunctionIntro = findViewById(R.id.tvFunctionIntro);
        tvFunctionIntro.setOnClickListener(v -> ActUpdateContentList.launch(this));
    }
}
