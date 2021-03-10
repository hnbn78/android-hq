package com.desheng.app.toucai.panel;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.desheng.app.toucai.adapter.ReportAdapter;
import com.desheng.app.toucai.view.SimpleDividerDecoration;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.Params;
import com.desheng.base.model.TeamReport;
import com.desheng.base.util.CoinHelper;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.DrawableCenterTextView;
import com.pearl.view.mypicker.DatePickerDialog;
import com.pearl.view.mypicker.DateUtil;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * 彩票报表
 */
public class ActLotteryReport extends AbAdvanceActivity {
    public static void launch(Activity activity, boolean needTeam) {
        Intent itt = new Intent(activity, ActLotteryReport.class);
        itt.putExtra("needTeam", needTeam);
        activity.startActivity(itt);
    }

    private RadioGroup vgType;
    private RadioButton rbPersion;
    private RadioButton rbTeam;
    private View tvUserAccount;
    private View etUserAccount;

    private TextView tvStartDate, tvEndDate;

    private static final int PAGE_SIZE = 20;
    private int type = 0;

    private int totalCount;
    private long startMills = 0;
    private long stopMills = 0;
    private int currPage = -1;
    private Button Search_bt;
    private List<List<Params>> list = new ArrayList<>();
    private ReportAdapter reportAdapter;

    private boolean needTeam = false;
    private SwipeRefreshLayout refreshLayout;
    private String coinType;

