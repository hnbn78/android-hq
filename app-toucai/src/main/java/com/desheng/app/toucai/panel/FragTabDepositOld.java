package com.desheng.app.toucai.panel;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.ArraysAndLists;
import com.ab.util.Dates;
import com.ab.util.InputUtils;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.airsaid.pickerviewlibrary.OptionsPickerView;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.RechargeInfo;
import com.desheng.base.model.RechargeResult;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.ToggleImageButton;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * Created by lee on 2018/4/11.
 */
public class FragTabDepositOld extends AbBaseFragment implements SwipeRefreshLayout.OnRefreshListener, IRefreshableFragment {
    public static final String[] arrRefresh = {UserManager.EVENT_USER_LOGINED,
            UserManager.EVENT_USER_LOGOUTED};
    
    private Button btnNext;
    private EditText etAmount;
    private Button btnMoney0, btnMoney1, btnMoney2, btnMoney3;
    private View vgRechargeContent, vgEmpty;
    private Button[] buttons;
    private GridLayout glPayCategory;
    private TextView tvBankSelect;
    private ViewGroup vgBank;
    
    private RechargeInfo rechargeInfo;
    private ToggleImageButton[] arrToggleBg;
    private ArrayList<String> bankNameList;
    private ArrayList<String> fixedMoneyList;
    private List<RechargeInfo.BanklistBean> bankList;
    private List<RechargeInfo.CommonChargeListBean> commonList;
    
