package com.desheng.app.toucai.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ab.util.Strs;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.panel.ActChaseRecordDetail;
import com.desheng.base.model.ChaseRecordBean;
import com.shark.tc.R;

import java.util.List;

public class ChaseRecordAdapter extends BaseQuickAdapter<ChaseRecordBean, ChaseRecordAdapter.ViewHolder> {
    private Context ctx;

    public ChaseRecordAdapter(Context ctx, @Nullable List<ChaseRecordBean> data) {
        super(R.layout.item_chase_record, data);
        this.ctx = ctx;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

    }

    @Override
    protected void convert(final ChaseRecordAdapter.ViewHolder helper, final ChaseRecordBean item) {

        helper.tv_lottery_amount.setText("" + item.totalMoney);
        helper.tv_Lottery_name.setText(item.lottery);

        helper.tv_play_method.setText(item.method);
        helper.tv_lottery_num.setText("第" + item.startIssue + "期");

        helper.tv_lottery_profit.setText("+" + item.winMoney);

        helper.tv_chase_times.setText("已追" + item.clearCount + "期");

        helper.tv_lottery_status.setText(item.statusStr);

        if (Strs.isNotEmpty(item.statusStr)&&item.statusStr.contains("撤单")) {
            helper.tv_lottery_amount.setTextColor(ctx.getResources().getColor(R.color.gray_content));
            helper.tv_lottery_status.setTextColor(ctx.getResources().getColor(R.color.gray_content));
            helper.tv_lottery_profit.setVisibility(View.GONE);
        } else {

            if(item.winMoney>0){
                helper.tv_lottery_amount.setTextColor(ctx.getResources().getColor(R.color.red_mine));
                helper.tv_lottery_profit.setTextColor(ctx.getResources().getColor(R.color.red_mine));
                helper.tv_lottery_profit.setVisibility(View.VISIBLE);
            }else{
                helper.tv_lottery_amount.setTextColor(ctx.getResources().getColor(R.color.blue_mine));
                helper.tv_lottery_profit.setVisibility(View.GONE);
            }

            helper.tv_lottery_status.setTextColor(ctx.getResources().getColor(R.color.red_mine));

        }

        helper.layout_bet_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActChaseRecordDetail.launch((Activity) mContext,""+item.billno ,String.valueOf(item.clearCount));
            }
        });
    }

    public class ViewHolder extends BaseViewHolder {
        TextView tv_Lottery_name;
        TextView tv_lottery_num;
        TextView tv_play_method;
        TextView tv_lottery_amount;
        TextView tv_lottery_profit;
        TextView tv_lottery_status;
        TextView tv_chase_times;
        FrameLayout layout_bet_item;

        public ViewHolder(View view) {
            super(view);
            layout_bet_item = view.findViewById(R.id.layout_bet_item);
            tv_Lottery_name = view.findViewById(R.id.tv_Lottery_name);
            tv_lottery_num = view.findViewById(R.id.tv_lottery_num);
            tv_play_method = view.findViewById(R.id.tv_play_method);
            tv_lottery_amount = view.findViewById(R.id.tv_lottery_amount);
            tv_lottery_profit = view.findViewById(R.id.tv_lottery_profit);
            tv_lottery_status = view.findViewById(R.id.tv_lottery_status);
            tv_chase_times = view.findViewById(R.id.tv_chase_times);


        }
    }
}
