package com.desheng.app.toucai.panel;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.Config;
import com.ab.global.ENV;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.InputUtils;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.ab.view.MaterialDialog;
import com.airsaid.pickerviewlibrary.OptionsPickerView;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.model.BanklistBean;
import com.desheng.app.toucai.model.QrCodeListBean;
import com.desheng.app.toucai.model.ThridListBean;
import com.desheng.app.toucai.model.TransferListBean;
import com.desheng.base.action.HttpAction;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.ChargeOrderInfo;
import com.desheng.base.model.RechargeInfo;
import com.desheng.base.model.RechargeSuccessBean;
import com.desheng.base.util.DeviceUtil;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.Request;

public class ActRechargePay extends AbAdvanceActivity implements View.OnClickListener {

    private Button btn_charge_next;
    private EditText et_recharge_amount;
    private TextView tv_recharge_tips, tv_recharge_tips2;
    private TextView tvBankSelect, tvFraction, tv_hint, tv_j_tips;
    private RelativeLayout rl_bank_choose;
    private EditText et_tip, etBankNo;
    private RelativeLayout layout_tips, layout_recharge, rl_bank_input;
    private GridView gv_amount;

    private ArrayList<String> bankNameList;
    private List<BanklistBean> bankList;
    private ChargeOrderInfo orderInfo;
    private RechargeSuccessBean successBean;

    public static final int PAY_TYPE_OTHER = 0, PAY_TYPE_BANK = 1, PAY_TYPE_QR = 2, PAY_TYPE_TRANSFER = 3;
    private int pay_type = 0;
    private String bankcode;
    private String url;
    private String pid;
    private double max;
    private double min;
    private double rate;
    private String bankName;
    private int bankType;
    private String cardId;
    private String cardName;
    //    private int position;
    private String fuyan;
    private String strAmount;
    private String name;
    private int amountInputType;
    private String fixAmount;
    private String[] amountlist;
    private String bankno;
    private TransferListBean transferListBean;
    private QrCodeListBean mQrCodeListBean;
    private String qrFileByte;
    private String mPayHint;
    private String mRechargeHint;
    private boolean isExtra;

    private boolean mIsXunibi;
    private String mUstdRate;
    private String mUstdBeanId;

