package com.desheng.base.panel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.global.AbDevice;
import com.ab.global.Config;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.module.MM;
import com.ab.util.ClipboardUtils;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.ab.view.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.BetStatus;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryOrderInfo;
import com.desheng.base.view.ListMenuPopupWindow;
import com.desheng.base.view.SpinnerListAdapter;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.mypicker.DataPickerDialog;
import com.pearl.view.mypicker.DatePickerDialog;
import com.pearl.view.mypicker.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * 投注记录
 */
public class ActBettingRecordList extends AbAdvanceActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private RecyclerView rvOrderList;
    private OrderAdapter adapter;
    private SwipeRefreshLayout srlRefresh;

    public static void launch(Activity ctx) {
        if (Config.custom_flag.equals(BaseConfig.FLAG_HETIANXIA)
                || Config.custom_flag.equals(BaseConfig.FLAG_ZHONGXIN)
                ) {
            ActBettingRecordListHETIANXIA.launch(ctx);
        } else {
            simpleLaunch(ctx, ActBettingRecordList.class);
        }
    }

    private static final int PAGE_SIZE = 15;
    private TextView tvStartDate, tvEndDate;
    private Button btnSearch;
    private ListMenuPopupWindow lotteryPopup = null;
    private ListMenuPopupWindow statePopup = null;


    private RelativeLayout vgAllLottery;
    private Dialog chooseDialog;
    private RelativeLayout vgAllState;

    private LinearLayout mContentView;

    private ArrayList<LotteryInfo> listAllLottery;
    private String arrayLottery[] = null;
    private String arrayStatus[] = new String[]{"全部状态", "未开奖", "已撤销", "未中奖", "已派奖", "系统撤销"};
    private TextView tvAllLottery;
    private TextView tvAllState;


    private String lotteryCode;
    private long startMills = 0;
    private long stopMills = 0;
    private int currPage = -1;
    private Integer status;
    private EditText etIssueNo;
    private List<LotteryOrderInfo> listOrders = new ArrayList<>();
    private int totalCount;

    @Override
    protected int getLayoutId() {
        return R.layout.act_betting_record_list;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "投注记录");
        setStatusBarTranslucentAndLightContentWithPadding();

        initView();

        initLottory();

        updateTable();
    }

    /**
     * 初始化所有控件
     */
    private void initView() {
        mContentView = findViewById(R.id.contentView);

        tvAllLottery = findViewById(R.id.tvAllLottery);
        vgAllLottery = findViewById(R.id.vgOrderType);


        tvAllState = findViewById(R.id.tvAllState);
        vgAllState = findViewById(R.id.vgAllState);
        statePopup = new ListMenuPopupWindow(ActBettingRecordList.this, AbDevice.SCREEN_WIDTH_PX, arrayStatus);
        statePopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {
                if (position == 0) {
                    status = null;
                } else {
                    status = CtxLottery.getUserBetsStatus(arrayStatus[position]);
                }
                tvAllState.setText(arrayStatus[position]);
            }
        });


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

        btnSearch = findViewById(R.id.btnSearch);

        etIssueNo = findViewById(R.id.etIssueNo);
        rvOrderList = findViewById(R.id.rvOrderList);
        rvOrderList.setLayoutManager(Views.genLinearLayoutManagerV(ActBettingRecordList.this));
        adapter = new OrderAdapter(ActBettingRecordList.this);
        rvOrderList.setAdapter(adapter);
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                updateTable();
            }
        }, rvOrderList);
        srlRefresh = findViewById(R.id.srlRefresh);
        srlRefresh.setOnRefreshListener(this);

        btnSearch.setOnClickListener(this);
        vgAllLottery.setOnClickListener(this);
        vgAllState.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == vgAllState) {
            statePopup.showAsDropDown(vgAllState, 0, 0);
        } else if (v == btnSearch) {
            currPage = -1;
            listOrders.clear();
            updateTable();
        } else if (v == vgAllLottery) {
            if (lotteryPopup != null) {
                lotteryPopup.showAsDropDown(vgAllLottery, 0, 0);//显示在rl_spinner的下方
            }
        }
    }

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
                    lotteryPopup = new ListMenuPopupWindow(ActBettingRecordList.this, AbDevice.SCREEN_WIDTH_PX, arrayLottery);
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
                } else {
                    Toasts.show(ActBettingRecordList.this, msg, false);
                }


                return true;
            }

            @Override
            public void onAfter(int id) {

            }
        });
    }

    @Override
    public void onRefresh() {
        currPage = -1;
        listOrders.clear();
        updateTable();
        srlRefresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                srlRefresh.setRefreshing(false);
            }
        }, 500);
    }

    public void updateTable() {
        String issue = Views.getText(etIssueNo);
        if (Strs.isEmpty(issue)) {
            issue = null;
        }
        currPage++;
        search(lotteryCode, status, startMills, stopMills, currPage, PAGE_SIZE, issue);
    }

    public void search(String lottery, Integer optStatus, long sTime, long eTime, final int page,
                       int size, String optIssue) {
        String startTime = Dates.getStringByFormat(sTime, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(eTime, Dates.dateFormatYMDHMS);
        if (Strs.isNotEmpty(optIssue)) {
            optStatus = null;
            lottery = null;
        }
        HttpAction.searchOrder(ActBettingRecordList.this, lottery, ""+optStatus, startTime, stopTime, page, size, optIssue, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                if (page == 0) {
                    Dialogs.showProgressDialog(ActBettingRecordList.this, "搜索中...");
                }

            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespSearchOrder.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                HttpAction.RespSearchOrder respSearchOrder = getField(extra, "data", null);
                if (respSearchOrder != null) {
                    if (page == 0) {
                        listOrders.clear();
                    }
                    totalCount = respSearchOrder.totalCount;
                    listOrders.addAll(respSearchOrder.list);
                    adapter.setNewData(listOrders);

                    if (listOrders.size() >= totalCount) {
                        adapter.setEnableLoadMore(false);
                        Toasts.show(ActBettingRecordList.this, "已经全部加载完毕!");
                    }
                } else {
                    Toasts.show(ActBettingRecordList.this, msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                if (page == 0) {
                    Dialogs.hideProgressDialog(ActBettingRecordList.this);
                }
            }
        });
    }

    /**
     * chooseDialog
     */
    private void showChooseDialog(List<String> mlist) {
        DataPickerDialog.Builder builder = new DataPickerDialog.Builder(this);
        chooseDialog = builder.setData(mlist).setSelection(1).setTitle("取消")
                .setOnDataSelectedListener(new DataPickerDialog.OnDataSelectedListener() {
                    @Override
                    public void onDataSelected(String itemValue, int position) {
                        tvStartDate.setText(itemValue);
                    }

                    @Override
                    public void onCancel() {

                    }
                }).create();

        chooseDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MM.http.cancellAllByTag(this);
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

    protected class OrderAdapter extends BaseQuickAdapter<LotteryOrderInfo, OrderAdapter.OrderVH> {

        private final Context ctx;

        public OrderAdapter(Context ctx) {
            super(R.layout.item_lottery_order_list, new ArrayList<LotteryOrderInfo>());
            this.ctx = ctx;
        }

        @Override
        protected void convert(final OrderVH holder, final LotteryOrderInfo item) {
            holder.tvIssueNo.setText(item.issue + "期");
            holder.tvPlayName.setText(item.lottery);
            holder.tvResult.setText("开奖号码: " + (Strs.isEmpty(item.openCode) ? "等待开奖结果" : item.openCode));
            holder.tvMethod.setText("玩法: " + item.method);
            holder.tvBillNo.setText("订单编号: " + item.billno);
            holder.tvBillDate.setText("订单时间: " + Dates.getStringByFormat(item.orderTime, Dates.dateFormatYMDHMS));
            holder.tvOrderContent.setText(item.content.length() >= 50 ? item.content + "..." : item.content);
            holder.tvBetMoney.setText("投注金额: " + Views.fromStrings(R.string.money_sign) + Nums.formatDecimal(item.money, 3));
            holder.tvWinMoney.setText("中奖金额: " + Views.fromStrings(R.string.money_sign) + Nums.formatDecimal(item.winMoney, 3));
            String color = "#000";
            if (("未开奖").equals(item.statusRemark)) {
                color = "#0000ff";
            } else if ("个人撤单".equals(item.statusRemark)) {
                color = "#ff0000";
            } else if ("已派奖".equals(item.statusRemark)) {
                color = "#ff0000";
            }

            String status = String.format("投注状态: <font color=\"%s\">%s</font>", color, item.statusRemark);
            holder.tvStatus.setText(Html.fromHtml(status));
            if (item.status == BetStatus.NORMAL.getCode()) {
                holder.tvCancelBtn.setTag(item);
                holder.tvCancelBtn.setVisibility(View.VISIBLE);
            } else {
                holder.tvCancelBtn.setTag(null);
                holder.tvCancelBtn.setVisibility(View.INVISIBLE);
            }

            holder.tv_copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardUtils.copyText(ctx, item.issue);
                    Toasts.show(ctx, "已复制到剪切板");
                }
            });

            holder.tvCancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final LotteryOrderInfo item = (LotteryOrderInfo) v.getTag();
                    if (item == null) {
                        Toasts.show(ActBettingRecordList.this, "只有未开奖的订单可以撤销!", false);
                        return;
                    }

                    final MaterialDialog dialog = new MaterialDialog(ActBettingRecordList.this);

                    dialog.setTitle("撤销提示");
                    dialog.setMessage("您确定要撤销订单？");
                    dialog.setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HttpAction.cancelOrder(ActBettingRecordList.this, item.billno, new AbHttpResult() {
                                @Override
                                public void onBefore(Request request, int id, String host, String funcName) {
                                    holder.tvCancelBtn.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Dialogs.showProgressDialog(ActBettingRecordList.this, "");
                                        }
                                    });
                                }

                                @Override
                                public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                    dialog.dismiss();

                                    if (code == 0 && error == 0) {
//                                        Toasts.show(ActBettingRecordList.this, "撤单成功", true);
                                        onRefresh();
                                        UserManager.getIns().refreshUserData();
                                    } else {
                                        Toasts.show(ActBettingRecordList.this, msg, false);
                                    }
                                    return true;
                                }

                                @Override
                                public void onAfter(int id) {
                                    holder.tvCancelBtn.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Dialogs.hideProgressDialog(ActBettingRecordList.this);
                                        }
                                    }, 500);
                                }
                            });
                        }
                    });
                    dialog.show();


                }
            });

            holder.vgInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActLotteryDetail.launch(ActBettingRecordList.this, item);
                }
            });
        }

        protected class OrderVH extends BaseViewHolder {
            private LinearLayout vgInfo;
            private TextView tvPlayName;
            private TextView tvIssueNo;
            private TextView tvMethod;
            private TextView tvResult;
            private TextView tvBillNo;
            private TextView tvBillDate;
            private TextView tvOrderContent;
            private TextView tvStatus;
            private TextView tvCancelBtn;
            private TextView tvBetMoney;
            private TextView tvWinMoney;
            private TextView tv_copy;

            public OrderVH(View view) {
                super(view);
                vgInfo = view.findViewById(R.id.vgInfo);
                tvPlayName = view.findViewById(R.id.tvPlayName);
                tvIssueNo = view.findViewById(R.id.tvIssueNo);
                tvMethod = view.findViewById(R.id.tvMethod);
                tvResult = view.findViewById(R.id.tvResult);
                tvBillNo = view.findViewById(R.id.tvBillNo);
                tvBillDate = view.findViewById(R.id.tvBillDate);
                tvOrderContent = view.findViewById(R.id.tvOrderContent);
                tvStatus = view.findViewById(R.id.tvStatus);
                tvCancelBtn = view.findViewById(R.id.tvCancelBtn);
                tvBetMoney = view.findViewById(R.id.tvBetMoney);
                tvWinMoney = view.findViewById(R.id.tvWinMoney);
                tv_copy = view.findViewById(R.id.tv_copy);
            }
        }
    }
}