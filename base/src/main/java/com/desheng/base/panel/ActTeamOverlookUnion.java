package com.desheng.base.panel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.model.TeamOverviewBean;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.DisableShiftModeHelper;
import com.pearl.view.mypicker.DatePickerDialog;
import com.pearl.view.mypicker.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import okhttp3.Request;

import static com.pearl.act.util.UIHelper.BACK_WHITE_ARROR;

/**
 * 团队总览
 * Created by ray on 2018/11/2.
 */
public class ActTeamOverlookUnion extends AbAdvanceActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener, LineChartOnValueSelectListener {

    public static void launch(Activity act) {
        simpleLaunch(act, ActTeamOverlookUnion.class);
    }

    //
    private TextView tvStartDate, tvEndDate;

    private GridView gv_data;
    private TabLayout tabLayout;
    private LinearLayout layout_choose;
    private TextView tv_value_type;
    private LineChartView chartView;

    //x轴坐标对应的数据
    private List<Float> xCoordinates = new ArrayList<>();

    private String start;
    private String end;
    private Date currDate;

    private TeamOverviewBean teamDataBean;
    private String date;
    private static final int YCOUNT = 5;
    public HashMap<String, Object> extras = new HashMap<>();

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

    private int date_index = 1;
    private List<TableValue> tableValues;
    private TableAdapter mAdapter;

