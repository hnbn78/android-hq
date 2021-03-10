package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.event.RequestRefreshMode;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.BetStatus;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.BetOrder;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import okhttp3.Request;

public class ActBetRecordDetail extends AbAdvanceActivity {

    private TextView tv_bet_num, tv_order_num, tv_issue_num,
            tv_game_type, tv_game_result, tv_order_time, tv_result_status, tv_bet_amount,
            tv_award_amount, tv_status, tv_bet_unit, tv_award_unit;

    private Button btn_cancel_order, btn_once_more;

    private LinearLayout layout_open_code;

    private String billno;
    private BetOrder betOrder;

    public static void launch(Activity act, String billno) {
        Intent intent = new Intent(act, ActBetRecordDetail.class);
        intent.putExtra("billno", billno);
        act.startActivity(intent);
    }

    @Override
    protected void init() {
        setStatusBarTranslucentAndLightContentWithPadding();
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "投注详情");

        tv_bet_num = findViewById(R.id.tv_bet_num);
        tv_order_num = findViewById(R.id.tv_order_num);
        tv_issue_num = findViewById(R.id.tv_issue_num);
        tv_game_type = findViewById(R.id.tv_game_type);
        tv_game_result = findViewById(R.id.tv_game_result);
        tv_order_time = findViewById(R.id.tv_order_time);
        tv_result_status = findViewById(R.id.tv_result_status);
        tv_bet_amount = findViewById(R.id.tv_bet_amount);
        tv_award_amount = findViewById(R.id.tv_award_amount);
        tv_status = findViewById(R.id.tv_status);
        btn_cancel_order = findViewById(R.id.btn_cancel_order);
        btn_once_more = findViewById(R.id.btn_once_more);
        tv_bet_unit = findViewById(R.id.tv_bet_unit);
        tv_award_unit = findViewById(R.id.tv_award_unit);
        layout_open_code = findViewById(R.id.layout_open_code);
        findViewById(R.id.vgCountIssue).setVisibility(View.GONE);
        findViewById(R.id.vgCountChased).setVisibility(View.GONE);
        findViewById(R.id.vgLotteryName).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.tv_issue)).setText("期号");
        btn_cancel_order.setOnClickListener(this);
        btn_once_more.setOnClickListener(this);

        billno = getIntent().getStringExtra("billno");

        getOrderDetail(billno);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_bet_detail;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == btn_cancel_order) {
            DialogsTouCai.showDialog(this, "撤销提示", "您确定要撤销订单？", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCancelOrder();
                }
            });

        } else if (v == btn_once_more) {

            HttpActionTouCai.onceMoreBetting(this, billno, new AbHttpResult() {
                @Override
                public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                    if (code == 0 && error == 0) {
                        DialogsTouCai.showLotteryBuyOkDialog(ActBetRecordDetail.this);
                        EventBus.getDefault().post(new RequestRefreshMode());
                    }
                    return super.onSuccessGetObject(code, error, msg, extra);
                }
            });
        }
    }

    private void onCancelOrder() {
        HttpAction.cancelOrder(ActBetRecordDetail.this, billno, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                btn_cancel_order.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActBetRecordDetail.this, "");
                    }
                });
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    Toasts.show(ActBetRecordDetail.this, "撤单成功", true);
                    EventBus.getDefault().post(new RequestRefreshMode());
                    getOrderDetail(billno);
                    UserManager.getIns().refreshUserData();
                } else {
                    Toasts.show(ActBetRecordDetail.this, msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                btn_cancel_order.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideProgressDialog(ActBetRecordDetail.this);
                    }
                }, 500);
            }
        });
    }

    private void getOrderDetail(String billNo) {
        HttpAction.getOrderDetail(ActBetRecordDetail.this, billNo, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", BetOrder.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActBetRecordDetail.this, "");
                    }
                });
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                betOrder = getField(extra, "data", null);

                if (betOrder != null)
                    updateData(betOrder);
                return true;
            }

            @Override
            public void onAfter(int id) {
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideProgressDialog(ActBetRecordDetail.this);
                    }
                });
            }
        });
    }

    private void updateData(BetOrder betOrder) {
        tv_bet_num.setText("" + betOrder.content);
        tv_order_num.setText(betOrder.billno);
        tv_issue_num.setText(betOrder.issue);
        tv_game_type.setText(betOrder.lottery);

        tv_order_time.setText(Dates.getStringByFormat(betOrder.orderTime, Dates.dateFormatYMDHMS));
        tv_result_status.setText(betOrder.statusRemark);
        tv_bet_amount.setText("" + betOrder.money);
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), betOrder.lottery);

        if (Strs.isNotEmpty(betOrder.openCode)) {
            tv_game_result.setVisibility(View.GONE);
            layout_open_code.removeAllViews();
            String[] codes = betOrder.openCode.split(",");
            for (String code : codes) {
                TextView tv_code = (TextView) LayoutInflater.from(this).inflate(R.layout.view_circle_code, null);
                tv_code.setText(code);
                layout_open_code.addView(tv_code);
            }

        } else {
            tv_game_result.setVisibility(View.VISIBLE);
            tv_game_result.setText(betOrder.statusRemark);
        }

        tv_status.setText(betOrder.statusRemark);
        String model = "元";
//        if ("yuan".equals(betOrder.model))
//            model = "元";
//        else if ("jiao".equals(betOrder.model))
//            model = "角";
//        else if ("fen".equals(betOrder.model))
//            model = "分";
//        else if ("li".equals(betOrder.model))
//            model = "厘";
//        else
//            model = "";
        tv_award_unit.setText("中奖金额(" + model + ")");
        tv_bet_unit.setText("投注金额(" + model + ")");

        switch (BetStatus.find(betOrder.status)) {
            case NORMAL:
            case WAIT_AWARD:
                break;

            default:
                tv_award_amount.setText("" + betOrder.winMoney);
        }

        if (BetStatus.NORMAL.getCode() == betOrder.status)
            btn_cancel_order.setVisibility(View.VISIBLE);
        else
            btn_cancel_order.setVisibility(View.GONE);
    }
}
