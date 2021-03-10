package com.desheng.base.panel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.AbDateUtil;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.adapter.PopupItemAdapter;
import com.desheng.base.model.TeamOverviewBean;
import com.desheng.base.view.ChartView;
import com.desheng.base.view.CustomMarkView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.noober.background.view.BLButton;
import com.noober.background.view.BLRelativeLayout;
import com.noober.background.view.BLTextView;
import com.orhanobut.logger.Logger;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.download.utils.Constants;
import com.pearl.act.util.UIHelper;
import com.pearl.view.mypicker.DatePickerDialog;
import com.pearl.view.mypicker.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

import static com.pearl.act.util.UIHelper.BACK_WHITE_ARROR;

/**
 * 团队总览
 * Created by user on 2018/3/19.
 */
public class ActTeamOverlook extends AbAdvanceActivity implements View.OnClickListener {

    private PopupItemAdapter popupAdapter;
    private PopupWindow mPopupWindow;
    private LineChart lineChart;
    private CustomMarkView markView;

    public static void launch(Activity act) {
        simpleLaunch(act, ActTeamOverlook.class);
    }

    String[] types = {"充值", "提现", "投注量", "派奖", "活动", "返点", "新增用户"};
    String[] dates = {"今天", "最近三天", "最近七天"};
    //    private RadioGroup rgDate;
//    private RadioButton rbToday;
//    private RadioButton rbDay7;
//    private RadioButton rbDay30;
    private LinearLayout relativeSecond;
    private TextView rechargeText;
    private TextView withdrawalText;
    private TextView consumptionText;
    private TextView sendPriceText;
    private TextView returnText;
    private LinearLayout priceLayout;
    private TextView tvDeposite;
    private TextView tvWithdraw;
    private TextView tvCost;
    private TextView tvDispatch;
    private TextView tvReturn;
    private BLRelativeLayout teamMember;
    private TextView tvMemInfo;
    private TextView tvMemOnline;
    private TextView tvMemLeft;
    private HorizontalScrollView hsvFuncs;
    private RadioGroup rgFuncs;
    private RadioButton rbRecharge;
    private RadioButton rbWithdrawals;
    private RadioButton rbBetting;
    private RadioButton rbReturn;
    private RadioButton rbActivity;
    private RadioButton rbReturnPoint;
    private RadioButton rbNewUser;
    private ChartView vChart;

    private BLTextView tvStartDate, tvEndDate, selectFastBL, selectMethodBL;
    private BLButton btnSearch;

    //x轴坐标对应的数据
    private List<String> xCoordinates = new ArrayList<>();
    //y轴坐标对应的数据
    private List<Float> yCoordinates = new ArrayList<>();
    //折线对应的数据
    private Map<String, Float> value = new HashMap<>();

    private String start;
    private String end;
    private Date currDate;

    private TeamOverviewBean data;
    private String date;
    private static final int YCOUNT = 5;
    public HashMap<String, Object> extras = new HashMap<>();

    private String[] args = types;
    private boolean isDates;
    private String startTime, endTime;

    /**
     * 数据封装
     */
    private List<Float> depositAmountList = new ArrayList<>();//充值
    private List<Float> withdrawAmountList = new ArrayList<>();//取款
    private List<Float> profitAmountList = new ArrayList<>();//投注量
    private List<Float> awardAmountList = new ArrayList<>();//返派奖
    private List<Float> pointAmountList = new ArrayList<>();//返点
    private List<Float> activityAmountList = new ArrayList<>();//活动
    private List<Float> newUserCountList = new ArrayList<>();//新增用户数
    private List<Float> feeAmountList = new ArrayList<>();
    private List<Float> orderBonusAmountList = new ArrayList<>();
    private List<Float> confirmAmountList = new ArrayList<>();

    private int choose_index = -1;
    private static final int CHOOSE_DEPOSIT_AMOUNT = 1;
    private static final int CHOOSE_WITHDRAW_AMOUNT = 2;
    private static final int CHOOSE_CONFIRM_AMOUNT = 3;
    private static final int CHOOSE_AWARD_AMOUNT = 4;
    private static final int CHOOSE_POINT_AMOUNT = 5;
    private static final int CHOOSE_ACTIVITY_AMOUNT = 6;
    private static final int CHOOSE_NEW_USER_COUNT = 7;

