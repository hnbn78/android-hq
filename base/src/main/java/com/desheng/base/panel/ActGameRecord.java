package com.desheng.base.panel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.ab.util.ClipboardUtils;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.BetStatus;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.model.GameRecord;
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
 * Created by user on 2018/3/14.
 */

public class ActGameRecord extends AbAdvanceActivity {

    public static void launch(Activity act) {
        simpleLaunch(act, ActGameRecord.class);
    }

    private TextView tvAllLottery;
    private ArrayList<LotteryInfo> listAllLottery;
    private String arrayLottery[] = null;
    private String lotteryCode;
    private long startMills = 0;
    int page;
    int size;
    private long stopMills = 0;
    private int currPage = -1;
    private LockTableView mLockTableView;
    private int totalCount;
    private XRecyclerView mXRecyclerView;
    //0????????????  4????????????  1???????????? 2???????????? 5???????????????
    private String arrayStatus[] = new String[]{"????????????", "?????????", "?????????", "?????????", "?????????", "????????????"};
    private static final int PAGE_SIZE = 20;
    private TextView tvStartDate, tvEndDate;
    private Button btnSearch;
    private ViewGroup vgAllState;
    private TextView tvAllState;
    private ListMenuPopupWindow lotteryPopup = null;
    private ListMenuPopupWindow statePopup = null;
    private EditText etIssueNo;
    private String issue;
    private List<GameRecord> listOrders = new ArrayList<>();
    private ArrayList<ArrayList<String>> mTableDatas = new ArrayList<>();
    private RelativeLayout vgAllLottery;

    private LinearLayout mContentView;

    private EditText etUserName;
    private String userName;
    private Integer status;

    @Override
    protected int getLayoutId() {
        return R.layout.act_game_record;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "????????????");
        setStatusBarTranslucentAndLightContentWithPadding();

        initView();

        initLottory();

        initLockTable();