    @Deprecated
    public static void launchThird(Context context, RechargeInfo rechargeInfo, int position, int pay_type) {
        Intent intent = new Intent(context, ActRechargePay.class);
        intent.putExtra("recharge_info", rechargeInfo);
        intent.putExtra("pay_type", pay_type);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    public static void launchThird(Context context, ThridListBean thridListBean, int pay_type, String payHint,
                                   String rechargeHint, boolean isXunibi,String ustdBeanId,String ustdRate) {
        Intent intent = new Intent(context, ActRechargePay.class);
        intent.putExtra("recharge_info", thridListBean);
        intent.putExtra("pay_type", pay_type);
        intent.putExtra("payHint", payHint);
        intent.putExtra("rechargeHint", rechargeHint);
        intent.putExtra("isXunibi", isXunibi);
        intent.putExtra("ustdRate", ustdRate);
        intent.putExtra("ustdBeanId", ustdBeanId);
        context.startActivity(intent);
    }

    @Deprecated
    public static void launchTransfer(Context context, RechargeInfo.TransferListBean rechargeInfo, int pay_type) {
        Intent intent = new Intent(context, ActRechargePay.class);
        intent.putExtra("recharge_info", rechargeInfo);
        intent.putExtra("pay_type", pay_type);
        context.startActivity(intent);
    }

    public static void launchQrPayment(Context context, QrCodeListBean qrCodeListBean, int pay_type, String payHint, String rechargeHint) {
        Intent intent = new Intent(context, ActRechargePay.class);
        intent.putExtra("qr_payment_info", qrCodeListBean);
        intent.putExtra("pay_type", pay_type);
        intent.putExtra("payHint", payHint);
        intent.putExtra("rechargeHint", rechargeHint);
        context.startActivity(intent);
    }

    public static void launchThird(Context context, ThridListBean thridListBean, int pay_type, String payHint, String rechargeHint) {
        Intent intent = new Intent(context, ActRechargePay.class);
        intent.putExtra("recharge_info", thridListBean);
        intent.putExtra("pay_type", pay_type);
        intent.putExtra("payHint", payHint);
        intent.putExtra("rechargeHint", rechargeHint);
        context.startActivity(intent);
    }

    public static void launchTransfer(Context context, TransferListBean transferListBean, int pay_type, String payHint, String rechargeHint) {
        Intent intent = new Intent(context, ActRechargePay.class);
        intent.putExtra("transfer_info", transferListBean);
        intent.putExtra("pay_type", pay_type);
        intent.putExtra("payHint", payHint);
        intent.putExtra("rechargeHint", rechargeHint);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_recharge_pay;
    }

    @Override
    protected void init() {
        setStatusBarTranslucentAndLightContentWithPadding();

        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "充值");

        bankNameList = new ArrayList<>();
        bankList = new ArrayList<>();

        gv_amount = findViewById(R.id.gv_amount);
        tvBankSelect = findViewById(R.id.tvBankSelect);
        btn_charge_next = findViewById(R.id.btn_charge_next);
        et_recharge_amount = findViewById(R.id.et_recharge_amount);
        tvFraction = findViewById(R.id.tvFraction);
        tv_hint = findViewById(R.id.tv_hint);
        tv_j_tips = findViewById(R.id.tv_j_tips);
        tv_recharge_tips = findViewById(R.id.tv_recharge_tips);
        tv_recharge_tips2 = findViewById(R.id.tv_recharge_tips2);
        btn_charge_next.setOnClickListener(this);
        rl_bank_choose = findViewById(R.id.rl_bank_choose);
        rl_bank_input = findViewById(R.id.rl_bank_input);
        et_tip = findViewById(R.id.et_tip);
        etBankNo = findViewById(R.id.etBankNo);
        layout_tips = findViewById(R.id.layout_tips);
        layout_recharge = findViewById(R.id.layout_recharge);
        tvBankSelect.setOnClickListener(this);
        pay_type = getIntent().getIntExtra("pay_type", 0);
        mPayHint = getIntent().getStringExtra("payHint");
        mRechargeHint = getIntent().getStringExtra("rechargeHint");

        mIsXunibi = getIntent().getBooleanExtra("isXunibi", false);
        mUstdRate = getIntent().getStringExtra("ustdRate");
        mUstdBeanId = getIntent().getStringExtra("ustdBeanId");

        if (pay_type == PAY_TYPE_OTHER) {
            ThridListBean thridListBean = (ThridListBean) getIntent().getParcelableExtra("recharge_info");
            bankList = thridListBean.getBanklist();
            if (bankList != null) {
                for (int i = 0; i < bankList.size(); i++) {
                    bankNameList.add(getKey(UserManager.getIns().getMapBank(), bankList.get(i).getCode()));
                }
            }

            bankcode = String.valueOf(thridListBean.getCode());
            url = thridListBean.getLink();
            pid = String.valueOf(thridListBean.getId());
            max = thridListBean.getMaxUnitRecharge();
            min = thridListBean.getMinUnitRecharge();
            rate = thridListBean.getRate();
            name = thridListBean.getName();
            fixAmount = thridListBean.getFixAmount();
            amountInputType = thridListBean.getAmountInputType();
            isExtra = thridListBean.getIsExtra() == 1;
            rl_bank_choose.setVisibility(View.GONE);
            et_tip.setHint(thridListBean.getExtraPayPrompt());
            layout_tips.setVisibility(isExtra ? View.VISIBLE : View.GONE);

        } else if (pay_type == PAY_TYPE_BANK) {
            ThridListBean thridListBean = (ThridListBean) getIntent().getParcelableExtra("recharge_info");
            bankList = thridListBean.getBanklist();
            if (bankList != null) {
                for (int i = 0; i < bankList.size(); i++) {
                    bankNameList.add(getKey(UserManager.getIns().getMapBank(), bankList.get(i).getCode()));
                }
            }

            bankcode = String.valueOf(thridListBean.getCode());
            url = thridListBean.getLink();
            pid = String.valueOf(thridListBean.getId());
            max = thridListBean.getMaxUnitRecharge();
            min = thridListBean.getMinUnitRecharge();
            rate = thridListBean.getRate();
            name = thridListBean.getName();
            fixAmount = thridListBean.getFixAmount();
            amountInputType = thridListBean.getAmountInputType();
            isExtra = thridListBean.getIsExtra() == 1;

            rl_bank_choose.setVisibility(View.VISIBLE);
            tvBankSelect.setEnabled(true);
            et_tip.setHint(thridListBean.getExtraPayPrompt());
            layout_tips.setVisibility(isExtra ? View.VISIBLE : View.GONE);

        } else if (pay_type == PAY_TYPE_TRANSFER) {//支付宝转账
            rl_bank_choose.setVisibility(View.VISIBLE);
            tvBankSelect.setEnabled(false);
            transferListBean = (TransferListBean) getIntent().getParcelableExtra("transfer_info");

            if (null != transferListBean) {
                bankName = transferListBean.getBankName();
                tvBankSelect.setText(bankName);
                pid = "" + transferListBean.getId();
                //url = transferListBean.getLink();
                max = transferListBean.getMaxUnitRecharge();
                min = transferListBean.getMinUnitRecharge();
                bankType = transferListBean.getBankType();
                cardId = transferListBean.getCardId();
                cardName = transferListBean.getCardName();
                fixAmount = transferListBean.getFixAmount();
                amountInputType = transferListBean.getAmountInputType();
                rate = 0.0;

                if (bankType == 6) {
                    layout_tips.setVisibility(View.GONE);
                } else {
                    layout_tips.setVisibility(View.VISIBLE);
                }
            }
        } else if (pay_type == PAY_TYPE_QR) {
            rl_bank_choose.setVisibility(View.GONE);
            mQrCodeListBean = (QrCodeListBean) getIntent().getParcelableExtra("qr_payment_info");
            if (null != mQrCodeListBean) {
                pid = "" + mQrCodeListBean.getId();
                bankType = Integer.parseInt(mQrCodeListBean.getCodeType());
                max = mQrCodeListBean.getMaxUnitRecharge();
                min = mQrCodeListBean.getMinUnitRecharge();
                qrFileByte = mQrCodeListBean.getFileByte();
                fixAmount = mQrCodeListBean.getFixAmount();
                amountInputType = mQrCodeListBean.getAmountInputType();
                rate = 0.0;

                layout_tips.setVisibility(View.GONE);
            }
        }

        setAmountList();

        /**
         * amountInputType
         * fixAmount
         *
         * 1常规形式
         * 2小数点
         * 3固定金额
         * 4排除倍数金额
         * 5排除固定金额
         * 6需要输入银行卡号
         */
        if (amountInputType == 3 && Strs.isNotEmpty(fixAmount)) {
            tv_j_tips.setVisibility(View.VISIBLE);
            gv_amount.setVisibility(View.VISIBLE);
            layout_recharge.setVisibility(View.GONE);
            tv_j_tips.setVisibility(View.GONE);
            rl_bank_input.setVisibility(View.GONE);
            AmountAdapter mAdapter = new AmountAdapter();
            gv_amount.setAdapter(mAdapter);
            gv_amount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    view.setBackgroundResource(R.drawable.shape_round_corner_primary_3dp);
                    ((TextView) view).setTextColor(Color.parseColor("#FFFFFF"));
                    strAmount = ((TextView) view).getText().toString();
                    for (int i = 0; i < gv_amount.getChildCount(); i++) {
                        if (i != position) {
                            ((TextView) gv_amount.getChildAt(i)).setTextColor(Color.parseColor("#000000"));
                            gv_amount.getChildAt(i).setBackgroundResource(R.drawable.shape_round_corner_gray_3dp);
                        }
                    }
                }
            });

        } else if (amountInputType == 4) {
            tv_j_tips.setVisibility(View.VISIBLE);
            tv_j_tips.setText("提示:充值金额除以" + fixAmount + "余额尾数为0都会风控。请输入，例如：1001、9999.9等");
            rl_bank_input.setVisibility(View.GONE);
        } else if (amountInputType == 6) {
            rl_bank_input.setVisibility(View.VISIBLE);
        } else {
            tv_j_tips.setVisibility(View.GONE);
            layout_recharge.setVisibility(View.VISIBLE);
            rl_bank_input.setVisibility(View.GONE);
        }

        tv_hint.setHint("单笔限额:" + min + "~" + max + "元");
        String replace = mRechargeHint
                .replace("{{minAmount}}", " <font color=\"#000000\">" + String.valueOf(min) + "</font> ")
                .replace("{{maxAmount}}", " <font color=\"#000000\">" + String.valueOf(max) + "</font> ")
                .replace("{{minRate}}", " <font color=\"#000000\">" + String.valueOf(rate * 100) + "</font> ")
                .replace("|", "<br>");

        tv_recharge_tips.setText(Html.fromHtml(replace));
        tv_recharge_tips.setTextColor(Color.RED);

        if (Config.custom_flag.equals(BaseConfig.FLAG_WANSHANG)) {
            tv_recharge_tips2.setVisibility(View.VISIBLE);
        }

        layout_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Views.setEditTextFocus(et_recharge_amount);
            }
        });

        if (amountInputType == 2) {
            tvFraction.setVisibility(View.VISIBLE);
        } else {
            tvFraction.setVisibility(View.GONE);
        }

        et_recharge_amount.addTextChangedListener(new TextWatcher() {
            int frag = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                //avoid triggering event when text is too short
                if (amountInputType == 2) {
                    if (Strs.isNotEmpty(s.toString()) && Strs.isEmpty(tvFraction.getText().toString())) {
                        if (frag == 0) {
                            frag = new Random().nextInt(99) + 1;
                        }
                        if (frag < 10) {
                            tvFraction.setText(".0" + frag);
                        } else {
                            tvFraction.setText("." + frag);
                        }
                    } else if (Strs.isEmpty(s.toString())) {
                        frag = 0;
                        tvFraction.setText("");
                    }
                }

                if (Strs.isNotEmpty(s.toString()) || Strs.isNotEmpty(tvFraction.getText().toString())) {
                    tv_hint.setVisibility(View.INVISIBLE);
                } else if (Strs.isEmpty(s.toString())) {
                    tv_hint.setVisibility(View.VISIBLE);
                }
            }
        });

        et_recharge_amount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“GO”键*/
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm != null && imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }
//                    if(timer != null) {
//                        timer.cancel();
//                    }
                    //checkAndSetAmountFraction();
                    return true;
                }
                return false;
            }
        });

        mPvOptions = new OptionsPickerView(this);
        mPvOptions.setCancelText("取消");//取消按钮文字
        mPvOptions.setTitle("");
        mPvOptions.setCancelText("取消");
        mPvOptions.setCancelTextColor(Color.BLACK);
        mPvOptions.setCancelTextSize(16f);
        mPvOptions.setSubmitText("确定");
        mPvOptions.setSubmitTextColor(Color.BLACK);
        mPvOptions.setSubmitTextSize(16f);
        mPvOptions.setHeadBackgroundColor(Color.WHITE);

        mPvOptions.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int option1, int option2, int option3) {
                //返回的分别是三个级别的选中位置
                String s = bankNameList.get(option1);
                tvBankSelect.setTag(bankList.get(option1).getCode());
                tvBankSelect.setText(s);
            }
        });
        mPvOptions.setPicker(bankNameList);
    }

    private OptionsPickerView mPvOptions;

    private void setAmountList() {
        if ((amountInputType == 3 || amountInputType == 4 || amountInputType == 5) && Strs.isNotEmpty(fixAmount)) {
            amountlist = fixAmount.split("\\|");
        }
    }


    /**
     * amountInputType
     * fixAmount
     * <p>
     * 1常规形式
     * 2小数点
     * 3固定金额
     * 4排除倍数金额
     * 5排除固定金额
     * 6需要输入银行卡号
     */
    private boolean verifyAmount() {

        if (amountInputType == 3) {
            if (Strs.isEmpty(strAmount)) {
                Toasts.show(ActRechargePay.this, "请先选择金额！", false);
                return false;
            }
        } else if (amountInputType == 4) {

            if (Strs.isNotEmpty(Views.getText(et_recharge_amount)) && amountlist != null) {
                for (String str : amountlist) {
                    if (Double.parseDouble(Views.getText(et_recharge_amount)) % Double.parseDouble(str) == 0) {
                        Toasts.show(ActRechargePay.this, "充值金额不能是" + fixAmount + "的整数倍！", false);
                        return false;
                    }
                }
            } else {
                Toasts.show(ActRechargePay.this, "请输入充值金额!", false);
                return false;
            }

        } else if (amountInputType == 5) {
            if (Strs.isNotEmpty(Views.getText(et_recharge_amount)) && amountlist != null) {
                for (String str : amountlist) {
                    if (str.equals(Views.getText(et_recharge_amount))) {
                        Toasts.show(ActRechargePay.this, "充值金额不能是" + fixAmount + "！", false);
                        return false;
                    }
                }
            } else {
                Toasts.show(ActRechargePay.this, "请输入充值金额!", false);
                return false;
            }
        } else if (amountInputType == 6) {

            if (Strs.isEmpty(etBankNo.getText().toString())) {
                Toasts.show(ActRechargePay.this, "请输入银行卡号!", false);
                return false;
            } else if (Views.getText(etBankNo).length() == 16 || Views.getText(etBankNo).length() == 18 || Views.getText(etBankNo).length() == 19) {
                bankno = etBankNo.getText().toString();
            } else {
                Toasts.show(ActRechargePay.this, "请输入正确的银行卡号!", false);
                return false;
            }

        } else {
            if (Strs.isEmpty(Views.getText(et_recharge_amount))) {
                Toasts.show(ActRechargePay.this, "请输入充值金额!", false);
                return false;
            } else {
                strAmount = Views.getText(et_recharge_amount);
            }
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == tvBankSelect) {
            InputUtils.hideKeyboard(v);
            showBankList();
        } else if (v == btn_charge_next) {

            if (!verifyAmount()) {
                return;
            }

            if (pay_type == PAY_TYPE_BANK) {

                if (amountInputType != 3) {
                    String bank_code = (String) tvBankSelect.getTag();
                    if (null != bank_code && bank_code.trim().length() > 0) {
                        bankcode = (String) tvBankSelect.getTag();
                    }
                    strAmount = Views.getText(et_recharge_amount) + Views.getText(tvFraction);
                }

                if (isExtra) {
                    if (et_tip.getVisibility() == View.VISIBLE && Strs.isEmpty(et_tip.getText().toString().trim())) {
                        Toasts.show(this, "附言不能为空");
                        return;
                    } else {
                        if (Strs.isAllChinese(et_tip.getText().toString())) {
                            fuyan = et_tip.getText().toString();
                        } else {
                            Toasts.show(this, "附言必须是中文");
                            return;
                        }
                    }
                }

                reChargeThirdPay(isExtra);

            } else if (pay_type == PAY_TYPE_OTHER) {

                if (amountInputType == 2) {
                    strAmount = Views.getText(et_recharge_amount) + Views.getText(tvFraction);
                } else {
                    if (amountInputType != 3) {
                        strAmount = Views.getText(et_recharge_amount);
                    }
                }

                if (isExtra) {
                    if (et_tip.getVisibility() == View.VISIBLE && Strs.isEmpty(et_tip.getText().toString().trim())) {
                        Toasts.show(this, "附言不能为空");
                        return;
                    } else {
                        if (Strs.isAllChinese(et_tip.getText().toString())) {
                            fuyan = et_tip.getText().toString();
                        } else {
                            Toasts.show(this, "附言必须是中文");
                            return;
                        }
                    }
                }

                if (mIsXunibi) {
                    ThridListBean thridListBean = (ThridListBean) getIntent().getParcelableExtra("recharge_info");
                    if (thridListBean == null) {
                        ActXunibiCharge.launch(this, mUstdBeanId, mUstdRate,strAmount);
                    } else {
                        ActXunibiCharge.launch(this, mUstdBeanId, mUstdRate,
                                String.valueOf(thridListBean.getMinUnitRecharge()), String.valueOf(thridListBean.getMaxUnitRecharge()),strAmount);
                    }
                } else {
                    reChargeThirdPay(isExtra);
                }

            } else if (pay_type == PAY_TYPE_TRANSFER) {

                if (amountInputType == 2) {

                    if (Strs.isEmpty(et_recharge_amount.getText().toString().trim())) {
                        Toasts.show(this, "请输入金额");
                        return;
                    }

                    strAmount = Views.getText(et_recharge_amount) + Views.getText(tvFraction);

                    double amount = Nums.parse(strAmount, -1.0);
//                if (amount <= 0 || amount < min || amount > max) {
//                    Toasts.show(ActRechargePay.this, "请填写正确金额", false);
//                    return;
//                }

                    if (!Nums.isFractionDigits(strAmount, 2)) {
                        Toasts.show(ActRechargePay.this, "请填写两位小数的金额", false);
                        return;
                    }

                } else if (amountInputType != 3) {
                    strAmount = Views.getText(et_recharge_amount);
                }

                if (bankType != 6) {
                    if (et_tip.getVisibility() == View.VISIBLE && Strs.isEmpty(et_tip.getText().toString().trim())) {
                        Toasts.show(this, "附言不能为空");
                        return;
                    } else {
                        if (Strs.isAllChinese(et_tip.getText().toString())) {
                            fuyan = et_tip.getText().toString();
                        } else {
                            Toasts.show(this, "附言必须是中文");
                            return;
                        }
                    }
                }
                reChargeTransfer();
            } else if (pay_type == PAY_TYPE_QR) {
                if (amountInputType == 2) {
                    strAmount = Views.getText(et_recharge_amount) + Views.getText(tvFraction);
                } else {
                    if (amountInputType != 3) {
                        strAmount = Views.getText(et_recharge_amount);
                    }
                }

                reChargeTransferByQR();
            }

        }
    }

    public void startIExplore(String url) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        if (intent.resolveActivity(getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(getPackageManager());
            // 打印Log   ComponentName到底是什么
            startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            Toast.makeText(getApplicationContext(), "没有匹配的程序", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputUtils.hideKeyboard(et_recharge_amount);
    }

    private void reChargeThirdPay(boolean isExtraFuyan) {

        HttpAction.rechargeThird(null, pid, bankcode, bankno, strAmount, isExtraFuyan ? fuyan : null, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
                btn_charge_next.setEnabled(false);

            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", ChargeOrderInfo.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    orderInfo = getFieldObject(extra, "data", null);

                    if (orderInfo.getLink().startsWith("http") || (orderInfo.getLink().startsWith("https"))) {
                        url = String.format("%s?text=%s", orderInfo.getLink(), orderInfo.getText());
                    } else {
                        url = String.format("%s%s?text=%s", ENV.curr.host, orderInfo.getLink(), orderInfo.getText());
                    }

                    final MaterialDialog dialog = new MaterialDialog(ActRechargePay.this);
                    dialog.setMessage("充值金额:" + strAmount + "\n" + "订单号:" + orderInfo.getBillno());
                    dialog.setTitle("支付");

                    dialog.setPositiveButton("去支付", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            onLoadData(url);
                        }
                    });
                    dialog.setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                } else {
                    Toasts.show(ActRechargePay.this, msg);
                }

                return true;
            }

            @Override
            public boolean onGetString(String str) {
                return super.onGetString(str);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                btn_charge_next.setEnabled(true);
            }
        });

    }

    private void onLoadData(String url) {
        HttpActionTouCai.getBitCoinAddress(this, "DC", new AbHttpResult() {


            @Override
            public void onFinish() {
                startIExplore(url);
            }

            @Override
            public boolean onError(int status, String content) {
                return true;
            }
        });
    }

    private void reChargeTransfer() {
        HttpAction.rechargeTransfer(null, pid, strAmount, fuyan, bankType, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", RechargeSuccessBean.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
                btn_charge_next.setEnabled(false);

            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                btn_charge_next.setEnabled(true);
                if (code == 0 && error == 0) {

                    et_recharge_amount.setText("");
                    tv_hint.setText("");
                    et_tip.setText("");
                    successBean = getFieldObject(extra, "data", null);
                    fuyan = transferListBean.getCardName();
                    ActRechargeSuccessInfo.launch(ActRechargePay.this, successBean, bankName, cardName, cardId, mPayHint);
                } else {
                    Toasts.show(ActRechargePay.this, msg, false);
                }

                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onGetString(String str) {
                return super.onGetString(str);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                btn_charge_next.setEnabled(true);
            }
        });
    }

    private void reChargeTransferByQR() {
        HttpAction.rechargeQR(null, strAmount, bankType, pid, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", RechargeSuccessBean.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
                btn_charge_next.setEnabled(false);

            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                btn_charge_next.setEnabled(true);
                if (code == 0 && error == 0 && getFieldObject(extra, "data", null) != null) {
                    successBean = getFieldObject(extra, "data", null);
                    et_recharge_amount.setText("");
                    tv_hint.setText("");
                    et_tip.setText("");
                    ActRechargeSuccessByQr.launch(ActRechargePay.this, successBean, qrFileByte, mPayHint);
                } else {
                    Toasts.show(ActRechargePay.this, msg, false);
                }

                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onGetString(String str) {
                return super.onGetString(str);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                btn_charge_next.setEnabled(true);
            }
        });
    }

    private void showBankList() {
        DeviceUtil.hideInputKeyboard(ActRechargePay.this);
        if (mPvOptions == null) {
            return;
        }
        mPvOptions.show();
    }

    private String getKey(Map<String, String> map, String value) {
        String key = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                key = entry.getKey();
            }
        }
        return key;
    }


    class AmountAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return amountlist.length;
        }

        @Override
        public Object getItem(int position) {
            return amountlist[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(ActRechargePay.this).inflate(R.layout.item_text_amount, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvAmount.setText(amountlist[position]);

            return convertView;
        }

        class ViewHolder {
            TextView tvAmount;

            public ViewHolder(View view) {
                tvAmount = view.findViewById(R.id.tv_amount);
            }
        }

    }
}
