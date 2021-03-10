package com.desheng.app.toucai.panel;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.desheng.app.toucai.manager.USDTbean;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.XunibiAddressBean;
import com.desheng.app.toucai.util.Utils;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.google.gson.reflect.TypeToken;
import com.kongzue.dialog.interfaces.OnMenuItemClickListener;
import com.kongzue.dialog.v3.BottomMenu;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Request;

public class ActXunibiWithdraw extends AbAdvanceActivity implements View.OnClickListener {


    private EditText etWithdrawAmount, withdrawPassword;
    private TextView tvPocketAddress, selectPocketAddress, etWithdrawAmountZH, tvLastHuilv, tvPocketYuE, tvWithdrawTips, tvGongshi;
    private ArrayList<XunibiAddressBean> mXunibiAddressBeans;
    private String selectAddressId;
    private double mUsdtWithdrawRate;
    private int usdtDrawMinCount;
    private String withdrawTimeStr;

    public static void launch(Context context, ArrayList<XunibiAddressBean> xunibiAddressBeans,
                              double usdtWithdrawRate, int usdtDrawMinCount, String withdrawTimeStr) {
        Intent intent = new Intent(context, ActXunibiWithdraw.class);
        intent.putParcelableArrayListExtra("xunibiAddressBeans", xunibiAddressBeans);
        intent.putExtra("usdtWithdrawRate", usdtWithdrawRate);
        intent.putExtra("usdtDrawMinCount", usdtDrawMinCount);
        intent.putExtra("withdrawTimeStr", withdrawTimeStr);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_xunibi_withdraw;
    }

    @Override
    protected void init() {
        setStatusBarTranslucentAndLightContentWithPadding();
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "提现");
        findViewById(R.id.submit_bt).setOnClickListener(this::onClick);
        mXunibiAddressBeans = getIntent().getParcelableArrayListExtra("xunibiAddressBeans");
        mUsdtWithdrawRate = getIntent().getDoubleExtra("usdtWithdrawRate", 0);
        usdtDrawMinCount = getIntent().getIntExtra("usdtDrawMinCount", 0);
        withdrawTimeStr = getIntent().getStringExtra("withdrawTimeStr");
        tvPocketAddress = ((TextView) findViewById(R.id.tvPocketAddress));
        selectPocketAddress = ((TextView) findViewById(R.id.selectPocketAddress));
        etWithdrawAmount = ((EditText) findViewById(R.id.etWithdrawAmount));
        etWithdrawAmountZH = ((TextView) findViewById(R.id.etWithdrawAmountZH));
        tvLastHuilv = ((TextView) findViewById(R.id.tvLastHuilv));
        tvPocketYuE = ((TextView) findViewById(R.id.tvPocketYuE));
        tvWithdrawTips = ((TextView) findViewById(R.id.tvWithdrawTips));
        tvGongshi = ((TextView) findViewById(R.id.tvGongshi));
        withdrawPassword = ((EditText) findViewById(R.id.withdrawPassword));

        selectPocketAddress.setOnClickListener(this::onClick);
        tvLastHuilv.setText("1USDT = " + mUsdtWithdrawRate + "CNY");
        tvPocketYuE.setText("可提现金额：" + UserManager.getIns().getLotteryAvailableBalance() + "元");
        tvWithdrawTips.setText("1.取款时间："+withdrawTimeStr);

        tvGongshi.setText("预计到账 0.0000 USDT" +
                "\n" +
                "提款公式（提款金额0-手续费：0%）/汇率" + mUsdtWithdrawRate +
                "\n" +
                "最小提款额 " + usdtDrawMinCount + " USDT");

        etWithdrawAmount.setHint("最少" + usdtDrawMinCount * mUsdtWithdrawRate + "CNY");

        etWithdrawAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = s.toString();
                if (Strs.isEmpty(temp) || temp.startsWith("0")) {
                    etWithdrawAmountZH.setText("= 0.0000 USDT");
                    return;
                }
                long l = Long.parseLong(temp);
                String tempData = Utils.getDoubleNotRound2Str(l / mUsdtWithdrawRate * 1.0);
                if (Strs.isNotEmpty(tempData) && tempData.contains(".")) {
                    String[] split = tempData.split("\\.");
                    if (split.length < 2) {
                        return;
                    }
                    int length = split[1].length();
                    if (length == 3) {
                        tempData = tempData + "0";
                    } else if (length == 2) {
                        tempData = tempData + "00";
                    } else if (length == 1) {
                        tempData = tempData + "000";
                    }
                }
                etWithdrawAmountZH.setText("= " + tempData + " USDT");

