package com.desheng.base.panel;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.model.BetOrder;
import com.desheng.base.model.ChaseRecordBean;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;

import java.util.HashMap;

import okhttp3.Request;

/**
 * 订单详情
 */
public class ActBetOrderDetail extends AbAdvanceActivity {
    
    private TextView tv_order_number;
    private TextView tv_order_type;
    private TextView tv_play_type;
    private TextView tv_money_type;
    //    private TextView tv_award_type;
    private TextView tv_join_time;
    private TextView tv_confirm_amount;
    private TextView tv_award;
    //    private TextView tv_profit_status;
    private TextView tv_status;
    private TextView tv_period_number;
    private TextView tv_order_count;
    //    private TextView tv_times_count;
    private TextView tv_back_point;
    private TextView tv_award_num;
//    private TextView tv_confirm_num;
    
    private ChaseRecordBean recordBean;
    
    public static void launch(Activity activity, ChaseRecordBean recordBean) {
        Intent intent = new Intent(activity, ActBetOrderDetail.class);
        intent.putExtra("chase_detail", recordBean);
        activity.startActivity(intent);
    }
    
    @Override
    protected int getLayoutId() {
        return R.layout.act_chase_detail;
    }
    
    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "订单详情");
        setStatusBarTranslucentAndLightContentWithPadding();
        recordBean = (ChaseRecordBean) getIntent().getSerializableExtra("chase_detail");
        
        if (null != recordBean) {
            tv_order_number = (TextView) findViewById(R.id.tv_order_number);
            tv_order_type = (TextView) findViewById(R.id.tv_order_type);
            tv_play_type = (TextView) findViewById(R.id.tv_play_type);
            tv_money_type = (TextView) findViewById(R.id.tv_money_type);
            tv_join_time = (TextView) findViewById(R.id.tv_join_time);
            tv_confirm_amount = (TextView) findViewById(R.id.tv_confirm_amount);
            tv_award = (TextView) findViewById(R.id.tv_award);
            tv_status = (TextView) findViewById(R.id.tv_status);
            tv_period_number = (TextView) findViewById(R.id.tv_period_number);
            tv_order_count = (TextView) findViewById(R.id.tv_order_count);
            tv_back_point = (TextView) findViewById(R.id.tv_back_point);
            tv_award_num = (TextView) findViewById(R.id.tv_award_num);
        } else {
            finish();
        }
        
        
        tv_order_number.setText(recordBean.billno);
        tv_order_type.setText(recordBean.lottery);
        tv_play_type.setText(recordBean.method);
        tv_money_type.setText("元");
//        tv_award_type.setText();
        tv_join_time.setText(Dates.getStringByFormat(recordBean.orderTime, Dates.dateFormatYMDHM));
        tv_confirm_amount.setText("" + recordBean.totalMoney);
        tv_award.setText("" + recordBean.winMoney);
        tv_status.setText(recordBean.statusStr);
        tv_period_number.setText("" + recordBean.startIssue);
        tv_order_count.setText("" + recordBean.totalCount);
        tv_back_point.setText((recordBean.point * 100) + "%");
        tv_award_num.setText(recordBean.content);
    }
   
    
    private void getOrderDetail(String billNo){
        HttpAction.getOrderDetail(ActBetOrderDetail.this, billNo, new AbHttpResult(){
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", BetOrder.class);
            }
    
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActBetOrderDetail.this, "");
                    }
                });
            }
    
            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                
                return true;
            }
    
            @Override
            public void onAfter(int id) {
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideProgressDialog(ActBetOrderDetail.this);
                    }
                });
            }
        });
    }
}
