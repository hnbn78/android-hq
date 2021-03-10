package com.desheng.app.toucai.panel;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.desheng.app.toucai.adapter.ChaseRecordAdapter;
import com.desheng.base.action.HttpAction;
import com.desheng.base.model.ChaseRecordBean;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.SpaceTopDecoration;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 */
public class FragTabChaseRecordTwo extends AbBaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvDeposit;
    private ChaseRecordAdapter mAdapter;
    private List<ChaseRecordBean> listData = new ArrayList<>();

    private int page, size = 20, day_length = 3;
    private int totalCount;
    private View layout_nodata;
    private TextView tvAwardAmount;
    private TextView tvbetAmount;

    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_chase_record;
    }

    @Override
    public void init(View root) {
        srlRefresh = root.findViewById(R.id.srlRefresh);
        rvDeposit = root.findViewById(R.id.rvFund);
        layout_nodata = root.findViewById(R.id.layout_nodata);
        tvAwardAmount = root.findViewById(R.id.tv_awardAmount);
        tvbetAmount = root.findViewById(R.id.tv_betAmount);

        srlRefresh.setColorSchemeResources(com.desheng.base.R.color.colorPrimary, com.desheng.base.R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);
        rvDeposit.setLayoutManager(Views.genLinearLayoutManagerV(getActivity()));
        rvDeposit.addItemDecoration(new SpaceTopDecoration(7));

        mAdapter = new ChaseRecordAdapter(getActivity(), listData);
        rvDeposit.setAdapter(mAdapter);

        mAdapter.setEnableLoadMore(true);

        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getBetRecord(page, size, day_length);
            }
        }, rvDeposit);
        getBetRecord(page, size, day_length);
    }

    @Override
    public void onRefresh() {
        page = 0;
        getBetRecord(page, size, day_length);
    }

    /**
     * 追号记录
     */
    public void getBetRecord(final int page, int size, int days) {
        long currentTimeMillis = System.currentTimeMillis();
        String startTime = Dates.getStringByFormat(currentTimeMillis - Dates.DAY_MILLIS * days, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(currentTimeMillis, Dates.dateFormatYMDHMS);

        HttpAction.searchChaseRecord(startTime, stopTime, "", page, size, new AbHttpResult() {

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
                entity.putField("data", HttpAction.RespSearchChaseRecord.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                HttpAction.RespSearchChaseRecord respSearchOrder = getField(extra, "data", null);
                if (respSearchOrder != null) {
                    if (page == 0) {
                        listData.clear();
                    }

                    if (respSearchOrder.statistics != null) {
                        tvbetAmount.setText(Nums.formatDecimal(respSearchOrder.statistics.betAmount, 3));
                        tvAwardAmount.setText(Nums.formatDecimal(respSearchOrder.statistics.awardAmount, 3));
                    }

                    totalCount = respSearchOrder.totalCount;
                    listData.addAll(respSearchOrder.list);
                    layout_nodata.setVisibility(totalCount == 0 ? View.VISIBLE : View.GONE);
                    if (listData.size() < totalCount) {
                        mAdapter.setEnableLoadMore(true);
                    } else {
                        mAdapter.setEnableLoadMore(false);
                        mAdapter.loadMoreEnd();
                    }

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
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(false);
                    }
                }, 100);
                mAdapter.loadMoreComplete();

            }
        });
    }

}
