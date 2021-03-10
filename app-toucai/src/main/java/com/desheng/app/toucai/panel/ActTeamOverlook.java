package com.desheng.app.toucai.panel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.AbDateUtil;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.adapter.PopupItemAdapter;
import com.desheng.base.action.HttpAction;
import com.desheng.base.model.TeamOverviewBean;
import com.desheng.base.view.CustomMarkView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.noober.background.view.BLButton;
import com.noober.background.view.BLTextView;
import com.orhanobut.logger.Logger;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.view.mypicker.DatePickerDialog;
import com.pearl.view.mypicker.DateUtil;
import com.shark.tc.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ActTeamOverlook extends AbAdvanceActivity implements View.OnClickListener {

    private long startMills, stopMills = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.act_team_over;
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, ActTeamOverlook.class));
    }

    private static final int CHOOSE_DEPOSIT_AMOUNT = 0;
    private static final int CHOOSE_WITHDRAW_AMOUNT = 1;
    private static final int CHOOSE_CONFIRM_AMOUNT = 2;
    private static final int CHOOSE_AWARD_AMOUNT = 3;
    private static final int CHOOSE_ACTIVITY_AMOUNT = 4;
    private static final int CHOOSE_POINT_AMOUNT = 5;
    private static final int CHOOSE_NEW_USER_COUNT = 6;

    private int type = CHOOSE_DEPOSIT_AMOUNT;

    private TextView tvTeamBalance, tvTeamCount, tvOnline, tvOnlineUser,
            tvLineSetting, tvString;
    private BLTextView tvSetDate, mStartTime, mStopTime;
    private BLButton btnSearch;
    private RecyclerView teamRecycler;
    private String startTime, endTime;
    private TeamOverviewBean data;
    private LineChart lineChart;
    private CustomMarkView markView;
    private ArrayList<Entry> listValue = new ArrayList<>();
    String[] types = {"充值", "提现", "投注量", "派奖", "活动", "返点", "新增用户"};
    String[] dates = {"今天", "最近三天", "最近七天"};
    private PopupWindow popupWindow;
    private PopupItemAdapter popupAdapter;
    private String[] args = types;
    private boolean isDates;
    private int point_count = 4;


    @Override
    protected void init() {
        setStatusBarTranslucentAndDarkContent();
        hideToolbar();
        tvTeamBalance = findViewById(R.id.tvTeamBalance);
        tvTeamCount = findViewById(R.id.tvTeamCount);
        tvOnline = findViewById(R.id.tvOnline);
        tvOnlineUser = findViewById(R.id.tvOnlineUser);
        tvSetDate = findViewById(R.id.tvSetDate);
        tvSetDate.setOnClickListener(this);
        tvLineSetting = findViewById(R.id.tvLineSetting);
        tvLineSetting.setOnClickListener(this);
        teamRecycler = findViewById(R.id.teamRecycler);
        mStartTime = findViewById(R.id.tvStartDate);
        mStopTime = findViewById(R.id.tvEndDate);
        btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(this);
        mStartTime.setOnClickListener(this);
        mStopTime.setOnClickListener(this);

        lineChart = findViewById(R.id.lineChart);
        tvString = findViewById(R.id.tvString);
        lineChart.setNoDataTextColor(Color.BLUE);//没有数据时显示文字的颜色
        lineChart.setDrawGridBackground(false);//chart 绘图区后面的背景矩形将绘制
        lineChart.setDrawBorders(false);//禁止绘制图表边框的线
        lineChart.setDragEnabled(true);
        lineChart.getDescription().setEnabled(false);
        Legend mLegend = lineChart.getLegend();
        // mLegend.setPosition(LegendPosition.BELOW_CHART_CENTER);
        // 图例样式 (CIRCLE圆形；LINE线性；SQUARE是方块）
        mLegend.setForm(Legend.LegendForm.CIRCLE);
        findViewById(R.id.imgBack).setOnClickListener(this);
        // 隐藏右侧Y轴（只在左侧的Y轴显示刻度）
        lineChart.getAxisRight().setEnabled(false);
        XAxis xAxis = lineChart.getXAxis();
        // 显示X轴上的刻度值
        xAxis.setDrawLabels(true);
        // 设置X轴的数据显示在报表的下方
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //设置X轴显示的文字
        xAxis.setValueFormatter((value, axis) -> {
            int position = (int) (value / 10);
            Logger.d("size ===> " + data.getReports().dayReportList.size() + "\n　position　＝＝＝＞　" + position);
            int val = (int) (value % 10);
            if (val == 0) {
                try {
                    String dateStr = data.getReports().dayReportList.get(position).getDate();
                    return dateStr.substring(
                            5, dateStr.length());
                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }

            } else
                return "";
        });
        lineChart.getAxisLeft().setDrawGridLines(true);
        markView = new CustomMarkView(this, R.layout.mark_view);
        markView.setChartView(lineChart);
        lineChart.setMarker(markView);

        startMills = Dates.getTimeDayStart().getTime();
        stopMills = Dates.getTimeDayStop().getTime();
        mStartTime.setText(Dates.getCurrentDate(Dates.dateFormatYMD));
        mStopTime.setText(Dates.getCurrentDate(Dates.dateFormatYMD));

        endTime = AbDateUtil.getCurrentDate(AbDateUtil.dateFormatYMDHMS);
        long st = startMills;
        startTime = AbDateUtil.getStringByFormat(st, AbDateUtil.dateFormatYMDHMS);
        teamRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        popupWindow = createPopupWindow();
        onLoadData();
    }

    private void bindData() {
        String userCount = data.userCount + "人";
        tvTeamCount.setText(userCount);
        String poxyCount = data.agentCount + "人";
        tvOnline.setText(poxyCount);
        String memberCount = data.memberCount + "人";
        tvOnlineUser.setText(memberCount);
        String balance = data.teamBalance + "元";
        tvTeamBalance.setText(balance);
        List<TeamData> list = new ArrayList<>();
        list.add(new TeamData("充值", Nums.formatDecimal(data.reports.depositAmountSum, point_count)));
        list.add(new TeamData("提现", Nums.formatDecimal(data.reports.withdrawAmountSum, point_count)));
        list.add(new TeamData("消费", Nums.formatDecimal(data.reports.confirmAmountSum, point_count)));
        list.add(new TeamData("派奖", Nums.formatDecimal(data.reports.awardAmountSum, point_count)));
        list.add(new TeamData("返点", Nums.formatDecimal(data.reports.pointAmountSum, point_count)));
        TeamDataAdapter adapter = new TeamDataAdapter(list);
        teamRecycler.setAdapter(adapter);
        bindChartView();
    }

    private void bindChartView() {
        try {
            markView.clearDate();
            listValue.clear();
            String des;
            des = setXValue();
            markView.setKey(des);
            tvString.setText(des);
            LineDataSet lineDataSet = new LineDataSet(listValue, des);
            lineDataSet.enableDashedLine(10f, 5f, 0f);
            lineDataSet.enableDashedHighlightLine(10f, 5f, 0f);
            lineDataSet.setColor(Color.BLACK);
            lineDataSet.setCircleColor(Color.BLACK);
            lineDataSet.setLineWidth(1f);//设置线宽
            lineDataSet.setCircleRadius(3f);//设置焦点圆心的大小
            lineDataSet.setFormLineWidth(1f);
            lineDataSet.setDrawCircleHole(false);
            lineDataSet.setHighLightColor(Color.RED);//设置点击交点后显示交高亮线的颜色
            lineDataSet.setValueTextSize(9f);//设置显示值的文字大小
            lineDataSet.setDrawFilled(false);//设置禁用范围背景填充
            //格式化显示数据
            final DecimalFormat mFormat = new DecimalFormat("###,###,##0");
            lineDataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> mFormat.format(value));
            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ab_bg_toast);
                lineDataSet.setFillDrawable(drawable);//设置范围背景填充
            } else {
                lineDataSet.setFillColor(Color.BLACK);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(lineDataSet);
            LineData data = new LineData(dataSets);
            // 添加到图表中
            lineChart.setData(data);
            //绘制图表
            lineChart.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String setXValue() {
        String des;
        switch (type) {
            case CHOOSE_WITHDRAW_AMOUNT:
                des = "提现";
                for (int i = 0; i < data.getReports().dayReportList.size(); i++) {
                    TeamOverviewBean.ReportsBean.DayReportListBean bean = data.getReports().dayReportList.get(i);
                    listValue.add(new Entry(10 * i, bean.getWithdrawAmount()));
                    markView.addDate(bean.date);
                }
                break;
            case CHOOSE_CONFIRM_AMOUNT:
                des = "投注量";
                for (int i = 0; i < data.getReports().dayReportList.size(); i++) {
                    TeamOverviewBean.ReportsBean.DayReportListBean bean = data.getReports().dayReportList.get(i);
                    listValue.add(new Entry(10 * i, bean.getConfirmAmount()));
                    markView.addDate(bean.date);
                }
                break;
            case CHOOSE_AWARD_AMOUNT:
                des = "派奖";
                for (int i = 0; i < data.getReports().dayReportList.size(); i++) {
                    TeamOverviewBean.ReportsBean.DayReportListBean bean = data.getReports().dayReportList.get(i);
                    listValue.add(new Entry(10 * i, bean.getAwardAmount()));
                    markView.addDate(bean.date);
                }
                break;
            case CHOOSE_POINT_AMOUNT:
                des = "返点";
                for (int i = 0; i < data.getReports().dayReportList.size(); i++) {
                    TeamOverviewBean.ReportsBean.DayReportListBean bean = data.getReports().dayReportList.get(i);
                    listValue.add(new Entry(10 * i, bean.getPointAmount()));
                    markView.addDate(bean.date);
                }
                break;

            case CHOOSE_ACTIVITY_AMOUNT:
                des = "活动";
                for (int i = 0; i < data.getReports().dayReportList.size(); i++) {
                    TeamOverviewBean.ReportsBean.DayReportListBean bean = data.getReports().dayReportList.get(i);
                    listValue.add(new Entry(10 * i, bean.getActivityAmount()));
                    markView.addDate(bean.date);
                }
                break;

            case CHOOSE_NEW_USER_COUNT:
                des = "注册量";
                for (int i = 0; i < data.getReports().dayReportList.size(); i++) {
                    TeamOverviewBean.ReportsBean.DayReportListBean bean = data.getReports().dayReportList.get(i);
                    listValue.add(new Entry(10 * i, data.getRegisterList().get(i).getCount()));
                    markView.addDate(bean.date);
                }
                break;
            default:
                des = "充值";
                for (int i = 0; i < data.getReports().dayReportList.size(); i++) {
                    TeamOverviewBean.ReportsBean.DayReportListBean bean = data.getReports().dayReportList.get(i);
                    listValue.add(new Entry(10 * i, bean.getDepositAmount()));
                    markView.addDate(bean.date);
                }
                break;
        }
        return des;
    }

    private void onLoadData() {
        HttpAction.getTeamOver(startTime, endTime, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActTeamOverlook.this, "");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", TeamOverviewBean.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (getField(extra, "data", null) != null) {
                    data = getField(extra, "data", null);
//                    setChartValueData(data);
                    bindData();
                }

                return true;
            }

            @Override
            public void onAfter(int id) {
                Dialogs.hideProgressDialog(ActTeamOverlook.this);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.tvSetDate:
                isDates = true;
                args = dates;
                popupAdapter.resetArgs(args);
                popupWindow.showAsDropDown(tvSetDate, -50, 10);
                break;
            case R.id.tvLineSetting:
                isDates = false;
                args = types;
                popupAdapter.resetArgs(args);
                popupWindow.showAsDropDown(tvLineSetting, -50, 10);
                break;

            case R.id.btnSearch:
                startTime = Dates.getStringByFormat(startMills, Dates.dateFormatYMDHMS);
                endTime = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
                onLoadData();
                break;
            case R.id.tvStartDate:
                Calendar calendarStart = Calendar.getInstance();
                showDateDialog(mStartTime, Arrays.asList(calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH) + 1,
                        calendarStart.get(Calendar.DAY_OF_MONTH)));
                break;
            case R.id.tvEndDate:
                Calendar calendarStop = Calendar.getInstance();
                showDateDialog(mStopTime, Arrays.asList(calendarStop.get(Calendar.YEAR), calendarStop.get(Calendar.MONTH) + 1,
                        calendarStop.get(Calendar.DAY_OF_MONTH)));
                break;
        }
    }


    /**
     * show popup window method reuturn PopupWindow
     */
    private PopupWindow createPopupWindow() {
        // initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(this);

        View view = View.inflate(this, R.layout.simple_dropdown_item_1line, null);
        // the drop down list is a list view
        ListView listViewSort = (ListView) view;
        popupAdapter = new PopupItemAdapter(this, args);
        // set our adapter and pass our pop up window contents
        listViewSort.setAdapter(popupAdapter);
        // set on item selected
        listViewSort.setOnItemClickListener((parent, view1, position, id) -> {
            if (isDates) {
                switch (position) {
                    case 0:
                        startTime = Dates.getStringByFormat(Dates.getTimeDayStart().getTime(), AbDateUtil.dateFormatYMDHMS);
                        startMills = Dates.getTimeDayStart().getTime();
                        break;
                    case 1:
                        startTime = Dates.getStringByFormat(Dates.getTimeDayStart().getTime() - 1000 * 60 * 60 * 24 * 2, AbDateUtil.dateFormatYMDHMS);
                        startMills = Dates.getTimeDayStart().getTime() - 1000 * 60 * 60 * 24 * 2;
                        break;
                    case 2:
                        startTime = Dates.getStringByFormat(Dates.getTimeDayStart().getTime() - 1000 * 60 * 60 * 24 * 6, AbDateUtil.dateFormatYMDHMS);
                        startMills = Dates.getTimeDayStart().getTime() - 1000 * 60 * 60 * 24 * 6;
                        break;
                }
                mStartTime.setText(Dates.getStringByFormat(Dates.getMillisOfStr(startTime, Dates.dateFormatYMDHMS), Dates.dateFormatYMD));
                mStopTime.setText(Dates.getStringByFormat(stopMills, Dates.dateFormatYMD));
                tvSetDate.setText(args[position]);
                onLoadData();
            } else {
                type = position;
                tvLineSetting.setText(args[position]);
                bindChartView();
            }
            popupWindow.dismiss();
        });
        // some other visual settings for popup window
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_b0_r3_white));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(150);
        popupWindow.setContentView(listViewSort);
        return popupWindow;
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

                            String stoptime = mStopTime.getText().toString().trim();
                            if (Strs.isNotEmpty(stoptime) && stopMills > startMills) {
                                startTime = Dates.getStringByFormat(startMills, Dates.dateFormatYMDHMS);
                                endTime = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
                                onLoadData();
                            } else {
                                mStartTime.setText(Dates.getCurrentDate(Dates.dateFormatYMD));
                                startMills = Dates.getTimeDayStart().getTime();
                                Toasts.show(ActTeamOverlook.this, "请选择正确的开始时间", false);
                            }

                        } else if (view == mStopTime) {
                            stopMills = Dates.getMillisOfStr(text, Dates.dateFormatYMD) + 24 * 60 * 60 * 1000 - 1000;
                            mStopTime.setText(text);

                            String starttime = mStartTime.getText().toString().trim();
                            if (Strs.isNotEmpty(starttime) && stopMills > startMills) {
                                startTime = Dates.getStringByFormat(startMills, Dates.dateFormatYMDHMS);
                                endTime = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
                                onLoadData();
                            } else {
                                mStopTime.setText(Dates.getCurrentDate(Dates.dateFormatYMD));
                                stopMills = Dates.getTimeDayStop().getTime();
                                Toasts.show(ActTeamOverlook.this, "请选择正确的截止时间", false);
                            }
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


    private class TeamDataAdapter extends BaseQuickAdapter<TeamData, BaseViewHolder> {

        TeamDataAdapter(@Nullable List<TeamData> data) {
            super(R.layout.item_team_data, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, TeamData item) {
            helper.setText(R.id.tvTeamCount, item.value);
            helper.setText(R.id.tvTeamKey, item.key);
        }
    }

    private class TeamData {
        String key;
        String value;

        TeamData(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
