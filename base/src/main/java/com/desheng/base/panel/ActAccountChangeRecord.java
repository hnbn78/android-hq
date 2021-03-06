package com.desheng.base.panel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.global.BaseConfig;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.AccountChangeRecordBean;
import com.desheng.base.model.LotteryOrderInfo;
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
 * Created by user on 2018/2/27.
 */

public class ActAccountChangeRecord extends AbAdvanceActivity {
    private static final int PAGE_SIZE = 20;

    public static void launch(Context act) {
        Intent itt = new Intent(act, ActAccountChangeRecord.class);
        act.startActivity(itt);
    }

    private TextView Start_tv, End_tv;
    private Button Search_bt;
    private LinearLayout mContentView;
    private long startMills = 0;
    private long stopMills = 0;
    private int totalCount;
    private int currPage = -1;

    private RelativeLayout layout_AllAccount, layout_AllCategory;
    private ListMenuPopupWindow accountPopup = null;
    private ListMenuPopupWindow categoryPopup = null;
    private TextView tvAllAccount, tvAllCategory;


    private XRecyclerView mXRecyclerView;
    private List<LotteryOrderInfo> listOrders = new ArrayList<>();
    private ArrayList<ArrayList<String>> mTableDatas = new ArrayList<>();
    private LockTableView mLockTableView;
    private List<AccountChangeRecordBean> accountChangeRecordBeanList = new ArrayList<>();

    private String[] arrayAccount = {"????????????", "????????????"};
    private String[] arrayAccountValue = {"","1"};
    private String[] arrayCategory;
    private String[] arrayCategoryNormal = {"??????", "????????????", "??????", "??????", "????????????", "??????", "??????"};
    private String[] arrayCategoryAgent = {"??????", "????????????", "??????", "??????", "??????", "????????????", "??????", "??????"};
    private String[] arrayCategoryValue;
    private String[] arrayCategoryValueNormal = {"","7","90","3","80","1","2"};
    private String[] arrayCategoryValueAgent  = {"","7","90","91","3","80","1","2"};
    private String zbType="", accountType="";

