package com.desheng.app.toucai.panel;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.action.HttpAction;
import com.desheng.base.model.WithdrawRecord;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.SpaceTopDecoration;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 */
public class FragTabWithdrawRecord extends AbBaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvTransfer;
    private WidthDrawAdapter mAdapter;
    private List<WithdrawRecord> listData = new ArrayList<>();
    private LinearLayout layout_nodata;
    private int page, size = 20;
    private TextView totalAmount;
    private TextView feeAmount;
    private TextView practicalAmount;

    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_withdraw_record;
    }

    @Override
    public void init(View root) {
        srlRefresh = root.findViewById(R.id.srlRefresh);
        rvTransfer = root.findViewById(R.id.rvFund);
        layout_nodata = root.findViewById(R.id.layout_nodata);
        totalAmount = root.findViewById(R.id.tv_totalAmount);
        feeAmount = root.findViewById(R.id.tv_feeAmount);
        practicalAmount = root.findViewById(R.id.tv_practicalAmount);

        srlRefresh.setColorSchemeResources(com.desheng.base.R.color.colorPrimary, com.desheng.base.R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);
        rvTransfer.setLayoutManager(Views.genLinearLayoutManagerV(getActivity()));
        rvTransfer.addItemDecoration(new SpaceTopDecoration(7));

        mAdapter = new WidthDrawAdapter(getActivity(), listData);
        rvTransfer.setAdapter(mAdapter);

        mAdapter.setEnableLoadMore(true);

        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getWithDrawRecord(page, size);
            }
        }, rvTransfer);
        getWithDrawRecord(page, size);
    }

    @Override
    public void onRefresh() {
        page = 0;
        getWithDrawRecord(page, 20);
    }

    public void getWithDrawRecord(final int page, int size) {
        long currenttime = System.currentTimeMillis();
        String startTime = Dates.getStringByFormat(currenttime - 6 * 24 * 60 * 60 * 1000, Dates.dateFormatYMD);
        startTime = startTime + " 00:00:00";
        String stopTime = Dates.getStringByFormat(currenttime, Dates.dateFormatYMD) + " 23:59:59";

        HttpAction.searchWithdrawRecord(getActivity(), "", startTime, stopTime, page, size, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                if (page == 0) {
                    Dialogs.showProgressDialog(getActivity(), "搜索中...");
                }

                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(true);
                    }
                }, 100);
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespSearchWithdrawRecord.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                HttpAction.RespSearchWithdrawRecord respSearchOrder = getField(extra, "data", null);
                if (respSearchOrder != null) {

                    if (page == 0) {
                        listData.clear();
                    }

                    if (respSearchOrder.statistics != null) {
                        totalAmount.setText(Nums.formatDecimal(respSearchOrder.statistics.totalAmount, 3));
                        feeAmount.setText(Nums.formatDecimal(respSearchOrder.statistics.feeAmount, 3));
                        practicalAmount.setText(Nums.formatDecimal(respSearchOrder.statistics.practicalAmount, 3));
                    }

                    listData.addAll(respSearchOrder.list);

                    if (listData.size() >= respSearchOrder.totalCount) {
                        mAdapter.setEnableLoadMore(false);
                        mAdapter.loadMoreEnd();
                    } else {
                        mAdapter.setEnableLoadMore(true);
                    }

                    layout_nodata.setVisibility(respSearchOrder.list.size() == 0 ? View.VISIBLE : View.GONE);

                    mAdapter.notifyDataSetChanged();
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
                mAdapter.loadMoreComplete();
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(false);
                    }
                }, 600);
            }
        });
    }

    public static class WidthDrawAdapter extends BaseQuickAdapter<WithdrawRecord, WidthDrawAdapter.ViewHolder> {
        private Context ctx;

        public WidthDrawAdapter(Context ctx, @Nullable List<WithdrawRecord> data) {
            super(R.layout.item_withdraw_record, data);
            this.ctx = ctx;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);

        }

        @Override
        protected void convert(final WidthDrawAdapter.ViewHolder helper, final WithdrawRecord item) {

            helper.tv_withdraw_time.setText(Dates.getStringByFormat(item.orderTime, Dates.dateFormatYMDHMS));
            if (item.orderStatus == 0) {
                helper.tv_transfer_status.setText("提现成功");
                helper.tv_transfer_status.setBackgroundResource(R.mipmap.ic_transfer_failure);
            } else if (item.orderStatus == 6 || item.orderStatus == 7 || item.orderStatus == 2) {
                helper.tv_transfer_status.setText("审批失败");
                helper.tv_transfer_status.setBackgroundResource(R.mipmap.ic_transfer_ing);
            } else {
                helper.tv_transfer_status.setText("待处理");
                helper.tv_transfer_status.setBackgroundResource(R.mipmap.ic_transfer_ing);
            }

            helper.tv_withdraw_amount.setText(Nums.formatDecimal(item.amount,0).replace(".",""));
            helper.tv_service_charge.setText(item.feeAmount);
            helper.tv_real_withdraw_amount.setText(item.actualAmount);
            helper.tv_billno.setText(item.billno);

        }

        public class ViewHolder extends BaseViewHolder {

            TextView tv_withdraw_time;
            TextView tv_transfer_status;
            TextView tv_billno;
            TextView tv_withdraw_amount;
            TextView tv_service_charge;
            TextView tv_real_withdraw_amount;

            public ViewHolder(View view) {
                super(view);
                tv_billno = view.findViewById(R.id.tv_billno);
                tv_withdraw_time = view.findViewById(R.id.tv_withdraw_time);
                tv_withdraw_amount = view.findViewById(R.id.tv_withdraw_amount);
                tv_service_charge = view.findViewById(R.id.tv_service_charge);
                tv_real_withdraw_amount = view.findViewById(R.id.tv_real_withdraw_amount);
                tv_transfer_status = view.findViewById(R.id.tv_transfer_status);

            }
        }
    }


}
