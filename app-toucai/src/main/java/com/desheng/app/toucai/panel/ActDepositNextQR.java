package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.callback.AbCallback;
import com.ab.global.Global;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.thread.ThreadCollector;
import com.ab.util.ClipboardUtils;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.ImageUtils;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.ab.util.base64.Base64;
import com.ab.view.MaterialDialog;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.util.DialogDepositHelp2;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.DepositCategory;
import com.desheng.base.model.LastOrderInfo;
import com.desheng.base.model.PersonInfo;
import com.desheng.base.model.RechargeInfo;
import com.desheng.base.model.RechargeRecordBean;
import com.desheng.base.model.RechargeResult;
import com.desheng.base.panel.ActWeb;
import com.desheng.base.panel.ActWebX5;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.download.DownloadConfig;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;
import com.shark.tc.databinding.ActDepositNextQrBinding;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * 快速入款
 * Created by user on 2018/4/16.
 */
public class ActDepositNextQR extends AbAdvanceActivity<ActDepositNextQrBinding> implements View.OnClickListener {

    private ImageView ivQrCode1;
    private String qrCode;
    private MaterialDialog materialDialog;

    private Button btFinish1, btQuestion1;
    private RechargeResult data;
    private TextView tvAmount1;

    private String payName;
    DepositCategory cate;
    private int chargeType;
    private int stage = 1;
    private boolean isStoped = false;
    private Bitmap bitmap;
    private CountDownTimer timer;
    private RechargeInfo.QrCodeListBean lastQrPay;
    private int seconds;
    private int start;

    private ImageView guide_finish_bg, guide_confirm_bg, guide_copy_bg;
    private TextView guide_finish_tip, guide_confirm_tip, guide_copy_tip;

    private boolean showGuide;
    private ScaleAnimation scaleAnimation;
    private boolean isComplete;
    private UserManager.EventReceiver receiver;

    @Override
    protected int getLayoutId() {
        return R.layout.act_deposit_next_qr;
    }

    public static void launchForStage1(Activity act, DepositCategory cate, RechargeResult data, String qrCode, String payName) {
        Intent intent = new Intent();
        intent.putExtra("stage", 1);
        intent.putExtra("chargeType", RechargeInfo.CHARGE_TYPE_QR);
        intent.putExtra("cate", cate);
        intent.putExtra("data", data);
        intent.putExtra("payName", payName);
        UserManagerTouCai.getIns().setQR(qrCode);
        intent.setClass(act, ActDepositNextQR.class);
        act.startActivity(intent);
    }


    public static void launchForStage2(Activity act, DepositCategory cate, LastOrderInfo lastOrderInfo) {
        Intent intent = new Intent();
        intent.putExtra("stage", 2);
        intent.putExtra("chargeType", RechargeInfo.CHARGE_TYPE_QR);
        intent.putExtra("cate", cate);

        setIntent(lastOrderInfo, intent);

        intent.putExtra("payName", lastOrderInfo.qrCodePayInfo.nickName);
        UserManagerTouCai.getIns().setQR(lastOrderInfo.qrCodePayInfo.fileByte);

        intent.setClass(act, ActDepositNextQR.class);
        act.startActivity(intent);
    }

    public static void setIntent(LastOrderInfo lastOrderInfo, Intent intent) {
        RechargeResult toData = new RechargeResult();
        toData.billno = lastOrderInfo.rechargeOrder.spsn;
        toData.amount = lastOrderInfo.rechargeOrder.cash;
        toData.date = Dates.getStringByFormat(lastOrderInfo.rechargeOrder.orderDate, Dates.dateFormatYMDHMS);
        intent.putExtra("data", toData);
        intent.putExtra("seconds", lastOrderInfo.rechargeOrder.seconds);
    }


    private void reSetGuideUI(boolean show) {

        isComplete = (stage == 1) ? UserManager.getIns().getDepositQrStage1Complete() : UserManager.getIns().getDepositQrStage2Complete();

        if (show && !isComplete) {
            if (stage == 1) {
                guide_confirm_bg.setVisibility(View.VISIBLE);
                guide_confirm_bg.startAnimation(scaleAnimation);
                guide_confirm_tip.setVisibility(View.VISIBLE);
            } else {
                guide_copy_bg.setVisibility(View.VISIBLE);
                guide_copy_tip.setVisibility(View.VISIBLE);
                guide_copy_bg.startAnimation(scaleAnimation);
                guide_finish_bg.clearAnimation();
                guide_finish_bg.setVisibility(View.GONE);
                guide_finish_tip.setVisibility(View.GONE);

            }
        } else {
            hideAnimation();
        }
    }

