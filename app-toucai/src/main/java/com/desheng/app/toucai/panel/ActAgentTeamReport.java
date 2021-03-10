package com.desheng.app.toucai.panel;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.BonusSalaryBean;
import com.desheng.base.model.TeamReport;
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

public class ActAgentTeamReport extends AbAdvanceActivity implements View.OnClickListener {
    private static final int PAGE_SIZE = 20;
    private TextView tv_title;
    private RecyclerView recyclerView;
    private BaseQuickAdapter<TeamReport, BaseViewHolder> adapter;
    private BLTextView mStartTime, mStopTime, mSearch, mShangji;
    private BLEditText mUsername;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int currentPage = 0;
    private long startMills = 0;
    private long stopMills = 0;
    private List<TeamReport> listOrders = new ArrayList<>();
    private RelativeLayout emptyView;
    private RecyclerView recycleView_Chain;
    private BaseQuickAdapter<String, BaseViewHolder> mAgentChainAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.act_agent_team_reports;
    }

    @Override
    protected void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();
        tv_title = ((TextView) headRoot.findViewById(R.id.tv_title));
        tv_title.setText("团队报表");
        initView();
    }

    private void initView() {
        recyclerView = ((RecyclerView) findViewById(R.id.recycleView));
        recycleView_Chain = ((RecyclerView) findViewById(R.id.recycleView_Chain));
        swipeRefreshLayout = ((SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout));
        emptyView = ((RelativeLayout) findViewById(R.id.emptyView));
        mStartTime = findViewById(R.id.startTime);
        mStopTime = findViewById(R.id.stopTime);
        mSearch = findViewById(R.id.search);
        mShangji = findViewById(R.id.shangji);
        mUsername = findViewById(R.id.username);

        mSearch.setOnClickListener(this);
        mStartTime.setOnClickListener(this);
        mStopTime.setOnClickListener(this);
        mShangji.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BaseQuickAdapter<TeamReport, BaseViewHolder>(R.layout.item_agent_team_reports) {
            @Override
            protected void convert(BaseViewHolder helper, TeamReport item) {
                RecyclerView innerRc = (RecyclerView) helper.getView(R.id.innerRecycleView);
                if (Strs.isNotEmpty(item.userName)) {
                    helper.setText(R.id.username, Html.fromHtml("<u>" + item.userName + "</u>"));
                } else {
                    helper.setText(R.id.username, "汇总");
                }
                helper.getView(R.id.usertype).setBackgroundColor(item.userType == 0 ? Color.BLUE : Color.RED);
                if (Strs.isNotEmpty(item.userName)) {
                    helper.getView(R.id.usertype).setVisibility(View.VISIBLE);
                    helper.setText(R.id.usertype, item.userType == 0 ? "玩家" : "代理");
                    helper.getView(R.id.tv1).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.usertype).setVisibility(View.GONE);
                    helper.getView(R.id.tv1).setVisibility(View.GONE);
                }

                List<TempEntity> keyValueLists = new ArrayList<>();
                keyValueLists.add(new TempEntity("余额", item.balance));
                keyValueLists.add(new TempEntity("充值", String.valueOf(item.depositAmount)));
                keyValueLists.add(new TempEntity("提现", String.valueOf(item.withdrawAmount)));
                keyValueLists.add(new TempEntity("腾讯投注额", String.valueOf(item.txsscConfirmAmount)));
                keyValueLists.add(new TempEntity("投注", String.valueOf(item.confirmAmount)));
                keyValueLists.add(new TempEntity("奖金", String.valueOf(item.awardAmount)));
                if (UserManager.getIns().getShowDandanZjjj()) {
                    keyValueLists.add(new TempEntity("中单工资", String.valueOf(item.orderzjjjAmount)));
                }
                if (UserManager.getIns().getShowDandanFh()) {
                    keyValueLists.add(new TempEntity("挂单工资", String.valueOf(item.orderDdfhAmount)));
                }

                if (UserManager.getIns().getHasDandanGZ()) {
                    keyValueLists.add(new TempEntity("单单日结", String.valueOf(item.orderBonusAmount)));
                }
                keyValueLists.add(new TempEntity("返点", String.valueOf(item.pointAmount)));
                keyValueLists.add(new TempEntity("活动", String.valueOf(item.activityAmount)));
                keyValueLists.add(new TempEntity("手续费", String.valueOf(item.feeAmount)));
                keyValueLists.add(new TempEntity("总盈亏", String.valueOf(-item.profitAmount)));

                innerRc.setLayoutManager(new GridLayoutManager(ActAgentTeamReport.this, 2));
                BaseQuickAdapter<TempEntity, BaseViewHolder> innerAdapter = new BaseQuickAdapter<TempEntity,
                        BaseViewHolder>(R.layout.inneritem_agent_team_reports, keyValueLists) {
                    @Override
                    protected void convert(BaseViewHolder holder, TempEntity item) {
                        holder.setText(R.id.name, item.key);
                        holder.setText(R.id.value, item.value);
                    }
                };
                innerRc.setAdapter(innerAdapter);

                ((TextView) helper.getView(R.id.username)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentPage = 0;
                        listOrders.clear();
                        mUsername.setText(item.userName);
                        String userName = item.userName;
                        search(userName, startMills, stopMills, PAGE_SIZE);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.setEnableLoadMore(true);

        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                String username = mUsername.getText().toString().trim();
                search(username, startMills, stopMills, PAGE_SIZE);
            }
        }, recyclerView);

        startMills = Dates.getTimeDayStart().getTime();
        stopMills = Dates.getTimeDayStop().getTime();
        mStartTime.setText(Dates.getCurrentDate(Dates.dateFormatYMD));
        mStopTime.setText(Dates.getCurrentDate(Dates.dateFormatYMD));
        getQuerySalaryBonus();

        //刷新数据
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String username = mUsername.getText().toString().trim();
                currentPage = 0;
                search(username, startMills, stopMills, PAGE_SIZE);
            }
        });

        //代理线
        recycleView_Chain.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAgentChainAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_name_chain) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(com.desheng.base.R.id.tv_item_name, item + " > ");
            }
        };
        recycleView_Chain.setAdapter(mAgentChainAdapter);

        mAgentChainAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String name = (String) adapter.getData().get(position);
                if (position == 0) {
                    mUsername.setText("");
                    currentPage = 0;
                    search("", startMills, stopMills, PAGE_SIZE);
                } else {
                    if (Strs.isNotEmpty(name)) {
                        mUsername.setText(name);
                        mUsername.setSelection(name.length());
                        currentPage = 0;
                        search(name, startMills, stopMills, PAGE_SIZE);
                    }
                }
            }
        });
    }

    private void getQuerySalaryBonus() {
        HttpAction.getQuerySalaryBonus(new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", BonusSalaryBean.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActAgentTeamReport.this, "搜索中...");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                BonusSalaryBean bonusSalaryBean;
                if (error == 0 && code == 0) {
                    bonusSalaryBean = getField(extra, "data", null);
                    UserManager.getIns().setHasDandanGZ(bonusSalaryBean.getShowdandan());
                    UserManager.getIns().setShowDandanFh(bonusSalaryBean.getShowdandanFh());
                    UserManager.getIns().setShowDandanZjjj(bonusSalaryBean.getShowdandanZjjj());
                } else {
                    Toasts.show(ActAgentTeamReport.this, msg);
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                Dialogs.hideProgressDialog(ActAgentTeamReport.this);
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                search(null, startMills, stopMills, PAGE_SIZE);
            }
        });
    }

    public void search(String username, long sTime, long eTime, int size) {
        String startTime = Dates.getStringByFormat(sTime, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(eTime, Dates.dateFormatYMDHMS);

        // 如果用户名为当前用户，则不传参数（否则返回空）
        if (username != null && username.equals(UserManager.getIns().getMainUserName())) {
            username = null;
        }

        String finalUsername = username;
        HttpAction.getTeamReport(ActAgentTeamReport.this, username, startTime, stopTime, currentPage, size, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActAgentTeamReport.this, "搜索中...");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespTeamReport.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                HttpAction.RespTeamReport respSearchOrder = getField(extra, "data", null);
                if (respSearchOrder != null) {
                    if (currentPage == 0) {
                        listOrders.clear();
                    }

                    List<TeamReport> templist = respSearchOrder.list;

                    if (templist != null) {
                        listOrders.addAll(templist);
                    } else {
                        listOrders.addAll(new ArrayList<TeamReport>());
                    }
                    adapter.setNewData(listOrders);

                    int TOTAL_COUNTER = respSearchOrder.totalCount;

                    if (PAGE_SIZE * (currentPage + 1) >= TOTAL_COUNTER) {
                        adapter.loadMoreEnd(true);
                        adapter.disableLoadMoreIfNotFullPage();
                        adapter.setEnableLoadMore(false);
                    } else {
                        currentPage++;
                    }

                    List<String> statistics = respSearchOrder.statistics;
                    if (statistics != null && statistics.size() > 0) {
                        agentShangji = statistics.get(statistics.size() - 1);
                    }


                    //代理线
                    if (respSearchOrder.statistics != null) {
                        String tempStr = "";
                        if (Strs.isEmpty(finalUsername)) {
                            tempStr = UserManager.getIns().getMainUserName();
                        } else {
                            tempStr = finalUsername;
                        }
                        respSearchOrder.statistics.add(tempStr);
                        mAgentChainAdapter.setNewData(respSearchOrder.statistics);
                    } else {
                        String tempStr = "";
                        tempStr = UserManager.getIns().getMainUserName();
                        List<String> list = new ArrayList<>();
                        list.add(tempStr);
                        mAgentChainAdapter.setNewData(list);
                    }

                    if (listOrders.size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                    }

                } else {
                    Toasts.show(ActAgentTeamReport.this, msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                Dialogs.hideProgressDialog(ActAgentTeamReport.this);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private String agentShangji = "";

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.search:
                String username = mUsername.getText().toString().trim();
                currentPage = 0;
                search(username, startMills, stopMills, PAGE_SIZE);
                break;
            case R.id.shangji:
                currentPage = 0;
                search(agentShangji, startMills, stopMills, PAGE_SIZE);
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

    static class TempEntity {
        String key;
        String value;

        public TempEntity(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

}