    private SwipeRefreshLayout srlRefresh;
    private UserManager.EventReceiver receiver;
    
    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_deposit_old;
    }
    
    @Override
    public void init(View root) {
        srlRefresh = (SwipeRefreshLayout) root.findViewById(R.id.srlDepositRefresh);
        srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);
        
        vgBank = (ViewGroup) root.findViewById(R.id.vgBank);
        glPayCategory = (GridLayout) root.findViewById(R.id.glPayCategory);
        vgRechargeContent = root.findViewById(R.id.vgRechargeContent);
        glPayCategory = (GridLayout) root.findViewById(R.id.glPayCategory);
        vgEmpty = root.findViewById(R.id.vgEmpty);
        
        btnNext = (Button) root.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recharge();
            }
        });
        
        tvBankSelect = (TextView) root.findViewById(R.id.tvBankSelect);
        
        btnMoney0 = (Button) root.findViewById(R.id.btnMoney0);
        btnMoney1 = (Button) root.findViewById(R.id.btnMoney1);
        btnMoney2 = (Button) root.findViewById(R.id.btnMoney2);
        btnMoney3 = (Button) root.findViewById(R.id.btnMoney3);
        etAmount = (EditText) root.findViewById(R.id.etAmount);
        
        buttons = new Button[]{btnMoney0, btnMoney1, btnMoney2, btnMoney3};
        for (Button button : buttons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (Button button1 : buttons) {
                        if (v == button1) {
                            button1.setBackgroundResource(R.drawable.button_border_focused);
                            etAmount.setText(button1.getText());
                        } else {
                            button1.setBackgroundResource(R.drawable.sh_bd_menu_card);
                        }
                    }
                }
            });
        }
        
        receiver = new UserManager.EventReceiver().register(getActivity(), new UserManager.UserEventHandler() {
            @Override
            public void handleEvent(String eventName, Bundle bundle) {
                if (ArraysAndLists.findIndexWithEquals(eventName, arrRefresh) != -1) {
                    onRefresh();
                }
            }
        });
        
        commonList = new ArrayList<>();
        
        etAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vgRechargeContent.scrollTo(0, 10000);
            }
        });
        etAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    vgRechargeContent.scrollTo(0, 10000);
                }else{
                    vgRechargeContent.scrollTo(0, 0);
                }
            }
        });
    }
    
    
    @Override
    public void onShow() {
        super.onShow();
        onRefresh();
    }

    @Override
    public void onDestroy() {
        receiver.unregister(getActivity());
        super.onDestroy();
    }

    @Override
    public void refresh(String eventName, Bundle newBundle) {
        onRefresh();
    }
    
    
    @Override
    public void onRefresh() {
        if (UserManager.getIns().isLogined()) {
            getPayMethod();
        }
    }
    
    private void getPayMethod() {
        HttpAction.getAllThirdPayment(getActivity(), new AbHttpResult() {
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
                entity.putField("data", RechargeInfo.class);
            }
            
            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    rechargeInfo = getFieldObject(extra, "data", null);
                    if (!rechargeInfo.rechargeConfig.isOpen ||
                            rechargeInfo.thridList == null ||
                            rechargeInfo.thridList.size() == 0) {
                        vgEmpty.setVisibility(View.VISIBLE);
                        vgRechargeContent.setVisibility(View.GONE);
                        ((TextView) (vgEmpty.findViewById(R.id.tvAnnounce))).setText(rechargeInfo.rechargeConfig.serviceMsg);
                    } else {
                        bankNameList = new ArrayList<>();
                        fixedMoneyList = new ArrayList<>();
                        vgEmpty.setVisibility(View.GONE);
                        vgRechargeContent.setVisibility(View.VISIBLE);
                        LayoutInflater inflater = LayoutInflater.from(getActivity());
                        glPayCategory.removeAllViews();
                        commonList.clear();
                        
                        //组装thridList和qrList
                        for (int i = 0; i < rechargeInfo.thridList.size(); i++) {
                            commonList.add(rechargeInfo.thridList.get(i).getCommon());
                        }
                        for (int i = 0; i < rechargeInfo.qrCodeList.size(); i++) {
                            if("奉南琴".equals(rechargeInfo.qrCodeList.get(i).nickName) ||
                                    "这个 可以有".equals(rechargeInfo.qrCodeList.get(i).nickName)){
                                //nothing
                            }else {
                                commonList.add(rechargeInfo.qrCodeList.get(i).getCommon());
                            }
                        }
                        arrToggleBg = new ToggleImageButton[commonList.size()];
                        
                        //补齐4个
                        if (commonList.size() < 4) {
                            for (int i = 0; i < 4 - (rechargeInfo.thridList.size() + rechargeInfo.qrCodeList.size()); i++) {
                                commonList.add(new RechargeInfo.CommonChargeListBean());
                            }
                            Views.setHeight(glPayCategory, Views.dp2px(150));
                        } else {
                            int row = (commonList.size()) / 2;
                            if (commonList.size() % 2 == 1) {
                                row += 1;
                            }
                            Views.setHeight(glPayCategory, Views.dp2px(row * 150 / 2));
                        }
                        
                        
                        //添加thirdlist
                        for (int i = 0; i < commonList.size(); i++) {
                            RechargeInfo.CommonChargeListBean data = commonList.get(i);
                            ViewGroup item = (ViewGroup) inflater.inflate(R.layout.item_charge_type, glPayCategory, false);
                            if (data.chargeType == -1) {
                                item.setVisibility(View.INVISIBLE);
                            } else if (data.chargeType == RechargeInfo.CHARGE_TYPE_THIRD) {
                                item.setVisibility(View.VISIBLE);
                                ((ImageView) item.findViewById(R.id.ivChargeType)).setImageResource(getPayCategoryIcon(data.name));
                                ((TextView) item.findViewById(R.id.tvChargeType)).setText(data.name);
                                arrToggleBg[i] = ((ToggleImageButton) item.findViewById(R.id.tibBg));
                                item.setTag(i);
                                item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int tag = (int) v.getTag();
                                        for (int j = 0; j < arrToggleBg.length; j++) {
                                            if (j != tag) {
                                                arrToggleBg[j].setChecked(false);
                                            } else {
                                                arrToggleBg[j].setChecked(true);
                                                syncSelection(j);
                                            }
                                        }
                                    }
                                });
                            } else if (data.chargeType == RechargeInfo.CHARGE_TYPE_QR) {
                                item.setVisibility(View.VISIBLE);
                                ((ImageView) item.findViewById(R.id.ivChargeType)).setImageResource(getPayCategoryIcon(data.nickName));
                                ((TextView) item.findViewById(R.id.tvChargeType)).setText(data.nickName);
                                arrToggleBg[i] = ((ToggleImageButton) item.findViewById(R.id.tibBg));
                                item.setTag(i);
                                item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int tag = (int) v.getTag();
                                        for (int j = 0; j < arrToggleBg.length; j++) {
                                            if (j != tag) {
                                                arrToggleBg[j].setChecked(false);
                                            } else {
                                                arrToggleBg[j].setChecked(true);
                                                syncSelection(j);
                                            }
                                        }
                                    }
                                });
                            }
                            
                            if (i == 0) {
                                arrToggleBg[i].setChecked(true);
                                syncSelection(0);
                            }
                            glPayCategory.addView(item,
                                    Views.genGridLayoutItemParam());
                        }
                        
                    }
                }
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
    
    private void syncSelection(int position) {
        RechargeInfo.CommonChargeListBean data = commonList.get(position);
        etAmount.setHint("请输入" + data.minUnitRecharge +
                "~" + data.maxUnitRecharge);
        etAmount.setText("");
        Views.setEditorEnable(etAmount, true);
        etAmount.setOnClickListener(null);
        
        if (data.chargeType == RechargeInfo.CHARGE_TYPE_THIRD) {//third list处理
            bankList = data.banklist;
            if (bankList == null || bankList.size() == 0) {
                vgBank.setVisibility(View.GONE);
            } else {
                vgBank.setVisibility(View.VISIBLE);
                tvBankSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputUtils.hideKeyboard(v);
                        bankNameList.clear();
                        for (int i = 0; i < bankList.size(); i++) {
                            bankNameList.add(UserManagerTouCai.getIns().findBankName(bankList.get(i).code));
                        }

                        showBankList();
                    }
                });
            }
            
            if(Strs.isNotEmpty(data.fixAmount) && data.fixAmount.split(",").length > 0){
                fixedMoneyList.clear();
                String[] arr = data.fixAmount.split(",");
                for (int i = 0; i < arr.length; i++) {
                    if (Strs.isNotEmpty(arr[i])) {
                        fixedMoneyList.add(arr[i]);
                    }
                }
                
                Views.setEditorEnable(etAmount, false);
                etAmount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showFixedMoneyList();
                    }
                });
                
            }
        } else if (data.chargeType == RechargeInfo.CHARGE_TYPE_QR) {//qr list处理
            vgBank.setVisibility(View.GONE);
        }
    }

    private void showFixedMoneyList() {

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
                String s = fixedMoneyList.get(option1);
                etAmount.setText(s);
            }
        });
        pvOptions.setPicker(fixedMoneyList);
        pvOptions.show();

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
                String s = bankNameList.get(option1);
                tvBankSelect.setTag(bankList.get(option1));
                tvBankSelect.setText(s);
            }
        });
        pvOptions.setPicker(bankNameList);
        pvOptions.show();

    }
    
    public void recharge() {
        String pid = "";
        RechargeInfo.CommonChargeListBean bean = null;
        for (int j = 0; j < arrToggleBg.length; j++) {
            if (arrToggleBg[j].isChecked()) {
                pid = commonList.get(j).id;
                bean = commonList.get(j);
            }
        }
        if (Strs.isEmpty(pid) || bean == null) {
            Toasts.show(getActivity(), "请选择支付渠道", false);
        } else if (bean.chargeType == RechargeInfo.CHARGE_TYPE_THIRD) {
            rechargeThird(pid, bean);
        } else if (bean.chargeType == RechargeInfo.CHARGE_TYPE_QR) {
            rechargeQR(pid, bean);
        }
    }
    
    public void rechargeThird(String pid, RechargeInfo.CommonChargeListBean bean) {
        if ("请选择开户银行".equals(tvBankSelect.getText()) && vgBank.getVisibility() == (View.VISIBLE)) {
            Toasts.show(getActivity(), "请选择开户银行", false);
            return;
        }
        
        String bankco = "unknown";
        if (bean.banklist == null ||
                bean.banklist.size() == 0) {
            bankco = "";
        } else {
            bankco = ((RechargeInfo.BanklistBean) tvBankSelect.getTag()).code;
        }
        
        int amount = Nums.parse(Views.getText(etAmount), -1);
        if (amount <= 0) {
            Toasts.show(getActivity(), "请填写正确金额", false);
            return;
        }
        
        HttpAction.rechargeThird(getActivity(), pid, bankco, Strs.of(amount), new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", RechargeResult.class);
            }
            
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                etAmount.post(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.showProgressDialog(getActivity(), "");
                    }
                });
            }
            
            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getFieldObject(extra, "data", null) != null) {
                    RechargeResult data = (RechargeResult) getFieldObject(extra, "data", null);
                    data.date = Dates.getCurrentDate(Dates.dateFormatYMDHMS);
                    ActFastCharge.launchForThird(getActivity(), data);
                    etAmount.setText("");
                    tvBankSelect.setText("请选择开户银行");
                    tvBankSelect.setTag(null);
                } else {
                    Toasts.show(getActivity(), msg, false);
                }
                return true;
            }
            
            @Override
            public boolean onError(int status, String content) {
                Toasts.show(getActivity(), content, false);
                return true;
            }
            
            @Override
            public void onAfter(int id) {
                etAmount.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.hideProgressDialog(getActivity());
                    }
                }, 400);
            }
        });
    }
    
    public void rechargeQR(String pid, final RechargeInfo.CommonChargeListBean bean) {
        int amount = Nums.parse(Views.getText(etAmount), -1);
        if (amount <= 0) {
            Toasts.show(getActivity(), "请填写正确金额", false);
            return;
        }
        
        HttpAction.rechargeQR(getActivity(), pid, Strs.of(amount), Strs.of(bean.codeType), bean.id,  new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", RechargeResult.class);
            }
            
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                etAmount.post(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.showProgressDialog(getActivity(), "");
                    }
                });
            }
            
            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getFieldObject(extra, "data", null) != null) {
                    RechargeResult data = (RechargeResult) getFieldObject(extra, "data", null);
                    data.date = Dates.getCurrentDate(Dates.dateFormatYMDHMS);
                    ActFastCharge.launchForQR(getActivity(), data, bean.fileByte);
                    etAmount.setText("");
                    tvBankSelect.setText("请选择开户银行");
                    tvBankSelect.setTag(null);
                } else {
                    Toasts.show(getActivity(), msg, false);
                }
                return true;
            }
            
            @Override
            public boolean onError(int status, String content) {
                Toasts.show(getActivity(), content, false);
                return true;
            }
            
            @Override
            public void onAfter(int id) {
                etAmount.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.hideProgressDialog(getActivity());
                    }
                }, 400);
            }
        });
    }
    
    private int getPayCategoryIcon(String cateName) {
        if (cateName.contains("银联")) {
            return R.mipmap.ic_pay_union_old;
        } else if (cateName.contains("网银")) {
            return R.mipmap.ic_pay_card;
        } else if (cateName.toLowerCase().contains("qq")) {
            return R.mipmap.ic_pay_qq_old;
        } else if (cateName.contains("微信")) {
            return R.mipmap.ic_pay_weixin_old;
        } else if (cateName.contains("支付宝")) {
            return R.mipmap.ic_pay_ailipay;
        }else if (cateName.contains("云闪付")) {
            return R.mipmap.ic_pay_smart_old;
        } else {
            return R.mipmap.ic_pay_card;
        }
        
    }
    
}
