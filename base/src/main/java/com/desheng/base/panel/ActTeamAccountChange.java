package com.desheng.base.panel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.global.AbDevice;
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
import com.desheng.base.global.AppBase;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.model.TeamAccountChangeRecord;
import com.desheng.base.view.ListMenuPopupWindow;
import com.desheng.base.view.SpinnerListAdapter;
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

public class ActTeamAccountChange extends AbAdvanceActivity {
    private static final int PAGE_SIZE = 20;
    
    public static void launch(Activity ctx) {
        simpleLaunch(ctx, ActTeamAccountChange.class);
    }
    private TextView tvStartDate, tvEndDate,tvAllCategory;
    private Button btnSearch;
    private LinearLayout mContentView;
    private long startMills = 0;
    private long stopMills = 0;
    private int currPage = -1;
    private EditText etUserName;
    private List<TeamAccountChangeRecord> listOrders = new ArrayList<>();
    private ArrayList<ArrayList<String>> mTableDatas = new ArrayList<>();
    private LockTableView mLockTableView;
    private int totalCount;
    private XRecyclerView mXRecyclerView;
    private RelativeLayout layout_AllCategory;
    private String zbType="";

    private ListMenuPopupWindow categoryPopup = null;
    private String[] arrayCategory = {"全部", "彩票游戏", "活动", "分红", "转账", "修正资金", "充值", "提现"};
    private String[] arrayValue = {"","7","90","91","3","80","1","2"};

    @Override
    protected int getLayoutId() {
        return R.layout.act_team_account_change;
    }
    
    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "团队帐变记录");
        setStatusBarTranslucentAndLightContentWithPadding();
        //username= UserManagerCaiHong.getIns().getAccount();
        initView();
        
        initLockTable();
        
        updateTable();
    }
    
    public void updateTable() {
        String userName = Views.getText(etUserName);
        if (Strs.isEmpty(userName)) {
            userName = null;
        }
        currPage++;
        search(userName,zbType,startMills, stopMills, currPage, PAGE_SIZE);
    }

    private void initView() {
        categoryPopup = new ListMenuPopupWindow(ActTeamAccountChange.this, AbDevice.SCREEN_WIDTH_PX, arrayCategory);
        mContentView = (LinearLayout) findViewById(R.id.contentView);
        layout_AllCategory = (RelativeLayout) findViewById(R.id.layout_AllCategory);
        tvAllCategory = (TextView) findViewById(R.id.tvAllCategory);
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

        layout_AllCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryPopup.showAsDropDown(layout_AllCategory, 0, 0);//显示在rl_spinner的下方
            }
        });

        categoryPopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {
                zbType = arrayValue[position];
                tvAllCategory.setText(arrayCategory[position]);
            }
        });
    }
    
    public void search( String username,String zbType, long sTime, long eTime,
                        final int page, int size) {
        String startTime = Dates.getStringByFormat(sTime, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(eTime, Dates.dateFormatYMDHMS);
        
        HttpAction.getAccountChange(ActTeamAccountChange.this, username, zbType,"",startTime, stopTime, page, size,  new AbHttpResult() {
            
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                if (page == 0) {
                    Dialogs.showProgressDialog(ActTeamAccountChange.this, "搜索中...");
                }
                
            }
            
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespTeamAccountChange.class);
            }
            
            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                HttpAction.RespTeamAccountChange respAccountChange = getField(extra, "data", null);
                if (respAccountChange != null) {
                    mTableDatas.clear();
                    addTableHeader();
                    if (page == 0) {
                        listOrders.clear();
                    }
                    totalCount = respAccountChange.totalCount;
                    listOrders.addAll(respAccountChange.list);
                    for (int i = 0; i < listOrders.size(); i++) {
                        ArrayList<String> mRowDatas = new ArrayList<String>();
                        TeamAccountChangeRecord bean = listOrders.get(i);
                        mRowDatas.add(bean.cn);
                        mRowDatas.add(bean.spsn);
                        mRowDatas.add(bean.createTime);
                        mRowDatas.add(bean.changeTypeStr);
                        HashMap<String, String>  mapDetail = new HashMap<>();
                        mapDetail.put("20", "彩票分红");
                        mapDetail.put("0", "申请提现");
                        String detaiStr = "";
                        if(Strs.isEmpty(bean.changeTypeDetailStr)){
                            detaiStr = mapDetail.get(bean.changeDetailType);
                        }else{
                            detaiStr = bean.changeTypeDetailStr;
                        }

                        if(Config.custom_flag.equals(BaseConfig.FLAG_QINGFENG)){
                             if(detaiStr.equals("单单分红")){
                                 detaiStr="挂单返佣";
                             }else if(detaiStr.equals("单单日结")){
                                 detaiStr="单单工资";
                             }
                        }

                        mRowDatas.add(detaiStr);
                        mRowDatas.add(Nums.formatDecimal(bean.amount, 3));
                        mRowDatas.add(Nums.formatDecimal(bean.point, 3));
                        String note_detail=bean.note;
                        if(Config.custom_flag.equals(BaseConfig.FLAG_QINGFENG)){
                            if(bean.note.equals("单单分红")){
                                note_detail="挂单返佣";
                            }else if(bean.note.equals("单单日结")){
                                note_detail="单单工资";
                            }
                        }
                        mRowDatas.add(note_detail);

                        mTableDatas.add(mRowDatas);
                    }
                    mLockTableView.setTableDatas(mTableDatas);
                    if (listOrders.size() >= totalCount) {
                        mLockTableView.getTableScrollView().setLoadingMoreEnabled(false);
//                        Toasts.show(AppBase.getInstance(), "已经全部加载完毕!");//手机内存不够的时候会导致ANR
                    } else {
                        mLockTableView.getTableScrollView().setLoadingMoreEnabled(true);
                    }


                } else {
                    Toasts.show(ActTeamAccountChange.this, msg, false);
                }
                return true;
            }
            
            @Override
            public void onAfter(int id) {
                if (page == 0) {
                    Dialogs.hideProgressDialog(ActTeamAccountChange.this);
                }
                if (ActTeamAccountChange.this.mXRecyclerView != null) {
                    ActTeamAccountChange.this.mXRecyclerView.refreshComplete();
                    ActTeamAccountChange.this.mXRecyclerView.loadMoreComplete();
                }
            }
        });
    }
    
    public void addTableHeader() {
        ArrayList<String> mfristData = new ArrayList<String>();
        
        //标题
        mfristData.add("账号");
        mfristData.add("订单号");
        mfristData.add("申请时间");
        mfristData.add("操作类型");
        mfristData.add("业务类型");
        mfristData.add("变动金额");
        mfristData.add("余额");
        mfristData.add("备注");
        
        mTableDatas.add(mfristData);
    }
    
    
    private void initLockTable() {
        addTableHeader();
        mLockTableView = new LockTableView(this, mContentView, mTableDatas);
        mLockTableView.setColumnWidth(7, 150);
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
                        ActTeamAccountChange.this.mXRecyclerView = mXRecyclerView;
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
                        ActTeamAccountChange.this.mXRecyclerView = mXRecyclerView;
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
        mLockTableView.getTableScrollView().setRefreshProgressStyle(ProgressStyle.BallBeat);
        
        
        
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
    
    
}
