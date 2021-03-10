package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import com.desheng.base.context.BetStatus;
import com.desheng.base.model.GameRecord;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.view.ListMenuPopupWindow;
import com.desheng.base.view.SpinnerListAdapter;
import com.google.gson.reflect.TypeToken;
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

public class ActAgentTeamLotteryCenter extends AbAdvanceActivity implements View.OnClickListener {
    private static final int PAGE_SIZE = 20;
    private TextView tv_title;
    private RecyclerView recyclerView;
    private BaseQuickAdapter<GameRecord, BaseViewHolder> adapter;
    private BLTextView lotteryType, tvStatus, startTime, stopTime, search;
    private BLEditText mUsername, qihao;
    private String userName;
    private int currPage = 0;
    private Integer status;
    private ArrayList<LotteryInfo> listAllLottery;
    private String arrayLottery[] = null;
    private String lotteryCode;
    private ListMenuPopupWindow lotteryPopup = null;
    private ListMenuPopupWindow statePopup = null;
    private long startMills = 0;
    private long stopMills = 0;
    private List<GameRecord> listOrders = new ArrayList<>();
    private int totalCount;
    private int page;
    private int size;
    private String issue;
    private String arrayStatus[] = new String[]{"全部状态", "未中奖", "已撤销", "未开奖", "已派奖", "系统撤销"};
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout emptyView;
    private String mChandiUserName;

    public static void launcher(Activity activity, String userName) {
        Intent intent = new Intent(activity, ActAgentTeamLotteryCenter.class);
        intent.putExtra("userName", userName);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_agent_team_lottery_records;
    }

    @Override
    protected void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();
        tv_title = ((TextView) headRoot.findViewById(R.id.tv_title));
        tv_title.setText("团队投注记录");
        mChandiUserName = getIntent().getStringExtra("userName");
        initView();
    }

    private void initView() {
        lotteryType = ((BLTextView) findViewById(R.id.lotteryType));
        tvStatus = ((BLTextView) findViewById(R.id.status));
        mUsername = ((BLEditText) findViewById(R.id.username));
        qihao = ((BLEditText) findViewById(R.id.qihao));
        startTime = ((BLTextView) findViewById(R.id.startTime));
        stopTime = ((BLTextView) findViewById(R.id.stopTime));
        search = ((BLTextView) findViewById(R.id.search));
        swipeRefreshLayout = ((SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout));
        emptyView = ((RelativeLayout) findViewById(R.id.emptyView));

        if (Strs.isNotEmpty(mChandiUserName)) {
            mUsername.setText(mChandiUserName);
        }

        lotteryType.setOnClickListener(this);
        tvStatus.setOnClickListener(this);
        qihao.setOnClickListener(this);
        startTime.setOnClickListener(this);
        stopTime.setOnClickListener(this);
        search.setOnClickListener(this);

        recyclerView = ((RecyclerView) findViewById(R.id.recycleView));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BaseQuickAdapter<GameRecord, BaseViewHolder>(R.layout.item_agent_team_lottery_records) {
            @Override
            protected void convert(BaseViewHolder helper, GameRecord item) {
                helper.setText(R.id.username, item.account);
                helper.setText(R.id.number, item.billno);
                helper.setText(R.id.tvDate, (Dates.getStringByFormat(item.orderTime, Dates.dateFormatYMD)));
                helper.setText(R.id.lotteryType, (item.lottery + "\n" + item.method));
                helper.setText(R.id.beishu, (item.nums + "注" + "/" + item.multiple + "倍"));
                helper.setText(R.id.qihao, item.issue);
                helper.setText(R.id.status, item.statusRemark);
                helper.setText(R.id.bonus, (Nums.formatDecimal(item.winMoney, 3)));
                helper.setText(R.id.tvBalance, (Nums.formatDecimal(item.money, 3)));

                ((TextView) helper.getView(R.id.tvDetail)).setText(Html.fromHtml("<u>" + "详 情" + "</u>"));
                helper.getView(R.id.tvDetail).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActAgentTeamBetDetail.launch(ActAgentTeamLotteryCenter.this, item.billno);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);

        statePopup = new ListMenuPopupWindow(ActAgentTeamLotteryCenter.this, AbDevice.SCREEN_WIDTH_PX, arrayStatus);
        statePopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {
                if (position == 0) {
                    status = null;
                } else {
                    status = BetStatus.find(arrayStatus[position]).getCode();
                }
                tvStatus.setText(arrayStatus[position]);
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

        startMills = Dates.getTimeDayStart().getTime();
        stopMills = Dates.getTimeDayStop().getTime();
        startTime.setText(Dates.getCurrentDate(Dates.dateFormatYMD));
        stopTime.setText(Dates.getCurrentDate(Dates.dateFormatYMD));
        initLottory();
        updateTable();
    }

    public void initLottory() {
        HttpAction.getOpenLotterys(this, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {

            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<LotteryInfo>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && getField(extra, "data", null) != null) {
                    listAllLottery = getFieldObject(extra, "data", ArrayList.class);
                    arrayLottery = new String[listAllLottery.size() + 1];
                    arrayLottery[0] = "全部彩种";
                    for (int i = 1; i < arrayLottery.length; i++) {
                        arrayLottery[i] = listAllLottery.get(i - 1).getShowName();
                    }
                    lotteryPopup = new ListMenuPopupWindow(ActAgentTeamLotteryCenter.this, AbDevice.SCREEN_WIDTH_PX, arrayLottery);
                    lotteryPopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
                        @Override
                        public void click(int position, View view) {
                            String lotteryString = arrayLottery[position];
                            if (position == 0) {
                                lotteryCode = null;
                            } else {
                                lotteryCode = listAllLottery.get(position - 1).getCode();
                            }
                            lotteryType.setText(lotteryString);
                        }
                    });
                } else {
                    Toasts.show(ActAgentTeamLotteryCenter.this, msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {

            }
        });
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
            case R.id.lotteryType:
                if (lotteryPopup != null) {
                    lotteryPopup.showAsDropDown(lotteryType, 0, 0);//显示在rl_spinner的下方
                }
                break;
            case R.id.status:
                statePopup.showAsDropDown(tvStatus, 0, 0);
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
            default:
        }
    }

    /**
     *
     */
    public void search() {
        String startTime = Dates.getStringByFormat(startMills, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
        HttpAction.searchGameRecord(lotteryCode, status, userName, issue, startTime, stopTime, currPage, PAGE_SIZE, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActAgentTeamLotteryCenter.this, "搜索中...");
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespGameRecord.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                HttpAction.RespGameRecord respSearchOrder = getField(extra, "data", null);
                if (respSearchOrder != null) {
                    if (currPage == 0) {
                        listOrders.clear();
                    }

                    List<GameRecord> templist = respSearchOrder.list;
                    if (templist != null) {
                        listOrders.addAll(templist);
                    } else {
                        listOrders.addAll(new ArrayList<GameRecord>(0));
                    }

                    adapter.setNewData(listOrders);

                    int TOTAL_COUNTER = respSearchOrder.totalCount;

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
                    Toasts.show(ActAgentTeamLotteryCenter.this, msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                Dialogs.hideProgressDialog(ActAgentTeamLotteryCenter.this);
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

    public void updateTable() {
        issue = Views.getText(qihao);
        if (Strs.isEmpty(issue)) {
            issue = null;
        }
        userName = Views.getText(mUsername);
        if (Strs.isEmpty(userName)) {
            userName = null;
        }
        search();
    }
}
