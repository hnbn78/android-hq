package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;

import com.ab.callback.AbCallback;
import com.ab.thread.ThreadCollector;
import com.ab.util.Toasts;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.R;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.UserBindStatus;
import com.desheng.base.panel.ActBindBankCard;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.FragmentsHelper;
import com.pearl.act.util.UIHelper;

/**
 * 取款页面
 * Created by user on 2018/4/20.
 */
public class ActWithdrawalsTouCai extends AbAdvanceActivity {
    
    public static void launch(final Activity act) {
        UserManager.getIns().checkUserBindStatus(new UserManager.IUserBindCheckCallback() {
            @Override
            public void onBefore() {
                ThreadCollector.getIns().runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.showProgressDialog(act, "");
                    }
                });
            }
        
            @Override
            public void onUserBindChecked(UserBindStatus status) {
                if(status.isBindWithdrawPassword()){
                    Intent itt = new Intent(act, ActWithdrawalsTouCai.class);
                    act.startActivity(itt);
                }else{


                    DialogsTouCai.showBindDialog(act,"您暂时未绑定银行卡","请您先绑定银行卡在实行操作","","",true,new AbCallback<Object>() {
                        @Override
                        public boolean callback(Object obj) {
                            ActBindBankCard.launch(act, true);
                            return true;
                        }
                    });
                }
            }
        
            @Override
            public void onUserBindCheckFailed(String msg) {
                Toasts.show(act, msg, false);
            }
        
            @Override
            public void onAfter() {
                ThreadCollector.getIns().postDelayOnUIThread(400, new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.hideProgressDialog(act);
                    }
                });
            }
        });
     
    }

    
    @Override
    protected int getLayoutId() {
        return R.layout.act_frag_layout;
    }
    
    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "取款");
        setStatusBarTranslucentAndLightContentWithPadding();
        
        initView();
    }
    
    private void initView (){
        FragmentsHelper.addFragment(getSupportFragmentManager(), R.id.fragContainer, new FragTabWithdrawTouCai(), null);
    }
    
    

}

   
