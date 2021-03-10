package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.callback.AbCallback;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.thread.ThreadCollector;
import com.ab.util.ClipboardUtils;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.bumptech.glide.Glide;
import com.desheng.app.toucai.action.HttpActionTouCai;
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
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;
import com.shark.tc.databinding.ActDepositNextTransBinding;

import java.util.HashMap;

import okhttp3.Request;

/**
 * 快速入款
 * Created by user on 2018/4/16.
 */
public class ActDepositNextTrans extends AbAdvanceActivity<ActDepositNextTransBinding> implements View.OnClickListener {

    RechargeInfo.TransferListBean trans;
    DepositCategory cate;
    private int stage = 1;
    private int chargeType;
    private boolean isStoped;

    private RechargeResult data;
    private String payName;
    private CountDownTimer timer;
    private View.OnClickListener copyListner;
    private LastOrderInfo lastTransPay;
    private int seconds;
    private int start;

    private ImageView guide_confirm_bg1, guide_copy_bg_bankname1;
    private TextView guide_confirm_tip1, guide_copy_bank_name_tip1;

    private ImageView guide_finish_bg2, guide_copy_bg_bankname2, guide_copy_bg2;
    private TextView guide_finish_tip2, guide_copy_bank_name_tip2, guide_copy_tip2;

    private ScaleAnimation scaleAnimation;

    private boolean server_show;
    private boolean isComplete;
    private UserManager.EventReceiver receiver;

    @Override
    protected int getLayoutId() {
        return R.layout.act_deposit_next_trans;
    }

    public static void launchForStage1(Activity act, DepositCategory cate, RechargeResult data, RechargeInfo.TransferListBean trans, String payName) {
        Intent intent = new Intent();
        intent.putExtra("chargeType", RechargeInfo.CHARGE_TYPE_TRANS);
        intent.putExtra("data", data);
        intent.putExtra("cate", cate);
        intent.putExtra("trans", trans);
        intent.putExtra("payName", payName);
        intent.putExtra("stage", 1);
        intent.setClass(act, ActDepositNextTrans.class);
        act.startActivity(intent);
    }

    public static void launchForStage2(Activity act, DepositCategory cate, LastOrderInfo lastOrderInfo, String payName) {
        Intent intent = new Intent();
        intent.putExtra("chargeType", RechargeInfo.CHARGE_TYPE_TRANS);
        intent.putExtra("cate", cate);

        RechargeInfo.TransferListBean trans = setIntent(lastOrderInfo, intent);

        intent.putExtra("trans", trans);
        intent.putExtra("payName", payName);
        intent.putExtra("stage", 2);

        intent.setClass(act, ActDepositNextTrans.class);
        act.startActivity(intent);
    }

    @NonNull
    public static RechargeInfo.TransferListBean setIntent(LastOrderInfo lastOrderInfo, Intent intent) {
        RechargeResult toData = new RechargeResult();
        toData.billno = lastOrderInfo.rechargeOrder.spsn;
        toData.amount = lastOrderInfo.rechargeOrder.cash;
        toData.date = Dates.getStringByFormat(lastOrderInfo.rechargeOrder.orderDate, Dates.dateFormatYMDHMS);

        intent.putExtra("data", toData);

        RechargeInfo.TransferListBean trans = new RechargeInfo.TransferListBean();
        trans.bankName = lastOrderInfo.remitBankInfo.bankName;
        trans.cardId = lastOrderInfo.remitBankInfo.remitBankNo;
        trans.cardName = lastOrderInfo.remitBankInfo.sn;
        intent.putExtra("seconds", lastOrderInfo.rechargeOrder.seconds);
        return trans;
    }

    @Override
    protected void onResume() {
        super.onResume();

        UserManager.getIns().isFirstRecharge(new UserManager.IGuideCallBack() {
            @Override
            public void onCallBack(boolean show) {
                server_show = show;
                resetGuideUI(show);
                if (!UserManager.getIns().getShowVideo() && show && !cate.categoryName.contains("网银")) {
                    DialogDepositHelp2.showIns(ActDepositNextTrans.this, cate.categoryName + "转账");
                    UserManager.getIns().setShowVideo(true);
                }
            }
        });
    }