    @Override
    protected int getLayoutId() {
        return R.layout.act_account_change_record;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "????????????");
        setStatusBarTranslucentAndLightContentWithPadding();
        initView();
        initLockTable();
        updateTable();
    }

    public void updateTable() {
        currPage++;
        search(zbType, accountType, startMills, stopMills, currPage, PAGE_SIZE);
    }

    private void initView() {
        if (UserManager.getIns().isAgent()) {
            arrayCategory = arrayCategoryAgent;
            arrayCategoryValue = arrayCategoryValueAgent;
        } else {
            arrayCategory = arrayCategoryNormal;
            arrayCategoryValue = arrayCategoryValueNormal;
        }

        accountPopup = new ListMenuPopupWindow(ActAccountChangeRecord.this, AbDevice.SCREEN_WIDTH_PX, arrayAccount);
        categoryPopup = new ListMenuPopupWindow(ActAccountChangeRecord.this, AbDevice.SCREEN_WIDTH_PX, arrayCategory);

        String currDate = Dates.getStringByFormat(System.currentTimeMillis(), Dates.dateFormatYMD);
        startMills = Dates.getMillisOfStr(currDate, Dates.dateFormatYMD);
        mContentView = (LinearLayout) findViewById(R.id.contentView);
        layout_AllAccount = (RelativeLayout) findViewById(R.id.layout_AllAccount);
        layout_AllCategory = (RelativeLayout) findViewById(R.id.layout_AllCategory);
        Start_tv = (TextView) findViewById(R.id.tvStartDate);
        Start_tv.setText(currDate);
        Start_tv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                showDateDialog(Start_tv, Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
            }
        });
        tvAllAccount = (TextView) findViewById(R.id.tvAllAccount);
        tvAllCategory = (TextView) findViewById(R.id.tvAllCategory);
        End_tv = (TextView) findViewById(R.id.tvEndDate);
        End_tv.setText(currDate);
        stopMills = Dates.getMillisOfStr(currDate, Dates.dateFormatYMD) + 24 * 60 * 60 * 1000 - 1000;
        End_tv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                showDateDialog(End_tv, Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
            }
        });
        Search_bt = (Button) findViewById(R.id.btnSearch);
        Search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currPage = -1;
                listOrders.clear();
                mTableDatas.clear();
                mLockTableView.setTableDatas(mTableDatas);
                updateTable();
            }
        });

        layout_AllAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountPopup.showAsDropDown(layout_AllAccount, 0, 0);//?????????rl_spinner?????????
            }
        });

        layout_AllCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryPopup.showAsDropDown(layout_AllCategory, 0, 0);//?????????rl_spinner?????????
            }
        });

        categoryPopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {
                zbType = arrayCategoryValue[position];
                tvAllCategory.setText(arrayCategory[position]);
//                currPage = -1;
//                listOrders.clear();
//                mTableDatas.clear();
//                mLockTableView.setTableDatas(mTableDatas);
            }
        });

        accountPopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {
                accountType = arrayAccountValue[position];
                tvAllAccount.setText(arrayAccount[position]);

//                currPage = -1;
//                listOrders.clear();
//                mTableDatas.clear();
//                mLockTableView.setTableDatas(mTableDatas);
            }
        });

    }


    /**
     * ????????????
     */
    public void search(String zbType, String accountType, long sTime, long eTime, final int page, int size) {
        String startTime = Dates.getStringByFormat(sTime, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(eTime, Dates.dateFormatYMDHMS);

        HttpAction.getAccountChange(ActAccountChangeRecord.this, "", zbType, accountType, startTime, stopTime, page, size, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                if (page == 0) {
                    Dialogs.showProgressDialog(ActAccountChangeRecord.this, "?????????...");
                }
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespAccountChange.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                HttpAction.RespAccountChange respAccountChange = getField(extra, "data", null);
                if (respAccountChange != null) {
                    mTableDatas.clear();
                    addTableHeader();
                    if (page == 0) {
                        accountChangeRecordBeanList.clear();
                    }
                    totalCount = respAccountChange.totalCount;
                    accountChangeRecordBeanList.addAll(respAccountChange.list);
                    for (int i = 0; i < accountChangeRecordBeanList.size(); i++) {
                        ArrayList<String> mRowDatas = new ArrayList<String>();
                        AccountChangeRecordBean bean = accountChangeRecordBeanList.get(i);
                        mRowDatas.add(bean.cn);
                        mRowDatas.add(bean.spsn);
                        mRowDatas.add(bean.createTime);
                        mRowDatas.add(bean.changeTypeStr);
                        HashMap<String, String> mapDetail = new HashMap<>();
                        mapDetail.put("20", "????????????");
                        mapDetail.put("0", "????????????");
                        String detaiStr = "";
                        if (Strs.isEmpty(bean.changeTypeDetailStr)) {
                            detaiStr = mapDetail.get(bean.changeDetailType);
                        } else {
                            detaiStr = bean.changeTypeDetailStr;
                        }
                        if(Config.custom_flag.equals(BaseConfig.FLAG_QINGFENG)){
                            if(detaiStr.equals("????????????")){
                                detaiStr="????????????";
                            }else if(detaiStr.equals("????????????")){
                                detaiStr="????????????";
                            }
                        }
                        mRowDatas.add(detaiStr);
                        mRowDatas.add(bean.optType == 2 ? "-" + Nums.formatDecimal(bean.amount, 3) : Nums.formatDecimal(bean.amount, 3));
                        mRowDatas.add(Nums.formatDecimal(bean.point, 3));
                        String note_detail=bean.note;
                        if(Config.custom_flag.equals(BaseConfig.FLAG_QINGFENG)){
                            if(bean.note.equals("????????????")){
                                note_detail="????????????";
                            }else if(bean.note.equals("????????????")){
                                note_detail="????????????";
                            }
                        }
                        mRowDatas.add(note_detail);
                        mTableDatas.add(mRowDatas);
                    }
                    mLockTableView.setTableDatas(mTableDatas);
                    if (accountChangeRecordBeanList.size() >= totalCount) {
                        mLockTableView.getTableScrollView().setLoadingMoreEnabled(false);
//                        Toasts.show(ActAccountChangeRecord.this, "????????????????????????!");//????????????????????????????????????ANR
                    } else {
                        mLockTableView.getTableScrollView().setLoadingMoreEnabled(true);
                    }
                } else {
                    Toasts.show(ActAccountChangeRecord.this, msg, false);
                }
                return true;
            }

            @Override
            public void onAfter(int id) {
                if (page == 0) {
                    Dialogs.hideProgressDialog(ActAccountChangeRecord.this);
                }
                if (ActAccountChangeRecord.this.mXRecyclerView != null) {
                    ActAccountChangeRecord.this.mXRecyclerView.refreshComplete();
                    ActAccountChangeRecord.this.mXRecyclerView.loadMoreComplete();
                }
            }
        });
    }

    public void addTableHeader() {
        ArrayList<String> mfristData = new ArrayList<String>();

        //??????
        mfristData.add("?????????");
        mfristData.add("?????????");
        mfristData.add("??????");
        mfristData.add("????????????");
        mfristData.add("????????????");
        mfristData.add("????????????");
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
                        if (view == Start_tv) {
                            startMills = Dates.getMillisOfStr(text, Dates.dateFormatYMD);
                            Start_tv.setText(text);
                        } else if (view == End_tv) {
                            stopMills = Dates.getMillisOfStr(text, Dates.dateFormatYMD) + 24 * 60 * 60 * 1000 - 1000;
                            End_tv.setText(text);
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
        mLockTableView.setColumnWidth(7, 150);
        Log.e("??????????????????", "???????????????" + Thread.currentThread());
        mLockTableView.setLockFristColumn(true) //?????????????????????
                .setLockFristRow(true) //?????????????????????
                .setMaxColumnWidth(90) //???????????????
                .setMinColumnWidth(90) //???????????????
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
                        ActAccountChangeRecord.this.mXRecyclerView = mXRecyclerView;
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
                        ActAccountChangeRecord.this.mXRecyclerView = mXRecyclerView;
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
                        Log.e("????????????", position + "");
                    }
                })
                .setOnItemLongClickListenter(new LockTableView.OnItemLongClickListenter() {
                    @Override
                    public void onItemLongClick(View item, int position) {
                        Log.e("????????????", position + "");
                    }
                })
                .setOnItemSeletor(R.color.dashline_color)//??????Item???????????????
                .show(); //????????????,?????????????????????
        mLockTableView.getTableScrollView().setPullRefreshEnabled(true);
        mLockTableView.getTableScrollView().setLoadingMoreEnabled(true);
        mLockTableView.getTableScrollView().setRefreshProgressStyle(ProgressStyle.BallRotate);


    }


}