    @Override
    protected void init() {
        guide_finish_bg = findViewById(R.id.guide_finish_bg);
        guide_confirm_bg = findViewById(R.id.guide_confirm_bg);
        guide_finish_tip = findViewById(R.id.guide_finish_tip);
        guide_confirm_tip = findViewById(R.id.guide_confirm_tip);
        guide_copy_bg = findViewById(R.id.guide_copy_bg);
        guide_copy_tip = findViewById(R.id.guide_copy_tip);
        scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(ActDepositNextQR.this, R.anim.scale_animation_big);
        stage = getIntent().getIntExtra("stage", 1);
        cate = (DepositCategory) getIntent().getSerializableExtra("cate");
        payName = getIntent().getStringExtra("payName");
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), cate.categoryName + "扫码");
        setStatusBarTranslucentAndLightContentWithPadding();

        qrCode = UserManagerTouCai.getIns().getQR();
        UserManagerTouCai.getIns().setQR("");
        if (Strs.isNotEmpty(qrCode)) {
            byte[] image = Base64.decode(qrCode.getBytes());
            bitmap = ImageUtils.bytesToBimap(image);
            if (bitmap == null) {
                Toasts.show(Act, "服务端二维码解析失败!", false);
            }
        }

        if (cate.categoryName.equals("微信") || cate.categoryName.equals("支付宝") || cate.categoryName.equals("QQ")) {
            UIHelper.getIns().simpleToolbarLeftBackAndCenterTitleAndeRightTextBtn(this, getToolbar(), "充值", cate.categoryName + "扫码教程", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogDepositHelp2.showIns(ActDepositNextQR.this, cate.categoryName + "扫码");
                }
            });
        } else {
            UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "充值");
        }

        initView1();
        initView2();

        final String fileName = "充值扫码_" + data.billno + "_" + System.currentTimeMillis() + ".jpeg";
        String path = DownloadConfig.getInstance().getDownloadPath() + fileName;
        try {
            ImageUtils.saveJpegFile(bitmap, path);
            MediaStore.Images.Media.insertImage(this.getContentResolver(), new File(path).getAbsolutePath(), fileName, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        B.vgStage1.setVisibility(View.GONE);
        B.vgStage2.setVisibility(View.GONE);

//        updateGuideUI();

        receiver = new UserManager.EventReceiver().register(this, (eventName, bundle) -> {
            if (UserManager.EVENT_USER_DEPOSIT_OK.equals(eventName)) {
                checkLastOrder(cate);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateGuideUI();
    }

    private void updateGuideUI() {
        UserManager.getIns().isFirstRecharge(new UserManager.IGuideCallBack() {
            @Override
            public void onCallBack(boolean show) {
                showGuide = show;
                reSetGuideUI(show);
                if (!UserManager.getIns().getShowVideo() && show && !cate.categoryName.contains("网银")) {
                    DialogDepositHelp2.showIns(ActDepositNextQR.this, cate.categoryName + "扫码");
                    UserManager.getIns().setShowVideo(true);
                }
                if (stage == 1) {
                    B.vgStage1.setVisibility(View.VISIBLE);
                    B.vgStage2.setVisibility(View.GONE);
                    showTipDialog();
                } else {
                    updateOrder();
                }
            }
        });
    }

    private void updateOrder() {
        HttpActionTouCai.getRecordByCategoryId(Act, cate.id, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", LastOrderInfo.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    if (getFieldObject(extra, "data", null) != null) {
                        LastOrderInfo lastOrderInfo = getFieldObject(extra, "data", LastOrderInfo.class);
                        if (lastOrderInfo.rechargeOrder != null) {
                            if (lastOrderInfo.qrCodePayInfo != null) {
                                setIntent(lastOrderInfo, getIntent());

                                start = (int) (System.currentTimeMillis() / 1000);
                                seconds = getIntent().getIntExtra("seconds", 0);

                                setStage();
                            }
                        }
                    }
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                return true;
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        isStoped = true;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onDestroy() {
        receiver.unregister(this);
        super.onDestroy();
    }

    private void setStage() {
        B.vgStage1.setVisibility(View.GONE);
        B.vgStage2.setVisibility(View.VISIBLE);

        B.tvPayType2.setText(payName);
        B.tvOrderId2.setText(data.billno);
        B.tvAmount2.setText(Strs.of(data.amount));
        B.tvDate2.setText(data.date);

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        int tiemLeft = (int) (start + seconds - System.currentTimeMillis() / 1000);
        if (tiemLeft <= 0) {
            B.tvTimeLeft2.setText("支付已过期, 请先取消当前充值订单");
        } else {
            timer = new CountDownTimer(tiemLeft * 1000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                    long minute = millisUntilFinished / 1000 / 60;
                    long second = millisUntilFinished / 1000 % 60;

                    B.tvTimeLeft2.setText(Html.fromHtml("<font color=\"#999999\">请您在</font><font color=\"#FC3439\">"
                            + (minute + ":" + second)
                            + "</font><font color=\"#999999\">分钟内完成支付</font>"));

                    if (minute < 10 && second > 10) {
                        B.tvTimeLeft2.setText(Html.fromHtml("<font color=\"#999999\">请您在</font><font color=\"#FC3439\">"
                                + "0" + (minute + ":" + second)
                                + "</font><font color=\"#999999\">分钟内完成支付</font>"));
                    }

                    if (minute > 10 && second < 10) {
                        B.tvTimeLeft2.setText(Html.fromHtml("<font color=\"#999999\">请您在</font><font color=\"#FC3439\">"
                                + (minute + ":0" + second)
                                + "</font><font color=\"#999999\">分钟内完成支付</font>"));
                    }

                    if (minute < 10 && second < 10) {
                        B.tvTimeLeft2.setText(Html.fromHtml("<font color=\"#999999\">请您在</font><font color=\"#FC3439\">"
                                + "0" + (minute + ":0" + second)
                                + "</font><font color=\"#999999\">分钟内完成支付</font>"));
                    }

                }

                @Override
                public void onFinish() {
                    B.tvTimeLeft2.setText("支付已过期, 请先取消当前充值订单");
                }
            }.start();


        }
    }

    private void initView1() {
        chargeType = getIntent().getIntExtra("chargeType", 0);
        data = (RechargeResult) getIntent().getSerializableExtra("data");

        B.tvAmount1.setText(Nums.formatDecimal(data.amount, 2));

        B.btConfirm1.setText("前往" + cate.categoryName);
        B.btConfirm1.setOnClickListener((view) -> {
            UserManager.getIns().setDepositQrStage1Complete(true);
            openApp(cate.categoryName);
            setGuideUI(B.btConfirm1);
        });

        if (bitmap != null) {
            B.ivQrCode1.setImageBitmap(bitmap);
        }
    }

    private void initView2() {
        chargeType = getIntent().getIntExtra("chargeType", 0);
        data = (RechargeResult) getIntent().getSerializableExtra("data");

        B.tvAmount2.setText(Nums.formatDecimal(data.amount, 2));

        B.btConfirm2.setOnClickListener((view) -> {
            getDepositRecord();
            UserManager.getIns().setDepositQrStage2Complete(true);
            setGuideUI(B.btConfirm2);
        });
        B.btnOpenApp2.setText("打开" + cate.categoryName);
        B.btnOpenApp2.setOnClickListener((view) -> {
            openApp(cate.categoryName);
        });
        B.btnCancel2.setOnClickListener((view) -> {


            DialogsTouCai.showBindDialog(ActDepositNextQR.this, "检测当前订单未支付，是否取消订单重新下单？", "", "否", "是",
                    true, new AbCallback<Object>() {
                        @Override
                        public boolean callback(Object obj) {
                            HttpActionTouCai.cancelRechargeOrder(Act, data.billno, new AbHttpResult() {
                                @Override
                                public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                    if (code == 0 && error == 0) {
                                        Toasts.show(Act, "取消成功!", true);
                                        ThreadCollector.getIns().postDelayOnUIThread(300, new Runnable() {
                                            @Override
                                            public void run() {
                                                finish();
                                            }
                                        });
                                    } else {
                                        Toasts.show(Act, msg, false);
                                    }
                                    return true;
                                }
                            });
                            return false;
                        }
                    });

        });

        if (bitmap != null) {
            B.ivQrCode2.setImageBitmap(bitmap);
        }

        B.btQuestion1.setOnClickListener((view) -> {
            UserManager.getIns().getPersonSettingInfo(new UserManager.IUserInfoGetCallBack() {
                @Override
                public void onCallBack(PersonInfo personInfo) {
                    if (personInfo.isVipFlag()) {
                        ActWebX5.launch(Act, personInfo.getVipChannelUrl(), true);
                    } else {
                        ActWeb.launchCustomService(Act);
                    }
                }
            });
        });
    }


    public void openApp(String name) {
        try {
            String pkg = "";
            String cls = "";
            if (name.contains("微信")) {
                pkg = "com.tencent.mm";
                cls = "com.tencent.mm.ui.LauncherUI";
            } else if (name.toLowerCase().contains("qq")) {
                pkg = "com.tencent.mobileqq";
                cls = "com.tencent.mobileqq.activity.SplashActivity";
            } else if (name.contains("支付宝")) {
                pkg = "com.eg.android.AlipayGphone";
                cls = "com.eg.android.AlipayGphone.AlipayLogin";
            } else if (name.contains("云闪付")) {
                pkg = "com.unionpay";
                cls = "com.unionpay.activity.UPActivityWelcome";
            }
            ComponentName componet = new ComponentName(pkg, cls);
            //pkg 就是第三方应用的包名
            //cls 就是第三方应用的进入的第一个Activity
            Intent intent = new Intent();
            intent.setComponent(componet);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            if (name.contains("微信")) {
                DialogsTouCai.showBindDialog(this, "打开微信客户端失败或未安装微信客户端", "", "取消", "确认", true, new AbCallback<Object>() {
                    @Override
                    public boolean callback(Object obj) {
                        DialogsTouCai.hideBindTipDialog();
                        return false;
                    }
                });
            } else if (name.toLowerCase().contains("qq")) {
                DialogsTouCai.showBindDialog(this, "打开QQ客户端失败或未安装QQ客户端", "", "取消", "确认", true, new AbCallback<Object>() {
                    @Override
                    public boolean callback(Object obj) {
                        DialogsTouCai.hideBindTipDialog();
                        return false;
                    }
                });
            } else if (name.contains("支付宝")) {
                DialogsTouCai.showBindDialog(this, "打开支付宝客户端失败或未安装支付宝客户端", "",
                        "取消", "确认", true, new AbCallback<Object>() {
                            @Override
                            public boolean callback(Object obj) {
                                DialogsTouCai.hideBindTipDialog();
                                return false;
                            }
                        });
            } else if (name.contains("云闪付")) {
                DialogsTouCai.showBindDialog(this, "打开云闪付客户端失败或未安装云闪付客户端", "",
                        "取消", "确认", true, new AbCallback<Object>() {
                            @Override
                            public boolean callback(Object obj) {
                                DialogsTouCai.hideBindTipDialog();
                                return false;
                            }
                        });
            }
        }
    }

    public void findAll(String[] args) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager mPackageManager = Global.app.getPackageManager();
        List<ResolveInfo> mAllApps = mPackageManager.queryIntentActivities(mainIntent, 0);
        //按报名排序
        Collections.sort(mAllApps, new ResolveInfo.DisplayNameComparator(mPackageManager));

        for (ResolveInfo res : mAllApps) {
            //该应用的包名和主Activity
            String pkg = res.activityInfo.packageName;
            String cls = res.activityInfo.name;
            Log.d("pkg_cls", "pkg---" + pkg + "  cls---" + cls);

        }
    }

    public void copyMoney(View view) {
        ClipboardUtils.copyText(this, Views.getText(B.tvAmount2));
        Toasts.show("复制成功", true);
        setGuideUI(view);
    }


    private void setGuideUI(View view) {
        if (showGuide && !isComplete) {
            if (view.getId() == R.id.tvCopyAmount2) {
                guide_copy_bg.clearAnimation();
                guide_copy_bg.setVisibility(View.GONE);
                guide_copy_tip.setVisibility(View.GONE);

                guide_finish_bg.setVisibility(View.VISIBLE);
                guide_finish_bg.startAnimation(scaleAnimation);
                guide_finish_tip.setVisibility(View.VISIBLE);
            } else if (view.getId() == R.id.btConfirm2) {
                guide_finish_bg.clearAnimation();
                guide_finish_bg.setVisibility(View.GONE);
                guide_finish_tip.setVisibility(View.GONE);
            } else if (view.getId() == R.id.btConfirm1) {
                guide_confirm_tip.setVisibility(View.GONE);
                guide_confirm_bg.clearAnimation();
                guide_confirm_bg.setVisibility(View.GONE);
            }
        } else {
            hideAnimation();
        }
    }

    private void hideAnimation() {
        guide_copy_bg.clearAnimation();
        guide_copy_bg.setVisibility(View.GONE);
        guide_copy_tip.setVisibility(View.GONE);
        guide_finish_bg.clearAnimation();
        guide_finish_bg.setVisibility(View.GONE);
        guide_finish_tip.setVisibility(View.GONE);
        guide_confirm_tip.setVisibility(View.GONE);
        guide_confirm_bg.clearAnimation();
        guide_confirm_bg.setVisibility(View.GONE);
    }


    private void showTipDialog() {
        if (materialDialog != null) {
            materialDialog.dismiss();
        }

        ViewGroup viewGroup;

        if (showGuide) {
            viewGroup = (ViewGroup) LayoutInflater.from(ActDepositNextQR.this).inflate(R.layout.dialog_custom_tip_with_guide, null);
            ImageView guide_sure_bg = viewGroup.findViewById(R.id.guide_sure_bg);
            TextView guide_sure_tip = viewGroup.findViewById(R.id.guide_sure_tip);
            guide_sure_tip.setText("打开" + cate.categoryName + "进行充值");
            ScaleAnimation scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(ActDepositNextQR.this, R.anim.scale_animation_big);
            guide_sure_bg.startAnimation(scaleAnimation);

        } else {
            viewGroup = (ViewGroup) LayoutInflater.from(ActDepositNextQR.this).inflate(R.layout.dialog_custom_tip_half_colored, null);
        }

        final TextView tvContent = (TextView) viewGroup.findViewById(R.id.tvContent);
        tvContent.setText("充值二维码已保存到相册\n请打开" + cate.categoryName + "进行充值");

        Button btnNegative = (Button) viewGroup.findViewById(R.id.btnNegative);
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });

        Button btnConfirm = (Button) viewGroup.findViewById(R.id.btnPositive);
        btnConfirm.setText("打开" + cate.categoryName);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                stage = 2;
                openApp(cate.categoryName);
                materialDialog.dismiss();
            }
        });
        materialDialog = Dialogs.showCustomDialog(ActDepositNextQR.this, viewGroup, true);
    }


    public void getDepositRecord() {
        HttpAction.searchRechargeRecord(ActDepositNextQR.this, data.billno, "", null, null, null, 0, 1, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActDepositNextQR.this, "搜索中...");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespSearchRechargeRecord.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    HttpAction.RespSearchRechargeRecord respSearchOrder = getField(extra, "data", null);
                    if (respSearchOrder != null && respSearchOrder.list != null && respSearchOrder.list.size() != 0) {

                        RechargeRecordBean rechargeRecordBean = respSearchOrder.list.get(0);
                        if (rechargeRecordBean.orderStatus == 0) {
                            Toasts.show(ActDepositNextQR.this, "充值成功", true);
                            finish();
                        } else if (rechargeRecordBean.orderStatus == 6 || rechargeRecordBean.orderStatus == 7) {
                            DialogsTouCai.showBindDialog(ActDepositNextQR.this, "充值失败", "", "联系客服", "确定", true, new AbCallback<Object>() {
                                @Override
                                public boolean callback(Object obj) {
                                    ActWeb.launchCustomService(ActDepositNextQR.this);
                                    return false;
                                }
                            }, new AbCallback<Object>() {
                                @Override
                                public boolean callback(Object obj) {

                                    DialogsTouCai.hideBindTipDialog();
                                    return false;
                                }
                            });
                        } else {
                            DialogsTouCai.showBindDialog(ActDepositNextQR.this, "检测金额未到账", "请稍后查询",
                                    "联系客服", "确定", true, new AbCallback<Object>() {
                                        @Override
                                        public boolean callback(Object obj) {

                                            ActWeb.launchCustomService(ActDepositNextQR.this);

                                            return false;
                                        }
                                    }, new AbCallback<Object>() {
                                        @Override
                                        public boolean callback(Object obj) {

                                            DialogsTouCai.hideBindTipDialog();

                                            return false;
                                        }
                                    });
                        }

                    }
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                Dialogs.hideProgressDialog(ActDepositNextQR.this);
            }
        });
    }

    private void checkLastOrder(DepositCategory cate) {
        HttpActionTouCai.getRecordByCategoryId(this, cate.id, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", LastOrderInfo.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    if (getFieldObject(extra, "data", null) != null) {
                        LastOrderInfo lastOrderInfo = getFieldObject(extra, "data", LastOrderInfo.class);
                        if (lastOrderInfo == null
                                || lastOrderInfo.rechargeOrder == null
                                || lastOrderInfo.qrCodePayInfo == null
                                || (data != null && Strs.isEqual(data.billno, lastOrderInfo.rechargeOrder.spsn))) {
                            finish();
                        }
                    } else {
                        finish();
                    }
                }
                return true;
            }

        });
    }
}
