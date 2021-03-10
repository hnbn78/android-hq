package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Toasts;
import com.ab.view.MaterialDialog;
import com.desheng.app.toucai.model.ChaseDetailBean;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.BetStatus;
import com.desheng.base.manager.UserManager;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.shark.tc.R;

import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ActChaseRecordDetail extends AbAdvanceActivity {

    private TextView tv_bet_num, tv_order_num, tv_issue_num,
            tv_game_type, tv_order_time, tv_result_status, tv_bet_amount,
            tv_award_amount, tv_status, tv_bet_unit, tv_award_unit,tvCountIssue,tvLotteryName,tvCountChased;

    private Button btn_cancel_order, btn_chase_list;
    private String billno;
    private List<ChaseDetailBean.ChaseListBean> listBeans;

    /**
     * @param billno 期数
     * @param chased 已追期号
     */
    public static void launch(Activity act, String billno,String chased) {
        Intent intent = new Intent(act, ActChaseRecordDetail.class);
        intent.putExtra("billno", billno);
        intent.putExtra("chased", chased);
        act.startActivity(intent);

    }

    @Override
    protected void init() {
        setStatusBarTranslucentAndLightContentWithPadding();
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "追号详情");

        billno = getIntent().getStringExtra("billno");
        String chased  = getIntent().getStringExtra("chased");
        tv_bet_num = findViewById(R.id.tv_bet_num);
        tv_order_num = findViewById(R.id.tv_order_num);
        tv_issue_num = findViewById(R.id.tv_issue_num);
        tv_game_type = findViewById(R.id.tv_game_type);
        tv_order_time = findViewById(R.id.tv_order_time);
        tv_result_status = findViewById(R.id.tv_result_status);
        tv_bet_amount = findViewById(R.id.tv_bet_amount);
        tv_award_amount = findViewById(R.id.tv_award_amount);
        tv_status = findViewById(R.id.tv_status);
        btn_cancel_order = findViewById(R.id.btn_cancel_order);
        btn_chase_list = findViewById(R.id.btn_chase_list);
        tv_bet_unit = findViewById(R.id.tv_bet_unit);
        tv_award_unit = findViewById(R.id.tv_award_unit);
        tvCountIssue = findViewById(R.id.tvCountIssue);
        tvLotteryName = findViewById(R.id.tvLotteryName);
        tvCountChased = findViewById(R.id.tvCountChased);
        tvCountChased.setText(chased);
        btn_cancel_order.setOnClickListener(this);
        btn_chase_list.setOnClickListener(this);
        btn_chase_list.setVisibility(View.VISIBLE);
        findViewById(R.id.vg_result_status).setVisibility(View.GONE);
        findViewById(R.id.gameResult).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.tvGameType)).setText("玩法");
        getChaseDetail();
    }

    private ChaseDetailBean betOrder;
    private void getChaseDetail(){
        HttpAction.getChaseDetail(null,billno,new AbHttpResult(){

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                super.setupEntity(entity);
                entity.putField("data", ChaseDetailBean.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActChaseRecordDetail.this, "");
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
                        Dialogs.hideProgressDialog(ActChaseRecordDetail.this);
                    }
                });
            }

        });
    }

    private void updateData(ChaseDetailBean betOrder) {
        listBeans = betOrder.chaseList;
        tv_bet_num.setText("" + betOrder.content);
        tv_order_num.setText(betOrder.billno);
        tv_issue_num.setText(betOrder.startIssue);
        tv_game_type.setText(betOrder.method);
        tv_order_time.setText(Dates.getStringByFormat(betOrder.orderTime, Dates.dateFormatYMDHMS));
        tvCountIssue.setText(String.valueOf(listBeans.size()));
        tvLotteryName.setText(betOrder.lottery);

        if(betOrder.winMoney>0){
            tv_result_status.setText("已中奖");
        }else{
            tv_result_status.setText("未中奖");
        }

        tv_bet_amount.setText( Nums.formatDecimal( betOrder.totalMoney,3));

        tv_status.setText(betOrder.statusStr);
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
            case AWARDED:
            case WAIT_AWARD:
            case WIN:
            case NON_WIN:
                break;

            default:
                tv_award_amount.setText(Nums.formatDecimal( betOrder.winMoney,3));
        }

        if (betOrder.allowCancel)
            btn_cancel_order.setVisibility(View.VISIBLE);
        else
            btn_cancel_order.setVisibility(View.GONE);

        if (betOrder.totalCount>0){
            btn_chase_list.setVisibility(View.VISIBLE);
        }else{
            btn_chase_list.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_bet_detail;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == btn_cancel_order) {

            final MaterialDialog dialog = new MaterialDialog(ActChaseRecordDetail.this);

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
                    HttpAction.cancelChase(ActChaseRecordDetail.this, billno, new AbHttpResult() {
                        @Override
                        public void onBefore(Request request, int id, String host, String funcName) {
                            btn_cancel_order.post(new Runnable() {
                                @Override
                                public void run() {
                                    Dialogs.showProgressDialog(ActChaseRecordDetail.this, "");
                                }
                            });
                        }

                        @Override
                        public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                            dialog.dismiss();

                            if (code == 0 && error == 0) {
                                Toasts.show(ActChaseRecordDetail.this, "撤单成功", true);
                                getChaseDetail();
                                UserManager.getIns().refreshUserData();
                            } else {
                                Toasts.show(ActChaseRecordDetail.this, msg, false);
                            }
                            return true;
                        }

                        @Override
                        public void onAfter(int id) {
                            btn_cancel_order.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Dialogs.hideProgressDialog(ActChaseRecordDetail.this);
                                }
                            }, 500);
                        }
                    });
                }
            });
            dialog.show();

        } else if (v == btn_chase_list) {
            if (null != betOrder && null!=betOrder.chaseList) {
                ActChaseIssueList.launch(this, betOrder.chaseList);
            } else {
                Toasts.show(ActChaseRecordDetail.this, "投注详情获取失败");
            }
        }
    }
}
