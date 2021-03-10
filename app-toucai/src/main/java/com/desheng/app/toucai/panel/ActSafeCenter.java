package com.desheng.app.toucai.panel;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Toasts;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.BankCardInfo;
import com.desheng.base.model.UserBindStatus;
import com.desheng.base.panel.ActBindBankCard;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;
import com.shark.tc.databinding.ActSafeCenterBinding;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Request;

/**
 * Created by user on 2018/4/20.
 */

public class ActSafeCenter extends AbAdvanceActivity<ActSafeCenterBinding> implements View.OnClickListener{
    
    public static void launch(Context act) {
        Intent itt = new Intent(act, ActSafeCenter.class);
        act.startActivity(itt);
    }
    
    private int cardCount = 0;
    
    @Override
    protected int getLayoutId() {
        return R.layout.act_safe_center;
    }
    
    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "安全中心");
        setStatusBarTranslucentAndLightContentWithPadding();
        initView();
        initData();
        
    }
    
    private void initView() {
        getBinding().vgBindCard.setOnClickListener(this);
        getBinding().vgUser.setOnClickListener(this);
        getBinding().vgPhone.setOnClickListener(this);
    }
    
    private void initData() {
        HttpAction.getUserBankCardList(ActSafeCenter.this, new AbHttpResult(){
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                getBinding().vgBindCard.post(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.showProgressDialog(ActSafeCenter.this, "");
                    }
                });
            }
    
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<BankCardInfo>>(){}.getType());
            }
            
            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getFieldObject(extra, "data", null) != null) {
                    ArrayList<BankCardInfo> listCard = (ArrayList<BankCardInfo>) getFieldObject(extra, "data", null);
                    cardCount = listCard.size();
                }
                return true;
            }
    
            @Override
            public void onAfter(int id) {
                getBinding().vgBindCard.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.hideProgressDialog(ActSafeCenter.this);
                    }
                }, 400);
            }
        });
    }
    
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vgBindCard:
                if (cardCount >= 5) {
                    Toasts.show(ActSafeCenter.this, "银行卡最多添加5张!", false);
                    return;
                }
                UserManagerTouCai.getIns().checkUserBindStatus(new UserManager.IUserBindCheckCallback() {
                    @Override
                    public void onBefore() {
                        getBinding().vgBindCard.post(new Runnable() {
                            @Override
                            public void run() {
                                DialogsTouCai.showProgressDialog(ActSafeCenter.this, "");
                            }
                        });
                    }
    
                    @Override
                    public void onUserBindChecked(UserBindStatus status) {
                        ActBindBankCard.launch(ActSafeCenter.this, !status.isBindCard());
                    }
    
                    @Override
                    public void onUserBindCheckFailed(String msg) {
                        Toasts.show(ActSafeCenter.this, msg, false);
                    }
    
                    @Override
                    public void onAfter() {
                        getBinding().vgBindCard.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                DialogsTouCai.hideProgressDialog(ActSafeCenter.this);
                            }
                        }, 400);
                    }
                });
                break;
            case R.id.vgUser:
                Toasts.show(ActSafeCenter.this, "即将推出, 敬请期待", true);
                break;
            case R.id.vgPhone:
                Toasts.show(ActSafeCenter.this, "即将推出, 敬请期待", true);
                /*
                UserManagerTouCai.getIns().checkUserBindStatus(new UserManager.IUserBindCheckCallback() {
                    @Override
                    public void onBefore() {
                        getBinding().vgBindCard.post(new Runnable() {
                            @Override
                            public void run() {
                                DialogsTouCai.showProgressDialog(ActSafeCenter.this, "");
                            }
                        });
                    }
        
                    @Override
                    public void onUserBindChecked(UserBindStatus status) {
                        if(status.isBindCellphone()){
                            Toasts.show(ActSafeCenter.this, "手机号已绑定", true);
                        }else{
                            ActBindPhone.launchForThird(ActSafeCenter.this);
                        }
                    }
        
                    @Override
                    public void onUserBindCheckFailed(String msg) {
                        Toasts.show(ActSafeCenter.this, msg, false);
                    }
        
                    @Override
                    public void onAfter() {
                        getBinding().vgBindCard.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                DialogsTouCai.hideProgressDialog(ActSafeCenter.this);
                            }
                        }, 400);
                    }
                });*/
                break;
        }
    }
}
