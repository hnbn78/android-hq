package com.desheng.base.panel;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Maps;
import com.ab.util.Nums;
import com.ab.util.Toasts;
import com.ab.view.MaterialDialog;
import com.desheng.base.R;

import com.desheng.base.action.HttpAction;
import com.desheng.base.context.BetStatus;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.BetOrder;
import com.desheng.base.model.GameRecord;
import com.desheng.base.model.LotteryOrderInfo;
import com.google.gson.Gson;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.Request;

public class ActLotteryDetail extends AbAdvanceActivity implements View.OnClickListener {
    public static final String ID = "_id";
    public static final String STATE = "_state";


    public static void launch(Context context, GameRecord info) {
        Intent itt = new Intent(context, ActLotteryDetail.class);
        HashMap<String, Object> lotteryInfo = new HashMap<>(18);
        lotteryInfo.put(ID, info.billno);
        lotteryInfo.put(CtxLottery.Order.LOTTERY, info.lottery);
        lotteryInfo.put(CtxLottery.Order.PLAY_NAME, info.method);
        lotteryInfo.put(CtxLottery.Order.ISSUE, info.issue);
        lotteryInfo.put(CtxLottery.Order.MODEL, info.model);
        lotteryInfo.put(CtxLottery.Order.CODE, info.code);
        lotteryInfo.put(CtxLottery.Order.MONEY, info.money);
        lotteryInfo.put(CtxLottery.Order.MULTIPLE, info.multiple);
        lotteryInfo.put(CtxLottery.Order.HIT, info.nums);
        lotteryInfo.put(STATE, CtxLottery.formatUserBetsStatus(info.status));
        lotteryInfo.put(CtxLottery.Order.CONTENT, info.content);
        itt.putExtra("info", lotteryInfo);
        itt.putExtra("kind", info.lotteryId);
        context.startActivity(itt);
    }

    public static void launch(Context context, LotteryOrderInfo info) {
        Intent itt = new Intent(context, ActLotteryDetail.class);
        HashMap<String, Object> lotteryInfo = new HashMap<>(18);
        lotteryInfo.put(ID, info.billno);
        lotteryInfo.put(CtxLottery.Order.LOTTERY, info.lottery);
        lotteryInfo.put(CtxLottery.Order.PLAY_NAME, info.method);
        lotteryInfo.put(CtxLottery.Order.ISSUE, info.issue);
        lotteryInfo.put(CtxLottery.Order.MODEL, info.model);
        lotteryInfo.put(CtxLottery.Order.CODE, info.code);
        lotteryInfo.put(CtxLottery.Order.MONEY, info.money);
        lotteryInfo.put(CtxLottery.Order.MULTIPLE, info.multiple);
        lotteryInfo.put(CtxLottery.Order.HIT, info.nums);
        lotteryInfo.put(STATE, CtxLottery.formatUserBetsStatus(info.status));
        lotteryInfo.put(CtxLottery.Order.CONTENT, info.content);
        itt.putExtra("info", lotteryInfo);
        itt.putExtra("kind", info.lotteryId);
        context.startActivity(itt);
    }

    public static void launch(Context context, HashMap<String, Object> lotteryInfo, int kind) {
        Intent itt = new Intent(context, ActLotteryDetail.class);
        lotteryInfo.put(STATE, "未加入");
        itt.putExtra("info", lotteryInfo);
        itt.putExtra("kind", kind);
        context.startActivity(itt);

    }

    private Button btnCancelOrder;
    private int kind;
    private String lotteryId;

    @Override
    protected int getLayoutId() {
        return R.layout.act_lottery_detail;
    }

    @Override
    protected void init() {
        setToolbarLeftBtn(UIHelper.BACK_WHITE_ARROR);
        setToolbarTitleCenter("订单详情");
        if (UIHelper.toolbarBgResId > 0)
            setToolbarBgImage(UIHelper.toolbarBgResId);
        else
            setToolbarBgColor(R.color.colorPrimary);
        setToolbarTitleCenterColor(com.desheng.base.R.color.white);
        setStatusBarTranslucentAndLightContentWithPadding();

        HashMap<String, Object> info = (HashMap<String, Object>) getIntent().getSerializableExtra("info");
        lotteryId = String.valueOf(info.get(ID));
        kind = getIntent().getIntExtra("kind", 0);

        btnCancelOrder = findViewById(R.id.btn_cancelOrder);
        btnCancelOrder.setOnClickListener(this);
        getOrderDetail(lotteryId);
    }

