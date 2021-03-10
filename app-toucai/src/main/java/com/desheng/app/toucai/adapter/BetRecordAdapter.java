package com.desheng.app.toucai.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.http.AbHttpResult;
import com.ab.util.Strs;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.panel.ActBetRecordDetail;
import com.desheng.base.model.LotteryOrderInfo;
import com.shark.tc.R;

import java.util.HashMap;
import java.util.List;

public class BetRecordAdapter extends BaseQuickAdapter<LotteryOrderInfo, BetRecordAdapter.ViewHolder> {
    private Context ctx;

    public BetRecordAdapter(Context ctx, @Nullable List<LotteryOrderInfo> data) {
        super(R.layout.item_bet_record, data);
        this.ctx = ctx;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

    }

    @Override
    protected void convert(final BetRecordAdapter.ViewHolder helper, final LotteryOrderInfo item) {

        helper.tv_lottery_amount.setText("" + item.money);
        helper.tv_Lottery_name.setText(item.lottery);
        //{"KY棋牌", "AGIN视讯", "IM体育", "OG真人", "CQ9电游", "LEG棋牌", "BBIN真人", "PT电子"};
        helper.tv_play_method.setText(item.method);
        if (item.lottery.contains("KY") || item.lottery.contains("DS") || item.lottery.contains("PT")
                || item.lottery.contains("AGIN") || item.lottery.contains("IM") || item.lottery.contains("OG")
                || item.lottery.contains("LEG") || item.lottery.contains("BBIN")) {
            helper.tv_lottery_status.setVisibility(View.GONE);
        } else {
            helper.tv_lottery_status.setText(item.statusRemark);
        }

        if (!item.lottery.contains("KY") && !item.lottery.contains("DS") && !item.lottery.contains("PT")
                && !item.lottery.contains("AGIN") && !item.lottery.contains("IM") && !item.lottery.contains("OG")
                && !item.lottery.contains("LEG") && !item.lottery.contains("BBIN")) {
            helper.tv_lottery_num.setText("第" + item.issue + "期");
            helper.tv_lottery_num.setVisibility(View.VISIBLE);
        } else {
            helper.tv_lottery_num.setVisibility(View.GONE);
        }
        if (item.winMoney == 0) {
            helper.tv_lottery_profit.setVisibility(View.GONE);
        } else {
            helper.tv_lottery_profit.setVisibility(View.VISIBLE);
            helper.tv_lottery_profit.setText("+" + item.winMoney);
        }

        if (Strs.isNotEmpty(item.statusRemark) && item.statusRemark.contains("撤单")) {

            helper.tv_lottery_amount.setTextColor(ctx.getResources().getColor(R.color.gray));
            helper.tv_lottery_status.setTextColor(ctx.getResources().getColor(R.color.gray));
        } else {
            helper.tv_lottery_amount.setTextColor(ctx.getResources().getColor(R.color.red_mine));
            helper.tv_lottery_status.setTextColor(ctx.getResources().getColor(R.color.red_mine));
        }

        if ("未中奖".equals(item.statusRemark)) {
            helper.tv_lottery_status.setTextColor(ctx.getResources().getColor(R.color.blue_mine));
            helper.tv_lottery_amount.setTextColor(ctx.getResources().getColor(R.color.blue_mine));
        } else if ("未开奖".equals(item.statusRemark)) {
            helper.tv_lottery_status.setTextColor(ctx.getResources().getColor(R.color.gray));
            helper.tv_lottery_amount.setTextColor(ctx.getResources().getColor(R.color.gray));
        }

        helper.layout_bet_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.lottery.contains("KY") || item.lottery.contains("DS") || item.lottery.contains("PT")
                        || item.lottery.contains("AGIN") || item.lottery.contains("IM") || item.lottery.contains("OG")
                        || item.lottery.contains("LEG") || item.lottery.contains("BBIN"))
                    return;
                ActBetRecordDetail.launch((Activity) mContext, item.billno);
            }
        });

        if (item.lottery.contains("KY") || item.lottery.contains("DS") || item.lottery.contains("PT")
                || item.lottery.contains("AGIN") || item.lottery.contains("IM") || item.lottery.contains("OG")
                || item.lottery.contains("LEG") || item.lottery.contains("BBIN")) {
            helper.tv_lottery_onceMore.setText(item.statusRemark);
            helper.tv_lottery_onceMore.setTextColor(Strs.isEqual("赢", item.statusRemark)
                    ? Color.RED : ctx.getResources().getColor(R.color.blue_mine));
            helper.tv_lottery_onceMore.setBackgroundResource(0);
            helper.tv_lottery_onceMore.setTextSize(22);
        } else {
            helper.tv_lottery_onceMore.setText("再来一注");
            helper.tv_lottery_onceMore.setBackgroundResource(R.drawable.shape_circle_pink);
            helper.tv_lottery_onceMore.setTextColor(Color.WHITE);
            helper.tv_lottery_onceMore.setTextSize(15);
        }

        helper.tv_lottery_onceMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HttpActionTouCai.onceMoreBetting(BetRecordAdapter.this, item.billno, new AbHttpResult() {
                    @Override
                    public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                        if (code == 0 && error == 0) {
                            if (moreBettingSuccess != null) {
                                moreBettingSuccess.onceMoreBetting();
                            }
                        }
                        return super.onSuccessGetObject(code, error, msg, extra);
                    }
                });

            }
        });
    }

    IonceMoreBettingSuccess moreBettingSuccess;

    public void setMoreBettingSuccess(IonceMoreBettingSuccess moreBettingSuccess) {
        this.moreBettingSuccess = moreBettingSuccess;
    }

    public interface IonceMoreBettingSuccess {
        void onceMoreBetting();
    }

    public class ViewHolder extends BaseViewHolder {
        TextView tv_Lottery_name;
        TextView tv_lottery_num;
        TextView tv_play_method;
        TextView tv_lottery_amount;
        TextView tv_lottery_profit;
        TextView tv_lottery_status;
        LinearLayout layout_bet_item;
        TextView tv_lottery_onceMore;


        public ViewHolder(View view) {
            super(view);
            layout_bet_item = view.findViewById(R.id.layout_bet_item);
            tv_Lottery_name = view.findViewById(R.id.tv_Lottery_name);
            tv_lottery_num = view.findViewById(R.id.tv_lottery_num);
            tv_play_method = view.findViewById(R.id.tv_play_method);
            tv_lottery_amount = view.findViewById(R.id.tv_lottery_amount);
            tv_lottery_profit = view.findViewById(R.id.tv_lottery_profit);
            tv_lottery_status = view.findViewById(R.id.tv_lottery_status);
            tv_lottery_onceMore = view.findViewById(R.id.tv_lottery_onceMore);

        }
    }
}
