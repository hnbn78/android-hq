package com.desheng.base.panel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.support.design.internal.NavigationMenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.global.Config;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.adapter.NameChainAdapter;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.TeamReport;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.mypicker.DatePickerDialog;
import com.pearl.view.mypicker.DateUtil;
import com.pearl.view.rmondjone.locktableview.LockTableView;
import com.pearl.view.rmondjone.locktableview.ProgressStyle;
import com.pearl.view.rmondjone.locktableview.XRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * 团代报表界面
 */

public class ActTeamReports extends AbAdvanceActivity implements NameChainAdapter.OnItemClickListener {
    public static void launch(Activity ctx) {
        simpleLaunch(ctx, ActTeamReports.class);
    }

    private static final int PAGE_SIZE = 20;
    private TextView tvStartDate, tvEndDate;
    private Button btnSearch;

    private LinearLayout mContentView;
    private RecyclerView recycleView_Chain;
    private NameChainAdapter mAdapter;
    private List<String> subnamelist;
    private List<String> temp_list;
    private String subName;

    private long startMills = 0;
    private long stopMills = 0;
    private int currPage = -1;
    private EditText etUserName;
    private List<TeamReport> listOrders = new ArrayList<>();
    private ArrayList<ArrayList<String>> mTableDatas = new ArrayList<>();
    private LockTableView mLockTableView;
    private int totalCount;
    private XRecyclerView mXRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.act_team_report;
    }

    @Override
    protected void init() {
        subnamelist = new ArrayList<>();
        temp_list = new ArrayList<>();
        setToolbarLeftBtn(UIHelper.BACK_WHITE_ARROR);
        if (UIHelper.toolbarBgResId > 0)
            setToolbarBgImage(UIHelper.toolbarBgResId);
        else
            setToolbarBgColor(R.color.colorPrimary);
        setToolbarTitleCenter("团队报表");
        setToolbarTitleCenterColor(R.color.white);
        //setToolbarButtonRightText("导出");
        //setToolbarButtonRightTextColor(R.color.white);

        setStatusBarTranslucentAndLightContentWithPadding();
        //username= UserManagerCaiHong.getIns().getAccount();
        initThisView();
        initLockTable();
        updateTable();
    }

    public void updateTable() {
        String userName = Views.getText(etUserName);
        if (Strs.isEmpty(userName)) {
            userName = null;
        }
        currPage++;
        search(userName, startMills, stopMills, currPage, PAGE_SIZE);
    }


    private void initThisView() {

        temp_list.add(0, UserManager.getIns().getMainUserName());
        subnamelist.add(0, UserManager.getIns().getMainUserName());

        recycleView_Chain = ((RecyclerView) findViewById(R.id.rc_chain));
        recycleView_Chain.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycleView_Chain.setLayoutManager(linearLayoutManager);
        mAdapter = new NameChainAdapter(this, subnamelist, this);
        recycleView_Chain.setAdapter(mAdapter);

        mContentView = (LinearLayout) findViewById(R.id.contentView);
        String currDate = Dates.getStringByFormat(System.currentTimeMillis(), Dates.dateFormatYMD);
        startMills = Dates.getMillisOfStr(currDate, Dates.dateFormatYMD);
        tvStartDate = (TextView) findViewById(R.id.tvStartDate);
        tvStartDate.setText(currDate);
        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                showDateDialog(tvStartDate, Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
            }
        });
        tvEndDate = (TextView) findViewById(R.id.tvEndDate);
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
        etUserName = (EditText) findViewById(R.id.etUserName);
        etUserName.setText(UserManager.getIns().getMainUserName());
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currPage = -1;
                listOrders.clear();
                mTableDatas.clear();
                mLockTableView.setTableDatas(mTableDatas);
                updateTable();

            }
        });
    }

    @Override
    public void onItemClick(int position) {
        subName = subnamelist.get(position);
        subName = subName.contains(">") ? subName.replace(">", "") : subName;

        for (int i = position + 1; i < subnamelist.size(); i++) {
            temp_list.remove(subnamelist.get(i));
        }

        subnamelist.clear();
        subnamelist.addAll(temp_list);

        if (subnamelist.size() == 1) {
            subName = "";
        }

        etUserName.setText(subName);

        mAdapter.notifyDataSetChanged();
        currPage = 0;

        search(subName, startMills, stopMills, currPage, PAGE_SIZE);
    }

    public void search(String username, long sTime, long eTime,
                       final int page, int size) {
        String startTime = Dates.getStringByFormat(sTime, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(eTime, Dates.dateFormatYMDHMS);

        // 如果用户名为当前用户，则不传参数（否则返回空）
        if (username != null && username.equals(com.desheng.base.manager.UserManager.getIns().getMainUserName()))
            username = null;

        HttpAction.getTeamReport(ActTeamReports.this, username, startTime, stopTime, page, size, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                if (page == 0) {
                    Dialogs.showProgressDialog(ActTeamReports.this, "搜索中...");
                }

            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespTeamReport.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                HttpAction.RespTeamReport respSearchOrder = getField(extra, "data", null);
                if (respSearchOrder != null) {
                    mTableDatas.clear();
                    addTableHeader();
                    if (page == 0) {
                        listOrders.clear();
                    }
                    totalCount = respSearchOrder.totalCount;
                    listOrders.addAll(respSearchOrder.list);

                    for (int i = 0; i < listOrders.size(); i++) {
                        ArrayList<String> mRowDatas = new ArrayList<String>();
                        TeamReport orderInfo = listOrders.get(i);
                        mRowDatas.add(Strs.isEmpty(orderInfo.userName) ? "汇总" : orderInfo.userName);
                        mRowDatas.add(Strs.isEmpty(orderInfo.userName) ? "" : CtxLottery.formatUserType(orderInfo.userType));
                        mRowDatas.add(orderInfo.balance);
                        mRowDatas.add(Nums.formatDecimal(orderInfo.depositAmount, 3));
                        mRowDatas.add(Nums.formatDecimal(orderInfo.withdrawAmount, 3));
                        if (UserManager.getIns().showTXBettAmount()) {
                            mRowDatas.add(Nums.formatDecimal(orderInfo.txsscConfirmAmount, 3));
                        }
                        mRowDatas.add(Nums.formatDecimal(orderInfo.confirmAmount, 3));
                        mRowDatas.add(Nums.formatDecimal(orderInfo.awardAmount, 3));
                        if (UserManager.getIns().showDanDanSalary())
                            mRowDatas.add(Nums.formatDecimal(orderInfo.orderBonusAmount, 3));
                        mRowDatas.add(Nums.formatDecimal(orderInfo.pointAmount, 3));
                        mRowDatas.add(Nums.formatDecimal(orderInfo.activityAmount, 3));
                        mRowDatas.add(Nums.formatDecimal(-orderInfo.profitAmount, 3));
                        mTableDatas.add(mRowDatas);
                    }
                    mLockTableView.setTableDatas(mTableDatas);
                    if (listOrders.size() >= totalCount) {
                        mLockTableView.getTableScrollView().setLoadingMoreEnabled(false);
//                        Toasts.show(ActTeamReports.this, "已经全部加载完毕!");
                    } else {
                        mLockTableView.getTableScrollView().setLoadingMoreEnabled(true);
                    }
                } else {
                    Toasts.show(ActTeamReports.this, msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                if (page == 0) {
                    Dialogs.hideProgressDialog(ActTeamReports.this);
                }
                if (ActTeamReports.this.mXRecyclerView != null) {
                    ActTeamReports.this.mXRecyclerView.refreshComplete();
                    ActTeamReports.this.mXRecyclerView.loadMoreComplete();
                }
            }
        });
    }

    public void addTableHeader() {
        ArrayList<String> mfristData = new ArrayList<String>();

        //标题
        mfristData.add("账号");
        mfristData.add("类型");
        mfristData.add("余额");
        mfristData.add("充值");
        mfristData.add("提现");
        if (UserManager.getIns().showTXBettAmount()) {
            mfristData.add("腾讯投注额");
        }

        mfristData.add("投注额");
        mfristData.add("奖金");
        if (UserManager.getIns().showDanDanSalary()) {
            mfristData.add("单单日结");
        }

        mfristData.add("返点");
        mfristData.add("活动");
        mfristData.add("总盈亏");

        mTableDatas.add(mfristData);
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

    private void initLockTable() {
        addTableHeader();
        mLockTableView = new LockTableView(this, mContentView, mTableDatas);
        Log.e("表格加载开始", "当前线程：" + Thread.currentThread());
        mLockTableView.setLockFristColumn(true) //是否锁定第一列
                .setLockFristRow(true) //是否锁定第一行
                .setMaxColumnWidth(90) //列最大宽度
                .setMinColumnWidth(90) //列最小宽度
                .setMinRowHeight(40)//行最小高度
                .setMaxRowHeight(40)//行最大高度
                .setTextViewSize(12) //单元格字体大小
                .setFristRowBackGroudColor(R.color.colorPrimaryInverse)//表头背景色
                .setFirstRowTextColor(R.color.blue)
                .setTableHeadTextColor(R.color.black)//表头字体颜色
                .setTableContentTextColor(R.color.black)//单元格字体颜色
                .setNullableString("") //空值替换值
                .setTableViewListener(new LockTableView.OnTableViewListener() {
                    @Override
                    public void onTableViewScrollChange(int x, int y) {
//                        Log.e("滚动值","["+x+"]"+"["+y+"]");
                    }
                })//设置横向滚动回调监听
                .setTableViewRangeListener(new LockTableView.OnTableViewRangeListener() {
                    @Override
                    public void onLeft(HorizontalScrollView view) {
//                        Log.e("滚动边界","滚动到最左边");
                    }

                    @Override
                    public void onRight(HorizontalScrollView view) {
//                        Log.e("滚动边界","滚动到最右边");
                    }
                })//设置横向滚动边界监听
                .setOnLoadingListener(new LockTableView.OnLoadingListener() {
                    @Override
                    public void onRefresh(XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                        ActTeamReports.this.mXRecyclerView = mXRecyclerView;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("现有表格数据", mTableDatas.toString());
                                currPage = -1;
                                updateTable();
                            }
                        }, 1000);
                    }

                    @Override
                    public void onLoadMore(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                        ActTeamReports.this.mXRecyclerView = mXRecyclerView;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateTable();
                            }
                        }, 1000);
                    }
                })
                .setOnItemClickListenter(new LockTableView.OnItemClickListenter() {
                    @Override
                    public void onItemClick(View item, int position) {

                        if (listOrders.get(position - 1).userType >= 1) {
                            currPage = 0;
                            etUserName.setText(listOrders.get(position - 1).userName);
                            search(listOrders.get(position - 1).userName, startMills, stopMills, currPage, PAGE_SIZE);

                            subnamelist.clear();
                            subName = listOrders.get(position - 1).userName;

                            buildSubNameChainList();

                            subnamelist.addAll(temp_list);

                            mAdapter.notifyDataSetChanged();

                        } else {
                            currPage = -1;
                            updateTable();
                        }

                    }
                })
                .setOnItemLongClickListenter(new LockTableView.OnItemLongClickListenter() {
                    @Override
                    public void onItemLongClick(View item, int position) {
                        Log.e("长按事件", position + "");
                    }
                })
                .setOnItemSeletor(R.color.dashline_color)//设置Item被选中颜色
                .show(); //显示表格,此方法必须调用
        mLockTableView.getTableScrollView().setPullRefreshEnabled(true);
        mLockTableView.getTableScrollView().setLoadingMoreEnabled(true);
        mLockTableView.getTableScrollView().setRefreshProgressStyle(ProgressStyle.BallRotate);

    }

    private void buildSubNameChainList() {
        temp_list.add(temp_list.size(), subName);
        temp_list = Strs.removeDuplicateinOrder(temp_list);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < temp_list.size(); i++) {
            if (i != 0 && !temp_list.get(i).contains(">")) {
                list.add(">" + temp_list.get(i));
            } else {
                list.add(temp_list.get(i));
            }
        }

        temp_list.clear();
        temp_list.addAll(list);
        temp_list = Strs.removeDuplicateinOrder(temp_list);
    }
}
