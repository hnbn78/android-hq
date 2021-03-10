package com.desheng.app.toucai.panel;

import android.app.Activity;
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
import com.desheng.app.toucai.adapter.BetRecordAdapter;
import com.desheng.app.toucai.event.RequestRefreshMode;
import com.desheng.app.toucai.model.DajiangPushEventModel;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.model.LotteryOrderInfo;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.SpaceTopDecoration;
import com.shark.tc.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 */
public class FragTabBetRecordThree extends AbBaseFragment implements SwipeRefreshLayout.OnRefreshListener ,BetRecordAdapter.IonceMoreBettingSuccess {

    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvDeposit;
    private BetRecordAdapter mAdapter;
    private List<LotteryOrderInfo> listData = new ArrayList<>();

    private int page, size = 20, day_length = 7;
    private int totalCount;

    private LinearLayout layout_nodata;
    private TextView tvAwardAmount;
    private TextView tvConfirmAmount;
    private TextView tvbetAmount;

    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_fund_record;
    }

    @Override
    public void init(View root) {
        srlRefresh = root.findViewById(R.id.srlRefresh);
        rvDeposit = root.findViewById(R.id.rvFund);
        layout_nodata = root.findViewById(R.id.layout_nodata);
        tvAwardAmount = root.findViewById(R.id.tv_awardAmount);
        tvbetAmount = root.findViewById(R.id.tv_betAmount);
        tvConfirmAmount = root.findViewById(R.id.tv_confirmAmount);
        EventBus.getDefault().register(this);
        srlRefresh.setColorSchemeResources(com.desheng.base.R.color.colorPrimary, com.desheng.base.R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);
        rvDeposit.setLayoutManager(Views.genLinearLayoutManagerV(context));
        rvDeposit.addItemDecoration(new SpaceTopDecoration(7));

        mAdapter = new BetRecordAdapter(context, listData);
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
        mAdapter.setMoreBettingSuccess(this::onceMoreBetting);
    }

    @Override
    public void onRefresh() {
        page = 0;
        getBetRecord(page, size, day_length);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销注册
        EventBus.getDefault().unregister(this);
    }

    //定义处理接收的方法,处理大奖推送的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(RequestRefreshMode refreshMode) {
        page=0;
        getBetRecord(page, size,day_length);
    }

    public void getBetRecord(final int page, int size, int days) {
        long currentTimeMillis = System.currentTimeMillis();
        String startTime = Dates.getStringByFormat(currentTimeMillis - Dates.DAY_MILLIS * days, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(currentTimeMillis, Dates.dateFormatYMDHMS);

        HttpAction.searchOrder(context, "", null, startTime, stopTime, page, size, null, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                if (page == 0) {
                    Dialogs.showProgressDialog(((Activity) context), "搜索中...");
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
                entity.putField("data", HttpAction.RespSearchOrder.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                HttpAction.RespSearchOrder respSearchOrder = getField(extra, "data", null);
                if (respSearchOrder != null) {
                    if (page == 0) {
                        listData.clear();
                    }

                    if (respSearchOrder.statistics != null) {
                        tvAwardAmount.setText(Nums.formatDecimal(respSearchOrder.statistics.awardAmount, 3));
                        tvbetAmount.setText(Nums.formatDecimal(respSearchOrder.statistics.betAmount, 3));
                        tvConfirmAmount.setText(Nums.formatDecimal(respSearchOrder.statistics.confirmAmount, 3));
                    }

                    totalCount = respSearchOrder.totalCount;
                    listData.addAll(respSearchOrder.list);

                    if (listData.size() < totalCount) {
                        mAdapter.setEnableLoadMore(true);
                    } else {
                        mAdapter.setEnableLoadMore(false);
                        mAdapter.loadMoreEnd();
                    }

                    layout_nodata.setVisibility(respSearchOrder.list.size()==0?View.VISIBLE:View.GONE);

                    mAdapter.notifyDataSetChanged();
                } else {
                    Toasts.show(context, msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                if (page == 0) {
                    Dialogs.hideProgressDialog(((Activity) context));
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

    @Override
    public void onceMoreBetting() {
        DialogsTouCai.showLotteryBuyOkDialog(((Activity) context));
        page = 0;
        getBetRecord(page, size,day_length);
    }
}
