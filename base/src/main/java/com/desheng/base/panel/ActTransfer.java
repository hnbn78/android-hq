package com.desheng.base.panel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.global.Global;
import com.ab.http.AbHttpResult;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.aes.MD5;
import com.airsaid.pickerviewlibrary.OptionsPickerView;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.event.TransferSuccessEvent;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.TransferType;
import com.desheng.base.util.CoinHelper;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

import static com.desheng.base.manager.UserManager.AMOUNT_KEY;
import static com.desheng.base.manager.UserManager.EVENT_USER_INFO_UNPDATED;

public class ActTransfer extends AbAdvanceActivity implements View.OnClickListener {

    private TextView tv_transfer_name, tv_transfer_type;
    private EditText et_transfer_count, et_transfer_pwd, et_transfer_tips;
    private Button btn_confirm_transfer;
    private ImageView iv_transfer_type;
    private LinearLayout layout_transfer_type;

    private List<TransferType> types = new ArrayList<>();
    private ArrayList<String> type_names = new ArrayList<>();
    private String name;
    private int type = 0;
    private float transfer_amount;
    private String coinType;
    private ArrayList<TransferTypeListBean> typeListBeans;

    public static void launch(Context activty, String name) {
        Intent intent = new Intent(activty, ActTransfer.class);
        intent.putExtra("transfer_name", name);
        activty.startActivity(intent);
    }

    public static void launch(Context activty, String name, ArrayList<TransferTypeListBean> transferBean) {
        Intent intent = new Intent(activty, ActTransfer.class);
        intent.putExtra("transfer_name", name);
        intent.putParcelableArrayListExtra("transfer_bean", transferBean);
        activty.startActivity(intent);
    }

    @Override
    protected void init() {
        setToolbarLeftBtn(UIHelper.BACK_WHITE_ARROR);
        if (UIHelper.toolbarBgResId > 0)
            setToolbarBgImage(UIHelper.toolbarBgResId);
        else
            setToolbarBgColor(R.color.colorPrimary);
        setToolbarTitleCenter("转账");
        setToolbarTitleCenterColor(R.color.white);
        setStatusBarTranslucentAndLightContentWithPadding();
        tv_transfer_name = findViewById(R.id.tv_transfer_name);
        tv_transfer_type = findViewById(R.id.tv_transfer_type);
        et_transfer_count = findViewById(R.id.et_transfer_count);
        et_transfer_pwd = findViewById(R.id.et_transfer_pwd);
        et_transfer_tips = findViewById(R.id.et_transfer_tips);
        btn_confirm_transfer = findViewById(R.id.btn_confirm_transfer);
        iv_transfer_type = findViewById(R.id.iv_transfer_type);
        layout_transfer_type = findViewById(R.id.layout_transfer_type);
        btn_confirm_transfer.setOnClickListener(this);
        layout_transfer_type.setOnClickListener(this);
        name = getIntent().getStringExtra("transfer_name");
        typeListBeans = getIntent().getParcelableArrayListExtra("transfer_bean");
        if (Strs.isNotEmpty(name)) {
            tv_transfer_name.setText(name);
        }

        if (CoinHelper.getInstance().hasCoin) {
            coinType = CoinHelper.getCoinCode(CoinHelper.getInstance().currentCurrency);
//            String coinStr = coinType.equals("RMB") ? "人民币" : coinType;
//            tv_coin_type.setText(coinStr);
//            layout_coin.setVisibility(View.VISIBLE);
//            layout_coin.setOnClickListener(this);
        }

        if (typeListBeans != null) {
            for (TransferTypeListBean bean : typeListBeans) {
                TransferType type = new TransferType();
                type.setCode(Integer.parseInt(bean.getType()));
                type.setName(bean.getName());
                type_names.add(bean.getName());
                types.add(type);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_confirm_transfer) {

            if (Strs.isEmpty(et_transfer_count.getText().toString())
                    || Float.parseFloat(et_transfer_count.getText().toString()) < 0) {
                Toasts.show(this, "请输入正确的金额");
                return;
            }

            transfer_amount = Float.parseFloat(et_transfer_count.getText().toString());

            if (Strs.isEmpty(et_transfer_pwd.getText().toString())) {
                Toasts.show(this, "请输入资金密码");
                return;
            }

            commitTransfer();

        } else if (v == layout_transfer_type) {
            showBankList();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_transfer;
    }

    private void commitTransfer() {
        HttpAction.applyTransfer(name,
                coinType,
                transfer_amount,
                type,
                et_transfer_tips.getText().toString(),
                MD5.GetMD5Code(et_transfer_pwd.getText().toString(), true),
                new AbHttpResult() {
                    @Override
                    public void onBefore(Request request, int id, String host, String funcName) {
                        super.onBefore(request, id, host, funcName);
                    }

                    @Override
                    public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                        Toasts.show(ActTransfer.this, msg);
                        et_transfer_count.setText("");
                        et_transfer_pwd.setText("");
                        et_transfer_tips.setText("");

                        EventBus.getDefault().post(new TransferSuccessEvent());
                        Bundle bundle = new Bundle();
                        bundle.putDouble(AMOUNT_KEY, (double) transfer_amount);
                        UserManager.getIns().sendBroadcaset(Global.app, EVENT_USER_INFO_UNPDATED, bundle);
                        return true;
                    }

                    @Override
                    public boolean onGetString(String str) {
                        return super.onGetString(str);
                    }

                    @Override
                    public boolean onError(int status, String content) {
                        return super.onError(status, content);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

    private void showBankList() {

        OptionsPickerView pvOptions = new OptionsPickerView(ActTransfer.this);
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
                //返回的分别是三个级别的选中位置
                type = types.get(option1).getCode();
                tv_transfer_type.setText(types.get(option1).getName());
            }
        });
        pvOptions.setPicker(type_names);
        pvOptions.show();

    }


}
