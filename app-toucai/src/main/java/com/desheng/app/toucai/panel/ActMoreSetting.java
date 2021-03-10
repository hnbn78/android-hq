package com.desheng.app.toucai.panel;


import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.ab.util.Strs;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.PersonInfo;
import com.desheng.base.util.ResUtil;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

public class ActMoreSetting extends AbAdvanceActivity {

    private TextView tvQQ,tvWechat;

    public static void launcher(Activity activity, PersonInfo info) {
        Intent intent = new Intent(activity, ActMoreSetting.class);
        intent.putExtra("info", info);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_more_setting;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), ResUtil.getString(R.string.more_setting));
        setStatusBarTranslucentAndLightContentWithPadding();

        tvQQ = findViewById(R.id.tvQQ);
        tvWechat = findViewById(R.id.tvWechat);
    }

    public void onAddClick(View v) {
        switch (v.getId()) {
            case R.id.tvQQ:
                ActInput.launcher(this, ActInput.QQ);
                break;
            case R.id.tvWechat:
                ActInput.launcher(this, ActInput.WECHAT);
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(Strs.isNotEmpty(UserManager.getIns().getQQ())){
            tvQQ.setText(UserManager.getIns().getQQ());
            tvQQ.setEnabled(false);
        }else{
            tvQQ.setEnabled(true);
        }

        if(Strs.isNotEmpty(UserManager.getIns().getWeChatId())){
            tvWechat.setText(UserManager.getIns().getWeChatId());
            tvWechat.setEnabled(false);
        }else{
            tvWechat.setEnabled(true);
        }
    }
}
