package com.desheng.base.panel;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.ab.callback.AbCallback;
import com.ab.thread.ThreadCollector;
import com.ab.util.Dialogs;
import com.ab.util.Toasts;
import com.ab.view.MaterialDialog;
import com.desheng.base.R;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.UserBindStatus;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.FragmentsHelper;
import com.pearl.act.util.UIHelper;

/**
 * 取款页面
 * Created by user on 2018/4/20.
 */
public class ActWithdrawals extends AbAdvanceActivity {

    private static MaterialDialog materialDialog;

    public static void launch(final Activity act, final int type) {
        UserManager.getIns().checkUserBindStatus(new UserManager.IUserBindCheckCallback() {
            @Override
            public void onBefore() {
                ThreadCollector.getIns().runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(act, "");
                    }
                });
            }

            @Override
            public void onUserBindChecked(UserBindStatus status) {
                if (status.isBindCard() && status.isBindWithdrawPassword()) {
                    Intent itt = new Intent(act, ActWithdrawals.class);
                    itt.putExtra("withdrawType", type);
                    act.startActivity(itt);
                } else {
                    showCheckBankDialog(act, new AbCallback<Object>() {
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
                        Dialogs.hideProgressDialog(act);
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
        int withdrawType = getIntent().getIntExtra("withdrawType", 0);
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), withdrawType == 1 ? "VIP 提现" : "普通提现");
        setStatusBarTranslucentAndLightContentWithPadding();
        initView(withdrawType);
    }

    private void initView(int withdrawType) {
        FragmentsHelper.addFragment(getSupportFragmentManager(), R.id.fragContainer, FragTabWithdraw.newInstance(withdrawType), null);
    }


    public static void showCheckBankDialog(Activity act, final AbCallback<Object> callback) {
        if (materialDialog != null) {
            materialDialog.dismiss();
        }
        materialDialog = new MaterialDialog(act);
        materialDialog.setMessage("亲，先绑定取款人");
        materialDialog.setCanceledOnTouchOutside(true);
        materialDialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                callback.callback(null);
            }
        });
        materialDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();

    }
}

   
