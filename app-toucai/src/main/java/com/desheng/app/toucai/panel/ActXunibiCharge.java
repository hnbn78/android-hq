package com.desheng.app.toucai.panel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.ClipboardUtils;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.manager.USDTbean;
import com.desheng.base.action.HttpAction;
import com.desheng.base.util.QRCodeUtil;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.WriterException;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.util.HashMap;

import okhttp3.Request;

public class ActXunibiCharge extends AbAdvanceActivity implements View.OnClickListener {

    private String mPid;
    private TextView mPocketAddressTv, tvLatestHuilv,
            tvJiaochengPC, tvJiaochengApp, copyAddress, tvtip1, tvtip3, tvtip4, tvtip5, tvMinChargeValue, amountTip;
    private ImageView ivPocketErweima;
    private USDTbean usdTbean;
    private String mRate;
    private String minCharge;
    private String maxCharge;
    private String mAmount;

    public static void launch(Context context, String pid, String rate) {
        Intent intent = new Intent(context, ActXunibiCharge.class);
        intent.putExtra("pid", pid);
        intent.putExtra("rate", rate);
        context.startActivity(intent);
    }

    public static void launch(Context context, String pid, String rate, String minCharge, String maxCharge) {
        Intent intent = new Intent(context, ActXunibiCharge.class);
        intent.putExtra("pid", pid);
        intent.putExtra("rate", rate);
        intent.putExtra("minCharge", minCharge);
        intent.putExtra("maxCharge", maxCharge);
        context.startActivity(intent);
    }

    public static void launch(Context context, String pid, String rate, String amount) {
        Intent intent = new Intent(context, ActXunibiCharge.class);
        intent.putExtra("pid", pid);
        intent.putExtra("rate", rate);
        intent.putExtra("amount", amount);
        context.startActivity(intent);
    }

    public static void launch(Context context, String pid, String rate, String minCharge, String maxCharge, String amount) {
        Intent intent = new Intent(context, ActXunibiCharge.class);
        intent.putExtra("pid", pid);
        intent.putExtra("rate", rate);
        intent.putExtra("minCharge", minCharge);
        intent.putExtra("maxCharge", maxCharge);
        intent.putExtra("amount", amount);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_xunibi_layout;
    }

    @Override
    protected void init() {
        setStatusBarTranslucentAndLightContentWithPadding();
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "充值");
        mPid = getIntent().getStringExtra("pid");
        mRate = getIntent().getStringExtra("rate");
        minCharge = getIntent().getStringExtra("minCharge");
        maxCharge = getIntent().getStringExtra("maxCharge");
        mAmount = getIntent().getStringExtra("amount");

        mPocketAddressTv = ((TextView) findViewById(R.id.tvPocketAddress));
        ivPocketErweima = ((ImageView) findViewById(R.id.ivPocketErweima));
        tvLatestHuilv = ((TextView) findViewById(R.id.tvLatestHuilv));
        tvJiaochengPC = ((TextView) findViewById(R.id.tvJiaochengPC));
        tvJiaochengApp = ((TextView) findViewById(R.id.tvJiaochengApp));
        copyAddress = ((TextView) findViewById(R.id.copyAddress));
        tvMinChargeValue = ((TextView) findViewById(R.id.tvMinChargeValue));
        amountTip = ((TextView) findViewById(R.id.amountTip));
        tvtip1 = ((TextView) findViewById(R.id.tvtip1));
        tvtip3 = ((TextView) findViewById(R.id.tvtip3));
        tvtip4 = ((TextView) findViewById(R.id.tvtip4));
        tvtip5 = ((TextView) findViewById(R.id.tvtip5));

