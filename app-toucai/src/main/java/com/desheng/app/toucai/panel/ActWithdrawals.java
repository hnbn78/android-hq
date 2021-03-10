package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.global.AbDevice;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.ArraysAndLists;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.ab.view.MaterialDialog;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.util.Utils;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.WithdrawInfo;
import com.desheng.base.panel.ActChangeFundPassword;
import com.desheng.base.panel.ActWeb;
import com.desheng.base.util.DeviceUtil;
import com.desheng.base.view.BankListPopupWindow;
import com.desheng.base.view.BottomInputFundPwdDialog;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Request;

/**
 * 取款页面
 * Created by user on 2018/4/20.
 */
public class ActWithdrawals extends AbAdvanceActivity implements BankListPopupWindow.OnItemSelectedListener {

    public static final String[] arrRefresh = {UserManager.EVENT_USER_LOGINED,
            UserManager.EVENT_USER_LOGOUTED};
    private TextView tv_balance, tv_bank_name, tv_withdraw_tips, btn_withdraw_all;
    private EditText et_withdraw_amount;
    private Button btn_withdraw;
    private RelativeLayout layout_withdraw_bank;
    private RelativeLayout vgToolbarContent;
    private ImageView iv_back, iv_custom_service, imgBankFace;

    private UserManager.EventReceiver receiver;
    private WithdrawInfo data;
    private ArrayList<String> bankNameList;
    private double balance;
    public static final int WITHDRAW_OK_RESULT_OK = 103;

    public static void launch(final Activity act) {
        simpleLaunch(act, ActWithdrawals.class);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.act_withdraw;
    }

    @Override
    protected void init() {

        vgToolbarContent = findViewById(R.id.vgToolbarContent);
        hideToolbar();
        ViewGroup.LayoutParams layoutParams = vgToolbarContent.getLayoutParams();
        vgToolbarContent.setLayoutParams(layoutParams);
        vgToolbarContent.setPadding(0, getStatusHeight(), 0, 0);
        setStatusBarTranslucentAndLightContent();

        tv_balance = findViewById(R.id.tv_balance);
        tv_bank_name = findViewById(R.id.tv_bank_name);
        imgBankFace = findViewById(R.id.imgBankFace);
        tv_withdraw_tips = findViewById(R.id.tv_withdraw_tips);
        et_withdraw_amount = findViewById(R.id.et_withdraw_amount);
        btn_withdraw_all = findViewById(R.id.btn_withdraw_all);
        btn_withdraw = findViewById(R.id.btn_withdraw);
        layout_withdraw_bank = findViewById(R.id.layout_withdraw_bank);
        iv_back = findViewById(R.id.iv_back);
        iv_custom_service = findViewById(R.id.iv_custom_service);

        btn_withdraw.setOnClickListener(this);
        btn_withdraw_all.setOnClickListener(this);
        layout_withdraw_bank.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_custom_service.setOnClickListener(this);

        bankNameList = new ArrayList<>();

        updateUserInfo();
        receiver = new UserManager.EventReceiver().register(ActWithdrawals.this, new UserManager.UserEventHandler() {
            @Override
            public void handleEvent(String eventName, Bundle bundle) {
                if (ArraysAndLists.findIndexWithEquals(eventName, arrRefresh) != -1) {
                    updateUserInfo();
                }
            }
        });

    }