    private int type = CHOOSE_DEPOSIT_AMOUNT;

    private int date_index = 1;

    @Override
    protected void init() {
        setToolbarLeftBtn(BACK_WHITE_ARROR);
        if (UIHelper.toolbarBgResId > 0)
            setToolbarBgImage(UIHelper.toolbarBgResId);
        else
            setToolbarBgColor(R.color.colorPrimary);
        setToolbarTitleCenter("团队总览");
        setToolbarTitleCenterColor(R.color.white);
        setStatusBarTranslucentAndLightContentWithPadding();
        initView();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_team_overlook;
    }

    /**
     * 初始化所有控件
     */
    private void initView() {
//        rgDate = (RadioGroup) findViewById(R.id.rgDate);
//        rbToday = (RadioButton) findViewById(R.id.rbToday);
//        rbDay7 = (RadioButton) findViewById(R.id.rbDay7);
//        rbDay30 = (RadioButton) findViewById(R.id.rbDay30);
        btnSearch = (BLButton) findViewById(R.id.btnSearch);
        currDate = new Date();
        start = Dates.getStringByFormat(currDate, Dates.dateFormatYMD);
        end = Dates.getStringByFormat(currDate, Dates.dateFormatYMD);
        //relativeSecond = (LinearLayout) findViewById(R.id.relative_second);
        rechargeText = (TextView) findViewById(R.id.recharge_text);
        withdrawalText = (TextView) findViewById(R.id.withdrawal_text);
        consumptionText = (TextView) findViewById(R.id.consumption_text);
        sendPriceText = (TextView) findViewById(R.id.send_price_text);
        returnText = (TextView) findViewById(R.id.return_text);
        //priceLayout = (LinearLayout) findViewById(R.id.price_layout);
        tvDeposite = (TextView) findViewById(R.id.tvDeposite);
        tvWithdraw = (TextView) findViewById(R.id.tvWithdraw);
        tvCost = (TextView) findViewById(R.id.tvCost);
        tvDispatch = (TextView) findViewById(R.id.tvDispatch);
        tvReturn = (TextView) findViewById(R.id.tvReturn);
        teamMember = (BLRelativeLayout) findViewById(R.id.team_member);
        tvMemInfo = (TextView) findViewById(R.id.tvMemInfo);
        tvMemOnline = (TextView) findViewById(R.id.tvMemOnline);
        tvMemLeft = (TextView) findViewById(R.id.tvMemLeft);
        hsvFuncs = (HorizontalScrollView) findViewById(R.id.hsvFuncs);
        rgFuncs = (RadioGroup) findViewById(R.id.rgFuncs);
        rbRecharge = (RadioButton) findViewById(R.id.rbRecharge);
        rbWithdrawals = (RadioButton) findViewById(R.id.rbWithdrawals);
        rbBetting = (RadioButton) findViewById(R.id.rbBetting);
        rbReturn = (RadioButton) findViewById(R.id.rbReturn);
        rbActivity = (RadioButton) findViewById(R.id.rbActivity);
        rbReturnPoint = (RadioButton) findViewById(R.id.rbReturnPoint);
        rbNewUser = (RadioButton) findViewById(R.id.rbNewUser);

        selectFastBL = ((BLTextView) findViewById(R.id.selectFast));
        selectMethodBL = ((BLTextView) findViewById(R.id.selectMethod));


        lineChart = findViewById(R.id.lineChart);
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
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
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
            }
        });
        lineChart.getAxisLeft().setDrawGridLines(true);
        markView = new CustomMarkView(this, R.layout.mark_view);
        markView.setChartView(lineChart);
        lineChart.setMarker(markView);

        String currDate = Dates.getStringByFormat(System.currentTimeMillis(), Dates.dateFormatYMD);
        tvStartDate = (BLTextView) findViewById(R.id.tvStartDate);
        tvStartDate.setText(currDate);

        tvEndDate = (BLTextView) findViewById(R.id.tvEndDate);
        tvEndDate.setText(currDate);

        tvStartDate.setText(currDate);
        mPopupWindow = createPopupWindow();

        //目前要先用假数据填充一下表格，之后网络请求的数据才能正常显示
        initTempData();

        intListener();
        getTeamData();

    }

    private void initTempData() {
        HashMap<String, Object> teamOverviewHashMap = parseFields(Constants.month_three);
        data = (TeamOverviewBean) teamOverviewHashMap.get("data");
        setChartValueData(data);
        setDataOnView(data);
    }

    private void intListener() {
        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                showDateDialog(tvEndDate, Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
            }
        });
        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                showDateDialog(tvStartDate, Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Dates.getMillisOfStr(end, Dates.dateFormatYMD) - Dates.getMillisOfStr(start, Dates.dateFormatYMD) < 0) {
                    Toasts.show(ActTeamOverlook.this, "结束日期不能小于起始日期", false);
                    return;
                }

                getTeamData();
            }
        });

        rgFuncs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


            }
        });


        selectFastBL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDates = true;
                args = dates;
                popupAdapter.resetArgs(args);
                mPopupWindow.showAsDropDown(selectFastBL, -50, 10);
            }
        });

        selectMethodBL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDates = false;
                args = types;
                popupAdapter.resetArgs(args);
                mPopupWindow.showAsDropDown(selectMethodBL, -50, 10);
            }
        });
    }

    /**
     * 团队总揽
     */
    public void getTeamData() {
        HttpAction.getTeamOver(start, end, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                vChart.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActTeamOverlook.this, "");
                    }
                });
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", TeamOverviewBean.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (getField(extra, "data", null) != null) {
                    data = getField(extra, "data", null);
                    setChartValueData(data);
                    setDataOnView(data);
                }

                return true;
            }

            @Override
            public void onAfter(int id) {
                vChart.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideProgressDialog(ActTeamOverlook.this);
                    }
                }, 400);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public HashMap<String, Object> parseFields(String jsonStr) {
        HashMap<String, Object> extras = new HashMap<>();
        JsonElement jsonResp = new JsonParser().parse(jsonStr);
        if (jsonResp.isJsonObject()) {
            JsonObject obj = jsonResp.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                if ("code".equals(entry.getKey())) {
//                    code = obj.get("code").getAsInt();
                } else if ("msg".equals(entry.getKey()) || "message".equals(entry.getKey())) {
                    if (obj.get("msg") != null) {
//                        msg = obj.get("msg").getAsString();
                    } else if (obj.get("message") != null) {
//                        msg = obj.get("message").getAsString();
                    }
                } else if ("error".equals(entry.getKey())) {
//                    error = obj.get("error").getAsInt();
                } else {
//                    if (types.containsKey(entry.getKey())) {
                    Object valueObj = new Gson().fromJson(entry.getValue(), TeamOverviewBean.class);
                    extras.put(entry.getKey(), valueObj);
//                    }
                }
            }
        }

        return extras;
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
                            tvStartDate.setText(text);
                            start = text;
                        } else if (view == tvEndDate) {
                            tvEndDate.setText(text);
                            end = text;
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

    private void setDataOnView(TeamOverviewBean data) {
        tvDeposite.setText(Strs.of(data.reports.depositAmountSum));
        tvWithdraw.setText(Strs.of(data.reports.withdrawAmountSum));
        tvCost.setText(Strs.of(data.reports.confirmAmountSum));
        tvDispatch.setText(Strs.of(data.reports.awardAmountSum));
        tvReturn.setText(Strs.of(data.reports.pointAmountSum));
        //(代理%d人，玩家%d人)
        tvMemInfo.setText(String.format("%d人", data.userCount));

        tvMemOnline.setText(String.format("%d人", data.userOnlineCount));

        tvMemLeft.setText(Nums.formatDecimal(data.teamBalance,4));
    }

    private void setChartValueData(TeamOverviewBean data) {
        clearData();
        putDataIn(data);
        setYData();
        vChart.setValue(value, xCoordinates, yCoordinates);

    }

    private void setYData() {
        if (choose_index == -1) {
            setYCoordinates(depositAmountList);
        } else {
            switch (choose_index) {
                case CHOOSE_DEPOSIT_AMOUNT:
                    setYCoordinates(depositAmountList);
                    rgFuncs.check(R.id.rbRecharge);
                    break;
                case CHOOSE_WITHDRAW_AMOUNT:
                    setYCoordinates(withdrawAmountList);
                    rgFuncs.check(R.id.rbWithdrawals);
                    break;
                case CHOOSE_CONFIRM_AMOUNT:
                    setYCoordinates(confirmAmountList);
                    rgFuncs.check(R.id.rbBetting);
                    break;
                case CHOOSE_AWARD_AMOUNT:
                    setYCoordinates(awardAmountList);
                    rgFuncs.check(R.id.rbReturn);
                    break;
                case CHOOSE_ACTIVITY_AMOUNT:
                    setYCoordinates(activityAmountList);
                    rgFuncs.check(R.id.rbActivity);
                    break;
                case CHOOSE_POINT_AMOUNT:
                    setYCoordinates(pointAmountList);
                    rgFuncs.check(R.id.rbReturnPoint);
                    break;
                case CHOOSE_NEW_USER_COUNT:
                    setYCoordinates(newUserCountList);
                    rgFuncs.check(R.id.rbNewUser);
                    break;
            }
        }
    }

    private void putDataIn(TeamOverviewBean data) {

        for (int i = 0; i < data.getReports().dayReportList.size(); i++) {
            TeamOverviewBean.ReportsBean.DayReportListBean reportListBean = data.getReports().dayReportList.get(i);
            depositAmountList.add((reportListBean.getDepositAmount()));
            withdrawAmountList.add((reportListBean.getWithdrawAmount()));
            profitAmountList.add((reportListBean.getProfitAmount()));
            awardAmountList.add((reportListBean.getAwardAmount()));
            pointAmountList.add((reportListBean.getPointAmount()));
            activityAmountList.add((reportListBean.getActivityAmount()));
            confirmAmountList.add(reportListBean.getConfirmAmount());

            TeamOverviewBean.RegisterListBean registerListBean = data.getRegisterList().get(i);
            newUserCountList.add(registerListBean.getCount());

            date = reportListBean.getDate();
            date = date.substring(5, date.length());
            xCoordinates.add(date);

            if (choose_index == -1) {
                value.put(date, reportListBean.getDepositAmount());
            } else {
                switch (choose_index) {
                    case CHOOSE_DEPOSIT_AMOUNT:
                        value.put(date, reportListBean.getDepositAmount());
                        break;
                    case CHOOSE_WITHDRAW_AMOUNT:
                        value.put(date, reportListBean.getWithdrawAmount());
                        break;
                    case CHOOSE_CONFIRM_AMOUNT:
                        value.put(date, reportListBean.getConfirmAmount());
                        break;
                    case CHOOSE_AWARD_AMOUNT:
                        value.put(date, reportListBean.getAwardAmount());
                        break;
                    case CHOOSE_ACTIVITY_AMOUNT:
                        value.put(date, reportListBean.getActivityAmount());
                        break;
                    case CHOOSE_POINT_AMOUNT:
                        value.put(date, reportListBean.getPointAmount());
                        break;
                    case CHOOSE_NEW_USER_COUNT:
                        value.put(date, registerListBean.getCount());
                        break;
                }
            }
        }
    }

    private void clearData() {
        depositAmountList.clear();
        withdrawAmountList.clear();
        profitAmountList.clear();
        awardAmountList.clear();
        pointAmountList.clear();
        activityAmountList.clear();
        confirmAmountList.clear();
        xCoordinates.clear();
        value.clear();
        yCoordinates.clear();
        newUserCountList.clear();
    }

    private void setYCoordinates(List<Float> list) {
        yCoordinates.clear();
        float max = Collections.max(list);

        if (max == 0) {
            max = 1;

            if (choose_index == CHOOSE_NEW_USER_COUNT) {
                max = 5;
            }
        }

        float unite = max / YCOUNT;

        if (choose_index == CHOOSE_NEW_USER_COUNT) {

            int int_unit = (int) Math.ceil(unite);

            for (int i = 0; i <= YCOUNT; i++) {

                yCoordinates.add((float) (i * int_unit));
            }

        } else {
            for (int i = 0; i <= YCOUNT; i++) {
                float b = (float) (Math.round(unite * 100)) / 100;
                yCoordinates.add(i * b);
            }
        }
    }

    private PopupWindow createPopupWindow() {
        // initialize a pop up window type
        final PopupWindow popupWindow = new PopupWindow(this);

        View view = View.inflate(this, R.layout.simple_dropdown_item_1line, null);
        // the drop down list is a list view
        ListView listViewSort = (ListView) view;
        popupAdapter = new PopupItemAdapter(this, args);
        // set our adapter and pass our pop up window contents
        listViewSort.setAdapter(popupAdapter);
        // set on item selected
        listViewSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isDates) {
                    switch (position) {
                        case 0:
                            start = AbDateUtil.
                                    getStringByFormat(System.currentTimeMillis() - 1000 * 60 * 60 * 24,
                                            AbDateUtil.dateFormatYMDHMS);
                            break;
                        case 1:
                            start = AbDateUtil.
                                    getStringByFormat(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 3,
                                            AbDateUtil.dateFormatYMDHMS);
                            break;
                        case 2:
                            start = AbDateUtil.
                                    getStringByFormat(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 6,
                                            AbDateUtil.dateFormatYMDHMS);
                            break;
                    }
                    selectFastBL.setText(args[position]);
                    getTeamData();
                } else {
                    type = position;
                    selectMethodBL.setText(args[position]);
                    chartMethodChange(position);
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_b0_r3_white));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(150);
        popupWindow.setContentView(listViewSort);
        return popupWindow;
    }

    private void chartMethodChange(int position) {
        switch (position) {
            case 0:
                choose_index = CHOOSE_DEPOSIT_AMOUNT;
                value.clear();
                for (int i = 0; i < data.getReports().dayReportList.size(); i++) {
                    value.put(xCoordinates.get(i), data.getReports().dayReportList.get(i).getDepositAmount());
                }

                setYCoordinates(depositAmountList);
                vChart.setValue(value, xCoordinates, yCoordinates);
                break;
            case 1:
                choose_index = CHOOSE_WITHDRAW_AMOUNT;

                for (int i = 0; i < data.getReports().dayReportList.size(); i++) {
                    value.put(xCoordinates.get(i), data.getReports().dayReportList.get(i).getWithdrawAmount());
                }

                setYCoordinates(withdrawAmountList);

                vChart.setValue(value, xCoordinates, yCoordinates);
                break;
            case 2:
                choose_index = CHOOSE_CONFIRM_AMOUNT;

                for (int i = 0; i < data.getReports().dayReportList.size(); i++) {
                    value.put(xCoordinates.get(i), (data.getReports().dayReportList.get(i).getConfirmAmount()));
                }

                setYCoordinates(confirmAmountList);

                vChart.setValue(value, xCoordinates, yCoordinates);
                break;
            case 3:
                choose_index = CHOOSE_AWARD_AMOUNT;

                for (int i = 0; i < data.getReports().dayReportList.size(); i++) {
                    value.put(xCoordinates.get(i), data.getReports().dayReportList.get(i).getAwardAmount());
                }

                setYCoordinates(awardAmountList);
                vChart.setValue(value, xCoordinates, yCoordinates);
                break;
            case 4:
                choose_index = CHOOSE_ACTIVITY_AMOUNT;

                for (int i = 0; i < data.getReports().dayReportList.size(); i++) {
                    value.put(xCoordinates.get(i), data.getReports().dayReportList.get(i).getActivityAmount());
                }

                setYCoordinates(activityAmountList);

                vChart.setValue(value, xCoordinates, yCoordinates);
                break;
            case 5:
                choose_index = CHOOSE_POINT_AMOUNT;
                for (int i = 0; i < data.getReports().dayReportList.size(); i++) {
                    value.put(xCoordinates.get(i), data.getReports().dayReportList.get(i).getPointAmount());
                }

                setYCoordinates(pointAmountList);

                vChart.setValue(value, xCoordinates, yCoordinates);
                break;
            case 6:
                choose_index = CHOOSE_NEW_USER_COUNT;

                for (int i = 0; i < data.getRegisterList().size(); i++) {
                    value.put(xCoordinates.get(i), data.getRegisterList().get(i).getCount());
                }

                setYCoordinates(newUserCountList);

                vChart.setValue(value, xCoordinates, yCoordinates);
                break;
        }
    }

}