        tvLatestHuilv.setText("1USDT = " + mRate + "CNY");
        tvJiaochengPC.setText(Html.fromHtml("<u><strong><font color='#02A8F3'>" + "PC端" + "</font></strong></u>"));
        tvJiaochengApp.setText(Html.fromHtml("<u><strong><font color='#02A8F3'>" + "手机端" + "</font></strong></u>"));
        tvtip1.setText(Html.fromHtml("1.此充值方式使用的<strong><font color='#FF0000'>" + "【USDT为ERC20】" + "</font></strong>模式， \n\t请勿充值其他模式USDT或其他币种！"));
        tvtip3.setText(Html.fromHtml("3.USDT购买推荐交易所币安网页:\n<u><strong><font color='#FF0000'>" + "\tHTTPS://WWW.BINANCEZH.COM" + "</font><strong></u>"));
        tvtip4.setText(Html.fromHtml("4.USDT购买推荐交易所币安手机:\n<u><strong><font color='#FF0000'>" + "\tHTTPS://WWW.BINANCEZH.COM/CN/DOWNLOAD" + "</font><strong></u>"));
        tvtip5.setText("5.充值金额：单笔最低充值金额为 " + minCharge + "USDT，最高" + maxCharge + "USDT。");
        tvMinChargeValue.setText(Html.fromHtml("最小充值额：<font color='#FF0000'>" + minCharge + " USDT</font> 。"));

        amountTip.setText("必须按照该充币金额：" + mAmount + " USDT转账付款，否则无法到账，没有按照规定充值导致不到账由自己承担。");

        tvJiaochengPC.setOnClickListener(this::onClick);
        tvJiaochengApp.setOnClickListener(this::onClick);
        copyAddress.setOnClickListener(this::onClick);
        tvtip3.setOnClickListener(this::onClick);
        tvtip4.setOnClickListener(this::onClick);
        getXunibiPocketAddress();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvJiaochengPC:
                ActWebX5.launch(this, "https://huobiglobal.zendesk.com/hc/zh-cn/articles/360000674031-%E5%A6%82%E4%BD%95%E9%80%9A%E8%BF%87%E7%BD%91%E9%A1%B5%E7%AB%AF%E8%B4%AD%E4%B9%B0%E6%AF%94%E7%89%B9%E5%B8%81-");
                break;
            case R.id.tvJiaochengApp:
                ActWebX5.launch(this, "https://v.qq.com/x/page/q0671pkwoz1.html");
                break;
            case R.id.tvtip3:
                ActWebX5.launch(this, "https://www.binancezh.com");
                break;
            case R.id.tvtip4:
                ActWebX5.launch(this, "https://www.binancezh.com/cn/download");
                break;
            case R.id.copyAddress:
                if (usdTbean == null) {
                    return;
                }
                ClipboardUtils.copyText(this, usdTbean.getUsdt_address());
                Toasts.show(this, "已复制到剪切板");
                break;
            default:
        }
    }

    private void getXunibiPocketAddress() {
        HttpAction.getUsdtPocketInfo(this, mPid, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<USDTbean>() {
                }.getType());
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActXunibiCharge.this, "");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    usdTbean = getFieldObject(extra, "data", USDTbean.class);
                    if (usdTbean != null) {
                        mPocketAddressTv.setText(usdTbean.getUsdt_address());
                        amountTip.setText("必须按照该充币金额：" + mAmount
                                + (Strs.isNotEmpty(usdTbean.getUsdtPointAmount()) ? ("." + usdTbean.getUsdtPointAmount()) : "") +
                                " USDT转账付款，否则无法到账，没有按照规定充值导致不到账由自己承担。");
                        try {
                            Bitmap bitmap = QRCodeUtil.Create2DCode(usdTbean.getUsdt_address());
                            ivPocketErweima.setImageBitmap(bitmap);
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Toast.makeText(ActXunibiCharge.this, content + "", Toast.LENGTH_SHORT).show();
                Dialogs.hideProgressDialog(ActXunibiCharge.this);
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Dialogs.hideProgressDialog(ActXunibiCharge.this);
            }
        });
    }
}