                tvGongshi.setText("预计到账 " + tempData + " USDT" +
                        "\n" +
                        "提款公式（提款金额" + l + " - 手续费：0%）/汇率" + mUsdtWithdrawRate +
                        "\n" +
                        "最小提款额 " + usdtDrawMinCount + " USDT");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_bt:
                String withdrawAmount = etWithdrawAmount.getText().toString().trim();
                String withdrawPasswordStr = withdrawPassword.getText().toString().trim();
                String pocketAddress = tvPocketAddress.getText().toString().trim();

                if (Strs.isEmpty(pocketAddress)) {
                    Toast.makeText(ActXunibiWithdraw.this, "请选择钱包地址", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Strs.isEmpty(withdrawAmount)) {
                    Toast.makeText(ActXunibiWithdraw.this, "请输入提取数额", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Strs.isEmpty(withdrawPasswordStr)) {
                    Toast.makeText(ActXunibiWithdraw.this, "请输入资金密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Strs.isEmpty(selectAddressId)) {
                    Toast.makeText(ActXunibiWithdraw.this, "请选择提现钱包地址", Toast.LENGTH_SHORT).show();
                    return;
                }
                doWthidraw(withdrawAmount, selectAddressId, withdrawPasswordStr);
                break;
            case R.id.selectPocketAddress:
                if (mXunibiAddressBeans == null) {
                    return;
                }
                BottomMenu.show(this, new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return mXunibiAddressBeans.size();
                    }

                    @Override
                    public Object getItem(int position) {
                        return mXunibiAddressBeans.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return 0;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView textView = new TextView(ActXunibiWithdraw.this);
                        textView.setText(mXunibiAddressBeans.get(position).getAddress());
                        textView.setPadding(30, 30, 30, 30);
                        return textView;
                    }
                }, new OnMenuItemClickListener() {
                    @Override
                    public void onClick(String text, int index) {
                        tvPocketAddress.setText(mXunibiAddressBeans.get(index).getAddress());
                        selectAddressId = mXunibiAddressBeans.get(index).getId();
                    }
                });
                break;
            default:
        }
    }

    private void doWthidraw(String amount, String usdtId, String withdrawPasswordStr) {
        HttpAction.applyWithdrawal(withdrawPasswordStr, usdtId, amount, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<USDTbean>() {
                }.getType());
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActXunibiWithdraw.this, "");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    //usdTbean = getFieldObject(extra, "data", USDTbean.class);
                    etWithdrawAmount.setText("");
                    withdrawPassword.setText("");
                    refreshMoney();
                } else {
                    Toast.makeText(ActXunibiWithdraw.this, msg + "", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Toast.makeText(ActXunibiWithdraw.this, content + "", Toast.LENGTH_SHORT).show();
                Dialogs.hideProgressDialog(ActXunibiWithdraw.this);
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Dialogs.hideProgressDialog(ActXunibiWithdraw.this);
            }
        });
    }

    private void refreshMoney() {
        UserManager.getIns().initUserData(new UserManager.IUserDataSyncCallback() {

            @Override
            public void onUserDataInited() {
                Dialogs.showLoadingDialog(ActXunibiWithdraw.this, "");
            }

            @Override
            public void afterUserDataInited() {
                tvPocketYuE.setText("可提现金额：" + Utils.getDoubleNotRound2Str(UserManagerTouCai.getIns().getLotteryAvailableBalance()) + "元");
            }

            @Override
            public void onUserDataInitFaild() {

            }

            @Override
            public void onAfter() {
                tvPocketYuE.setText("可提现金额：" + Utils.getDoubleNotRound2Str(UserManagerTouCai.getIns().getLotteryAvailableBalance()) + "元");
                Dialogs.hideProgressDialog(ActXunibiWithdraw.this);
            }
        });
    }
}
