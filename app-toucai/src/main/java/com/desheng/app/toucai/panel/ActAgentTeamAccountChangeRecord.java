package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.global.AbDevice;
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
import com.desheng.base.model.TeamAccountChangeRecord;
import com.desheng.base.view.ListMenuPopupWindow;
import com.desheng.base.view.SpinnerListAdapter;
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

public class ActAgentTeamAccountChangeRecord extends AbAdvanceActivity implements View.OnClickListener {
    private static final int PAGE_SIZE = 20;
    private TextView tv_title;
    private RecyclerView recyclerView;
    private BaseQuickAdapter<TeamAccountChangeRecord, BaseViewHolder> adapter;
    private ListMenuPopupWindow categoryPopup = null;
    private String[] arrayCategory = {"全部", "彩票游戏", "活动", "分红", "转账", "修正资金", "充值", "提现"};
    private String[] arrayValue = {"", "7", "90", "91", "3", "80", "1", "2"};
    private BLTextView billType, startTime, stopTime, search;
    private BLEditText mUsername;
    private long startMills, stopMills = 0;
    private int currPage = 0;
    private String zbType = "";
    private List<TeamAccountChangeRecord> listOrders = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout emptyView;
    private String mChuandiUserName;

    public static void launcher(Activity activity, String userName) {
        Intent intent = new Intent(activity, ActAgentTeamAccountChangeRecord.class);
        intent.putExtra("userName", userName);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_agent_team_account_change_record;
    }

    @Override
    protected void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();
        tv_title = ((TextView) headRoot.findViewById(R.id.tv_title));
        tv_title.setText("团队账变记录");
        mChuandiUserName = getIntent().getStringExtra("userName");
        initView();
    }

    private void initView() {
        categoryPopup = new ListMenuPopupWindow(ActAgentTeamAccountChangeRecord.this, AbDevice.SCREEN_WIDTH_PX, arrayCategory);
        recyclerView = ((RecyclerView) findViewById(R.id.recycleView));
        swipeRefreshLayout = ((SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout));
        emptyView = ((RelativeLayout) findViewById(R.id.emptyView));
        billType = ((BLTextView) findViewById(R.id.billType));
        mUsername = ((BLEditText) findViewById(R.id.username));
        startTime = ((BLTextView) findViewById(R.id.startTime));
        stopTime = ((BLTextView) findViewById(R.id.stopTime));
        search = ((BLTextView) findViewById(R.id.search));

        if (Strs.isNotEmpty(mChuandiUserName)) {
            mUsername.setText(mChuandiUserName);
        }

        billType.setOnClickListener(this);
        search.setOnClickListener(this);
        startTime.setOnClickListener(this);
        stopTime.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BaseQuickAdapter<TeamAccountChangeRecord, BaseViewHolder>(R.layout.item_agent_team_account_change_record) {
            @Override
            protected void convert(BaseViewHolder helper, TeamAccountChangeRecord item) {
                helper.setText(R.id.username, item.cn);
                helper.setText(R.id.tvBillNo, item.spsn);
                helper.setText(R.id.tvDate, item.createTime);
                helper.setText(R.id.operateType, item.changeTypeStr);
                helper.setText(R.id.workType, item.changeTypeDetailStr);
                helper.setText(R.id.biandongMoney, Nums.formatDecimal(item.amount, 4));
                helper.setText(R.id.beizhu, item.note);
                helper.setText(R.id.tvbalance, Nums.formatDecimal(item.point, 4));
            }
        };
        recyclerView.setAdapter(adapter);

        startMills = Dates.getTimeDayStart().getTime();
        stopMills = Dates.getTimeDayStop().getTime();
        startTime.setText(Dates.getCurrentDate(Dates.dateFormatYMD));
        stopTime.setText(Dates.getCurrentDate(Dates.dateFormatYMD));

        categoryPopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {
                zbType = arrayValue[position];
                billType.setText(arrayCategory[position]);
            }
        });

        //刷新数据
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currPage = 0;
                listOrders.clear();
                updateTable();
            }
        });

        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                updateTable();
            }
        }, recyclerView);

        updateTable();
    }

    public void updateTable() {
        String userName = Views.getText(mUsername);
        if (Strs.isEmpty(userName)) {
            userName = null;
        }
        search(userName, zbType, startMills, stopMills, currPage, PAGE_SIZE);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.search:
                currPage = 0;
                listOrders.clear();
                updateTable();
                break;
            case R.id.startTime:
                Calendar calendarStart = Calendar.getInstance();
                showDateDialog(startTime, Arrays.asList(calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH) + 1,
                        calendarStart.get(Calendar.DAY_OF_MONTH)));
                break;
            case R.id.stopTime:
                Calendar calendarStop = Calendar.getInstance();
                showDateDialog(stopTime, Arrays.asList(calendarStop.get(Calendar.YEAR), calendarStop.get(Calendar.MONTH) + 1,
                        calendarStop.get(Calendar.DAY_OF_MONTH)));
                break;
            case R.id.billType:
                categoryPopup.showAsDropDown(billType, 0, 0);//显示在rl_spinner的下方
                break;
            default:
        }
    }

    public void search(String username, String zbType, long sTime, long eTime, int page, int size) {
        String startTime = Dates.getStringByFormat(sTime, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(eTime, Dates.dateFormatYMDHMS);

        HttpAction.getAccountChange(ActAgentTeamAccountChangeRecord.this, username, zbType, null, startTime, stopTime, page, size, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActAgentTeamAccountChangeRecord.this, "搜索中...");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespTeamAccountChange.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                HttpAction.RespTeamAccountChange respAccountChange = getField(extra, "data", null);
                if (respAccountChange != null) {
                    if (page == 0) {
                        listOrders.clear();
                    }

                    List<TeamAccountChangeRecord> templist = respAccountChange.list;
                    if (templist != null) {
                        listOrders.addAll(templist);
                    } else {
                        listOrders.addAll(new ArrayList<TeamAccountChangeRecord>(0));
                    }

                    adapter.setNewData(listOrders);

                    int TOTAL_COUNTER = respAccountChange.totalCount;

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
                    Toasts.show(ActAgentTeamAccountChangeRecord.this, msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                Dialogs.hideProgressDialog(ActAgentTeamAccountChangeRecord.this);
                swipeRefreshLayout.setRefreshing(false);

            }
        });
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
                        if (view == startTime) {
                            startMills = Dates.getMillisOfStr(text, Dates.dateFormatYMD);
                            startTime.setText(text);
                        } else if (view == stopTime) {
                            stopMills = Dates.getMillisOfStr(text, Dates.dateFormatYMD) + 24 * 60 * 60 * 1000 - 1000;
                            stopTime.setText(text);
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
