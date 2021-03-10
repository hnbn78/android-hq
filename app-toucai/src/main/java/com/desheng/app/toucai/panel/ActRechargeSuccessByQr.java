package com.desheng.app.toucai.panel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.util.ImageUtils;
import com.ab.util.Strs;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.view.BitmapUtils;
import com.desheng.base.model.RechargeSuccessBean;
import com.desheng.base.panel.ActDepositRecord;
import com.desheng.base.panel.ActWebX5;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

public class ActRechargeSuccessByQr extends AbAdvanceActivity implements View.OnClickListener {

    private RechargeSuccessBean rechargeSuccessBean;
    private String qrFileByte;
    private TextView btn_recharge_record, tv_constant_tips, tvChargeAmount, tvChargeBillNo;
    private Button btn_return;
    private ImageView ivQrImage;
    private String payHint;

    public static void launch(Context context, RechargeSuccessBean rechargeSuccessBean, String qrFileByte, String payHint) {
        Intent intent = new Intent(context, ActRechargeSuccessByQr.class);
        intent.putExtra("recharge_success_info", rechargeSuccessBean);
        intent.putExtra("qr_file_byte", qrFileByte);
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
        setToolbarTitleCenter("扫码充值");
        setToolbarTitleCenterColor(R.color.white);

        rechargeSuccessBean = (RechargeSuccessBean) getIntent().getSerializableExtra("recharge_success_info");
        qrFileByte = getIntent().getStringExtra("qr_file_byte");
        payHint = getIntent().getStringExtra("payHint");

        btn_recharge_record = ((TextView) findViewById(R.id.btn_recharge_record));
        tv_constant_tips = ((TextView) findViewById(R.id.tv_constant_tips));
        btn_return = ((Button) findViewById(R.id.btn_return));
        ivQrImage = ((ImageView) findViewById(R.id.ivQrImage));
        tvChargeAmount = ((TextView) findViewById(R.id.tvChargeAmount));
        tvChargeBillNo = ((TextView) findViewById(R.id.tvChargeBillNo));

        btn_return.setOnClickListener(this);
        btn_recharge_record.setOnClickListener(this);

        if (rechargeSuccessBean != null) {
            tvChargeAmount.setText("充值金额：" + rechargeSuccessBean.getAmount());
            tvChargeBillNo.setText("订单编号：" + rechargeSuccessBean.getBillno());

            String fuyan = Strs.isEmpty(rechargeSuccessBean.getAttach()) ?
                    UserManagerTouCai.getIns().getCN() + rechargeSuccessBean.getAttach() : rechargeSuccessBean.getAttach();
            String replace = payHint
                    .replace("【附言】", "<font color=\"#000000\">【 附言:" + fuyan + " 】</font>")
                    .replace("|", "<br>");
            tv_constant_tips.setText(Html.fromHtml(replace));
            tv_constant_tips.setTextColor(Color.RED);
        }

        if (Strs.isNotEmpty(qrFileByte)) {
            Bitmap bitmap = BitmapUtils.base64ToBitmap(qrFileByte);
            ivQrImage.setImageBitmap(bitmap);

            ivQrImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showAlert(bitmap);
//                    int width = bitmap.getWidth();
//                    int height = bitmap.getHeight();
//                    int[] data = new int[width * height];
//                    bitmap.getPixels(data, 0, width, 0, 0, width, height);
//                    RGBLuminanceSource source = new RGBLuminanceSource(width, height, data);
//                    BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
//                    QRCodeReader reader = new QRCodeReader();
//                    Result re = null;
//                    try {
//                        re = reader.decode(bitmap1);
//                    } catch (NotFoundException e) {
//                        e.printStackTrace();
//                    } catch (ChecksumException e) {
//                        e.printStackTrace();
//                    } catch (FormatException e) {
//                        e.printStackTrace();
//                    }
//                    if (re == null) {
//                        showAlert(bitmap);
//                    } else {
//                        showSelectAlert(bitmap, re.getText());
//                    }
                    return false;
                }
            });
        }

    }

    private void showAlert(final Bitmap bitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("保存图片")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterfacem, int i) {
                        ImageUtils.saveImageToGallery(ActRechargeSuccessByQr.this, bitmap, "pay_qr_code_image");
                        Toast.makeText(ActRechargeSuccessByQr.this, "二维码图片已保存至相册", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterfacem, int i) {
                    }
                });
        builder.show();
    }

    private void showSelectAlert(final Bitmap bitmap, final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择");
        String str[] = {"保存图片", "扫二维码"};
        builder.setItems(str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterfacem, int i) {
                switch (i) {
                    case 0: {
                        ImageUtils.saveImageToGallery(ActRechargeSuccessByQr.this, bitmap, "pay_qr_code_image");
                        Toast.makeText(ActRechargeSuccessByQr.this, "二维码图片已保存至相册", Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case 1: {
                        ActWebX5.launchOutside(ActRechargeSuccessByQr.this, url);
                    }
                    break;
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterfacem, int i) {

            }
        });
        builder.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_return:
                finish();
                break;
            case R.id.btn_recharge_record:
                ActDepositRecord.launch(this, false);
                break;
            default:
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_recharge_by_qr_success;
    }


}
