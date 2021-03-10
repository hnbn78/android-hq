package com.desheng.app.toucai.panel;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.manager.USDTbean;
import com.desheng.base.action.HttpAction;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.HashMap;

import okhttp3.Request;

public class ActAddXunibiPocketAddress extends AbAdvanceActivity implements View.OnClickListener {

    private int REQUEST_CODE_SAOMA = 12;
    private EditText pocketAddress, withdrawPassword;
    private RxPermissions rxPermissions;

    public static void launch(Context context) {
        Intent intent = new Intent(context, ActAddXunibiPocketAddress.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_add_xunibi_address;
    }

    @Override
    protected void init() {
        setStatusBarTranslucentAndLightContentWithPadding();
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "添加钱包地址");
        findViewById(R.id.submit_bt).setOnClickListener(this::onClick);
        findViewById(R.id.ivsaoma).setOnClickListener(this::onClick);
        rxPermissions = new RxPermissions(this);
        pocketAddress = ((EditText) findViewById(R.id.pocketAddress));
        withdrawPassword = ((EditText) findViewById(R.id.withdrawPassword));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_bt:
                String pocketAddressStr = pocketAddress.getText().toString().trim();
                String withdrawPasswordStr = withdrawPassword.getText().toString().trim();

                if (Strs.isEmpty(pocketAddressStr)) {
                    Toast.makeText(ActAddXunibiPocketAddress.this, "请输入钱包地址", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Strs.isEmpty(withdrawPasswordStr)) {
                    Toast.makeText(ActAddXunibiPocketAddress.this, "请输入资金密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                getXunibiPocketAddress(pocketAddressStr, withdrawPasswordStr);
                break;

            case R.id.ivsaoma:
                rxPermissions.requestEach(Manifest.permission.CAMERA)
                        .subscribe(permission -> {
                            if (permission.granted) {
                                Intent intent = new Intent(this, CaptureActivity.class);
                                startActivityForResult(intent, REQUEST_CODE_SAOMA);
                                return;
                            }
                            if (permission.shouldShowRequestPermissionRationale) {
                                return;
                            }
                        });

            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SAOMA) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    if (Strs.isEmpty(result)) {
                        Toasts.show("解析二维码错误", false);
                    } else {
                        pocketAddress.setText(result);
                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toasts.show("解析二维码失败", false);
                }
            }
        }
    }

    private void getXunibiPocketAddress(String address, String withdrawPasswordStr) {
        HttpAction.addUsdtWithdrawAddress(this, address, withdrawPasswordStr, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<USDTbean>() {
                }.getType());
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActAddXunibiPocketAddress.this, "");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    //usdTbean = getFieldObject(extra, "data", USDTbean.class);
                    Toast.makeText(ActAddXunibiPocketAddress.this, "操作成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ActAddXunibiPocketAddress.this, msg + "", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Toast.makeText(ActAddXunibiPocketAddress.this, content + "", Toast.LENGTH_SHORT).show();
                Dialogs.hideProgressDialog(ActAddXunibiPocketAddress.this);
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Dialogs.hideProgressDialog(ActAddXunibiPocketAddress.this);
            }
        });
    }
}
