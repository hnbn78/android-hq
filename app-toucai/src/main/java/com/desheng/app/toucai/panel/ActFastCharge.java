package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.global.ENV;
import com.ab.util.ImageUtils;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.base64.Base64;
import com.desheng.base.model.RechargeInfo;
import com.desheng.base.model.RechargeResult;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.download.DownloadConfig;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.io.File;

/**
 * 快速入款
 * Created by user on 2018/4/16.
 */
public class ActFastCharge extends AbAdvanceActivity implements View.OnClickListener {
    
    private ImageView ivQrCode;
    private String qrCode;
    private int chargeType = 0;
    RechargeInfo.TransferListBean trans;
    
    @Override
    protected int getLayoutId() {
        return R.layout.act_fast_charge;
    }
    
    private Button btFinish, btQuestion;
    private RechargeResult data;
    private TextView tvAmount, tvDate, tvOrderId;
    
    public static void launchForThird(Activity act, RechargeResult data) {
        Intent intent = new Intent();
        intent.putExtra("chargeType", RechargeInfo.CHARGE_TYPE_THIRD);
        intent.putExtra("data", data);
        intent.setClass(act, ActFastCharge.class);
        act.startActivity(intent);
    }
    
    public static void launchForTrans(Activity act, RechargeResult data, RechargeInfo.TransferListBean trans) {
        Intent intent = new Intent();
        intent.putExtra("chargeType", RechargeInfo.CHARGE_TYPE_TRANS);
        intent.putExtra("data", data);
        intent.putExtra("trans", trans);
        intent.setClass(act, ActFastCharge.class);
        act.startActivity(intent);
    }
    
    public static void launchForQR(Activity act, RechargeResult data, String qrCode) {
        Intent intent = new Intent();
        intent.putExtra("chargeType", RechargeInfo.CHARGE_TYPE_QR);
        intent.putExtra("data", data);
        intent.putExtra("qrCode", qrCode);
        intent.setClass(act, ActFastCharge.class);
        act.startActivity(intent);
    }
    
    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "快速入账");
        setStatusBarTranslucentAndLightContentWithPadding();
        initView();
    }
    
    private void initView() {
        data = (RechargeResult) getIntent().getSerializableExtra("data");
        chargeType = getIntent().getIntExtra("chargeType", 0);
        trans = (RechargeInfo.TransferListBean) getIntent().getSerializableExtra("trans");
        
        tvAmount = (TextView) findViewById(R.id.tvAmount);
        tvAmount.setText(Nums.formatDecimal(data.amount, 2));
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvDate.setText(data.date);
        tvOrderId = (TextView) findViewById(R.id.tvOrderId);
        tvOrderId.setText(data.billno);
        
        btFinish = (Button) findViewById(R.id.btConfirm);
        btFinish.setOnClickListener(this);
        btQuestion = (Button) findViewById(R.id.btQuestion);
        btQuestion.setOnClickListener(this);
        
        qrCode = getIntent().getStringExtra("qrCode");
        if (Strs.isNotEmpty(qrCode)) {
            ivQrCode = (ImageView) findViewById(R.id.ivQrCode);
            ivQrCode.setVisibility(View.VISIBLE);
            byte [] image = Base64.decode(qrCode.getBytes());
            Bitmap bitmap = ImageUtils.bytesToBimap(image);
            if (bitmap != null) {
                ivQrCode.setImageBitmap(bitmap);
                final String fileName = "充值扫码_" + data.billno + "_" + System.currentTimeMillis() + ".jpeg";
                String path = DownloadConfig.getInstance().getDownloadPath() + fileName;
                try {
                    ImageUtils.saveJpegFile(bitmap, path);
                    MediaStore.Images.Media.insertImage(this.getContentResolver(), new File(path).getAbsolutePath(), fileName, null);
                    ivQrCode.post(new Runnable() {
                        @Override
                        public void run() {
                            Toasts.show(ActFastCharge.this, "二维码已保存为" + fileName + ", 请在相册查看, 并扫描充值.", true);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btConfirm:
                if(Strs.isNotEmpty(qrCode)){
                    Toasts.show(ActFastCharge.this, "请扫描相册中的二维码，并转账", true);
                }else{
                    String payURL = String.format("%s%s?text=%s", ENV.curr.host , data.link, data.text);
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(payURL);
                    intent.setData(content_url);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.btQuestion:
                    finish();
                break;
            default:
        }
    }
}
