package com.desheng.base.panel;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ab.global.AbDevice;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Nums;
import com.ab.util.Toasts;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.model.ThirdGameBean;
import com.desheng.base.model.ThirdGameType;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ActThirdGameRecord extends AbAdvanceActivity implements View.OnClickListener {

    private TextView tv_all_third, tv_all_game, tvStartDate, tvEndDate;
    private RadioGroup rgDate;
    private RadioButton rbYesterday, rbDay, rbWeek, rbMonth;
    private Button btnSearch;

    private LinearLayout contentView;
    private List<ThirdGameBean.ListBean> listGame = new ArrayList<>();
    private ArrayList<ArrayList<String>> mTableDatas = new ArrayList<>();
    private LockTableView mLockTableView;
    private int totalCount;
    private XRecyclerView mXRecyclerView;

    private long startMills = 0;
    private long stopMills = 0;
    private int currPage = 0;

    private String start;
    private String end;
    private Date currDate;
    private String gameType;

    public static final String THIRD_TYPE_KY = "ky";
    public static final String THIRD_TYPE_IM = "im";
    public static final String THIRD_TYPE_AGIN = "agin";
    public static final String THIRD_TYPE_DS = "gm";
    public static final String THIRD_TYPE_PT = "pt";
    private String current_third_type = "";
    private ListMenuPopupWindow gamePopup = null;
    private String arrayGames[] = null;
    private String arrayThird[] = {"KY??????", "AGIN", "IM??????","DS??????","PT??????"};
    private String arrayKeys[] = {THIRD_TYPE_KY, THIRD_TYPE_AGIN, THIRD_TYPE_IM,THIRD_TYPE_DS,THIRD_TYPE_PT};
    private ListMenuPopupWindow thirdPopup = null;
    private ArrayList<ThirdGameType> thirdGameTypeList;
    private ThirdGameBean thirdGameBean;

    public static void launch(Activity ctx) {
        simpleLaunch(ctx, ActThirdGameRecord.class);
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "?????????????????????");
        setStatusBarTranslucentAndLightContentWithPadding();

        currDate = new Date();
        start = Dates.getStringByFormat(currDate, Dates.dateFormatYMD);
        end = Dates.getStringByFormat(currDate, Dates.dateFormatYMDHMS);
        contentView = (LinearLayout) findViewById(R.id.contentView);
        tv_all_third = (TextView) findViewById(R.id.tv_all_third);
        rgDate = (RadioGroup) findViewById(R.id.rgDate);
        rbYesterday = (RadioButton) findViewById(R.id.rbYesterday);
        rbDay = (RadioButton) findViewById(R.id.rbToday);
        rbWeek = (RadioButton) findViewById(R.id.rbWeek);
        rbMonth = (RadioButton) findViewById(R.id.rbMonth);
        tv_all_game = (TextView) findViewById(R.id.tv_all_game);
        tvStartDate = (TextView) findViewById(R.id.tvStartDate);
        tvEndDate = (TextView) findViewById(R.id.tvEndDate);
        btnSearch = (Button) findViewById(R.id.btnSearch);

        thirdPopup = new ListMenuPopupWindow(ActThirdGameRecord.this, AbDevice.SCREEN_WIDTH_PX, arrayThird);
        initListener();

        tv_all_third.setText(arrayThird[0]);
        current_third_type= THIRD_TYPE_KY;
        gameType="";
        getGameType(current_third_type);
        getGameReport(gameType, start, end, current_third_type, currPage, 20);
    }

    private void initListener() {
        String currDateString = Dates.getStringByFormat(System.currentTimeMillis(), Dates.dateFormatYMD);

        tvStartDate.setText(currDateString);
        tvEndDate.setText(currDateString);

        startMills = Dates.getMillisOfStr(currDateString, Dates.dateFormatYMD);
        stopMills = Dates.getMillisOfStr(currDateString, Dates.dateFormatYMD) + 24 * 60 * 60 * 1000 - 1000;

        rgDate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rbToday) {
                    start = Dates.getStringByOffset(currDate, Dates.dateFormatYMD, Calendar.DAY_OF_YEAR, 0);
                    end = Dates.getStringByFormat(currDate, Dates.dateFormatYMD);
                    tvEndDate.setText(end);
                    tvStartDate.setText(start);
                    getGameReport(gameType, start, end, current_third_type, currPage, 10);
                } else if (checkedId == R.id.rbYesterday) {
                    start = Dates.getStringByOffset(currDate, Dates.dateFormatYMD, Calendar.DAY_OF_YEAR, -1);
                    end = Dates.getStringByOffset(currDate, Dates.dateFormatYMD, Calendar.DAY_OF_YEAR, -1);
                    tvEndDate.setText(end);
                    tvStartDate.setText(start);
                    getGameReport(gameType, start, end, current_third_type, currPage, 10);
                } else if (checkedId == R.id.rbWeek) {
                    start =Dates.getFirstDayOfWeek(Dates.dateFormatYMD);
                    end = Dates.getStringByFormat(currDate, Dates.dateFormatYMD);
                    tvStartDate.setText(start);
                    tvEndDate.setText(end);
                    getGameReport(gameType, start, end, current_third_type, currPage, 10);
                } else if (checkedId == R.id.rbMonth) {
                    start =Dates.getFirstDayOfMonth(Dates.dateFormatYMD);
                    end =  Dates.getStringByFormat(currDate, Dates.dateFormatYMD);
                    tvStartDate.setText(start);
                    tvEndDate.setText(end);
                    getGameReport(gameType, start, end, current_third_type, currPage, 10);
                }
            }
        });

        tv_all_game.setOnClickListener(this);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        tv_all_third.setOnClickListener(this);

        thirdPopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {
                String lotteryString = arrayThird[position];
                tv_all_third.setText(lotteryString);
                current_third_type = arrayKeys[position];
                getGameType(current_third_type);
            }
        });

        initLockTable();
    }

    @Override
    public void onClick(View v) {

        if (v == tv_all_third) {
            if (null != thirdPopup) {
                thirdPopup.showAsDropDown(tv_all_third, 0, 0);//?????????rl_spinner?????????
            }
        } else if (v == tv_all_game) {
            if (gamePopup != null) {
                gamePopup.showAsDropDown(tv_all_game, 0, 0);//?????????rl_spinner?????????
            }
        } else if (v == tvStartDate) {
            Calendar calendar = Calendar.getInstance();
            showDateDialog(tvStartDate, Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));

        } else if (v == tvEndDate) {
            Calendar calendar = Calendar.getInstance();
            showDateDialog(tvEndDate, Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));

        } else if (v == btnSearch) {

            getGameReport(gameType, start, end, current_third_type, currPage, 10);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_third_game_record;
    }


    private void initLockTable() {
        addTableHeader();
        mLockTableView = new LockTableView(this, contentView, mTableDatas);
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
                        ActThirdGameRecord.this.mXRecyclerView = mXRecyclerView;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("??????????????????", mTableDatas.toString());
                                currPage = 0;

                                getGameReport(gameType, start, end, current_third_type, currPage, 10);
                            }
                        }, 1000);
                    }

                    @Override
                    public void onLoadMore(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                        ActThirdGameRecord.this.mXRecyclerView = mXRecyclerView;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                currPage ++;

                                getGameReport(gameType, start, end, current_third_type, currPage, 10);

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


    public void addTableHeader() {
        ArrayList<String> mfristData = new ArrayList<String>();

        //??????
        mfristData.add("?????????");
        mfristData.add("????????????");
        mfristData.add("????????????");
        mfristData.add("??????");
        mfristData.add("??????");
        mfristData.add("????????????");
        mfristData.add("??????");
        mfristData.add("??????");

        mTableDatas.add(mfristData);
    }

    private void getGameType(final String thirdtype) {
        HttpAction.getGameType(null, thirdtype, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                super.setupEntity(entity);
                entity.putField("data", new TypeToken<ArrayList<ThirdGameType>>() {
                }.getType());

            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && getField(extra, "data", null) != null) {
                    thirdGameTypeList = getFieldObject(extra, "data", ArrayList.class);

                    ThirdGameType gameTypeBean= new ThirdGameType();
                    gameTypeBean.setKey("");
                    gameTypeBean.setValue("????????????");
                    thirdGameTypeList.add(0,gameTypeBean);

                    arrayGames = new String[thirdGameTypeList.size()];

                    for (int i = 0; i < arrayGames.length; i++) {
                        arrayGames[i] = thirdGameTypeList.get(i).getValue();
                    }

                    tv_all_game.setText(arrayGames[0]);
                    tv_all_game.setTag(0);
                    gamePopup = new ListMenuPopupWindow(ActThirdGameRecord.this, AbDevice.SCREEN_WIDTH_PX, arrayGames);

                    gamePopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
                        @Override
                        public void click(int position, View view) {
                            String lotteryString = arrayGames[position];
                            tv_all_game.setText(lotteryString);
                            tv_all_game.setTag(thirdGameTypeList.get(position).getKey());
                            gameType = thirdGameTypeList.get(position).getKey();
                        }
                    });
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onGetString(String str) {
                return super.onGetString(str);
            }

            @Override
            public boolean onError(int status, String content) {
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    /**
     * ???????????????????????????
     *
     * @param gameType
     * @param betDateBegin
     * @param betDateEnd
     * @param page
     * @param size
     */
    private void getGameReport(String gameType, String betDateBegin, String betDateEnd, String game, final int page, int size) {
        HttpAction.getThirdReport(null, gameType, betDateBegin, betDateEnd, page, size, game, new AbHttpResult() {

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                super.setupEntity(entity);
                entity.putField("data", ThirdGameBean.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0) {
                   thirdGameBean = getFieldObject(extra, "data",  ThirdGameBean.class);
                    if (thirdGameBean != null) {
                        mTableDatas.clear();
                        addTableHeader();
                        if (page == 0) {
                            listGame.clear();
                        }
                        totalCount = thirdGameBean.getTotalCount();
                        listGame.addAll(thirdGameBean.getList());
                        for (int i = 0; i < listGame.size(); i++) {
                            ArrayList<String> mRowDatas = new ArrayList<String>();
                            ThirdGameBean.ListBean listItemBean = listGame.get(i);

//                            mfristData.add("?????????");
//                            mfristData.add("????????????");
//                            mfristData.add("????????????");
//                            mfristData.add("??????");
//                            mfristData.add("??????");
//                            mfristData.add("????????????");
//                            mfristData.add("??????");
//                            mfristData.add("??????");

                            mRowDatas.add(listItemBean.getUserName());
                            mRowDatas.add(listItemBean.getGameName());

                            try {
                                mRowDatas.add(Dates.getStringByFormat(Long.parseLong(listItemBean.getDate()),Dates.dateFormatYMD));
                            }catch (NumberFormatException nfexception){
                                mRowDatas.add(listItemBean.getDate() );
                            }


                            mRowDatas.add(Nums.formatDecimal(listItemBean.getTransferInAmount(),3));
                            mRowDatas.add(Nums.formatDecimal(listItemBean.getTransferOutAmount(),3));
                            mRowDatas.add(Nums.formatDecimal(listItemBean.getConfirmAmount(),3));
                            mRowDatas.add(Nums.formatDecimal(listItemBean.getAwardAmount(),3));
                            mRowDatas.add(Nums.formatDecimal(listItemBean.getProfitAmount(), 3));
                            mTableDatas.add(mRowDatas);
                        }

                        mLockTableView.setTableDatas(mTableDatas);
                        if (listGame.size() >= totalCount) {
                            mLockTableView.getTableScrollView().setLoadingMoreEnabled(false);
//                            Toasts.show(ActThirdGameRecord.this, "????????????????????????!");
                        } else {
                            mLockTableView.getTableScrollView().setLoadingMoreEnabled(true);
                        }
                    } else {
                        Toasts.show(ActThirdGameRecord.this, msg, false);
                    }
                }

                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onGetString(String str) {
                return super.onGetString(str);
            }

            @Override
            public boolean onError(int status, String content) {
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (ActThirdGameRecord.this.mXRecyclerView != null) {
                    ActThirdGameRecord.this.mXRecyclerView.refreshComplete();
                    ActThirdGameRecord.this.mXRecyclerView.loadMoreComplete();
                }
            }
        });
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
                            end = Dates.getStringByFormat(stopMills,Dates.dateFormatYMD);
                        }

                        int days = -(int) ((stopMills - startMills) / Dates.DAY_MILLIS);
                        start = Dates.getStringByOffset(currDate, Dates.dateFormatYMD, Calendar.DAY_OF_YEAR, days);

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
