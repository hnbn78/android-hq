package com.desheng.base.panel;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.global.Config;
import com.ab.global.Global;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.ArraysAndLists;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.airsaid.pickerviewlibrary.OptionsPickerView;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.WithdrawInfo;
import com.desheng.base.view.TipDialog;
import com.pearl.act.base.AbBaseFragment;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Request;

import static com.desheng.base.manager.UserManager.EVENT_USER_INFO_UNPDATED;

/**
 * 取款
 * Created by lee on 2018/4/11.
 */
public class FragTabWithdraw extends AbBaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String[] arrRefresh = {UserManager.EVENT_USER_LOGINED,
            UserManager.EVENT_USER_LOGOUTED};
    private TextView tvBank;
    private WithdrawInfo data;
    private ArrayList<String> bankNameList;
    private TextView tvCardNum;
    private TextView tvUserName;
    private Button btnConfirm;
    private UserManager.EventReceiver receiver;
    private SwipeRefreshLayout srlRefresh;
    private EditText etAmount;
    private EditText etPassword;
    private TextView tv_withdraw_amount;
    private TextView tvTip, tvTip2;
    private RelativeLayout layout_withdraw_amount;
    private double totle;
    private int mWithdrawType;
    private WithdrawInfo.WithdrawConfigBean withdrawConfig;

    public static FragTabWithdraw newInstance(int withdrawType) {
        FragTabWithdraw fragment = new FragTabWithdraw();
        Bundle args = new Bundle();
        args.putInt("withdraw_type", withdrawType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_withdraws;
    }

    @Override
    public void init(final View root) {
        mWithdrawType = getArguments().getInt("withdraw_type");
        srlRefresh = (SwipeRefreshLayout) root.findViewById(R.id.srlWithdrawRefresh);
        srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);

        tvBank = (TextView) root.findViewById(R.id.tvBank);
        tvCardNum = (TextView) root.findViewById(R.id.tvCardNum);
        tvUserName = (TextView) root.findViewById(R.id.tvUserName);
        tvTip2 = (TextView) root.findViewById(R.id.tvTip2);
        tv_withdraw_amount = (TextView) root.findViewById(R.id.tv_withdraw_amount);

        tvBank = root.findViewById(R.id.tvBank);
        tvCardNum = root.findViewById(R.id.tvCardNum);
        tvUserName = root.findViewById(R.id.tvUserName);
        tvTip2 = root.findViewById(R.id.tvTip2);
        tv_withdraw_amount = root.findViewById(R.id.tv_withdraw_amount);
        RelativeLayout rlSelect = (RelativeLayout) root.findViewById(R.id.second__layout);
        bankNameList = new ArrayList<>();
        rlSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data == null || data.getAccountCardList() == null || data.getAccountCardList().size() == 0) {
                    return;
                }
                bankNameList.clear();
                for (int i = 0; i < data.getAccountCardList().size(); i++) {
                    bankNameList.add(data.getAccountCardList().get(i).getBankName() + " " + Strs.of(data.getAccountCardList().get(i).getBankCardId()));
                }

                OptionsPickerView pvOptions = new OptionsPickerView(getActivity());
                pvOptions.setTitle("选择银行");
                pvOptions.setCancelText("取消");
                pvOptions.setCancelTextColor(Color.GRAY);
                pvOptions.setCancelTextSize(14f);
                pvOptions.setSubmitText("确定");
                pvOptions.setSubmitTextColor(Color.BLACK);
                pvOptions.setSubmitTextSize(14f);
                pvOptions.setHeadBackgroundColor(Color.WHITE);

                pvOptions.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int option1, int option2, int option3) {
                        //返回的分别是三个级别的选中位置

                        tvBank.setText(Strs.of(data.getAccountCardList().get(option1).getBankName()));
                        tvCardNum.setText(Strs.of(data.getAccountCardList().get(option1).getBankCardId()));
                        tvUserName.setText(Strs.of(data.getAccountCardList().get(option1).getBankCardName()));
                    }
                });
                pvOptions.setPicker(bankNameList);
                pvOptions.show();

            }
        });


        etAmount = (EditText) root.findViewById(R.id.etAmount);
        etPassword = (EditText) root.findViewById(R.id.etPassword);
        btnConfirm = (Button) root.findViewById(R.id.btnConfirm);
        layout_withdraw_amount = (RelativeLayout) root.findViewById(R.id.layout_withdraw_amount);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bank = Views.getText((TextView) root.findViewById(R.id.tvBank));
                if (Strs.isEmpty(bank)) {
                    Toasts.show(getActivity(), "请选择银行", false);
                    return;
                }
                if (data == null || data.getAccountCardList() == null) {
                    return;
                }

                String bankId = "";
                int size = data.getAccountCardList().size();
                for (int i = 0; i < size; i++) {
                    if (data.getAccountCardList().get(i).getBankName().equals(bank)) {
                        bankId = Strs.of(data.getAccountCardList().get(i).getId());
                        break;
                    }
                }

                String amount = Views.getText(etAmount);
                if (Nums.parse(amount, 0) <= 0) {
                    Toasts.show(getActivity(), "请输入正确数量", false);
                    return;
                }
                String password = Views.getText(etPassword);
                if (Strs.isEmpty(password)) {
                    Toasts.show(getActivity(), "请输入当前密码", false);
                    return;
                }
                withDrawals(bankId, amount, password);
            }
        });

        tvTip = (TextView) root.findViewById(R.id.tvTip);

        receiver = new UserManager.EventReceiver().register(getActivity(), new UserManager.UserEventHandler() {
            @Override
            public void handleEvent(String eventName, Bundle bundle) {
                if (ArraysAndLists.findIndexWithEquals(eventName, arrRefresh) != -1) {
                    updateUserInfo();
                }
            }
        });

        if (mWithdrawType == 1) {
            etAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (withdrawConfig == null) {
                        return;
                    }

                    if (Strs.isNotEmpty(s.toString())) {
                        int money = Integer.parseInt(s.toString());
                        double vipFeeRate = withdrawConfig.getVipFeeRate();
                        tvTip2.setText("2. 提现手续费：" + vipFeeRate * 100 +
                                "%，提款金额：" + money + "元，取款到账金额：" + (money - money * vipFeeRate) + "元，手续费金额：" + money * vipFeeRate + "元");
                    } else {
                        String tips2Vip = String.format("2.提现手续费：%s ，提款金额：%s 元，取款到账金额：%s 元，手续费金额：%s 元。",
                                "xx" + "%", "xx", "xx", "xx");
                        tvTip2.setText(tips2Vip);
                    }
                }
            });
        } else {
            tvTip2.setText("2.当天前3次提款免手续费，当天提款超过3次每次按提款金额1%收取手续费，最低1元，最高25元。");
        }

    }


    @Override
    public void onShow() {
        super.onShow();
        updateUserInfo();
    }

    @Override
    public void onRefresh() {
        updateUserInfo();
    }

    public void updateUserInfo() {
        if (UserManager.getIns().isLogined()) {
            initBankInfo();
        } else {
            data = null;
            bankNameList.clear();
            tvBank.setText("");
            tvCardNum.setText("");
            tvUserName.setText("");
            etAmount.setText("");
            etPassword.setText("");
        }
    }


    /**
     * 提现
     *
     * @return
     */
    private void withDrawals(String cardId, final String amount, String withdrawPassword) {
        HttpAction.applyWithdrawal(UserManager.getIns().getAccount(), cardId, amount, withdrawPassword, mWithdrawType, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                tvBank.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(getActivity(), "");
                    }
                });
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    Toasts.show(getActivity(), "提交成功, 请等待客服处理!", true);
                    totle -= Double.valueOf(amount);
                    tv_withdraw_amount.setText(Nums.formatDecimal(totle, 2));

                    UserManager.getIns().setLotteryAvailableBalance(totle);
                    //更新tabPerson
                    UserManager.getIns().sendBroadcaset(Global.app, EVENT_USER_INFO_UNPDATED, null);
                    //是否要改冻结资金？？？
                } else {
                    com.kongzue.dialog.v3.TipDialog.show(((AppCompatActivity) getActivity()), msg, com.kongzue.dialog.v3.TipDialog.TYPE.ERROR);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(getActivity(), content, false);
                return super.onError(status, content);
            }

            @Override
            public void onAfter(int id) {
                tvBank.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideProgressDialog(getActivity());
                        etPassword.setText("");
                    }
                }, 400);
            }

        });


    }

    private void initBankInfo() {
        HttpAction.prepareWithdraw(getContext(), new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                srlRefresh.post(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(true);
                    }
                });
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
                        tvBank.setText(Strs.of(data.getAccountCardList().get(0).getBankName()));
                        tvCardNum.setText(Strs.of(data.getAccountCardList().get(0).getBankCardId()));
                        tvUserName.setText(Strs.of(data.getAccountCardList().get(0).getBankCardName()));