    @Override
    protected void init() {

        stage = getIntent().getIntExtra("stage", 0);
        B.btConfirm2.setOnClickListener((view) -> {
            setGuideView(B.btConfirm2);
            getDepositRecord();
        });
        cate = (DepositCategory) getIntent().getSerializableExtra("cate");
        payName = getIntent().getStringExtra("payName");
        data = (RechargeResult) getIntent().getSerializableExtra("data");
        chargeType = getIntent().getIntExtra("chargeType", 0);
        trans = (RechargeInfo.TransferListBean) getIntent().getSerializableExtra("trans");

        if (cate.categoryName.equals("微信") || cate.categoryName.equals("支付宝")) {

            UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "充值");

            setToolbarButtonRightText(cate.categoryName + "转账教程");

            setToolbarButtonRightTextColor(R.color.white);

            setToolbarRightButtonGroupClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogDepositHelp2.showIns(ActDepositNextTrans.this, cate.categoryName + "转账");
                }
            });

        } else {
            UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "充值");
        }

        setStatusBarTranslucentAndLightContentWithPadding();


        copyListner = (view) -> {
            TextView target = (TextView) view.getTag();

            setGuideView(view);

            ClipboardUtils.copyText(Act, target.getText(), (success) -> {
                if (success) {
                    Toasts.show(Act, "复制成功", true);
                } else {
                    Toasts.show(Act, "剪切板不可用", false);
                }
            });
        };

        initGuideView();
        initView1();
        initView2();
        B.vgStage1.setVisibility(View.GONE);
        B.vgStage2.setVisibility(View.GONE);

        receiver = new UserManager.EventReceiver().register(this, (eventName, bundle) -> {
            if (UserManager.EVENT_USER_DEPOSIT_OK.equals(eventName)) {
                checkLastOrder(cate);
            }
        });
    }

    @Override
    protected void onDestroy() {
        receiver.unregister(this);
        super.onDestroy();
    }

    private void resetGuideUI(boolean show) {

        isComplete = (stage == 1) ? UserManager.getIns().getStageGuideComplete() : UserManager.getIns().getTransferGuideComplete();

        if (show && !isComplete) {
            if (stage == 1) {
                if (!isComplete) {
                    guide_copy_bank_name_tip1.setVisibility(show ? View.VISIBLE : View.GONE);
                    if (!show) {
                        guide_copy_bg_bankname1.clearAnimation();
                    }
                    guide_copy_bg_bankname1.setVisibility(show ? View.VISIBLE : View.GONE);
                } else {
                    guide_copy_bank_name_tip1.setVisibility(View.GONE);
                    guide_copy_bg_bankname1.clearAnimation();
                    guide_copy_bg_bankname1.setVisibility(View.GONE);
                }
                guide_confirm_bg1.clearAnimation();
                guide_confirm_bg1.setVisibility(View.GONE);
                guide_confirm_tip1.setVisibility(View.GONE);

                if (show) {
                    guide_copy_bg_bankname1.startAnimation(scaleAnimation);
                }

            } else if (stage == 2) {

                if (!isComplete) {

                    guide_copy_bank_name_tip2.setVisibility((show && !isComplete) ? View.VISIBLE : View.GONE);
                    if (!show) {
                        guide_copy_bg_bankname2.clearAnimation();
                    }
                    guide_copy_bg_bankname2.setVisibility((show && !isComplete) ? View.VISIBLE : View.GONE);
                } else {
                    guide_copy_bank_name_tip2.setVisibility(View.GONE);
                    guide_copy_bg_bankname2.clearAnimation();
                    guide_copy_bg_bankname2.setVisibility(View.GONE);
                }

                guide_copy_bg2.clearAnimation();
                guide_copy_bg2.setVisibility(View.GONE);
                guide_copy_tip2.setVisibility(View.GONE);

                guide_finish_bg2.clearAnimation();
                guide_finish_bg2.setVisibility(View.GONE);
                guide_finish_tip2.setVisibility(View.GONE);
            }
        } else {
            hideAnimation();
        }

    }

    private void hideAnimation() {
        guide_copy_bg2.clearAnimation();
        guide_copy_bg2.setVisibility(View.GONE);
        guide_copy_tip2.setVisibility(View.GONE);

        guide_copy_bg_bankname2.clearAnimation();
        guide_copy_bg_bankname2.setVisibility(View.GONE);
        guide_copy_bank_name_tip2.setVisibility(View.GONE);

        guide_copy_bg2.clearAnimation();
        guide_copy_bg2.setVisibility(View.GONE);
        guide_copy_tip2.setVisibility(View.GONE);

        guide_copy_bg_bankname1.clearAnimation();
        guide_copy_bg_bankname1.setVisibility(View.GONE);
        guide_copy_bank_name_tip1.setVisibility(View.GONE);

        guide_finish_bg2.clearAnimation();
        guide_finish_bg2.setVisibility(View.GONE);
        guide_finish_tip2.setVisibility(View.GONE);
    }

    private void setGuideView(View view) {
        isComplete = (stage == 1) ? UserManager.getIns().getStageGuideComplete() : UserManager.getIns().getTransferGuideComplete();

        if (server_show && !isComplete) {
            if (stage == 2) {
                if (view.equals(B.tvCopyCardName2)) {
                    guide_copy_bg_bankname2.clearAnimation();
                    guide_copy_bg_bankname2.setVisibility(View.GONE);
                    guide_copy_bank_name_tip2.setVisibility(View.GONE);
                    guide_copy_bg2.setVisibility(View.VISIBLE);
                    guide_copy_bg2.startAnimation(scaleAnimation);
                    guide_copy_tip2.setVisibility(View.VISIBLE);
                } else if (view.equals(B.tvCopyAmount2)) {
                    guide_copy_bg2.clearAnimation();
                    guide_copy_bg2.setVisibility(View.GONE);
                    guide_copy_tip2.setVisibility(View.GONE);
                    guide_finish_bg2.setVisibility(View.VISIBLE);
                    guide_finish_bg2.startAnimation(scaleAnimation);
                    guide_finish_tip2.setVisibility(View.VISIBLE);
                } else if (view.equals(B.btConfirm2)) {
                    guide_finish_bg2.clearAnimation();
                    guide_finish_bg2.setVisibility(View.GONE);
                    guide_finish_tip2.setVisibility(View.GONE);
                    UserManager.getIns().setTransferGuideComplete(true);
                }

            } else {

                if (view.equals(B.btConfirm1)) {
                    guide_confirm_bg1.clearAnimation();
                    guide_confirm_bg1.setVisibility(View.GONE);
                    guide_confirm_tip1.setVisibility(View.GONE);
                    UserManager.getIns().setStageGuideComplete(true);
                } else if (view.equals(B.tvCopyCardName1)) {
                    guide_copy_bg_bankname1.clearAnimation();
                    guide_copy_bg_bankname1.setVisibility(View.GONE);
                    guide_copy_bank_name_tip1.setVisibility(View.GONE);
                    if (!cate.categoryName.contains("网银")) {
                        UserManager.getIns().setStageGuideComplete(true);
                        guide_confirm_bg1.setVisibility(View.VISIBLE);
                        guide_confirm_tip1.setVisibility(View.VISIBLE);
                        guide_confirm_bg1.startAnimation(scaleAnimation);
                    }
                }
            }
        } else {
            hideAnimation();
        }
    }


    private void initGuideView() {
        scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(ActDepositNextTrans.this, R.anim.scale_animation_big);

        guide_confirm_bg1 = findViewById(R.id.guide_confirm_bg1);
        guide_confirm_tip1 = findViewById(R.id.guide_confirm_tip1);
        guide_copy_bg_bankname1 = findViewById(R.id.guide_copy_bg_bankname1);
        guide_copy_bank_name_tip1 = findViewById(R.id.guide_copy_bank_name_tip1);

        guide_finish_bg2 = findViewById(R.id.guide_finish_bg2);
        guide_finish_tip2 = findViewById(R.id.guide_finish_tip2);
        guide_copy_bg_bankname2 = findViewById(R.id.guide_copy_bg_bankname2);
        guide_copy_bank_name_tip2 = findViewById(R.id.guide_copy_bank_name_tip2);
        guide_copy_bg2 = findViewById(R.id.guide_copy_bg2);
        guide_copy_tip2 = findViewById(R.id.guide_copy_tip2);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (stage == 1) {
            B.vgStage1.setVisibility(View.VISIBLE);
            B.vgStage2.setVisibility(View.GONE);
        } else {
            getRecord();
        }

        isComplete = (stage == 1) ? UserManager.getIns().getStageGuideComplete() : UserManager.getIns().getStageGuideComplete();


    }

    private void initView1() {
        //银行卡
        B.tvAmount1.setText(Nums.formatDecimal(data.amount, 2));
        B.tvBankName1.setText(trans.bankName);
        String regex = "(.{" + 4 + "})";
        B.tvCardNum1.setText(trans.cardId.replaceAll(regex, "$1  "));
        B.tvCardName1.setText(trans.cardName);
        String bankFace = UserManager.getIns().getBankFace(trans.bankName);
        if (Strs.isNotEmpty(bankFace)) {
            Glide.with(this).load(Uri.parse(bankFace)).
                    placeholder(R.mipmap.ceb).
                    into(B.ivBankIcon1);
        }
        String bankBgTop = UserManager.getIns().getBankBgTop(trans.bankName);
        if (Strs.isNotEmpty(bankBgTop)) {
            Glide.with(this).load(Uri.parse(bankBgTop)).
                    into(B.ivBankCardUpBg1);
        }
        String bankBgBottom = UserManager.getIns().getBankBgBottom(trans.bankName);
        if (Strs.isNotEmpty(bankBgBottom)) {
            Glide.with(this).load(Uri.parse(bankBgBottom)).
                    into(B.ivBankCardBottomBg1);
        }

        B.tvCopyAmount1.setTag(B.tvAmount1);
        B.tvCopyAmount1.setOnClickListener(copyListner);

        B.tvCopyCardName1.setTag(B.tvCardName1);
        B.tvCopyCardName1.setOnClickListener(copyListner);

        B.tvCopyCardNum1.setTag(B.tvCardNum1);
        B.tvCopyCardNum1.setOnClickListener(copyListner);

        String cateName = cate.categoryName;
        if (Strs.isEmpty(cateName)) {
            cateName = "确定";
        } else {
            cateName = "前往" + cateName;
        }
        B.btConfirm1.setText(cateName);
        B.btConfirm1.setOnClickListener((view) -> {
            openApp(cate.categoryName);
            resetGuideUI(server_show);
        });

        if (cateName.contains("网银")) {
            B.btConfirm1.setVisibility(View.GONE);

        } else {
            B.btConfirm1.setVisibility(View.VISIBLE);
        }

        B.tvInfo.setText(Html.fromHtml("<font color=\"#FB7577\">为更快到账，请您按照系统生成的</font><font color=\"#259CFA\">存入金额</font><font color=\"#FB7577\">转账</font>"));

        B.tvService.setOnClickListener((view) -> {
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

    private void initView2() {
        B.tvAmount2.setText(Nums.formatDecimal(data.amount, 2));
        B.tvBankName2.setText(trans.bankName);
        String regex = "(.{" + 4 + "})";
        B.tvCardNum2.setText(trans.cardId.replaceAll(regex, "$1  "));
        B.tvCardName2.setText(trans.cardName);
        String bankFace = UserManager.getIns().getBankFace(trans.bankName);
        if (Strs.isNotEmpty(bankFace)) {
            Glide.with(this).load(Uri.parse(bankFace)).
                    placeholder(R.mipmap.ceb).
                    into(B.ivBankIcon2);
        }
        String bankBgTop = UserManager.getIns().getBankBgTop(trans.bankName);
        if (Strs.isNotEmpty(bankBgTop)) {
            Glide.with(this).load(Uri.parse(bankBgTop)).
                    into(B.ivBankCardUpBg2);
        }
        String bankBgBottom = UserManager.getIns().getBankBgBottom(trans.bankName);
        if (Strs.isNotEmpty(bankBgBottom)) {
            Glide.with(this).load(Uri.parse(bankBgBottom)).
                    into(B.ivBankCardBottomBg2);
        }

        B.tvCopyAmount2.setTag(B.tvAmount2);
        B.tvCopyAmount2.setOnClickListener(copyListner);

        B.tvCopyCardName2.setTag(B.tvCardName2);
        B.tvCopyCardName2.setOnClickListener(copyListner);

        B.tvCopyCardNum2.setTag(B.tvCardNum2);
        B.tvCopyCardNum2.setOnClickListener(copyListner);

        B.btnCancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogsTouCai.showBindDialog(ActDepositNextTrans.this, "检测当前订单未支付，是否取消订单重新下单？", "", "否", "是", true, new AbCallback<Object>() {
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

            }
        });

        start = (int) (System.currentTimeMillis() / 1000);
        seconds = getIntent().getIntExtra("seconds", 0);

        String cateName = cate.categoryName;
        if (Strs.isEmpty(cateName)) {
            cateName = "确定";
        } else {
            cateName = "打开" + cateName;
        }
        B.btnOpenApp2.setText(cateName);
        B.btnOpenApp2.setOnClickListener((view) -> {
            openApp(cate.categoryName);

            setGuideView(B.btnOpenApp2);

        });

        if (cateName.contains("网银")) {
            B.btnOpenApp2.setVisibility(View.GONE);
        } else {
            B.btnOpenApp2.setVisibility(View.VISIBLE);
        }
    }


    private void getRecord() {
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
                            if (lastOrderInfo.remitBankInfo != null) {
                                setIntent(lastOrderInfo, getIntent());

                                start = (int) (System.currentTimeMillis() / 1000);
                                seconds = getIntent().getIntExtra("seconds", 0);

                                stage = 2;
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

    private void setStage() {
        if (stage == 1) {
            B.vgStage1.setVisibility(View.VISIBLE);
            B.vgStage2.setVisibility(View.GONE);
        } else {
            B.vgStage1.setVisibility(View.GONE);
            B.vgStage2.setVisibility(View.VISIBLE);

            B.tvPayType2.setText(payName);
            B.tvOrderId2.setText(data.billno);
            B.tvAmount2.setText(Strs.of(data.amount));
            B.tvDate2.setText(data.date);

            if (timer != null) {
                timer.cancel();
            }

            int tiemLeft = (int) (start + seconds - System.currentTimeMillis() / 1000);
            if (tiemLeft <= 0) {
                getRecord();
                return;
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
                        timer = null;
                    }
                }.start();
            }
        }
    }

    public void openApp(String code) {
        try {
            stage = 2;
            String pkg = "";
            String cls = "";
            if (code.equals("微信")) {
                pkg = "com.tencent.mm";
                cls = "com.tencent.mm.ui.LauncherUI";
            } else if (code.toLowerCase().equals("qq")) {
                pkg = "com.tencent.mobileqq";
                cls = "com.tencent.mobileqq.activity.SplashActivity";
            } else if (code.equals("支付宝")) {
                pkg = "com.eg.android.AlipayGphone";
                cls = "com.eg.android.AlipayGphone.AlipayLogin";
            } else if (code.equals("云闪付")) {
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

            if (code.equals("微信")) {
                DialogsTouCai.showBindDialog(this, "打开微信客户端失败或未安装微信客户端", "", "取消", "确认", true, new AbCallback<Object>() {
                    @Override
                    public boolean callback(Object obj) {
                        DialogsTouCai.hideBindTipDialog();
                        return false;
                    }
                });
            } else if (code.toLowerCase().equals("qq")) {
                DialogsTouCai.showBindDialog(this, "打开QQ客户端失败或未安装QQ客户端", "", "取消", "确认", true, new AbCallback<Object>() {
                    @Override
                    public boolean callback(Object obj) {
                        DialogsTouCai.hideBindTipDialog();
                        return false;
                    }
                });
            } else if (code.equals("支付宝")) {
                DialogsTouCai.showBindDialog(this, "打开支付宝客户端失败或未安装支付宝客户端", "", "取消", "确认", true, new AbCallback<Object>() {
                    @Override
                    public boolean callback(Object obj) {
                        DialogsTouCai.hideBindTipDialog();
                        return false;
                    }
                });
            } else if (code.equals("云闪付")) {
                DialogsTouCai.showBindDialog(this, "打开云闪付客户端失败或未安装云闪付客户端", "", "取消",
                        "确认", true, new AbCallback<Object>() {
                            @Override
                            public boolean callback(Object obj) {
                                DialogsTouCai.hideBindTipDialog();
                                return false;
                            }
                        });
            }
            stage = 2;
            setStage();
        }
    }

    public void getDepositRecord() {
        HttpAction.searchRechargeRecord(ActDepositNextTrans.this, data.billno, "", null, null, null, 0, 1, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActDepositNextTrans.this, "搜索中...");
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
                            Toasts.show("充值成功", true);
                            finish();
                        } else if (rechargeRecordBean.orderStatus == 6 || rechargeRecordBean.orderStatus == 7) {
                            DialogsTouCai.showBindDialog(ActDepositNextTrans.this, "充值失败", "", "联系客服", "确定", true, new AbCallback<Object>() {
                                @Override
                                public boolean callback(Object obj) {

                                    ActWeb.launchCustomService(ActDepositNextTrans.this);
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

                            DialogsTouCai.showBindDialog(ActDepositNextTrans.this, "检测金额未到账", "请稍后查询",
                                    "联系客服", "确定", true, new AbCallback<Object>() {
                                        @Override
                                        public boolean callback(Object obj) {

                                            ActWeb.launchCustomService(ActDepositNextTrans.this);

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
                Dialogs.hideProgressDialog(ActDepositNextTrans.this);
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
                                || lastOrderInfo.remitBankInfo == null
                                || (trans != null && Strs.isEqual(lastOrderInfo.remitBankInfo.remitBankNo, trans.cardId))) {
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
