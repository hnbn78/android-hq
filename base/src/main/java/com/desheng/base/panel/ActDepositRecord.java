package com.desheng.base.panel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.AbDevice;
import com.ab.global.Config;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.ClipboardUtils;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.model.RechargeRecordBean;
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
 * Created by user on 2018/4/20.
 */

public class ActDepositRecord extends AbAdvanceActivity {


    private View tvUserAccount;

    public static void launch(Activity activity, boolean needTeam) {
        Intent itt = new Intent(activity, ActDepositRecord.class);
        itt.putExtra("needTeam", needTeam);
        activity.startActivity(itt);
    }

    private static final int PAGE_SIZE = 20;
    private TextView tvStartDate, tvEndDate;
    private Button btnSearch;
    private ListMenuPopupWindow lotteryPopup = null;
    private ListMenuPopupWindow statePopup = null;


    private RelativeLayout vgAllLottery;
    private Dialog chooseDialog;
    private RelativeLayout vgAllState;

    private LinearLayout mContentView;

    private String arrayStatus[] = new String[]{"全部", "成功", "失败", "待处理"};
    private TextView tvAllState;

    private long startMills = 0;
    private long stopMills = 0;
    private int currPage = -1;
    private Integer status = 0;
    private EditText etUserAccount;
    private List<RechargeRecordBean> listOrders = new ArrayList<>();
    private ArrayList<ArrayList<String>> mTableDatas = new ArrayList<>();
    private LockTableView mLockTableView;
    private int totalCount;
    private XRecyclerView mXRecyclerView;

    private EditText etOrderNum;
    private TextView tvOrderNum;

    private boolean needTeam;

    @Override
    protected int getLayoutId() {
        return R.layout.act_deposit_record;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), getString(R.string.deposit_record));
        setStatusBarTranslucentAndLightContentWithPadding();

        initView();

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
        String userAccount = Views.getText(etUserAccount);
        if (Strs.isEmpty(userAccount)) {
            userAccount = null;
        }
        currPage++;
        search(status, startMills, stopMills, currPage, PAGE_SIZE, userAccount);
    }

    public void search(Integer optStatus, long sTime, long eTime, final int page,
                       int size, String userAccount) {
        String startTime = Dates.getStringByFormat(sTime, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(eTime, Dates.dateFormatYMDHMS);
        if (Strs.isNotEmpty(userAccount)) {
            startTime = null;
            stopTime = null;
            optStatus = null;
            Toasts.show(ActDepositRecord.this, "仅查找对应用户名!");
        }

        String billNo = etOrderNum.getText().toString();
        if (Strs.isEmpty(billNo))
            billNo = null;

        HttpAction.searchRechargeRecord(ActDepositRecord.this, billNo, userAccount, Strs.of(optStatus), startTime, stopTime, page, size, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                if (page == 0) {
                    Dialogs.showProgressDialog(ActDepositRecord.this, "搜索中...");
                }

            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespSearchRechargeRecord.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                HttpAction.RespSearchRechargeRecord respSearchOrder = getField(extra, "data", null);
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
                        RechargeRecordBean orderInfo = listOrders.get(i);

                        mRowDatas.add(orderInfo.billno);
                        mRowDatas.add(CtxLottery.formatUserRechargeType(orderInfo.method));
                        mRowDatas.add(Nums.formatDecimal(orderInfo.amount, 3));
                        mRowDatas.add(Dates.getStringByFormat(orderInfo.orderTime, Dates.dateFormatYMDHMS));
                        mRowDatas.add(CtxLottery.formatUserRechargeStatus(orderInfo.orderStatus));

                        mTableDatas.add(mRowDatas);
                    }
                    mLockTableView.setTableDatas(mTableDatas);
                    if (listOrders.size() >= totalCount) {
                        mLockTableView.getTableScrollView().setLoadingMoreEnabled(false);
//                        Toasts.show(ActDepositRecord.this, "已经全部加载完毕!");
                    }
                } else {
                    Toasts.show(ActDepositRecord.this, msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                if (page == 0) {
                    Dialogs.hideProgressDialog(ActDepositRecord.this);
                }
                if (ActDepositRecord.this.mXRecyclerView != null) {
                    ActDepositRecord.this.mXRecyclerView.refreshComplete();
                    ActDepositRecord.this.mXRecyclerView.loadMoreComplete();
                }
            }
        });
    }

    public void addTableHeader() {
        ArrayList<String> mfristData = new ArrayList<String>();

        //标题
        mfristData.add("    订单号    ");
        mfristData.add("充值方式");
        mfristData.add("充值金额");
        mfristData.add("充值日期");
        mfristData.add("状态");

        mTableDatas.add(mfristData);
    }


    /**
     * 初始化所有控件
     */
    private void initView() {

        tvUserAccount = findViewById(R.id.tvUserAccount);
        etUserAccount = (EditText) findViewById(R.id.etUserAccount);
        if (needTeam) {
            tvUserAccount.setVisibility(View.VISIBLE);
            etUserAccount.setVisibility(View.VISIBLE);
        }

        tvOrderNum = findViewById(R.id.tvOrderNum);
        etOrderNum = findViewById(R.id.etOrderNum);

        if (Config.custom_flag.equals(BaseConfig.FLAG_HETIANXIA) ||
                Config.custom_flag.equals(BaseConfig.FLAG_ZHONGXIN) ||
                Config.custom_flag.equals(BaseConfig.FLAG_JINDU)) {
            tvOrderNum.setVisibility(View.VISIBLE);
            etOrderNum.setVisibility(View.VISIBLE);
        }

        mContentView = (LinearLayout) findViewById(R.id.contentView);

        tvAllState = (TextView) findViewById(R.id.tvAllState);
        vgAllState = (RelativeLayout) findViewById(R.id.vgAllState);
        statePopup = new ListMenuPopupWindow(ActDepositRecord.this, AbDevice.SCREEN_WIDTH_PX, arrayStatus);
        statePopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {
                if (position == 0) {
                    status = 0;
                } else {
                    status = position;
                }
                tvAllState.setText(arrayStatus[position]);
            }
        });
        vgAllState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statePopup.showAsDropDown(vgAllState, 0, 0);
            }
        });

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
                        ActDepositRecord.this.mXRecyclerView = mXRecyclerView;
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
                        ActDepositRecord.this.mXRecyclerView = mXRecyclerView;
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
                        try {
                            ClipboardUtils.copyText(ActDepositRecord.this, mTableDatas.get(position).get(0));
                            Toast.makeText(ActDepositRecord.this, "订单号已复制到剪切板", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setOnItemSeletor(R.color.dashline_color)//设置Item被选中颜色
                .show(); //显示表格,此方法必须调用
        mLockTableView.getTableScrollView().setPullRefreshEnabled(true);
        mLockTableView.getTableScrollView().setLoadingMoreEnabled(true);
        mLockTableView.getTableScrollView().setRefreshProgressStyle(ProgressStyle.BallRotate);
    }

}
