package com.desheng.app.toucai.panel;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.ArraysAndLists;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.airsaid.pickerviewlibrary.OptionsPickerView;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.WithdrawInfo;
import com.pearl.act.base.AbBaseFragment;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Request;

/**
 * 取款
 * Created by lee on 2018/4/11.
 */
public class FragTabWithdrawTouCai extends AbBaseFragment implements SwipeRefreshLayout.OnRefreshListener{
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
    //private TextView tv_withdraw_amount;
    private TextView tvTip,tvTip2;
    private RelativeLayout layout_withdraw_amount;
    
    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_withdraws_toucai;
    }


    private void showBankList() {

        OptionsPickerView pvOptions = new OptionsPickerView(getActivity());
        pvOptions.setTitle("");
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


                tvBank.setText(Strs.of(data.getAccountCardList().get(option1).getBankName()));
                tvCardNum.setText(Strs.of(data.getAccountCardList().get(option1).getBankCardId()));
                tvUserName.setText(Strs.of(data.getAccountCardList().get(option1).getBankCardName()));

            }
        });
        pvOptions.setPicker(bankNameList);
        pvOptions.show();

    }
    @Override
    public void init(final View root) {
        srlRefresh = (SwipeRefreshLayout) root.findViewById(R.id.srlWithdrawRefresh);
        srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);
        
        tvBank = (TextView) root.findViewById(R.id.tvBank);
        tvCardNum = (TextView) root.findViewById(R.id.tvCardNum);
        tvUserName = (TextView) root.findViewById(R.id.tvUserName);
        tvTip2 = (TextView) root.findViewById(R.id.tvTip2);
        //tv_withdraw_amount = (TextView) root.findViewById(R.id.tv_withdraw_amount);
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
                showBankList();
            }
        });
        
        
        etAmount = (EditText) root.findViewById(R.id.etAmount);
        etPassword = (EditText) root.findViewById(R.id.etPassword);
        btnConfirm = (Button) root.findViewById(R.id.btnConfirm);
        layout_withdraw_amount = (RelativeLayout)root.findViewById(R.id.layout_withdraw_amount);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bank = Views.getText((TextView) root.findViewById(R.id.tvBank));
                if (Strs.isEmpty(bank)) {
                    Toasts.show(getActivity(), "请选择银行", false);
                    return;
                }
                String bankId = "";
                for (int i = 0; i < data.getAccountCardList().size(); i++) {
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
    
        tvTip = (TextView)root.findViewById(R.id.tvTip);
    
        receiver = new UserManager.EventReceiver().register(getActivity(), new UserManager.UserEventHandler() {
            @Override
            public void handleEvent(String eventName, Bundle bundle) {
                if (ArraysAndLists.findIndexWithEquals(eventName, arrRefresh) != -1) {
                    updateUserInfo();
                }
            }
        });

        tvTip2.setVisibility(View.GONE);
        //tvTip2.setText("2.当天前3次提款免手续费，当天提款超过3次每次按提款金额1%收取手续费，最低1元，最高25元。");
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
    
    public void updateUserInfo(){
        if (UserManager.getIns().isLogined()) {
            initBankInfo();
        }else{
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
    private void withDrawals(String cardId, String amount, String withdrawPassword) {
        HttpAction.applyWithdrawal(UserManager.getIns().getAccount(), cardId, amount, withdrawPassword, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                tvBank.post(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.showProgressDialog(getActivity(), "");
                    }
                });
            }
            
            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    Toasts.show(getActivity(), "提交成功, 请等待客服处理!", true);
                }else{
                    Toasts.show(getActivity(), msg, false);
                }
                return  true;
            }
    
            @Override
            public void onAfter(int id) {
                tvBank.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.hideProgressDialog(getActivity());
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
                    if(data.getAccountCardList() != null && data.getAccountCardList().size() > 0){
                        tvBank.setText(Strs.of(data.getAccountCardList().get(0).getBankName()));
                        tvCardNum.setText(Strs.of(data.getAccountCardList().get(0).getBankCardId()));
                        tvUserName.setText(Strs.of(data.getAccountCardList().get(0).getBankCardName()));
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
                       String str = String.format("取款时间  %s，单笔限额：%s-%s元，每日限制次数：%s",
                                withdrawConfig.getServiceTime(),
                                Strs.of(withdrawConfig.getMinUnitAmount()),
                                Strs.of(withdrawConfig.getMaxUnitAmount()),
                                Strs.of(withdrawConfig.getMaxDailyCount())
                                );
                        tvTip.setText(str);

                        /*String tips2=String.format("2.当天前 %s 次提款免手续费，当天提款超过 %s 次每次按提款金额 %s 收取手续费，最低 %s 元，最高 %s 元。",
                                Strs.of(withdrawConfig.getFreeDailyCount()),
                                Strs.of(withdrawConfig.getFreeDailyCount()),
                                withdrawConfig.getFeeRate()*100+"%",
                                Strs.of(withdrawConfig.getDayWithdrawRateMin()),
                                Strs.of(withdrawConfig.getDayWithdrawRateMax())
                        );
                        tvTip2.setText(tips2);*/


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
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(false);
                    }
                }, 400);
            }
        });
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            receiver.unregister(getActivity());
        }
    }
    
   
}
