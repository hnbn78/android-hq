package com.desheng.base.panel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.global.AbDevice;
import com.ab.global.Config;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.module.MM;
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
import com.desheng.base.global.BaseConfig;
import com.desheng.base.model.ChaseRecordBean;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.view.ListMenuPopupWindow;
import com.desheng.base.view.SpinnerListAdapter;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.mypicker.DatePickerDialog;
import com.pearl.view.mypicker.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import okhttp3.Request;

/**
 * 追号记录
 * Created by user on 2018/2/27.
 */
@Deprecated
public class ActChaseRecordList extends AbAdvanceActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RelativeLayout vgAllLottery;
    private RecyclerView rvOrderList;
    private OrderAdapter adapter;

    public static void launch(Context act) {
        Intent itt = new Intent(act, ActChaseRecordList.class);
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


    private String lotteryCode;


    private long startMills = 0;
    private long stopMills = 0;
    private int currPage = -1;
    private List<ChaseRecordBean> listOrders = new ArrayList<>();
    private int totalCount;

    private String arrayLottery[] = null;

    @Override
    protected int getLayoutId() {
        return R.layout.act_chase_record_list;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "追号记录");
        setStatusBarTranslucentAndLightContentWithPadding();

        initView();

        initLottory();

        updateTable();
    }

    /**
     * 初始化所有彩种
     */
    private void initView() {
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
                listOrders.clear();
                updateTable();
            }
        });

        rvOrderList = (RecyclerView) findViewById(R.id.rvOrderList);
        rvOrderList.setLayoutManager(Views.genLinearLayoutManagerV(ActChaseRecordList.this));
        adapter = new ActChaseRecordList.OrderAdapter(ActChaseRecordList.this);
        rvOrderList.setAdapter(adapter);
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                updateTable();
            }
        }, rvOrderList);
        srlRefresh = (SwipeRefreshLayout) findViewById(R.id.srlRefresh);
        srlRefresh.setOnRefreshListener(this);
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
                    lotteryPopup = new ListMenuPopupWindow(ActChaseRecordList.this, AbDevice.SCREEN_WIDTH_PX, arrayLottery);
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
                    Toasts.show(ActChaseRecordList.this, msg, false);
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
        updateTable();
        srlRefresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                srlRefresh.setRefreshing(false);
            }
        }, 500);
    }

    public void updateTable() {
        currPage++;
        search(lotteryCode, startMills, stopMills, currPage, PAGE_SIZE);
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
                    Dialogs.showProgressDialog(ActChaseRecordList.this, "搜索中...");
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
                    if (page == 0) {
                        listOrders.clear();
                    }
                    totalCount = respSeachAfter.totalCount;
                    listOrders.addAll(respSeachAfter.list);
                    adapter.setNewData(listOrders);

                    if (listOrders.size() >= totalCount) {
                        adapter.setEnableLoadMore(false);
                        Toasts.show(ActChaseRecordList.this, "已经全部加载完毕!");
                    }
                } else {
                    Toasts.show(ActChaseRecordList.this, msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                if (page == 0) {
                    Dialogs.hideProgressDialog(ActChaseRecordList.this);
                }
            }
        });


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

    protected class OrderAdapter extends BaseQuickAdapter<ChaseRecordBean, OrderAdapter.OrderVH> {

        private final Context ctx;

        public OrderAdapter(Context ctx) {
            super(R.layout.item_lottery_chase_list, new ArrayList<ChaseRecordBean>());
            this.ctx = ctx;
        }

        /**
         * mfristData.add("账号");
         * mfristData.add("彩种");
         * mfristData.add("玩法");
         * mfristData.add("开始期号");
         * mfristData.add("已追/总期数");
         * mfristData.add("总金额");
         * mfristData.add("总奖金");
         * mfristData.add("状态");
         * <p>
         * ChaseRecordBean chaseRecordBean = chaseRecordBeanList.get(i);
         * mRowDatas.add(chaseRecordBean.account);
         * mRowDatas.add(chaseRecordBean.lottery);
         * mRowDatas.add(chaseRecordBean.method);
         * mRowDatas.add(chaseRecordBean.startIssue);
         * mRowDatas.add(Strs.of(chaseRecordBean.clearCount)+"/"+Strs.of(chaseRecordBean.totalCount));
         * mRowDatas.add(Nums.formatDecimal(chaseRecordBean.totalMoney, 3));
         * mRowDatas.add(Nums.formatDecimal(chaseRecordBean.winMoney, 3));
         * mRowDatas.add(chaseRecordBean.statusStr);
         * <p>
         * "id" : 0,
         * "billno" : "826",
         * "account" : "test0001",
         * "lottery" : "腾讯分分彩",
         * "startIssue" : "20180605-0929",
         * "endIssue" : null,
         * "totalCount" : 10,
         * "method" : "后三码直选复式",
         * "content" : "1|1,8|5,6",
         * "compress" : null,
         * "nums" : 0,
         * "model" : null,
         * "code" : null,
         * "point" : 0.032,
         * "totalMoney" : 409.2,
         * "orderTime" : 1528183689000,
         * "status" : 0,
         * "clearCount" : 9,
         * "winMoney" : 0.0,
         * "allowCancel" : true,
         * "statusStr" : "进行中",
         * "chaseList" : null,
         * "lotteryId" : 911,
         * "winStop" : true
         *
         * @param holder
         * @param item
         */
        @Override
        protected void convert(final OrderVH holder, final ChaseRecordBean item) {
            holder.tvPlayName.setText("彩种:" + item.lottery);
            holder.tvMethod.setText("玩法: " + item.method);
            holder.tvIssueNo.setText("开始期号:" + item.startIssue + "期");
            holder.tvBillNo.setText("订单编号: " + item.billno);
            holder.tvOrderContent.setText("已追/总期数: " + Strs.of(item.clearCount) + "/" + Strs.of(item.totalCount));
            holder.tvBetMoney.setText("总金额: " + Nums.formatDecimal(item.totalMoney, 3));
            holder.tvWinMoney.setText("总奖金: " + Nums.formatDecimal(item.winMoney, 3));

            String color;
            String statusStr = item.statusStr;
            switch (item.statusStr) {
                case "进行中":
                    color = "#0000ff";
                    break;
                case "已完成":
                    color = "#ff0000";
                    /*
                     * //查看彩世纪 线上反馈 https://docs.google.com/spreadsheets/d/1f3eMxJ64P7HGUCp_buJRc4HgH30nDNwfSdXrsHaFD3M/edit#gid=1979491279
                     * 第33，46
                     * */
//                    if (Config.custom_flag.equals(BaseConfig.FLAG_CAISHIJI))
//                        statusStr = "已撤销";
                    break;
                default:
                    color = "#000";
                    break;
            }
            com.orhanobut.logger.Logger.d("status === >  " + item.status);
            String status = String.format("状态: <font color=\"%s\">%s</font>", color, statusStr);

            holder.tvStatus.setText(Html.fromHtml(status));
            if (item.allowCancel) {
                holder.tvCancelBtn.setTag(item);
                holder.tvCancelBtn.setVisibility(View.VISIBLE);
            } else {
                holder.tvCancelBtn.setTag(null);
                holder.tvCancelBtn.setVisibility(View.INVISIBLE);
            }
            holder.tvCancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ChaseRecordBean item = (ChaseRecordBean) v.getTag();
                    if (item == null) {
                        Toasts.show(ActChaseRecordList.this, "只有未开奖的订单可以撤销!", false);
                        return;
                    }
                    final MaterialDialog dialog = new MaterialDialog(ctx);
                    dialog.setMessage("确认撤销订单吗?");
                    dialog.setNegativeButton("取消", new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setPositiveButton("确认", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            HttpAction.cancelChase(ActChaseRecordList.this, item.billno, new AbHttpResult() {
                                @Override
                                public void onBefore(Request request, int id, String host, String funcName) {
                                    holder.tvCancelBtn.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Dialogs.showProgressDialog(ActChaseRecordList.this, "");
                                        }
                                    });
                                }

                                @Override
                                public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                    if (code == 0 && error == 0) {
                                        Toasts.show(ActChaseRecordList.this, "撤单成功", true);
                                        onRefresh();
                                    } else {
                                        Toasts.show(ActChaseRecordList.this, msg, false);
                                    }
                                    return true;
                                }

                                @Override
                                public void onAfter(int id) {
                                    holder.tvCancelBtn.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Dialogs.hideProgressDialog(ActChaseRecordList.this);
                                        }
                                    }, 500);
                                }
                            });
                        }
                    });
                    dialog.show();
                }
            });

            holder.tvDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ActChaseDetail.launch(ActChaseRecordList.this, item);

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
            private TextView tvDetail;
            private TextView tvBetMoney;
            private TextView tvWinMoney;

            public OrderVH(View view) {
                super(view);
                vgInfo = (LinearLayout) view.findViewById(R.id.vgInfo);
                tvPlayName = (TextView) view.findViewById(R.id.tvPlayName);
                tvIssueNo = (TextView) view.findViewById(R.id.tvIssueNo);
                tvMethod = (TextView) view.findViewById(R.id.tvMethod);
                tvResult = (TextView) view.findViewById(R.id.tvResult);
                tvBillNo = (TextView) view.findViewById(R.id.tvBillNo);
                tvBillDate = (TextView) view.findViewById(R.id.tvBillDate);
                tvOrderContent = (TextView) view.findViewById(R.id.tvOrderContent);
                tvStatus = (TextView) view.findViewById(R.id.tvStatus);
                tvCancelBtn = (TextView) view.findViewById(R.id.tvCancelBtn);
                tvBetMoney = (TextView) view.findViewById(R.id.tvBetMoney);
                tvWinMoney = (TextView) view.findViewById(R.id.tvWinMoney);
                tvDetail = (TextView) view.findViewById(R.id.tvDetail);
            }
        }
    }
}
