package com.desheng.base.panel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
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
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.model.ChaseRecordBean;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.view.ListMenuPopupWindow;
import com.desheng.base.view.SpinnerListAdapter;
import com.google.gson.reflect.TypeToken;
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
 * 追号记录
 * Created by user on 2018/2/27.
 */

public class ActChaseRecord extends AbAdvanceActivity {
    
    public static void launch(Context act) {
        Intent itt = new Intent(act, ActChaseRecord.class);
        act.startActivity(itt);
    }
    
    private TextView tvStartDate, tvEndDate;
    private Button Search_bt;
    private ArrayList<LotteryInfo> listAllLottery;
    private ListMenuPopupWindow lotteryPopup = null;
    private static final int PAGE_SIZE = 20;
    private TextView tvAllLottery;
    private SwipeRefreshLayout srlRefresh;
    private LinearLayout mContentView;
    
    
    private ArrayList<ArrayList<String>> mTableDatas = new ArrayList<>();
    private LockTableView mLockTableView;
    private int totalCount;
    private String lotteryCode;
    
    private List<ChaseRecordBean> chaseRecordBeanList = new ArrayList<>();
    private RelativeLayout vgAllLottery;
    
    private long startMills = 0;
    private long stopMills = 0;
    private int currPage = -1;
    
    private XRecyclerView mXRecyclerView;
    private String arrayLottery[] = null;
    
    @Override
    protected int getLayoutId() {
        return R.layout.act_chase_record;
    }
    
    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "追号记录");
        setStatusBarTranslucentAndLightContentWithPadding();
        
        initView();
        
        initLottory();
        
        initLockTable();
        
        updateTable();
    }
    
    /**
     * 追号记录
     */
    public void search(String lottery, long sTime, long eTime, final int page, int size) {
        String startTime = Dates.getStringByFormat(sTime, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(eTime, Dates.dateFormatYMDHMS);
        HttpAction.searchChaseRecord(startTime, stopTime, lottery, page, size, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                if (page == 0) {
                    Dialogs.showProgressDialog(ActChaseRecord.this, "搜索中...");
                }
                
            }
            
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespSearchChaseRecord.class);
            }
            
            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                HttpAction.RespSearchChaseRecord respSeachAfter = getField(extra, "data", null);
                if (respSeachAfter != null) {
                    mTableDatas.clear();
                    addTableHeader();
                    if (page == 0) {
                        chaseRecordBeanList.clear();
                    }
                    totalCount = respSeachAfter.totalCount;
                    chaseRecordBeanList.addAll(respSeachAfter.list);
                    for (int i = 0; i < chaseRecordBeanList.size(); i++) {
                        ArrayList<String> mRowDatas = new ArrayList<String>();
                        ChaseRecordBean chaseRecordBean = chaseRecordBeanList.get(i);
                        mRowDatas.add(chaseRecordBean.account);
                        mRowDatas.add(chaseRecordBean.lottery);
                        mRowDatas.add(chaseRecordBean.method);
                        mRowDatas.add(chaseRecordBean.startIssue);
                        mRowDatas.add(Strs.of(chaseRecordBean.clearCount)+"/"+Strs.of(chaseRecordBean.totalCount));
                        mRowDatas.add(Nums.formatDecimal(chaseRecordBean.totalMoney, 3));
                        mRowDatas.add(Nums.formatDecimal(chaseRecordBean.winMoney, 3));
                        mRowDatas.add(chaseRecordBean.statusStr);
                        
                        mTableDatas.add(mRowDatas);
                    }
                    mLockTableView.setTableDatas(mTableDatas);
                    if (chaseRecordBeanList.size() >= totalCount) {
                        mLockTableView.getTableScrollView().setLoadingMoreEnabled(false);
                        Toasts.show(ActChaseRecord.this, "已经全部加载完毕!");
                    } else {
                        mLockTableView.getTableScrollView().setLoadingMoreEnabled(true);
                    }
                } else {
                    Toasts.show(ActChaseRecord.this, msg, false);
                }
                return true;
            }
            
            @Override
            public void onAfter(int id) {
                if (page == 0) {
                    Dialogs.hideProgressDialog(ActChaseRecord.this);
                }
                if (ActChaseRecord.this.mXRecyclerView != null) {
                    ActChaseRecord.this.mXRecyclerView.refreshComplete();
                    ActChaseRecord.this.mXRecyclerView.loadMoreComplete();
                }
            }
        });
        
        
    }
    
    public void addTableHeader() {
        ArrayList<String> mfristData = new ArrayList<String>();
        
        //标题
        mfristData.add("账号");
        mfristData.add("彩种");
        mfristData.add("玩法");
        mfristData.add("开始期号");
        mfristData.add("已追/总期数");
        mfristData.add("总金额");
        mfristData.add("总奖金");
        mfristData.add("状态");
        
        mTableDatas.add(mfristData);
    }
    
    /**
     * 初始化所有彩种
     */
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
                    lotteryPopup = new ListMenuPopupWindow(ActChaseRecord.this, AbDevice.SCREEN_WIDTH_PX, arrayLottery);
                    lotteryPopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
                        @Override
                        public void click(int position, View view) {
                            String lotteryString = arrayLottery[position];
                            if (position == 0) {
                                lotteryCode = null;
                            } else {
                                lotteryCode = listAllLottery.get(position - 1).getCode();
                            }
                            tvAllLottery.setText(lotteryString);
                        }
                    });
                }
                return true;
            }
            
            @Override
            public void onAfter(int id) {
            
            }
        });
    }
    
    /**
     * 初始化所有彩种
     */
    private void initView() {
        mContentView = (LinearLayout) findViewById(R.id.contentView);
        tvAllLottery = (TextView) findViewById(R.id.tvAllLottery);
        vgAllLottery = (RelativeLayout) findViewById(R.id.all_lottery_layuot);
        vgAllLottery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lotteryPopup != null) {
                    lotteryPopup.showAsDropDown(vgAllLottery, 0, 0);//显示在rl_spinner的下方
                }
            }
        });
        
        
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
        Search_bt = (Button) findViewById(R.id.btnSearch);
        Search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currPage = -1;
                chaseRecordBeanList.clear();
                mTableDatas.clear();
                mLockTableView.setTableDatas(mTableDatas);
                
                
                updateTable();
                
                
            }
        });
        
    }
    
    public void updateTable() {
        currPage++;
        search(lotteryCode, startMills, stopMills, currPage, PAGE_SIZE);
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
                .setMaxColumnWidth(100) //列最大宽度
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
                        ActChaseRecord.this.mXRecyclerView = mXRecyclerView;
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
                        ActChaseRecord.this.mXRecyclerView = mXRecyclerView;
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
