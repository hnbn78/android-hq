package com.desheng.app.toucai.panel;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.action.HttpAction;
import com.desheng.base.model.RechargeRecordBean;
import com.desheng.base.util.ResUtil;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.SpaceTopDecoration;
import com.shark.tc.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 */
public class FragTabDepositRecord extends AbBaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvDeposit;
    private DepositAdapter mAdapter;
    private List<RechargeRecordBean> listOrders = new ArrayList<>();

    private LinearLayout layout_nodata;
    private int page, size = 20;
    private int totalCount;
    private TextView failCash;
    private TextView totalCash;
    private TextView successCash;

    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_deposit_record;
    }

    @Override
    public void init(View root) {
        srlRefresh = root.findViewById(R.id.srlRefresh);
        rvDeposit = root.findViewById(R.id.rvFund);
        layout_nodata = root.findViewById(R.id.layout_nodata);
        failCash = root.findViewById(R.id.tv_failCash);
        totalCash = root.findViewById(R.id.tv_totalCash);
        successCash = root.findViewById(R.id.tv_successCash);

        srlRefresh.setColorSchemeResources(com.desheng.base.R.color.colorPrimary, com.desheng.base.R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);
        rvDeposit.setLayoutManager(Views.genLinearLayoutManagerV(getActivity()));
        rvDeposit.addItemDecoration(new SpaceTopDecoration(7));

        mAdapter = new DepositAdapter(getActivity(), listOrders);
        rvDeposit.setAdapter(mAdapter);

        mAdapter.setEnableLoadMore(true);

        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getDepositRecord(page, size);
            }
        }, rvDeposit);
        getDepositRecord(page, size);
    }

    @Override
    public void onRefresh() {
        page = 0;
        getDepositRecord(page, 20);
    }

    public void getDepositRecord(final int page, int size) {
        long currenttime = System.currentTimeMillis();
        String startTime = Dates.getStringByFormat(currenttime - 6 * 24 * 60 * 60 * 1000, Dates.dateFormatYMD);
        startTime = startTime + " 00:00:00";
        String stopTime = Dates.getStringByFormat(currenttime, Dates.dateFormatYMD) + " 23:59:59";
        HttpAction.searchRechargeRecord(getActivity(), "", "", null, startTime, stopTime, page, size, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                if (page == 0) {
                    Dialogs.showProgressDialog(getActivity(), "搜索中...");
                }

            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespSearchRechargeRecord.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                HttpAction.RespSearchRechargeRecord respSearchOrder = getField(extra, "data", null);
                if (respSearchOrder != null) {
                    if (page == 0) {
                        listOrders.clear();
                    }

                    if (respSearchOrder.statistics != null) {
                        totalCash.setText(Nums.formatDecimal(respSearchOrder.statistics.totalCash, 3));
                        successCash.setText(Nums.formatDecimal(respSearchOrder.statistics.successCash, 3));
                        failCash.setText(Nums.formatDecimal(respSearchOrder.statistics.failCash, 3));
                    }

                    totalCount = respSearchOrder.totalCount;
                    listOrders.addAll(respSearchOrder.list);
                    mAdapter.setEnableLoadMore(listOrders.size() < totalCount);

                    layout_nodata.setVisibility(respSearchOrder.list.size() == 0 ? View.VISIBLE : View.GONE);


                } else {
                    Toasts.show(getActivity(), msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                if (page == 0) {
                    Dialogs.hideProgressDialog(getActivity());

                }
                srlRefresh.setRefreshing(false);
                mAdapter.loadMoreComplete();

            }
        });

    }

    public class DepositAdapter extends BaseQuickAdapter<RechargeRecordBean, DepositAdapter.ViewHolder> {
        private Context ctx;
        int[] backdrawables = {
                R.drawable.sh_bd_rec_half_orange_round,
                R.drawable.sh_bd_rec_half_blue_round,
                R.drawable.sh_bd_rec_half_red_round,
                R.drawable.sh_bd_rec_half_purple_round,
                R.drawable.sh_bd_rec_half_green_round,
                R.drawable.sh_bd_rec_half_shanfu_round
        };
        int[] textcolors = {
                R.color.orange_mine,
                R.color.blue_mine,
                R.color.red_e43d32,
                R.color.purple_mine,
                R.color.green_09af1c,
                R.color.shanfu_mine
        };

        DepositAdapter(Context ctx, @Nullable List<RechargeRecordBean> data) {
            super(R.layout.item_deposite_record, data);
            this.ctx = ctx;
        }

        @Override
        protected void convert(final DepositAdapter.ViewHolder helper, final RechargeRecordBean item) {
            int position = helper.getAdapterPosition();
            int color;
            if (!TextUtils.isEmpty(item.channelName)) {
                if (item.channelName.contains("支付宝")) {
                    helper.layoutTop.setBackgroundResource(backdrawables[0]);
                    color = textcolors[0];
                } else if (item.channelName.contains("网银")) {
                    helper.layoutTop.setBackgroundResource(backdrawables[1]);
                    color = textcolors[1];
                } else if (item.channelName.contains("微信")) {
                    helper.layoutTop.setBackgroundResource(backdrawables[2]);
                    color = textcolors[2];
                } else if (item.channelName.contains("银联")) {
                    helper.layoutTop.setBackgroundResource(backdrawables[3]);
                    color = textcolors[3];
                } else if (item.channelName.contains("QQ")) {
                    helper.layoutTop.setBackgroundResource(backdrawables[4]);
                    color = textcolors[4];
                } else {
                    helper.layoutTop.setBackgroundResource(backdrawables[5]);
                    color = textcolors[5];
                }
            } else {
                helper.layoutTop.setBackgroundResource(backdrawables[0]);
                color = textcolors[0];
            }
            helper.tvDepositNum.setText(item.billno);
            helper.tvDepositAmount.setText("+" + new DecimalFormat("#.00").format(item.amount));
            if (item.orderStatus == 0) {
                helper.tvDepositStatus.setTextColor(ResUtil.getColor(color));
                helper.tvDepositAmount.setTextColor(ResUtil.getColor(color));
                helper.tvDepositStatus.setText("充值成功");
            } else if (item.orderStatus == 6 || item.orderStatus == 7) {
                helper.tvDepositStatus.setText("充值失败");
                helper.tvDepositStatus.setTextColor(ctx.getResources().getColor(R.color.gray));
                helper.tvDepositAmount.setTextColor(ctx.getResources().getColor(R.color.gray));
            } else if (item.orderStatus == 8) {
                helper.tvDepositStatus.setText("已取消");
                helper.tvDepositStatus.setTextColor(ctx.getResources().getColor(R.color.gray));
                helper.tvDepositAmount.setTextColor(ctx.getResources().getColor(R.color.gray));
            } else {
                helper.tvDepositStatus.setText("待处理");
                helper.tvDepositStatus.setTextColor(ctx.getResources().getColor(R.color.gray));
                helper.tvDepositAmount.setTextColor(ctx.getResources().getColor(R.color.gray));
            }

            helper.tvDepositDate.setText(Dates.getStringByFormat(item.orderTime, Dates.dateFormatYMDHMS));

            if (Strs.isNotEmpty(item.channelName)) {
                helper.tvPayType.setText(item.channelName);
            } else {
                helper.tvPayType.setText("修正资金");
            }

        }

        public class ViewHolder extends BaseViewHolder {

            RelativeLayout layoutTop;
            TextView tvPayType;
            TextView tvDepositNum;
            TextView tvDepositDate;
            TextView tvDepositAmount;
            TextView tvDepositStatus;

            public ViewHolder(View view) {
                super(view);
                layoutTop = view.findViewById(R.id.layout_top);
                tvPayType = view.findViewById(R.id.tv_pay_type_name);
                tvDepositNum = view.findViewById(R.id.tv_deposit_num);
                tvDepositDate = view.findViewById(R.id.tv_deposit_date);
                tvDepositAmount = view.findViewById(R.id.tv_deposit_amount);
                tvDepositStatus = view.findViewById(R.id.tv_deposit_status);
            }
        }
    }


}
