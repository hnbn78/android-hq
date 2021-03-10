package com.desheng.base.panel;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    
    private ImageView back_icon;
    private TextView tvStartDate, tvEndDate;
    
    private Button Persion_bt, Team_bt;
    private LinearLayout mContentView;
    private EditText Input_et;
    private List<TeamReport> reportList = new ArrayList<>();
    private int flag = 0;
    private static final int PAGE_SIZE = 20;
    private  int type = 0;
    
    private int totalCount;
    private long startMills = 0;
    private long stopMills = 0;
    
    private XRecyclerView mXRecyclerView;
    private int currPage = -1;
    
    private LockTableView mLockTableView;
    private ArrayList<ArrayList<String>> mTableDatas = new ArrayList<>();
    private Button Search_bt;
    
    private boolean needTeam;
    
    @Override
    protected int getLayoutId() {
        return R.layout.act_lottery_report;
    }
    
    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "彩票报表");
        setStatusBarTranslucentAndLightContentWithPadding();
    
        needTeam = getIntent().getBooleanExtra("needTeam", true);
        
        initVew();
        initLockTable();
        updateTable();
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
        String userAccount = Views.getText((TextView) findViewById(R.id.etUserAccount));
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
        HttpAction.searchLotteryReport(username, startTime, stopTime, page, size, type, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                if (page == 0) {
                    Dialogs.showProgressDialog(ActLotteryReport.this, "搜索中...");
                }
                
            }
            
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespSearchLotteryReport.class);
            }
            
            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                HttpAction.RespSearchLotteryReport respSearchLotterylist = getField(extra, "data", null);
                if (respSearchLotterylist != null) {
                    mTableDatas.clear();
                    addTableHeader();
                    if (page == 0) {
                        reportList.clear();
                    }
                    totalCount = respSearchLotterylist.totalCount;
                    reportList.addAll(respSearchLotterylist.list);
                    
                    
                    for (int i = 0; i < reportList.size(); i++) {
                        ArrayList<String> mRowDatas = new ArrayList<String>();
                        TeamReport TeamReport = reportList.get(i);
                        //“账号、时间、充值、取款、投注、奖金、返点、活动、总盈亏”
                        mRowDatas.add(Strs.isEmpty(TeamReport.userName) ? "汇总" : TeamReport.userName);
                        mRowDatas.add(TeamReport.date);
                        mRowDatas.add(Nums.formatDecimal(TeamReport.depositAmount, 3));
                        mRowDatas.add(Nums.formatDecimal(TeamReport.withdrawAmount, 3));
                        mRowDatas.add(Nums.formatDecimal(TeamReport.confirmAmount,3));
                        if(UserManager.getIns().showTXBettAmount()) {
                            mRowDatas.add(Nums.formatDecimal(TeamReport.txsscConfirmAmount,3));
                        }
                        mRowDatas.add(Nums.formatDecimal(TeamReport.awardAmount,3));
                        if(UserManager.getIns().showDanDanSalary()){
                            mRowDatas.add(Nums.formatDecimal(TeamReport.orderBonusAmount,3));
                        }
                        mRowDatas.add(Nums.formatDecimal(TeamReport.pointAmount,3));
                        mRowDatas.add(Nums.formatDecimal(TeamReport.activityAmount, 3));
                        mRowDatas.add(Nums.formatDecimal(0 - TeamReport.profitAmount, 3));
                        //TODO团队
                        /*
                         *  '<td>' + (val.userName ?  val.userName : "汇总")+ '</td>'+
                         '<td>' + DataFormat.formatUserStatus(val.userType) + '</td>'+
                         '<td>' + (val.balance ? val.balance : 0) + '</td>'+
                         '<td>' + val.depositAmount + '</td>'+
                         '<td>' + val.withdrawAmount + '</td>'+
                         '<td>' + val.confirmAmount + '</td>'+
                         '<td>' + val.awardAmount + '</td>'+
                         '<td>' + val.pointAmount + '</td>'+
                         '<td>' + val.activityAmount + '</td>'+
                         '<td ' + ((0 - val.profitAmount)<0 ? ' class="red"' : '') + '>' +(0 - val.profitAmount) + '</td>'+
                         */
                        mTableDatas.add(mRowDatas);
                    }
                    mLockTableView.setTableDatas(mTableDatas);
                    if (reportList.size() >= totalCount) {
                        mLockTableView.getTableScrollView().setLoadingMoreEnabled(false);
//                        Toasts.show(ActLotteryReport.this, "已经全部加载完毕!");
                    } else {
                        mLockTableView.getTableScrollView().setLoadingMoreEnabled(true);
                    }
                }else{
                    Toasts.show(ActLotteryReport.this, msg, false);
                }
                return true;
            }
            
            @Override
            public void onAfter(int id) {
                if (page == 0) {
                    Dialogs.hideProgressDialog(ActLotteryReport.this);
                }
                if (ActLotteryReport.this.mXRecyclerView != null) {
                    ActLotteryReport.this.mXRecyclerView.refreshComplete();
                    ActLotteryReport.this.mXRecyclerView.loadMoreComplete();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (page == 0) {
                    Dialogs.hideProgressDialog(ActLotteryReport.this);
                }
                if (ActLotteryReport.this.mXRecyclerView != null) {
                    ActLotteryReport.this.mXRecyclerView.refreshComplete();
                    ActLotteryReport.this.mXRecyclerView.loadMoreComplete();
                }
            }
        });
    }

    public void addTableHeader() {
        ArrayList<String> mfristData = new ArrayList<String>();
        //“账号、时间、充值、取款、投注、奖金、返点、活动、总盈亏”
        mfristData.add("账号");
        mfristData.add("时间");
        mfristData.add("充值");
        mfristData.add("取款");
        mfristData.add("投注");
        if(UserManager.getIns().showTXBettAmount()) {
            mfristData.add("腾讯投注额");
        }
        mfristData.add("奖金");
        if(UserManager.getIns().showDanDanSalary() ){
            mfristData.add("单单日结");
        }

        mfristData.add("返点");
        mfristData.add("活动");
        mfristData.add("总盈亏");
        
        mTableDatas.add(mfristData);
    }
    
    private void initVew() {
        mContentView = (LinearLayout) findViewById(R.id.contentView);
        vgType =(RadioGroup) findViewById(R.id.vgType);
        rbPersion = ((RadioButton)findViewById(R.id.rbPersion));
        rbTeam = ((RadioButton) findViewById(R.id.rbTeam));
        tvUserAccount = findViewById(R.id.tvUserAccount);
        etUserAccount = findViewById(R.id.etUserAccount);
        if(needTeam){
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
        tvStartDate = (TextView) findViewById(R.id.tvStartDate);
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
        Search_bt = (Button) findViewById(R.id.btnSearch);
        Search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currPage = -1;
                reportList.clear();
                mTableDatas.clear();
                mLockTableView.setTableDatas(mTableDatas);
                updateTable();
            }
        });
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
                        ActLotteryReport.this.mXRecyclerView = mXRecyclerView;
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
                        ActLotteryReport.this.mXRecyclerView = mXRecyclerView;
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
                        Log.e("点击事件", position + "");
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
}