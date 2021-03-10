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
import com.ab.util.Nums;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.action.HttpAction;
import com.desheng.base.model.ActivityBean;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.SpaceTopDecoration;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 */
public class FragTabActivityRecord extends AbBaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvTransfer;
    private ActivityAdapter mAdapter;
    private List<ActivityBean.ActivityModel> listData = new ArrayList<>();
    private LinearLayout layout_nodata;
    private int page;
    private TextView totalAmount;
    private TextView refuseAmount;
    private TextView successAmount;
    private TextView pendingAmount;

    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_activity_record;
    }

    @Override
    public void init(View root) {
        srlRefresh = root.findViewById(R.id.srlRefresh);
        rvTransfer = root.findViewById(R.id.rvFund);
        layout_nodata = root.findViewById(R.id.layout_nodata);
        totalAmount = root.findViewById(R.id.tv_totalAmount);
        refuseAmount = root.findViewById(R.id.tv_refuseAmount);
        successAmount = root.findViewById(R.id.tv_successAmount);
        pendingAmount = root.findViewById(R.id.tv_pendingAmount);

        srlRefresh.setColorSchemeResources(com.desheng.base.R.color.colorPrimary, com.desheng.base.R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);
        rvTransfer.setLayoutManager(Views.genLinearLayoutManagerV(getActivity()));
        rvTransfer.addItemDecoration(new SpaceTopDecoration(7));

        mAdapter = new ActivityAdapter(getActivity(), listData);
        rvTransfer.setAdapter(mAdapter);

        mAdapter.setEnableLoadMore(false);
        getActivityRecord();

    }

    @Override
    public void onRefresh() {
        page = 0;
        listData.clear();
        getActivityRecord();
    }

    private void getActivityRecord() {
        long currenttime = System.currentTimeMillis();
        String startTime = Dates.getStringByFormat(currenttime - 6 * 24 * 60 * 60 * 1000, Dates.dateFormatYMD);
        startTime = startTime + " 00:00:00";
        String stopTime = Dates.getStringByFormat(currenttime, Dates.dateFormatYMD) + " 23:59:59";
        HttpAction.getActivityRecord(null, startTime, stopTime, page, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(true);
                    }
                }, 100);
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", ActivityBean.class);

            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    ActivityBean activityBean = getField(extra, "data", null);
                    if (page == 0) {
                        listData.clear();
                    }

                    if (activityBean.statistics != null) {
                        totalAmount.setText(Nums.formatDecimal(activityBean.statistics.totalAmount, 3));
                        refuseAmount.setText(Nums.formatDecimal(activityBean.statistics.refuseAmount, 3));
                        successAmount.setText(Nums.formatDecimal(activityBean.statistics.successAmount, 3));
                        pendingAmount.setText(Nums.formatDecimal(activityBean.statistics.pendingAmount, 3));
                    }

                    listData.addAll(activityBean.list);
                    boolean isLoadMore = listData.size() < activityBean.totalCount;
                    mAdapter.notifyDataSetChanged();
                    mAdapter.setEnableLoadMore(isLoadMore);
                    layout_nodata.setVisibility(activityBean.totalCount == 0 ? View.VISIBLE : View.GONE);
                    if (isLoadMore)
                        page++;
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(false);
                        mAdapter.loadMoreComplete();
                    }
                }, 600);
            }

            @Override
            public boolean onError(int status, String content) {
                if (page > 0)
                    page--;
                return super.onError(status, content);
            }
        });
    }

    public static class ActivityAdapter extends BaseQuickAdapter<ActivityBean.ActivityModel, ActivityAdapter.ViewHolder> {
        private Context ctx;

        public ActivityAdapter(Context ctx, @Nullable List<ActivityBean.ActivityModel> data) {
            super(R.layout.item_activity_record, data);
            this.ctx = ctx;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);

        }

        @Override
        protected void convert(final ActivityAdapter.ViewHolder helper, final ActivityBean.ActivityModel item) {

            helper.tv_activity_amount.setText("" + item.award);
            helper.tv_activity_time.setText("" + item.receiveTime);
            helper.tv_activity_name.setText("" + item.title);
            helper.tv_activity_status.setText("" + item.drawStatusStr);
            if (item.drawStatusStr.contains("失败")) {
                helper.tv_activity_status.setTextColor(ctx.getResources().getColor(R.color.gray_content));
                helper.tv_activity_amount.setTextColor(ctx.getResources().getColor(R.color.blue_mine));
                helper.tv_activity_name.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.sh_bd_rec_quarter_blue_round));
            } else {
                helper.tv_activity_status.setTextColor(ctx.getResources().getColor(R.color.red_mine));
                helper.tv_activity_amount.setTextColor(ctx.getResources().getColor(R.color.red_mine));
                helper.tv_activity_name.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.sh_bd_rec_quarter_red_round));
            }


        }

        public class ViewHolder extends BaseViewHolder {

            TextView tv_activity_name;
            TextView tv_activity_time;
            TextView tv_activity_amount;
            TextView tv_activity_status;

            public ViewHolder(View view) {
                super(view);
                tv_activity_name = view.findViewById(R.id.tv_activity_name);
                tv_activity_time = view.findViewById(R.id.tv_activity_time);
                tv_activity_amount = view.findViewById(R.id.tv_activity_amount);
                tv_activity_status = view.findViewById(R.id.tv_activity_status);
            }
        }
    }


}
