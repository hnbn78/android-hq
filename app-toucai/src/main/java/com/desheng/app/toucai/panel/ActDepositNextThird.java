package com.desheng.app.toucai.panel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.ENV;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.ClipboardUtils;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.alipay.sdk.app.PayTask;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.PersonInfo;
import com.desheng.base.model.RechargeInfo;
import com.desheng.base.model.RechargeResult;
import com.desheng.base.panel.ActWeb;
import com.desheng.base.panel.ActWebX5;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.util.HashMap;
import java.util.Map;

/**
 * 快速入款
 * Created by user on 2018/4/16.
 */
public class ActDepositNextThird extends AbAdvanceActivity implements View.OnClickListener {
    
    private int chargeType = 0;
    RechargeInfo.TransferListBean trans;
    RechargeInfo.CommonChargeListBean pay;
    
    @Override
    protected int getLayoutId() {
        return R.layout.act_deposit_next_third;
    }
    
    private Button btFinish, btQuestion;
    private RechargeResult data;
    private TextView tvAmount, tvOrderId;
    private TextView tvBank;
    private ImageView ivBank;
    private String payName;
    
    private String payStr = "";
    private int countTry = 0;
    private static final int PAY = 1;
    private static final int PAY_RESULT = 2;

    public static void launch(Activity act, RechargeInfo.CommonChargeListBean pay, RechargeResult data, String payName) {
        Intent intent = new Intent();
        intent.putExtra("chargeType", RechargeInfo.CHARGE_TYPE_THIRD);
        intent.putExtra("pay", pay);
        intent.putExtra("data", data);
        intent.putExtra("payName", payName);
        intent.setClass(act, ActDepositNextThird.class);
        act.startActivity(intent);
    }
    
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PAY:
                    /*countTry ++;
                    if (countTry > 10) {
                        return;
                    }*/
                    payStr = (String) msg.obj;
                    if(Strs.isEmpty(payStr)){
                        return;
                    }
                    Runnable payRunnable = new Runnable() {
        
                        @Override
                        public void run() {
                            PayTask alipay = new PayTask(ActDepositNextThird.this);
                            Map<String, String> result = alipay.payV2(payStr, true);
                            Log.i("msp", result.toString());
            
                            Message msg = new Message();
                            msg.what = PAY_RESULT;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    };
    
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                    break;
                case PAY_RESULT: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(ActDepositNextThird.this, "支付完成, 请售后检查余额", Toast.LENGTH_SHORT).show();
                    } else if(payResult.memo.contains("再试")){
                        if(!hasAlipay()){
                            Toasts.show(ActDepositNextThird.this, "您尚未安装支付宝,请安装!", false);
                        }else{
                            Toasts.show(ActDepositNextThird.this, payResult.memo, false);
                        }
                        return;
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(ActDepositNextThird.this, payResult.memo, Toast.LENGTH_SHORT).show();
                    }
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 300);
                    break;
                }
             
                default:
                    break;
            }
        };
    };
    
    @Override
    protected void init() {
        payName = getIntent().getStringExtra("payName");
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "充值");
        setStatusBarTranslucentAndLightContentWithPadding();
        initView();
    }
    
    private void initView() {
        data = (RechargeResult) getIntent().getSerializableExtra("data");
        pay = (RechargeInfo.CommonChargeListBean) getIntent().getSerializableExtra("pay");
        chargeType = getIntent().getIntExtra("chargeType", 0);
        trans = (RechargeInfo.TransferListBean) getIntent().getSerializableExtra("trans");
        
        tvAmount = (TextView) findViewById(R.id.tvAmount);
        tvAmount.setText(Nums.formatDecimal(data.amount, 2));
        
        tvOrderId = (TextView) findViewById(R.id.tvOrderId);
        tvOrderId.setText(data.billno);
        
        btFinish = (Button) findViewById(R.id.btConfirm);
        btFinish.setOnClickListener(this);
        btQuestion = (Button) findViewById(R.id.btQuestion);
        btQuestion.setOnClickListener(this);
    
        findViewById(R.id.tvService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManager.getIns().getPersonSettingInfo(new UserManager.IUserInfoGetCallBack() {
                    @Override
                    public void onCallBack(PersonInfo personInfo) {
                        if (personInfo.isVipFlag()) {
                            ActWebX5.launch(ActDepositNextThird.this, personInfo.getVipChannelUrl(), true);
                        } else {
                            ActWeb.launchCustomService(ActDepositNextThird.this);
                        }
                    }
                });
            }
        });
    
        TextView tvCopy = findViewById(R.id.tv_copy_label);
        if (payName != null &&
                (payName.contains("微信手动扫码")
                        || (payName.contains("微信") && payName.contains("转账"))
                        || (payName.contains("支付宝") && payName.contains("转账"))
                        || payName.contains("支付宝手动扫码")
                        || (payName.contains("QQ") && payName.contains("扫码"))
                        || (payName.contains("网银") && payName.contains("转账"))
                        || payName.contains("云闪付"))) {
            tvCopy.setVisibility(View.VISIBLE);
            tvCopy.setOnClickListener((view) -> {
                ClipboardUtils.copyText(Act, tvCopy.getText(), (success) -> {
                    if (success) {
                        Toasts.show(Act, "复制成功", true);
                    } else {
                        Toasts.show(Act, "剪切板不可用", false);
                    }
                });
            });
        } else {
            tvCopy.setVisibility(View.GONE);
        }
    }
    
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btConfirm:
                if("110092".equals(pay.channelCode)){
                    HttpAction.rechargeThirdAlipay(ActDepositNextThird.this, data.link, data.text, new AbHttpResult(){
                        @Override
                        public void setupEntity(AbHttpRespEntity entity) {
                            entity.putField("data", String.class);
                        }
    
                        @Override
                        public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                            String str = getField(extra, "data", null);
                            if (Strs.isNotEmpty(str)) {
                                Message message = new Message();
                                message.what = PAY;
                                message.obj = str;
                                mHandler.sendMessage(message);
                            }else{
                                Toasts.show(ActDepositNextThird.this, msg);
                            }
                            return true;
                        }
        
                        @Override
                        public boolean onError(int status, String content) {
                            Toasts.show(ActDepositNextThird.this, content);
                            return true;
                        }
                    });
                }else{
                    String payURL = String.format("%s%s?text=%s", ENV.curr.host , data.link, data.text);
                    ActWeb.launchOutside(this, payURL);
                    finish();
                }
                break;
            case R.id.btQuestion:
                    finish();
                break;
            default:
        }
    }

    public void copyMoney(View view) {
        ClipboardUtils.copyText(this, Views.getText(tvAmount));
    }
    
    public class PayResult {
        private String resultStatus;
        private String result;
        private String memo;
        
        public PayResult(Map<String, String> rawResult) {
            if (rawResult == null) {
                return;
            }
            
            for (String key : rawResult.keySet()) {
                if (TextUtils.equals(key, "resultStatus")) {
                    resultStatus = rawResult.get(key);
                } else if (TextUtils.equals(key, "result")) {
                    result = rawResult.get(key);
                } else if (TextUtils.equals(key, "memo")) {
                    memo = rawResult.get(key);
                }
            }
        }
        
        @Override
        public String toString() {
            return "resultStatus={" + resultStatus + "};memo={" + memo
                    + "};result={" + result + "}";
        }
        
        /**
         * @return the resultStatus
         */
        public String getResultStatus() {
            return resultStatus;
        }
        
        /**
         * @return the memo
         */
        public String getMemo() {
            return memo;
        }
        
        /**
         * @return the result
         */
        public String getResult() {
            return result;
        }
    }
    
    private boolean hasAlipay() {
        boolean has = true;
        try {
            Uri uri = Uri.parse("alipays://platformapi/startApp");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            ComponentName componentName = intent.resolveActivity(ActDepositNextThird.this.getPackageManager());
            has = (componentName != null);
        }catch (Exception e){
            has = false;
        }
        return has;
    }
}