//                        tv_withdraw_amount.setText(Strs.of(data.getAccountCardList().get(0).getLockTime()));
                    }
                    totle = data.getMyAccountStatus().getTotalBalance();
                    if (null != data.getMyAccountStatus()) {
                        String balance = Nums.formatDecimal(data.getMyAccountStatus().getAvailableBalance(), 3);
                        tv_withdraw_amount.setText(balance);
                        layout_withdraw_amount.setVisibility(View.VISIBLE);
                    } else {
                        layout_withdraw_amount.setVisibility(View.GONE);
                    }

                    withdrawConfig = data.getWithdrawConfig();
                    resetConfigText();
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                return true;
            }

            @Override
            public void onAfter(int id) {
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(false);
                    }
                }, 400);
            }
        });
    }

    private void resetConfigText() {
        if (withdrawConfig == null) return;
        String str = String.format("1.取款时间  %s，单笔限额：%s-%s元，每日限制次数：%s",
                withdrawConfig.getServiceTime(),
                Strs.of(withdrawConfig.getMinUnitAmount()),
                Strs.of(withdrawConfig.getMaxUnitAmount()),
                Strs.of(mWithdrawType == 1 ? withdrawConfig.getMaxVipDailyCount() : withdrawConfig.getMaxDailyCount())
        );
        tvTip.setText(str);
        etAmount.setHint(String.format("限额%s - %s元", Strs.of(withdrawConfig.getMinUnitAmount()), Strs.of(withdrawConfig.getMaxUnitAmount())));

//        String tips2 = String.format("2.当天前 %s 次提款免手续费，当天提款超过 %s 次每次按提款金额 %s 收取手续费，最低 %s 元，最高 %s 元。",
//                Strs.of(withdrawConfig.getFreeDailyCount()),
//                Strs.of(withdrawConfig.getFreeDailyCount()),
//                withdrawConfig.getFeeRate() * 100 + "%",
//                Strs.of(withdrawConfig.getDayWithdrawRateMin()),
//                Strs.of(withdrawConfig.getDayWithdrawRateMax())
//        );

        String tips2 = String.format("2.每天提现 %s 次，不收取手续费。",
                Strs.of(withdrawConfig.getFreeDailyCount()));

        String tips2Vip = String.format("2. 提现手续费：%s ，提款金额：%s 元，提款到账金额：%s 元，手续费金额：%s 元。",
                "xx" + "%",
                "xx",
                "xx",
                "xx"
        );
        tvTip2.setText(mWithdrawType == 1 ? tips2Vip : tips2);
        tvTip2.setTextColor(mWithdrawType == 1 ? Color.RED : Color.BLACK);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            receiver.unregister(getActivity());
        }
    }
}
