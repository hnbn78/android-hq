package com.desheng.app.toucai.panel;

import android.os.CountDownTimer;

import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;
import com.shark.tc.databinding.ActRegistTouCaiSuccessBinding;

public class ActRegistTouCaiSuccess extends AbAdvanceActivity<ActRegistTouCaiSuccessBinding> {
    
    public static void launchAndFinish(AbAdvanceActivity act){
        act.launchOtherAndFinish(ActRegistTouCaiSuccess.class);
    }
    
    private CountDownTimer countDown;
    
    @Override
    protected int getLayoutId() {
        return R.layout.act_regist_tou_cai_success;
    }
    
    @Override
    protected void init() {
        hideToolbar();
        setStatusBarTranslucentAndLightContent();
        B.tvReturn.setOnClickListener((view) -> {
            toMain();
        });
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        countDown = new CountDownTimer(3000, 100) {
        
            @Override
            public void onTick(long millisUntilFinished) {
                long cnt = millisUntilFinished / 1000 + 1;
                B.tvCountdown.setText(String.format("%ds后自动转入首页", cnt));
            }
        
            @Override
            public void onFinish() {
                toMain();
            }
        }.start();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        if (countDown != null) {
            countDown.cancel();
        }
    }
    
    private void toMain() {
        ActMain.mainKillSwitch = false;
        launchOther(ActMain.class);
    }
}
