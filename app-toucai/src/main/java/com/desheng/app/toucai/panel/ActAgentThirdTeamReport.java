package com.desheng.app.toucai.panel;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.ThirdTeamReportBean;
import com.noober.background.view.BLEditText;
import com.noober.background.view.BLTextView;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.view.mypicker.DatePickerDialog;
import com.pearl.view.mypicker.DateUtil;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ActAgentThirdTeamReport extends AbAdvanceActivity {
    private static final int PAGE_SIZE = 20;
    private TextView tv_title;
    private RecyclerView recyclerView;
    private BaseQuickAdapter<ThirdTeamReportBean.ListBean, BaseViewHolder> adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BLTextView mStartTime, mStopTime, mSearch;
    private BLEditText mUsername;
    private int currPage = 0;
    private long startMills = 0;
    private long stopMills = 0;
    private List<ThirdTeamReportBean.ListBean> listOrders = new ArrayList<>();
    private RelativeLayout emptyView;

    @Override
    protected int getLayoutId() {
        return R.layout.act_agent_third_team_reports;
    }

    @Override
    protected void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();
        tv_title = ((TextView) headRoot.findViewById(R.id.tv_title));
        tv_title.setText("三方团队报表");
        initView();
    }

    private void initView() {
        recyclerView = ((RecyclerView) findViewById(R.id.recycleView));
        swipeRefreshLayout = ((SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout));
        emptyView = ((RelativeLayout) findViewById(R.id.emptyView));
        mStartTime = findViewById(R.id.startTime);
        mStopTime = findViewById(R.id.stopTime);
        mSearch = findViewById(R.id.search);
        mUsername = findViewById(R.id.username);

        mSearch.setOnClickListener(this);
        mStartTime.setOnClickListener(this);
        mStopTime.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BaseQuickAdapter<ThirdTeamReportBean.ListBean, BaseViewHolder>(R.layout.item_agent_third_team_reports) {
            @Override
            protected void convert(BaseViewHolder helper, ThirdTeamReportBean.ListBean item) {
                helper.setText(R.id.username, item.getUserName());
                helper.getView(R.id.username).setVisibility(Strs.isNotEmpty(item.getUserName()) ? View.VISIBLE : View.GONE);
                helper.setText(R.id.tv1, (Strs.isNotEmpty(item.getUserName()) ? "用户名 :" : "汇总"));
                helper.setText(R.id.input, Nums.formatDecimal(item.getTransferInAmount(), 4));
                helper.setText(R.id.output, Nums.formatDecimal(item.getTransferOutAmount(), 4));
                helper.setText(R.id.realBetMonet, Nums.formatDecimal(item.getConfirmAmount(), 4));
                helper.setText(R.id.bonus, Nums.formatDecimal(item.getAwardAmount(), 4));
                helper.setText(R.id.yingkui, Nums.formatDecimal(item.getProfitAmount(), 4));
            }
        };
        recyclerView.setAdapter(adapter);

        startMills = Dates.getTimeDayStart().getTime();
        stopMills = Dates.getTimeDayStop().getTime();
        mStartTime.setText(Dates.getCurrentDate(Dates.dateFormatYMD));
        mStopTime.setText(Dates.getCurrentDate(Dates.dateFormatYMD));
        search(null, startMills, stopMills, PAGE_SIZE);

        //刷新数据
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String username = mUsername.getText().toString().trim();
                currPage = 0;
                search(username, startMills, stopMills, PAGE_SIZE);
            }
        });

        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                String username = mUsername.getText().toString().trim();
                search(username, startMills, stopMills, PAGE_SIZE);
            }
        }, recyclerView);
    }

    public void search(String username, long sTime, long eTime, int size) {
        String startTime = Dates.getStringByFormat(sTime, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(eTime, Dates.dateFormatYMDHMS);

        // 如果用户名为当前用户，则不传参数（否则返回空）
        if (username != null && username.equals(UserManager.getIns().getMainUserName())) {
            username = null;
        }


        HttpAction.getThirdTeamReport(ActAgentThirdTeamReport.this, username, startTime, stopTime, currPage, size, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActAgentThirdTeamReport.this, "搜索中...");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", ThirdTeamReportBean.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                ThirdTeamReportBean respSearchOrder = getField(extra, "data", null);
                if (respSearchOrder != null) {
                    if (currPage == 0) {
                        listOrders.clear();
                    }

                    List<ThirdTeamReportBean.ListBean> templist = respSearchOrder.getList();

                    if (templist != null) {
                        ThirdTeamReportBean.ListBean lastData = null;
                        if (templist.size() > 0) {//最后一个数据为总计，要先剔除 然后在尾部添加
                            lastData = templist.get(templist.size() - 1);
                            templist.remove(lastData);
                        }
                        listOrders.addAll(templist);

                        //再把最后一个总和数据添加到末尾
                        if (lastData != null) {
                            listOrders.add(lastData);
                        }
                    } else {
                        listOrders.addAll(new ArrayList<ThirdTeamReportBean.ListBean>(0));
                    }

                    adapter.setNewData(listOrders);

                    int TOTAL_COUNTER = respSearchOrder.getTotalCount();

                    if (PAGE_SIZE * (currPage + 1) >= TOTAL_COUNTER) {
                        adapter.loadMoreEnd(true);
                        adapter.disableLoadMoreIfNotFullPage();
                        adapter.setEnableLoadMore(false);
                    } else {
                        currPage++;
                    }

                    if (listOrders.size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                    }

                } else {
                    Toasts.show(ActAgentThirdTeamReport.this, msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                Dialogs.hideProgressDialog(ActAgentThirdTeamReport.this);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.search:
                String username = mUsername.getText().toString().trim();
                currPage = 0;
                search(username, startMills, stopMills, PAGE_SIZE);
                break;
            case R.id.startTime:
                Calendar calendarStart = Calendar.getInstance();
                showDateDialog(mStartTime, Arrays.asList(calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH) + 1,
                        calendarStart.get(Calendar.DAY_OF_MONTH)));
                break;
            case R.id.stopTime:
                Calendar calendarStop = Calendar.getInstance();
                showDateDialog(mStopTime, Arrays.asList(calendarStop.get(Calendar.YEAR), calendarStop.get(Calendar.MONTH) + 1,
                        calendarStop.get(Calendar.DAY_OF_MONTH)));
                break;
            default:
        }
    }

    private void showDateDialog(final View view, List<Integer> date) {
        DatePickerDialog.Builder builder = new DatePickerDialog.Builder(this);
        builder.setSelectYear(date.get(0) - 1)
                .setSelectMonth(date.get(1) - 1)
                .setSelectDay(date.get(2) - 1)
                .setOnDateSelectedListener(new DatePickerDialog.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(int[] dates) {
                        String text = dates[0] + "-" + (dates[1] > 9 ? dates[1] : ("0" + dates[1])) + "-"
                                + (dates[2] > 9 ? dates[2] : ("0" + dates[2]));
                        if (view == mStartTime) {
                            startMills = Dates.getMillisOfStr(text, Dates.dateFormatYMD);
                            mStartTime.setText(text);
                        } else if (view == mStopTime) {
                            stopMills = Dates.getMillisOfStr(text, Dates.dateFormatYMD) + 24 * 60 * 60 * 1000 - 1000;
                            mStopTime.setText(text);
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });


        builder.setMaxYear(DateUtil.getYear());
        builder.setMaxMonth(DateUtil.getDateForString(DateUtil.getToday()).get(1));
        builder.setMaxDay(DateUtil.getDateForString(DateUtil.getToday()).get(2));
        DatePickerDialog dateDialog = builder.create();
        dateDialog.show();
    }
}