    private String[] gridNames = {"团队人数", "代理人数", "玩家人数", "团队余额", "总充值", "总提现", "总投注", "总返奖", "总返点"};
    private String[] gridValues = new String[gridNames.length];
    static int[] funIds=
            {R.string.recharge,
            R.string.withdrawals,
            R.string.betting_liang,
            R.string.send_the_prirce,
            R.string.activity,
            R.string.return_dispoint,
            R.string.new_add};

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
        return R.layout.act_team_overlook_union;
    }

    /**
     * 初始化所有控件
     */
    private void initView() {
        tableValues = new ArrayList<>();
        gv_data = findViewById(R.id.gv_data);
        mAdapter = new TableAdapter();
        gv_data.setAdapter(mAdapter);

        layout_choose = findViewById(R.id.layout_choose);
        tv_value_type = findViewById(R.id.tv_value_type);
        chartView = findViewById(R.id.chartView);
        chartView.setOnValueTouchListener(this);
        layout_choose.setOnClickListener(this);

        chartView.setZoomType(ZoomType.HORIZONTAL);
        chartView.setInteractive(true);
        chartView.setZoomEnabled(true);
        chartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

        tabLayout = findViewById(R.id.tabDate);
        tabLayout.addTab(tabLayout.newTab().setText("今日"));
        tabLayout.addTab(tabLayout.newTab().setText("三日"));
        tabLayout.addTab(tabLayout.newTab().setText("七日"));
//        tabLayout.addTab(tabLayout.newTab().setText("一个月"));
        DisableShiftModeHelper.reflex(tabLayout);

        tabLayout.addOnTabSelectedListener(this);
        currDate = new Date();
        start = Dates.getStringByFormat(currDate, Dates.dateFormatYMD);
        end = Dates.getStringByFormat(currDate, Dates.dateFormatYMD);

        String currDate = Dates.getStringByFormat(System.currentTimeMillis(), Dates.dateFormatYMD);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvStartDate.setText(currDate);

        tvEndDate = findViewById(R.id.tvEndDate);
        tvEndDate.setText(currDate);

        tvStartDate.setText(currDate);

        intListener();
        getTeamData();

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        switch (tab.getPosition()) {
            case 0:
                start = end;
                date_index = 1;
                updateData();
                break;
            case 1:
                date_index = 2;
                start = Dates.getStringByOffset(currDate, Dates.dateFormatYMD, Calendar.DAY_OF_YEAR, -3);
                updateData();
                chartView.setZoomLevel(30, 50, 0.5f);
                break;
            case 2:
                date_index = 3;
                start = Dates.getStringByOffset(currDate, Dates.dateFormatYMD, Calendar.DAY_OF_YEAR, -7);
                updateData();
                chartView.setZoomLevel(30, 50, 2f);
                break;
            case 3:
                date_index = 4;
                Calendar ca = Calendar.getInstance();
                ca.setTime(currDate);
                ca.add(Calendar.MONTH, -1);
                Date lastMonth = ca.getTime();
                start = Dates.getStringByFormat(lastMonth, Dates.dateFormatYMD);
                updateData();
                break;
        }
    }

    private void updateData() {
        tvStartDate.setText(start);
        tvEndDate.setText(end);
        getTeamData();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onValueSelected(int i, int i1, PointValue pointValue) {
        Toasts.show("X:" + pointValue.getX() + "  Y:" + pointValue.getY() );
    }

    @Override
    public void onValueDeselected() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v==layout_choose){
            final ListPopupWindow popup = new ListPopupWindow(ActTeamOverlookUnion.this);
            popup.setBackgroundDrawable(getResources().getDrawable(R.mipmap.bg_popup_menu_lottery_play));
            popup.setAdapter(new PopupMenuAdapter(ActTeamOverlookUnion.this));
            popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    choose_index = position+1;
                    tv_value_type.setText(ActTeamOverlookUnion.this.getResources().getString(funIds[position]));
                    updateChartView();
                    popup.dismiss();
                }
            });

            popup.setAnchorView(tv_value_type);
            popup.setVerticalOffset(Views.dp2px(1));
            popup.setContentWidth(Views.dp2px(114.5f));
            popup.show();
        }
    }

    private void setGridData() {
        gridValues[0] = String.valueOf(teamDataBean.memberCount);
        gridValues[1] = String.valueOf(teamDataBean.agentCount);
        gridValues[2] = String.valueOf(teamDataBean.userCount);
        gridValues[3] = String.valueOf(teamDataBean.teamBalance);
        gridValues[4] = String.valueOf(teamDataBean.reports.depositAmountSum);
        gridValues[5] = String.valueOf(teamDataBean.reports.withdrawAmountSum);
        gridValues[6] = String.valueOf(teamDataBean.reports.confirmAmountSum);
        gridValues[7] = String.valueOf(teamDataBean.reports.awardAmountSum);
        gridValues[8] = String.valueOf(teamDataBean.reports.pointAmountSum);

        for (int i = 0; i < gridNames.length; i++) {
            TableValue value = new TableValue();
            value.name = gridNames[i];
            value.value = gridValues[i];
            tableValues.add(value);
        }

        mAdapter.notifyDataSetChanged();

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
    }

    /**
     * 团队总揽
     */
    public void getTeamData() {
        HttpAction.getTeamOver(start, end, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                chartView.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showProgressDialog(ActTeamOverlookUnion.this, "");
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
                    teamDataBean = getField(extra, "data", null);

                    setGridData();

                    clearData();

                    putDataInList(teamDataBean);

                    updateChartView();

                }

                return true;
            }

            @Override
            public void onAfter(int id) {
                chartView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.hideProgressDialog(ActTeamOverlookUnion.this);
                    }
                }, 400);
            }

            @Override
            public void onFinish() {
                super.onFinish();
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

    private void updateChartView() {

        if (choose_index == -1) {
//            setYCoordinates(depositAmountList);
            updatePointValues(depositAmountList);

        } else {
            switch (choose_index) {
                case CHOOSE_DEPOSIT_AMOUNT:
                    updatePointValues(depositAmountList);

                    break;
                case CHOOSE_WITHDRAW_AMOUNT:
                    updatePointValues(withdrawAmountList);

                    break;
                case CHOOSE_CONFIRM_AMOUNT:
                    updatePointValues(confirmAmountList);

                    break;
                case CHOOSE_AWARD_AMOUNT:
                    updatePointValues(awardAmountList);

                    break;
                case CHOOSE_ACTIVITY_AMOUNT:
                    updatePointValues(activityAmountList);
                    break;
                case CHOOSE_POINT_AMOUNT:
                    updatePointValues(pointAmountList);
                    break;
                case CHOOSE_NEW_USER_COUNT:
                    updatePointValues(newUserCountList);
                    break;
            }
        }
    }

    private void updatePointValues(List<Float> pointAmountList) {
        List<PointValue> pointValues = new ArrayList<PointValue>();

        for (int j = 0; j <pointAmountList.size(); ++j) {
            pointValues.add(new PointValue(xCoordinates.get(j), pointAmountList.get(j)));
        }

        setDataOnView(pointValues);
    }

    private LineChartData chartData;

    private ValueShape shape = ValueShape.CIRCLE;
    private List<Line> lines = new ArrayList<Line>();


    private void setDataOnView(List<PointValue> values) {
        lines.clear();
        Line line = new Line(values);
        line.setColor(ChartUtils.COLORS[0]);
        line.setShape(shape);
        line.setCubic(true);
        line.setFilled(false);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(true);
        line.setHasLines(true);
        line.setHasPoints(true);
//            line.setHasGradientToTransparent(hasGradientToTransparent);

        lines.add(line);

        chartData = new LineChartData(lines);

        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("Axis X");
        axisY.setName("Axis Y");
        chartData.setAxisXBottom(axisX);
        chartData.setAxisYLeft(axisY);

        chartData.setBaseValue(Float.NEGATIVE_INFINITY);
        chartView.setLineChartData(chartData);
        chartView.setZoomType(ZoomType.HORIZONTAL);


    }

    private void putDataInList(TeamOverviewBean data) {

        for (int i = 0; i < data.getReports().dayReportList.size(); i++) {
            TeamOverviewBean.ReportsBean.DayReportListBean reportListBean = data.getReports().dayReportList.get(i);
//            Random random=new Random();
//            float amount=random.nextFloat()*100f;
            depositAmountList.add(reportListBean.getDepositAmount());
            withdrawAmountList.add(reportListBean.getWithdrawAmount());
            profitAmountList.add(reportListBean.getProfitAmount());
            awardAmountList.add(reportListBean.getAwardAmount());
            pointAmountList.add(reportListBean.getPointAmount());
            activityAmountList.add(reportListBean.getActivityAmount());
            confirmAmountList.add(reportListBean.getConfirmAmount());

            TeamOverviewBean.RegisterListBean registerListBean = data.getRegisterList().get(i);
            newUserCountList.add(registerListBean.getCount());

            date = reportListBean.getDate().replaceAll("-","");
            date = date.substring(4, date.length());
            xCoordinates.add(Float.parseFloat(date));

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
        newUserCountList.clear();
    }

    class TableAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return tableValues.size();
        }

        @Override
        public Object getItem(int position) {
            return tableValues.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(ActTeamOverlookUnion.this).inflate(R.layout.item_team_over_view, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvName.setText(tableValues.get(position).name);
            holder.tvValue.setText(tableValues.get(position).value);

            return convertView;
        }
    }

    class ViewHolder {
        TextView tvName;
        TextView tvValue;

        public ViewHolder(View view) {
            tvName = view.findViewById(R.id.tvName);
            tvValue = view.findViewById(R.id.tvValue);
        }
    }

    class TableValue {
        public String name;
        public String value;
    }


    public static class PopupMenuAdapter extends BaseAdapter {

        private Context context;
        public PopupMenuAdapter(@android.support.annotation.NonNull Context context) {
            this.context=context;
        }

        @Override
        public int getCount() {
            return funIds.length;
        }

        @Override
        public Object getItem(int position) {
            return funIds[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @android.support.annotation.NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @android.support.annotation.NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popup_menu, parent, false);
            }

            TextView textView = convertView.findViewById(android.R.id.text1);
            textView.setText(context.getResources().getString(funIds[position]));

            if (position + 1 == getCount())
                convertView.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
            else
                convertView.findViewById(R.id.divider).setVisibility(View.VISIBLE);

            return convertView;
        }
    }


}
