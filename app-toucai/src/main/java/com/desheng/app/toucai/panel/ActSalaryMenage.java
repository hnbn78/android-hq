package com.desheng.app.toucai.panel;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.desheng.app.toucai.adapter.ReportAdapter;
import com.desheng.app.toucai.model.SalaryBean;
import com.desheng.app.toucai.model.SalaryData;
import com.desheng.app.toucai.view.SimpleDividerDecoration;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.Params;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.mypicker.DatePickerDialog;
import com.pearl.view.mypicker.DateUtil;
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
public class ActSalaryMenage extends AbAdvanceActivity implements View.OnClickListener {

    private boolean hasHourItem = false;
    private RelativeLayout emptyLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.act_salary_menage;
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, ActSalaryMenage.class));
    }

    private static final int PAGE_SIZE = 20;
    private TextView tvStartDate, tvEndDate, tvSalary;
    private EditText etUserName;
    private SwipeRefreshLayout refreshLayout;
    private long startMills = 0;
    private long stopMills = 0;
    private int total = 0, currPage = 1;
    private List<List<Params>> list = new ArrayList<>();
    private ReportAdapter reportAdapter;
    private List<Params> salarySumBean;//汇总的bean

    @Override
    protected void init() {
        setStatusBarTranslucentAndLightContentWithPadding();
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "工资管理");
        emptyLayout = findViewById(R.id.emptyLayout);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvStartDate.setOnClickListener(this);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvEndDate.setOnClickListener(this);
        etUserName = findViewById(R.id.etUserName);
        tvSalary = findViewById(R.id.tvSalary);
        RecyclerView recyclerView = findViewById(R.id.lottery_report_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SimpleDividerDecoration(this));
        reportAdapter = new ReportAdapter(list);
        recyclerView.setAdapter(reportAdapter);
        refreshLayout = findViewById(R.id.swipeRefreshLayout);
        //hasHourItem = BaseConfig.FLAG_QIFEI.equals(Config.custom_flag);
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


        onGetSelfSalary(startMills, stopMills);
        onSearch();
        refreshLayout.setOnRefreshListener(this::onSearch);
        reportAdapter.setEnableLoadMore(true);
        reportAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                updateTable();
            }
        }, recyclerView);
        findViewById(R.id.btnSearch).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                onSearch();
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


    private void onSearch() {
        currPage = 1;
        updateTable();
    }

    public void updateTable() {
        String userName = Views.getText(etUserName);
        if (Strs.isEmpty(userName)) {
            userName = null;
        }
        search(userName, startMills, stopMills, currPage, PAGE_SIZE);
    }

    private void onGetSelfSalary(long sTime, long eTime) {
        String startTime = Dates.getStringByFormat(sTime, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(eTime, Dates.dateFormatYMDHMS);
        HttpAction.getSelfSalaryData(ActSalaryMenage.this, null, startTime, stopTime, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<SalaryBean>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                ArrayList<SalaryBean> respSearchOrder = getField(extra, "data", null);
                if (respSearchOrder != null && respSearchOrder.size() > 0) {

                    SalaryBean bean = respSearchOrder.get(0);
                    double point = bean.getPoint() * 100;
                    String pointStr = "我的日工资比例: " + Nums.formatDecimal(point, 2) + "%";
                    tvSalary.setText(pointStr);
                }

                return true;
            }

        });
    }

    private void search(String username, long sTime, long eTime,
                        final int page, int size) {
        String startTime = Dates.getStringByFormat(sTime, Dates.dateFormatYMD);
        String stopTime = Dates.getStringByFormat(eTime, Dates.dateFormatYMD);

        // 如果用户名为当前用户，则不传参数（否则返回空）
        if (username != null && username.equals(UserManager.getIns().getMainUserName()))
            username = null;

        HttpAction.getSalaryData(ActSalaryMenage.this, username, startTime, stopTime, page, size, "0", "RMB", new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                if (page == 1) {
                    refreshLayout.setRefreshing(true);
                } else {
                    Dialogs.showProgressDialog(ActSalaryMenage.this, "搜索中...");
                }
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", SalaryData.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                SalaryData respSearchOrder = getField(extra, "data", null);
                if (respSearchOrder != null && respSearchOrder.list != null) {
                    if (page == 1) {
                        list.clear();
                        total = 0;
                    }
                    total = respSearchOrder.totalCount + 1;
                    for (int i = 0; i < respSearchOrder.list.size(); i++) {
                        SalaryBean bean = respSearchOrder.list.get(i);
                        if (i == respSearchOrder.list.size() - 1) {
                            if (salarySumBean != null) {
                                list.remove(salarySumBean);
                            }
                            salarySumBean = new ArrayList<>();
                            salarySumBean.add(new Params("汇总", ""));
                            String amount = "0";
                            if (bean.getTeamAmountSum() > 0) {
                                amount = Nums.formatDecimal(bean.getTeamAmountSum(), 2);
                            }
                            salarySumBean.add(new Params("团队流水", amount));
                            String bonusSum = "0";
                            if (bean.getBonusSum() > 0) {
                                bonusSum = Nums.formatDecimal(bean.getBonusSum(), 2);
                            }
                            salarySumBean.add(new Params("日工资发放", bonusSum));
                            salarySumBean.add(new Params("", ""));
                            list.add(list.size(), salarySumBean);
                        } else {
                            List<Params> params = new ArrayList<>();
                            params.add(new Params("时间", bean.getDay()));
                            params.add(new Params("用户名", bean.getUserName()));
                            double point = bean.getPoint() * 100;
                            String pointStr = Nums.formatDecimal(point, 2) + "%";
                            params.add(new Params("日工资比例", pointStr));
                            String amount = "0";
                            if (bean.getTeamAmount() > 0) {
                                amount = Nums.formatDecimal(bean.getTeamAmount(), 2);
                            }
                            params.add(new Params("团队流水", amount));
                            String bonusSum = "0";
                            if (bean.getBonus() > 0) {
                                bonusSum = Nums.formatDecimal(bean.getBonus(), 2);
                            }
                            params.add(new Params("日工资发放金额", bonusSum));
                            params.add(new Params("状态", bean.getStatusStr()));
                            list.add(params);
                        }
                    }

                    if (page == 1 && list.size() == 0) {
                        emptyLayout.setVisibility(View.VISIBLE);
                    } else {
                        emptyLayout.setVisibility(View.GONE);
                    }

                    boolean isLoadMore = list.size() < total;
                    reportAdapter.setEnableLoadMore(isLoadMore);
                    if (isLoadMore)
                        currPage++;
                    reportAdapter.loadMoreComplete();
                    reportAdapter.notifyDataSetChanged();
                } else {
                    emptyLayout.setVisibility(View.VISIBLE);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                reportAdapter.loadMoreFail();
                return super.onError(status, content);
            }

            @Override
            public void onAfter(int id) {
                if (page > 0) {
                    Dialogs.hideProgressDialog(ActSalaryMenage.this);
                }
                refreshLayout.setRefreshing(false);
            }
        });
    }


}
