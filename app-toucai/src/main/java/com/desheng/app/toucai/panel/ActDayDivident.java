package com.desheng.app.toucai.panel;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.desheng.app.toucai.adapter.DayBounsAdapter;
import com.desheng.app.toucai.model.DayBounusBean;
import com.desheng.base.action.HttpAction;
import com.desheng.base.model.Params;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.mypicker.DatePickerDialog;
import com.pearl.view.mypicker.DateUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Request;

/**
 * 工资管理
 */
public class ActDayDivident extends AbAdvanceActivity implements View.OnClickListener, OnLoadMoreListener, OnRefreshListener {

    private boolean hasHourItem = false;

    @Override
    protected int getLayoutId() {
        return R.layout.act_day_bonus;
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, ActDayDivident.class));
    }

    private static final int PAGE_SIZE = 50;
    private TextView tvStartDate, tvEndDate, tvSalary;
    private SmartRefreshLayout refreshLayout;
    private long startMills = 0;
    private long stopMills = 0;
    private int total = 0, currPage = 0;
    private List<List<Params>> list = new ArrayList<>();
    private DayBounsAdapter reportAdapter;

    @Override
    protected void init() {
        setStatusBarTranslucentAndLightContentWithPadding();
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "日分红");
        tvStartDate = findViewById(R.id.tvStartDate);
        tvStartDate.setOnClickListener(this);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvEndDate.setOnClickListener(this);
        tvSalary = findViewById(R.id.tvSalary);
        RecyclerView recyclerView = findViewById(R.id.lottery_report_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new DayBounsAdapter(list);
        reportAdapter.setEmptyView(R.layout.empty_view_layout, recyclerView);
        recyclerView.setAdapter(reportAdapter);

        refreshLayout = findViewById(R.id.swipeRefreshLayout);
        long currentTimeMillis = System.currentTimeMillis();
        stopMills = currentTimeMillis;
        String currDate;
        if (hasHourItem) {
            currDate = Dates.getStringByFormat(currentTimeMillis, Dates.dateFormatYMDH);
            startMills = stopMills - 1000 * 60 * 60;
        } else {
            long current = System.currentTimeMillis();//当前时间毫秒数
            long zero = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();//今天零点零分零秒的毫秒数
            currDate = Dates.getStringByFormat(zero, Dates.dateFormatYMD);
            startMills = zero;
        }
        tvStartDate.setText(currDate);
        tvEndDate.setText(currDate);
        currPage = 0;
        updateTable();
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
        findViewById(R.id.btnSearch).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                currPage = 0;
                list.clear();
                updateTable();
                break;
            case R.id.tvStartDate:
                Calendar calendar = Calendar.getInstance();
                List<Integer> integers = new ArrayList<>(Arrays.asList(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH)));
                showDateDialog(tvStartDate, integers);
                break;
            case R.id.tvEndDate:
                Calendar calendar1 = Calendar.getInstance();
                List<Integer> integers1 = new ArrayList<>(Arrays.asList(calendar1.get(Calendar.YEAR),
                        calendar1.get(Calendar.MONTH) + 1, calendar1.get(Calendar.DAY_OF_MONTH)));
                showDateDialog(tvEndDate, integers1);
                break;
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        currPage = 0;
        list.clear();
        updateTable();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        currPage++;
        updateTable();
    }

    private void showDateDialog(final View view, List<Integer> date) {
        DatePickerDialog.Builder builder = new DatePickerDialog.Builder(this);
        builder.setSelectYear(date.get(0) - 1)
                .setSelectMonth(date.get(1) - 1)
                .setSelectDay(date.get(2) - 1);

        if (date.size() > 3) {
            builder.setSelectHour(date.get(3));
            builder.setOnDateSelectedListener(new DatePickerDialog.OnDateSelectedListener() {
                @Override
                public void onDateSelected(int[] dates) {
                    String text = dates[0] + "-" + (dates[1] > 9 ? dates[1] : ("0" + dates[1])) + "-"
                            + (dates[2] > 9 ? dates[2] : ("0" + dates[2])) + " " + (dates[3] > 9 ? dates[3] : ("0" + dates[3]));
                    if (view == tvStartDate) {
                        startMills = Dates.getMillisOfStr(text, Dates.dateFormatYMDH);
                        tvStartDate.setText(text);
                    } else if (view == tvEndDate) {
                        stopMills = Dates.getMillisOfStr(text, Dates.dateFormatYMDH) + 1000 * 60 * 60 - 1000;
                        tvEndDate.setText(text);
                    }
                }

                @Override
                public void onCancel() {

                }
            });

        } else {
            builder.setOnDateSelectedListener(new DatePickerDialog.OnDateSelectedListener() {
                @Override
                public void onDateSelected(int[] dates) {
                    String text = dates[0] + "-" + (dates[1] > 9 ? dates[1] : ("0" + dates[1])) + "-"
                            + (dates[2] > 9 ? dates[2] : ("0" + dates[2]));
                    if (view == tvStartDate) {
                        startMills = Dates.getMillisOfStr(text, Dates.dateFormatYMD);
                        tvStartDate.setText(text);
                    } else if (view == tvEndDate) {
                        stopMills = Dates.getMillisOfStr(text, Dates.dateFormatYMD) + 24 * 60 * 60 * 1000 - 1000;
                        tvEndDate.setText(text);
                    }
                }

                @Override
                public void onCancel() {

                }
            });
        }
        builder.setMaxMonth(DateUtil.getDateForString(DateUtil.getToday()).get(1));
        builder.setMaxDay(DateUtil.getDateForString(DateUtil.getToday()).get(2));
        DatePickerDialog dateDialog = builder.create();
        dateDialog.show();
    }

    public void updateTable() {
        search(startMills, stopMills, currPage, PAGE_SIZE);
    }

    private void search(long sTime, long eTime, final int page, int size) {
        String startTime = Dates.getStringByFormat(sTime, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(eTime, Dates.dateFormatYMDHMS);

        HttpAction.getDayBonus(ActDayDivident.this, startTime, stopTime, page, size, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActDayDivident.this, "搜索中...");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", DayBounusBean.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                DayBounusBean respSearchOrder = getField(extra, "data", null);
                if (respSearchOrder != null && respSearchOrder.getList() != null) {
                    for (int i = 0; i < respSearchOrder.getList().size(); i++) {
                        DayBounusBean.ListBean bean = respSearchOrder.getList().get(i);
                        List<Params> params = new ArrayList<>();
                        params.add(new Params("团队投注金额", Nums.formatDecimal(bean.getOrderAmount(), 4) + "元"));
                        params.add(new Params("团队盈亏", "-" + Nums.formatDecimal(bean.getProfitAmount(), 4) + "元"));
                        params.add(new Params("活动奖金", Nums.formatDecimal(bean.getAward(), 4) + "元"));
                        if (Strs.isEqual("已发放", bean.getDrawStatusStr())) {
                            params.add(new Params("发放状态", "<font color='#2D7FDA'>" + bean.getDrawStatusStr() + "</font>"));
                        } else {
                            params.add(new Params("发放状态", bean.getDrawStatusStr()));
                        }
                        params.add(new Params("发放时间", bean.getAwardTime()));
                        list.add(params);
                    }
                    reportAdapter.notifyDataSetChanged();
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                return super.onError(status, content);
            }

            @Override
            public void onAfter(int id) {
                Dialogs.hideProgressDialog(ActDayDivident.this);
                if (page == 0) {
                    refreshLayout.finishRefresh();
                } else {
                    refreshLayout.finishLoadMore();
                }
            }
        });
    }
}
