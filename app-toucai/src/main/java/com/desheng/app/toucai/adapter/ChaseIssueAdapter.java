package com.desheng.app.toucai.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.view.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.model.ChaseDetailBean;
import com.desheng.base.action.HttpAction;
import com.desheng.base.util.MiuiUtils;
import com.shark.tc.R;

import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ChaseIssueAdapter extends BaseQuickAdapter<ChaseDetailBean.ChaseListBean, ChaseIssueAdapter.ViewHolder> {
    private Context ctx;
    private OnRefreshListListener refreshListListener;
    private List<ChaseDetailBean.ChaseListBean> listData;

    public ChaseIssueAdapter(Context ctx, @Nullable List<ChaseDetailBean.ChaseListBean> data, OnRefreshListListener refreshListListener) {
        super(com.shark.tc.R.layout.item_chase_issue, data);
        this.ctx = ctx;
        this.refreshListListener = refreshListListener;
        this.listData = data;
    }


    @Override
    public void onBindViewHolder(ChaseIssueAdapter.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        holder.tv_cancel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MaterialDialog dialog = new MaterialDialog(ctx);

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
                        HttpAction.cancelSingleChase(ctx, listData.get(position).billno, new AbHttpResult() {
                            @Override
                            public void onBefore(Request request, int id, String host, String funcName) {
                                holder.tv_cancel_order.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Dialogs.showProgressDialog((Activity) ctx, "");
                                    }
                                });
                            }

                            @Override
                            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                dialog.dismiss();

                                if (code == 0 && error == 0) {
                                    Toasts.show(ctx, msg, true);
                                    refreshListListener.onRefreshList(position);
                                } else {
                                    Toasts.show(ctx, msg, false);
                                }
                                return true;
                            }

                            @Override
                            public void onAfter(int id) {
                                holder.tv_cancel_order.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Dialogs.hideProgressDialog((Activity) ctx);
                                    }
                                }, 500);
                            }

                            @Override
                            public boolean onError(int status, String content) {
                                Toasts.show(ctx, content, false);
                                return super.onError(status, content);
                            }
                        });
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    protected void convert(final ChaseIssueAdapter.ViewHolder helper, final ChaseDetailBean.ChaseListBean item) {

        helper.layout_ball_container.removeAllViews();

        helper.tv_issue_num.setText("第" + item.issue + "期");
        helper.tv_status.setText(item.statusRemark);
        helper.tv_bet_amount.setText("￥" + Nums.formatDecimal(item.money, 3));

        if (item.winMoney > 0) {
            helper.tv_award_status.setText("已中奖");
        } else {
            helper.tv_award_status.setText("未中奖");
        }
        boolean isCancel = item.statusRemark.contains("撤单");
        if (item.statusRemark.contains("未开奖")) {
            helper.tv_award_amount.setText("--");
            helper.tv_award_status.setText("未开奖");
            helper.layout_award_status.setVisibility(View.GONE);
            helper.bottomLine.setVisibility(View.GONE);
        } else {
            helper.layout_award_status.setVisibility(isCancel ? View.GONE : View.VISIBLE);
            helper.bottomLine.setVisibility(isCancel ? View.GONE : View.VISIBLE);
            String amount = "￥" + Nums.formatDecimal(item.winMoney, 3);
            helper.tv_award_amount.setText(isCancel ? "--" : amount);
            if (item.winMoney > 0) {
                helper.tv_award_status.setText("已中奖");
            } else {
                if (item.statusRemark.contains("已下单") && Strs.isEmpty(item.openCode)) {
                    helper.tv_award_status.setText("未开奖");
                } else {
                    helper.tv_award_status.setText("未中奖");
                }
            }
        }

        helper.tv_award_open.setText(item.statusRemark);

        if (item.allowCancel) {
            helper.tv_cancel_order.setVisibility(View.VISIBLE);
        } else {
            helper.tv_cancel_order.setVisibility(View.INVISIBLE);
        }

        if (Strs.isNotEmpty(item.openCode)) {
            helper.tv_award_not_open.setVisibility(View.GONE);
            String[] openCodes = item.openCode.split(",");

            for (int i = 0; i < openCodes.length; i++) {

                TextView tvBall = new TextView(ctx);
                tvBall.setBackgroundResource(R.drawable.shape_circle_red);
                tvBall.setWidth((int) MiuiUtils.ViewUtils.dpToPx(22, ctx));
                tvBall.setHeight((int) MiuiUtils.ViewUtils.dpToPx(22, ctx));
                tvBall.setText(openCodes[i]);
                tvBall.setTextColor(ctx.getResources().getColor(R.color.white));
                tvBall.setGravity(Gravity.CENTER);
                helper.layout_ball_container.addView(tvBall);
            }
        } else {
            helper.tv_award_not_open.setVisibility(View.VISIBLE);
        }

    }

    public class ViewHolder extends BaseViewHolder {
        TextView tv_issue_num;
        TextView tv_status;
        TextView tv_award_not_open;
        TextView tv_cancel_order;
        TextView tv_award_open;
        TextView tv_bet_amount;
        TextView tv_award_amount;
        TextView tv_award_status;
        LinearLayout layout_ball_container;
        LinearLayout layout_award_status;
        View bottomLine;


        public ViewHolder(View view) {
            super(view);
            tv_issue_num = view.findViewById(com.shark.tc.R.id.tv_issue_num);
            bottomLine = view.findViewById(com.shark.tc.R.id.bottomLine);
            tv_status = view.findViewById(com.shark.tc.R.id.tv_status);
            tv_award_not_open = view.findViewById(com.shark.tc.R.id.tv_award_not_open);
            tv_cancel_order = view.findViewById(com.shark.tc.R.id.tv_cancel_order);
            tv_award_open = view.findViewById(com.shark.tc.R.id.tv_award_open);
            tv_bet_amount = view.findViewById(com.shark.tc.R.id.tv_bet_amount);
            tv_award_amount = view.findViewById(com.shark.tc.R.id.tv_award_amount);
            tv_award_status = view.findViewById(com.shark.tc.R.id.tv_award_status);
            layout_ball_container = view.findViewById(com.shark.tc.R.id.layout_ball_container);
            layout_award_status = view.findViewById(com.shark.tc.R.id.layout_award_status);

        }
    }

    public interface OnRefreshListListener {
        void onRefreshList(int position);
    }


}