        updateTable();
    }

    /**
     * ?????????????????????
     */
    private void initView() {
        mContentView = (LinearLayout) findViewById(R.id.contentView);

        tvAllLottery = (TextView) findViewById(R.id.tvAllLottery);
        vgAllLottery = (RelativeLayout) findViewById(R.id.vgOrderType);
        vgAllLottery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lotteryPopup != null) {
                    lotteryPopup.showAsDropDown(vgAllLottery, 0, 0);//?????????rl_spinner?????????
                }
            }
        });
        vgAllState = (ViewGroup) findViewById(R.id.vgAllState);
        tvAllState = (TextView) findViewById(R.id.tvAllState);
        vgAllState = (RelativeLayout) findViewById(R.id.vgAllState);
        statePopup = new ListMenuPopupWindow(ActGameRecord.this, AbDevice.SCREEN_WIDTH_PX, arrayStatus);
        statePopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {
                if (position == 0) {
                    status = null;
                } else {
                    status = BetStatus.find(arrayStatus[position]).getCode();
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

        etUserName = (EditText) findViewById(R.id.etUserName);
        etIssueNo = (EditText) findViewById(R.id.etIssueNo);
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
                    arrayLottery[0] = "????????????";
                    for (int i = 1; i < arrayLottery.length; i++) {
                        arrayLottery[i] = listAllLottery.get(i - 1).getShowName();
                    }
                    lotteryPopup = new ListMenuPopupWindow(ActGameRecord.this, AbDevice.SCREEN_WIDTH_PX, arrayLottery);
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
                    Toasts.show(ActGameRecord.this, msg, false);
                }


                return true;
            }

            @Override
            public void onAfter(int id) {

            }
        });
    }


    public void updateTable() {
        issue = Views.getText(etIssueNo);
        if (Strs.isEmpty(issue)) {
            issue = null;
        }
        userName = Views.getText(etUserName);
        if (Strs.isEmpty(userName)) {
            userName = null;
        }
        currPage++;
        search();
    }


    /**
     *
     */
    public void search() {
        String startTime = Dates.getStringByFormat(startMills, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
        HttpAction.searchGameRecord(lotteryCode, status, userName, issue, startTime, stopTime, currPage, PAGE_SIZE, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                if (page == 0) {
                    Dialogs.showProgressDialog(ActGameRecord.this, "?????????...");
                }

            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespGameRecord.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                HttpAction.RespGameRecord respSearchOrder = getField(extra, "data", null);
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
                        GameRecord orderInfo = listOrders.get(i);
                        mRowDatas.add(orderInfo.account);
                        mRowDatas.add(orderInfo.billno);
                        mRowDatas.add(orderInfo.lottery + "\n" + orderInfo.method);
                        mRowDatas.add(orderInfo.issue);
                        mRowDatas.add(Dates.getStringByFormat(orderInfo.orderTime, Dates.dateFormatYMD));
                        mRowDatas.add(orderInfo.nums + "???" + "/" + orderInfo.multiple + "???");
                        mRowDatas.add(Nums.formatDecimal(orderInfo.money, 3));
                        mRowDatas.add(Nums.formatDecimal(orderInfo.winMoney, 3));
                        mRowDatas.add(orderInfo.statusRemark);
                        mTableDatas.add(mRowDatas);
                    }
                    mLockTableView.setTableDatas(mTableDatas);
                    if (listOrders.size() >= totalCount) {
                        mLockTableView.getTableScrollView().setLoadingMoreEnabled(false);
//                        Toasts.show(ActGameRecord.this, "????????????????????????!");
                    } else {
                        mLockTableView.getTableScrollView().setLoadingMoreEnabled(true);
                    }
                } else {
                    Toasts.show(ActGameRecord.this, msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                if (page == 0) {
                    Dialogs.hideProgressDialog(ActGameRecord.this);
                }
                if (ActGameRecord.this.mXRecyclerView != null) {
                    ActGameRecord.this.mXRecyclerView.refreshComplete();
                    ActGameRecord.this.mXRecyclerView.loadMoreComplete();
                }
            }
        });


    }

    public void addTableHeader() {
        ArrayList<String> mfristData = new ArrayList<String>();

        //??????
        mfristData.add("??????");
        if (Config.custom_flag.equals(BaseConfig.FLAG_HETIANXIA)
                || Config.custom_flag.equals(BaseConfig.FLAG_ZHONGXIN)
                || Config.custom_flag.equals(BaseConfig.FLAG_JINDU))
            mfristData.add("?????????");
        else
            mfristData.add("??????");
        mfristData.add("??????/??????");
        mfristData.add("??????");
        mfristData.add("????????????");
        mfristData.add("??????/??????");
        mfristData.add("??????");
        mfristData.add("??????");
        mfristData.add("??????");

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
        Log.e("??????????????????", "???????????????" + Thread.currentThread());
        mLockTableView.setLockFristColumn(true) //?????????????????????
                .setLockFristRow(true) //?????????????????????
                .setMaxColumnWidth(100) //???????????????
                .setMinColumnWidth(100) //???????????????
                .setMinRowHeight(40)//???????????????
                .setMaxRowHeight(40)//???????????????
                .setTextViewSize(12) //?????????????????????
                .setFristRowBackGroudColor(R.color.colorPrimaryInverse)//???????????????
                .setTableHeadTextColor(R.color.black)//??????????????????
                .setTableContentTextColor(R.color.black)//?????????????????????
                .setNullableString("") //???????????????
                .setTableViewListener(new LockTableView.OnTableViewListener() {
                    @Override
                    public void onTableViewScrollChange(int x, int y) {
//                        Log.e("?????????","["+x+"]"+"["+y+"]");
                    }
                })//??????????????????????????????
                .setTableViewRangeListener(new LockTableView.OnTableViewRangeListener() {
                    @Override
                    public void onLeft(HorizontalScrollView view) {
//                        Log.e("????????????","??????????????????");
                    }

                    @Override
                    public void onRight(HorizontalScrollView view) {
//                        Log.e("????????????","??????????????????");
                    }
                })//??????????????????????????????
                .setOnLoadingListener(new LockTableView.OnLoadingListener() {
                    @Override
                    public void onRefresh(XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                        ActGameRecord.this.mXRecyclerView = mXRecyclerView;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("??????????????????", mTableDatas.toString());
                                currPage = -1;
                                updateTable();
                            }
                        }, 1000);
                    }

                    @Override
                    public void onLoadMore(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                        ActGameRecord.this.mXRecyclerView = mXRecyclerView;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateTable();
                            }
                        }, 1000);
                    }
                })
                .setOnCellLongClickListener(new LockTableView.OnCellLongClickListener() {
                    @Override
                    public void onCellLongClick(View item, int row, int column, String data) {

                        if (column == 0 || column == 2) {
                            ClipboardUtils.copyText(ActGameRecord.this, data);
                            Toasts.show(ActGameRecord.this, "?????????????????????", true);
                        }
                    }
                })
                .setOnItemClickListenter(new LockTableView.OnItemClickListenter() {
                    @Override
                    public void onItemClick(View item, int position) {
                        Log.e("????????????", position + "");
                        try {
                            ActLotteryDetail.launch(ActGameRecord.this, listOrders.get(position - 1));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setOnItemSeletor(R.color.dashline_color)//??????Item???????????????
                .show(); //????????????,?????????????????????
        mLockTableView.getTableScrollView().setPullRefreshEnabled(true);
        mLockTableView.getTableScrollView().setLoadingMoreEnabled(true);
        mLockTableView.getTableScrollView().setRefreshProgressStyle(ProgressStyle.BallRotate);
    }

}