    @Override
    public void onItemSelected(int position) {
        String bankname = bankNameList.get(position).substring(0, bankNameList.get(position).lastIndexOf(" "));
        String bankFace = UserManager.getIns().getBankFace(bankname);
        Views.loadImageAny(this, imgBankFace, bankFace);
        tv_bank_name.setText(bankNameList.get(position));
        bankid = "" + data.getAccountCardList().get(position).getId();
        bankCardName = data.getAccountCardList().get(position).getBankCardName();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == btn_withdraw) {
            String amount = et_withdraw_amount.getText().toString();
            if (Strs.isNotEmpty(amount)) {
                double money = Double.parseDouble(amount);
                if (money < minUnitAmount) {
                    Toasts.show("取现不能低于" + minUnitAmount + "元！", false);
                    return;
                }
                if (money > maxUnitAmount) {
                    Toasts.show("取现不能高于" + maxUnitAmount + "元！", false);
                    return;
                }
                showBottomPwdDialog();
            } else {
                Toasts.show("请输入金额");
            }

        } else if (v == btn_withdraw_all) {
            String allMoney = tv_balance.getText().toString();
            if (Strs.isNotEmpty(allMoney) && allMoney.contains(".")) {
                String amoun = allMoney.split("\\.")[0];
                et_withdraw_amount.setText(amoun);
                et_withdraw_amount.setSelection(amoun.length());
            }
        } else if (v == layout_withdraw_bank) {
            if (data == null || data.getAccountCardList() == null || data.getAccountCardList().size() == 0) {
                return;
            }
            bankNameList.clear();
            for (int i = 0; i < data.getAccountCardList().size(); i++) {
                bankNameList.add(data.getAccountCardList().get(i).getBankName() + " " + Strs.of(data.getAccountCardList().get(i).getBankCardId()));
            }

            DeviceUtil.hideInputKeyboard(ActWithdrawals.this);
            BankListPopupWindow bankChoosePopup = new BankListPopupWindow(this, AbDevice.SCREEN_WIDTH_PX, data.getAccountCardList(), this);
            bankChoosePopup.showAsDropDown(layout_withdraw_bank, 0, 0);
        } else if (v == iv_back) {
            onBackPressed();
        } else if (v == iv_custom_service) {
            ActWeb.launchCustomService(ActWithdrawals.this);
        }
    }

    public void updateUserInfo() {
        if (UserManager.getIns().isLogined()) {
            initBankInfo();
        } else {
            data = null;
            bankNameList.clear();
            tv_bank_name.setText("");
            et_withdraw_amount.setText("");
        }
    }

    private String bankid;
    private String bankCardName;
    private BottomInputFundPwdDialog dateDialog;

    private void showBottomPwdDialog() {
        BottomInputFundPwdDialog.Builder builder = new BottomInputFundPwdDialog.Builder(this);

        dateDialog = builder.create();
        builder.setOnConfirmClickListener(new BottomInputFundPwdDialog.OnConfirmListener() {
            @Override
            public void onClick(String pwd) {
//                if(!AbStrUtil.isNumber(pwd)){
//                    dateDialog.dismiss();
//                    showBindTip();
//                    return;
//                }

                withDrawals(bankid, et_withdraw_amount.getText().toString(), pwd);
            }
        });

        dateDialog.show();
    }

    private MaterialDialog dialog;

    private void showBindTip() {

        dialog = Dialogs.showTipDialog(ActWithdrawals.this, "温馨提示", "为了保障您的资金安全，现引用支付宝资金安全技术，需要您重新设定新的资金密码即可生效", "联系客服", "前往绑定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActWeb.launchCustomService(ActWithdrawals.this);
                dialog.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActChangeFundPassword.launch(ActWithdrawals.this);
                dialog.dismiss();
            }
        });

    }

    /**
     * 提现
     *
     * @return
     */
    private void withDrawals(String cardId, String amount, String withdrawPassword) {
        HttpAction.applyWithdrawal(UserManager.getIns().getAccount(), cardId, amount, withdrawPassword, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                tv_bank_name.post(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.showProgressDialog(ActWithdrawals.this, "");
                    }
                });
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0) {
                    if (dateDialog != null && dateDialog.isShowing())
                        dateDialog.dismiss();
                    balance -= Double.valueOf(amount);
                    tv_balance.setText(String.valueOf(amount));
                    UserManagerTouCai.getIns().setLotteryAvailableBalance(balance);
                    setResult(WITHDRAW_OK_RESULT_OK);
                    DialogsTouCai.showSuccessDialog(ActWithdrawals.this, 0, "提现成功", "完成");
                } else {
                    Toasts.show(msg, false);
                }

                return true;
            }

            @Override
            public void onAfter(int id) {
                tv_bank_name.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.hideProgressDialog(ActWithdrawals.this);
                    }
                }, 400);
            }

        });
    }

    private int maxUnitAmount, minUnitAmount;

    private void initBankInfo() {
        HttpAction.prepareWithdraw(this, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {

            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", WithdrawInfo.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    data = getField(extra, "data", null);
                    if (data.getAccountCardList() != null && data.getAccountCardList().size() > 0) {
                        WithdrawInfo.AccountCardListBean cardListBean = data.getAccountCardList().get(0);
                        String defaultCard = Strs.of(cardListBean.getBankName()) + cardListBean.getBankCardId();
                        tv_bank_name.setText(defaultCard);
                        String bankFace = UserManager.getIns().getBankFace(data.getAccountCardList().get(0).getBankName());
                        Views.loadImageAny(ActWithdrawals.this, imgBankFace, bankFace);
//                        tv_bank_name.setCompoundDrawablesRelativeWithIntrinsicBounds(data.getAccountCardList().get(0).);
                        bankid = "" + data.getAccountCardList().get(0).getId();
                        bankCardName = data.getAccountCardList().get(0).getBankCardName();

//                        tvCardNum.setText(Strs.of(data.getAccountCardList().get(0).getBankCardId()));
//                        tvUserName.setText(Strs.of(data.getAccountCardList().get(0).getBankCardName()));
//                        tv_withdraw_amount.setText(Strs.of(data.getAccountCardList().get(0).getLockTime()));
                    }

                    /*if(null!=data.getMyAccountStatus()){
                        tv_withdraw_amount.setText(""+data.getMyAccountStatus().getAvailableBalance());
                        layout_withdraw_amount.setVisibility(View.VISIBLE);
                    }else{
                        layout_withdraw_amount.setVisibility(View.GONE);
                    }*/

                    WithdrawInfo.WithdrawConfigBean withdrawConfig = data.getWithdrawConfig();
                    if (withdrawConfig != null) {
                        maxUnitAmount = withdrawConfig.getMaxUnitAmount();
                        minUnitAmount = withdrawConfig.getMinUnitAmount();
                        String str = String.format("取款时间  %s，单笔限额：%s-%s元，每日限制次数：%s",
                                withdrawConfig.getServiceTime(),
                                Strs.of(withdrawConfig.getMinUnitAmount()),
                                Strs.of(withdrawConfig.getMaxUnitAmount()),
                                Strs.of(withdrawConfig.getMaxDailyCount())
                        );
                        tv_withdraw_tips.setText(str);

//                        String tips2=String.format("2.当天前 %s 次提款免手续费，当天提款超过 %s 次每次按提款金额 %s 收取手续费，最低 %s 元，最高 %s 元。",
//                                Strs.of(withdrawConfig.getFreeDailyCount()),
//                                Strs.of(withdrawConfig.getFreeDailyCount()),
//                                withdrawConfig.getFeeRate()*100+"%",
//                                Strs.of(withdrawConfig.getDayWithdrawRateMin()),
//                                Strs.of(withdrawConfig.getDayWithdrawRateMax())
//                        );
//                        tvTip2.setText(tips2);

                    }

                    WithdrawInfo.MyAccountStatusBean myAccountStatusBean = data.getMyAccountStatus();
                    if (null != myAccountStatusBean) {
                        balance = Utils.getDoubleNotRound2(myAccountStatusBean.getAvailableBalance());
                        tv_balance.setText(Utils.getDoubleNotRound2Str(myAccountStatusBean.getAvailableBalance()));
                    }

                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                return true;
            }

            @Override
            public void onAfter(int id) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            receiver.unregister(this);
        }
    }
}

   
