package com.desheng.app.toucai.panel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ab.util.ClipboardUtils;
import com.ab.util.Toasts;
import com.desheng.base.model.RechargeSuccessBean;
import com.desheng.base.panel.ActDepositRecord;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

public class ActRechargeSuccessInfo extends AbAdvanceActivity implements View.OnClickListener {

    private TextView tv_transfer_bank_name, tv_order_num, tv_account_name, tv_bank_account, tv_bank_target, tv_recharge_money, tv_tips;
    private TextView tv_copy_account, tv_copy_bank, tv_copy_money, tv_copy_tips, tv_constant_tips, tv_copy_name;

    private Button btn_return, btn_recharge_record;
    private RechargeSuccessBean rechargeSuccessBean;
    private String depositeBank, account_name, account_num;
    private String payHint;

    public static void launch(Context context, RechargeSuccessBean rechargeSuccessBean,
                              String transferOutBank, String account, String account_num, String payHint) {
        Intent intent = new Intent(context, ActRechargeSuccessInfo.class);
        intent.putExtra("recharge_success_info", rechargeSuccessBean);
        intent.putExtra("transfer_out_bank_name", transferOutBank);
        intent.putExtra("account_name", account);
        intent.putExtra("account_num", account_num);
        intent.putExtra("payHint", payHint);
        context.startActivity(intent);
    }

    @Override
    protected void init() {

        setStatusBarTranslucentAndLightContentWithPadding();
        setToolbarLeftBtn(UIHelper.BACK_WHITE_ARROR);
        if (UIHelper.toolbarBgResId > 0) {
            setToolbarBgImage(UIHelper.toolbarBgResId);
        } else {
            setToolbarBgColor(R.color.colorPrimary);
        }
        setToolbarTitleCenter("充值");
        setToolbarTitleCenterColor(R.color.white);

        tv_transfer_bank_name = findViewById(R.id.tv_bank_name);
        tv_order_num = findViewById(R.id.tv_order_num);
        tv_account_name = findViewById(R.id.tv_account_name);
        tv_bank_account = findViewById(R.id.tv_bank_account);
        tv_bank_target = findViewById(R.id.tv_bank_target);
        tv_recharge_money = findViewById(R.id.tv_recharge_money);
        tv_tips = findViewById(R.id.tv_tips);
        tv_copy_account = findViewById(R.id.tv_copy_account);
        tv_copy_bank = findViewById(R.id.tv_copy_bank);
        tv_copy_money = findViewById(R.id.tv_copy_money);
        tv_copy_tips = findViewById(R.id.tv_copy_tips);
        btn_return = findViewById(R.id.btn_return);
        btn_recharge_record = findViewById(R.id.btn_recharge_record);
        tv_constant_tips = findViewById(R.id.tv_constant_tips);
        tv_copy_name = findViewById(R.id.tv_copy_name);

        tv_copy_account.setOnClickListener(this);
        tv_copy_bank.setOnClickListener(this);
        tv_copy_money.setOnClickListener(this);
        tv_copy_tips.setOnClickListener(this);
        btn_return.setOnClickListener(this);
        btn_recharge_record.setOnClickListener(this);
        tv_copy_name.setOnClickListener(this);

        rechargeSuccessBean = (RechargeSuccessBean) getIntent().getSerializableExtra("recharge_success_info");

        depositeBank = getIntent().getStringExtra("transfer_out_bank_name");
        account_name = getIntent().getStringExtra("account_name");
        account_num = getIntent().getStringExtra("account_num");
        payHint = getIntent().getStringExtra("payHint");

        tv_transfer_bank_name.setText(depositeBank);
        tv_account_name.setText(account_name);
        tv_bank_account.setText(account_num);

        tv_bank_target.setText(depositeBank);
        if (rechargeSuccessBean != null) {
            tv_recharge_money.setText("" + rechargeSuccessBean.getAmount());
            tv_tips.setText(rechargeSuccessBean.getAttach());
            tv_order_num.setText(rechargeSuccessBean.getBillno());

            String replace = payHint
                    .replace("【附言】", "<font color=\"#000000\">【 附言:" + rechargeSuccessBean.getAttach() + " 】</font>")
                    .replace("|", "<br>");
            tv_constant_tips.setText(Html.fromHtml(replace));
            tv_constant_tips.setTextColor(Color.RED);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == tv_copy_account) {

            ClipboardUtils.copyText(this, tv_bank_account.getText().toString());
            Toasts.show(this, "账号复制到剪切板");
        } else if (v == tv_copy_bank) {

            ClipboardUtils.copyText(this, tv_bank_target.getText().toString());
            Toasts.show(this, "存入银行复制到剪切板");
        } else if (v == tv_copy_money) {
            ClipboardUtils.copyText(this, tv_recharge_money.getText().toString());
            Toasts.show(this, "金额复制到剪切板");
        } else if (v == tv_copy_tips) {
            ClipboardUtils.copyText(this, tv_tips.getText().toString());
            Toasts.show(this, "附言复制到剪切板");

        } else if (v == btn_return) {
            finish();
        } else if (v == btn_recharge_record) {
            ActDepositRecord.launch(this, false);
        } else if (v == tv_copy_name) {
            ClipboardUtils.copyText(this, tv_account_name.getText().toString());
            Toasts.show(this, "姓名复制到剪切板");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_recharge_success;
    }
}