    @Override
    protected int getLayoutId() {
        return R.layout.act_lottery_reports;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "报表中心");
        setStatusBarTranslucentAndLightContentWithPadding();
        //needTeam = getIntent().getBooleanExtra("needTeam", true);
        initVew();
        updateTable();
        refreshLayout.setOnRefreshListener(() -> {
            currPage = -1;
            updateTable();
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
        builder.setMaxYear(DateUtil.getYear());
        builder.setMaxMonth(DateUtil.getDateForString(DateUtil.getToday()).get(1));
        builder.setMaxDay(DateUtil.getDateForString(DateUtil.getToday()).get(2));
        DatePickerDialog dateDialog = builder.create();
        dateDialog.show();
    }

    public void updateTable() {
        String userAccount = Views.getText(findViewById(R.id.etUserAccount));
        if (Strs.isEmpty(userAccount)) {
            userAccount = null;
        }
        currPage++;
        searchLotterty(userAccount, startMills, stopMills, currPage, PAGE_SIZE, type);

    }

    /**
     * 彩票报表
     */
    public void searchLotterty(String username, long sTime, long eTime, final int page, int size, final int type) {
        String startTime = Dates.getStringByFormat(sTime, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(eTime, Dates.dateFormatYMDHMS);
        HttpAction.searchLotteryReport(username, startTime, stopTime, coinType, page, size, type, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                if (page == 0) {
                    refreshLayout.setRefreshing(true);
                } else
                    Dialogs.showProgressDialog(ActLotteryReport.this, "搜索中...");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespSearchLotteryReport.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                HttpAction.RespSearchLotteryReport respSearchLotterylist = getField(extra, "data", null);
                if (respSearchLotterylist != null) {
                    if (page == 0) {
                        list.clear();
                    }
                    totalCount = respSearchLotterylist.totalCount;
                    List<TeamReport> reports = respSearchLotterylist.list;
                    for (TeamReport bean : reports) {
                        List<Params> params = new ArrayList<>();
                        if (Strs.isEmpty(bean.userName))
                            params.add(new Params("汇总", ""));
                        else
                            params.add(new Params("用户名", bean.userName));
                        if (type == 0)
                            params.add(new Params("时间", bean.date));
                        params.add(new Params("充值", Nums.formatDecimal(bean.depositAmount, 4)));
                        params.add(new Params("提现", Nums.formatDecimal(bean.withdrawAmount, 4)));
                        params.add(new Params("投注", Nums.formatDecimal(bean.confirmAmount, 4)));
                        if (UserManager.getIns().showTXBettAmount()) {
                            params.add(new Params("腾讯投注额", Nums.formatDecimal(bean.txsscConfirmAmount, 4)));
                        }
                        params.add(new Params("奖金", Nums.formatDecimal(bean.awardAmount, 4)));
                        if (UserManager.getIns().showDanDanClear())
                            params.add(new Params("单单日结", Nums.formatDecimal(bean.orderBonusAmount, 4)));

                        if (UserManager.getIns().showDanDanGZ()) {
                            String key = "单单工资";
                            params.add(new Params(key, Nums.formatDecimal(bean.bonusAmount, 4)));
                        }

                        if (UserManager.getIns().getShowDandanZjjj()) {
                            params.add(new Params("中单工资", Nums.formatDecimal(bean.orderzjjjAmount, 4)));
                        }
                        if (UserManager.getIns().getShowDandanFh()) {
                            params.add(new Params("挂单工资", Nums.formatDecimal(bean.orderDdfhAmount, 4)));
                        }

                        params.add(new Params("返点", Nums.formatDecimal(bean.pointAmount, 4)));
                        params.add(new Params("活动", Nums.formatDecimal(bean.activityAmount, 4)));
                        if (UserManager.getIns().showFeeAmount()) {
                            params.add(new Params("手续费", Nums.formatDecimal(bean.feeAmount, 4)));
                        }

                        /*总盈亏前台写的要和后台返回的相反*/
                        String val = Nums.formatDecimal(-bean.profitAmount, 4);
                        params.add(new Params("总盈亏", val));
                        if (params.size() % 2 != 0) {
                            params.add(new Params("", ""));
                        }
                        list.add(params);
                    }
                    boolean isLoadMore = list.size() < totalCount;
                    reportAdapter.setEnableLoadMore(isLoadMore);

                    if (page == 0 && list.size() == 0) {
                        emptyLayout.setVisibility(View.VISIBLE);
                    } else {
                        emptyLayout.setVisibility(View.GONE);
                    }

                    if (isLoadMore)
                        currPage++;
                    reportAdapter.notifyDataSetChanged();
                } else {
                    Toasts.show(ActLotteryReport.this, msg, false);
                    emptyLayout.setVisibility(View.VISIBLE);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                if (currPage > 0)
                    currPage--;
                return super.onError(status, content);

            }

            @Override
            public void onAfter(int id) {
                if (page == 0) {
                    Dialogs.hideProgressDialog(ActLotteryReport.this);
                }
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private RelativeLayout emptyLayout;

    private void initVew() {
        vgType = findViewById(R.id.vgType);
        rbPersion = findViewById(R.id.rbPersion);
        rbTeam = findViewById(R.id.rbTeam);
        tvUserAccount = findViewById(R.id.tvUserAccount);
        etUserAccount = findViewById(R.id.etUserAccount);
        refreshLayout = findViewById(R.id.swipeRefreshLayout);
        emptyLayout = findViewById(R.id.emptyLayout);

        DrawableCenterTextView tvCoinType = findViewById(R.id.tvCoinType);
        RecyclerView recyclerView = findViewById(R.id.lottery_report_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SimpleDividerDecoration(this));
        reportAdapter = new ReportAdapter(list);
        recyclerView.setAdapter(reportAdapter);
        if (needTeam) {
            tvUserAccount.setVisibility(View.VISIBLE);
            etUserAccount.setVisibility(View.VISIBLE);
            vgType.setVisibility(View.VISIBLE);
            vgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    rbPersion.setTextColor(Color.BLACK);
                    rbTeam.setTextColor(Color.BLACK);

                    if (checkedId == R.id.rbPersion) {
                        rbPersion.setTextColor(Color.WHITE);
                        type = 0;
                    } else if (checkedId == R.id.rbTeam) {
                        rbTeam.setTextColor(Color.WHITE);
                        type = 1;
                    }

                    currPage = -1;
                    updateTable();
                }
            });
        }

        String currDate = Dates.getStringByFormat(System.currentTimeMillis(), Dates.dateFormatYMD);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvStartDate.setText(currDate);
        startMills = Dates.getMillisOfStr(currDate, Dates.dateFormatYMD);
        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                showDateDialog(tvStartDate, Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
            }
        });
        tvEndDate = findViewById(R.id.tvEndDate);
        tvEndDate.setText(currDate);
        stopMills = Dates.getMillisOfStr(currDate, Dates.dateFormatYMD) + 24 * 60 * 60 * 1000 - 1000;
        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                showDateDialog(tvEndDate, Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
            }
        });
        Search_bt = findViewById(R.id.btnSearch);
        Search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currPage = -1;
                updateTable();
            }
        });
        if (CoinHelper.getInstance().hasCoin) {
            coinType = CoinHelper.getCoinCode(CoinHelper.getInstance().currentCurrency);
            CoinHelper.createCoinPopup(tvCoinType, otcType -> coinType = otcType);
        }
    }

}