    private void getOrderDetail(String billNo) {
        HttpAction.getOrderDetail(ActLotteryDetail.this, billNo, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", BetOrder.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActLotteryDetail.this, "");
                    }
                });
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                BetOrder betOrder = getField(extra, "data", null);

                if (betOrder != null)
                    updateData(betOrder);

                return true;
            }

            @Override
            public void onAfter(int id) {
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideProgressDialog(ActLotteryDetail.this);
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        final MaterialDialog dialog = new MaterialDialog(ActLotteryDetail.this);

        dialog.setTitle("撤销提示");
        dialog.setMessage("您确定要撤销订单？");
        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAction.cancelOrder(ActLotteryDetail.this, String.valueOf(lotteryId), new AbHttpResult() {
                    @Override
                    public void onBefore(Request request, int id, String host, String funcName) {
                        btnCancelOrder.post(new Runnable() {
                            @Override
                            public void run() {
                                Dialogs.showProgressDialog(ActLotteryDetail.this, "");
                            }
                        });
                    }

                    @Override
                    public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                        dialog.dismiss();

                        if (code == 0 && error == 0) {
                            Toasts.show(ActLotteryDetail.this, "撤单成功", true);
                            btnCancelOrder.setVisibility(View.GONE);
                            UserManager.getIns().refreshUserData();
                        } else {
                            Toasts.show(ActLotteryDetail.this, msg, false);
                        }
                        return true;
                    }

                    @Override
                    public void onAfter(int id) {
                        btnCancelOrder.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Dialogs.hideProgressDialog(ActLotteryDetail.this);
                                getOrderDetail(lotteryId);
                            }
                        }, 500);
                    }
                });
            }
        });
        dialog.show();
    }

    private void updateData(BetOrder betOrder) {
        ((TextView) findViewById(R.id.tv_billno)).setText("" + betOrder.billno);
        ((TextView) findViewById(R.id.tv_kind)).setText("" + betOrder.lottery);
        ((TextView) findViewById(R.id.tv_method)).setText("" + betOrder.method);
        ((TextView) findViewById(R.id.tv_issue)).setText("" + betOrder.issue);
        ((TextView) findViewById(R.id.tv_code)).setText("" + betOrder.code);
        ((TextView) findViewById(R.id.tv_order_time)).setText(Dates.getStringByFormat(betOrder.orderTime, Dates.dateFormatYMDHMS));
        ((TextView) findViewById(R.id.tv_money)).setText(betOrder.money + "元");
        ((TextView) findViewById(R.id.tv_multi)).setText("" + betOrder.multiple);
        ((TextView) findViewById(R.id.tv_hit)).setText("" + betOrder.nums);
        ((TextView) findViewById(R.id.tv_win)).setText("" + betOrder.winMoney + "元");
        ((TextView) findViewById(R.id.tv_state)).setText("" + betOrder.statusRemark);
        ((TextView) findViewById(R.id.tv_point)).setText("" + Nums.formatDecimal(betOrder.point, 1));
        ((TextView) findViewById(R.id.tv_open)).setText("" + betOrder.openCode);
        ((TextView) findViewById(R.id.tv_content)).setText("" + betOrder.content);

        String model;
        if ("yuan".equals(betOrder.model))
            model = "元";
        else if ("jiao".equals(betOrder.model))
            model = "角";
        else if ("fen".equals(betOrder.model))
            model = "分";
        else if ("li".equals(betOrder.model))
            model = "厘";
        else
            model = "";
        ((TextView) findViewById(R.id.tv_model)).setText(model);


        switch (BetStatus.find(betOrder.status)) {
            case AWARDED:
            case WAIT_AWARD:
            case WIN:
            case NON_WIN:
                ((TextView) findViewById(R.id.tv_profit)).setText("" + Nums.formatDecimal((betOrder.winMoney - betOrder.money), 4) + "元");
            break;

            default:
                ((TextView) findViewById(R.id.tv_profit)).setText("");
        }

        if (BetStatus.NORMAL.getCode() == betOrder.status)
            btnCancelOrder.setVisibility(View.VISIBLE);
        else
            btnCancelOrder.setVisibility(View.GONE);
    }
}
