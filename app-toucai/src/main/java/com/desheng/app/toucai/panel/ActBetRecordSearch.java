package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.global.AbDevice;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.adapter.BetRecordAdapter;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.model.LotteryInfo;
import com.desheng.base.model.LotteryOrderInfo;
import com.desheng.base.model.ThirdGameBean;
import com.desheng.base.model.ThirdGameType;
import com.desheng.base.panel.ActThirdGameRecord;
import com.desheng.base.view.ListMenuPopupWindow;
import com.desheng.base.view.SpinnerListAdapter;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.SpaceTopDecoration;
import com.pearl.view.mypicker.DatePickerDialog;
import com.pearl.view.mypicker.DateUtil;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * 投注记录
 */
public class ActBetRecordSearch extends AbAdvanceActivity {

    public static void launch(Activity ctx) {
        simpleLaunch(ctx, ActBetRecordSearch.class);
    }

    private static final int PAGE_SIZE = 20;
    private TextView tvStartDate, tvEndDate;
    private TextView btnFastChoose;
    private RelativeLayout vgAllLottery;
    private RelativeLayout vgAllState, vgGameType;
    private ArrayList<LotteryInfo> listAllLottery;

    private String arrayStatus[] = new String[]{"全部", "未开奖", "已撤销", "未中奖", "已派奖", "系统撤销"};
    private TextView tvAllLottery, tvGameType, tvLotteryName, tvGameName;
    private TextView tvAllState, tvStateName;
    private static final String REQUEST_TYPE_THIRD = "third";
    private static final String REQUEST_TYPE_CP = "cp";
    private static final String REQUEST_TYPE_VR_TC = "vr_tc";
    private static final String REQUEST_TYPE_VR = "vr";
    private static final String REQUEST_TYPE_DS = "gm";
    private static final String REQUEST_TYPE_PT = "pt";

    public static final String GAME_TYPE_ALL = "all";
    public static final String GAME_TYPE_CP = "cp";
    public static final String THIRD_TYPE_KY = "ky";
    public static final String THIRD_TYPE_IM = "im";
    public static final String THIRD_TYPE_AGIN = "agin";
    public static final String THIRD_TYPE_VR = "vr";
    public static final String THIRD_TYPE_DS = "gm";
    public static final String THIRD_TYPE_PT = "pt";
    private String arrayGameType[] = {"彩票", "KY棋牌", "AGIN", "IM体育", "VR游戏", "DS棋牌","PT电子"};
    private String arrayGameTypeKeys[] = {GAME_TYPE_CP, THIRD_TYPE_KY, THIRD_TYPE_AGIN, THIRD_TYPE_IM, THIRD_TYPE_VR, THIRD_TYPE_DS,THIRD_TYPE_PT};

    private Map<String, List<ThirdGameType>> gameMap;
    private ArrayList<ThirdGameType> thirdGameIMList;
    private ArrayList<ThirdGameType> thirdGameKYList;
    private ArrayList<ThirdGameType> thirdGameAGList;
    private ArrayList<ThirdGameType> thirdGameDSList;
    private ArrayList<ThirdGameType> thirdGamePTList;
    private ArrayList<ThirdGameType> lotteryGameTypeList;
    private List<ThirdGameType> vrGameTypeList;

    private String arrayGames[] = null;

    private ListMenuPopupWindow statePopup = null;
    private ListMenuPopupWindow dateChoosePopup = null;
    private ListMenuPopupWindow gamePopup = null;
    private ListMenuPopupWindow lotteryPopup = null;

    private String arrayDateLength[] = {"今日", "三日", "七日"};

    private long startMills = 0;
    private long stopMills = 0;
    private int currPage = 0;

    private Integer status;
    private String gameCode;
    private String lotteryType;
    private String dateLength;

    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvDeposit;
    private BetRecordAdapter mAdapter;
    private List<LotteryOrderInfo> listOrderData = new ArrayList<>();
    private LinearLayout layout_search_view;
    private TextView tv_search;

    private int date_length = 0;
    private String request_type = REQUEST_TYPE_CP;
    private String gameType;
    private View emptyView;
    private View layout_amount;
    private String beginDate, overDate;
    private TextView tvAwardAmount;
    private TextView tvConfirmAmount;
    private TextView tvbetAmount;

    @Override
    protected int getLayoutId() {
        return R.layout.act_betting_record_search;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "投注记录");
        setStatusBarTranslucentAndLightContentWithPadding();
        emptyView = LayoutInflater.from(this).inflate(R.layout.empty_view, null);
        layout_amount = findViewById(R.id.layout_amount);
        initView();
        getOpenLotteries();
    }

    /**
     * 初始化所有控件
     */
    private void initView() {
        setToolbarButtonRightText("取消");
        setToolbarButtonRightTextColor(R.color.white);

        gameMap = new HashMap<>();
        thirdGameIMList = new ArrayList<>();
        thirdGameKYList = new ArrayList<>();
        thirdGameAGList = new ArrayList<>();
        thirdGameDSList = new ArrayList<>();
        thirdGamePTList = new ArrayList<>();
        lotteryGameTypeList = new ArrayList<>();
        vrGameTypeList = new ArrayList<>();

        ThirdGameType gameType = new ThirdGameType();
        gameType.setKey("VR金星1.5分彩");
        gameType.setValue("VR金星1.5分彩");
        gameType.setType(REQUEST_TYPE_VR);
        ThirdGameType gameType2 = new ThirdGameType();
        gameType2.setKey("天彩VR");
        gameType2.setValue("天彩VR");
        gameType2.setType(REQUEST_TYPE_VR_TC);
        vrGameTypeList.add(gameType);
        vrGameTypeList.add(gameType2);

        setToolbarRightButtonGroupClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_search_view.getVisibility() == View.VISIBLE) {
                    finish();
                } else {
                    layout_search_view.setVisibility(View.VISIBLE);
                    //layout_amount.setVisibility(View.GONE);
                    setToolbarButtonRightText("取消");
                }
            }
        });

        layout_search_view = findViewById(R.id.layout_search_view);
        srlRefresh = findViewById(R.id.srlRefresh);
        rvDeposit = findViewById(R.id.rvFund);
        tv_search = findViewById(R.id.tv_search);

        srlRefresh.setColorSchemeResources(com.desheng.base.R.color.colorPrimary, com.desheng.base.R.color.colorPrimaryInverse);
        rvDeposit.setLayoutManager(Views.genLinearLayoutManagerV(this));
        rvDeposit.addItemDecoration(new SpaceTopDecoration(7));

        mAdapter = new BetRecordAdapter(this, listOrderData);
        rvDeposit.setAdapter(mAdapter);

        mAdapter.setEnableLoadMore(true);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                currPage++;
                String startTime = Dates.getStringByFormat(startMills, Dates.dateFormatYMD);
                String stopTime = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
                startTime = startTime + " 00:00:00";
                onLoadData(startTime, stopTime);
            }
        }, rvDeposit);

        tvAwardAmount = findViewById(R.id.tv_awardAmount);
        tvbetAmount = findViewById(R.id.tv_betAmount);
        tvConfirmAmount = findViewById(R.id.tv_confirmAmount);
        tvAllLottery = findViewById(R.id.tvAllLottery);
        tvLotteryName = findViewById(R.id.tvLotteryName);
        tvGameName = findViewById(R.id.tvGameName);
        tvGameType = findViewById(R.id.tvGameType);
        tvAllState = findViewById(R.id.tvAllState);
        tvStateName = findViewById(R.id.tvStateName);
        vgAllState = findViewById(R.id.vgAllState);
        vgAllLottery = findViewById(R.id.vgOrderType);
        vgGameType = findViewById(R.id.vgGameType);
        btnFastChoose = findViewById(R.id.btnFastChoose);

        initPopup();

        String currDate = Dates.getStringByFormat(System.currentTimeMillis(), Dates.dateFormatYMD);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvStartDate.setText(currDate);
        startMills = Dates.getMillisOfStr(currDate, Dates.dateFormatYMD);

        tvEndDate = findViewById(R.id.tvEndDate);
        tvEndDate.setText(currDate);
        stopMills = Dates.getMillisOfStr(currDate, Dates.dateFormatYMD) + 24 * 60 * 60 * 1000 - 1000;

        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        btnFastChoose.setOnClickListener(this);
        vgAllState.setOnClickListener(this);
        vgGameType.setOnClickListener(this);
        vgAllLottery.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        tvLotteryName.setText(arrayGameType[0]);
        overDate = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currPage = 0;
                onLoadData(beginDate, overDate);
            }
        });
    }

    private void initPopup() {
        dateChoosePopup = new ListMenuPopupWindow(ActBetRecordSearch.this, AbDevice.SCREEN_WIDTH_PX / 2, arrayDateLength);
        dateChoosePopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {
                dateLength = arrayDateLength[position];
                long currenttime = System.currentTimeMillis();
                if (mAdapter.getEmptyView() == null)
                    mAdapter.setEmptyView(emptyView);
                switch (position) {
                    case 0:
                        date_length = 1;
                        String start = Dates.getStringByFormat(currenttime, Dates.dateFormatYMD);
                        tvStartDate.setText(start);
                        start = start + " 00:00:00";
                        startMills = Dates.getMillisOfStr(start, Dates.dateFormatYMDHMS);
                        beginDate = start;
                        break;
                    case 1:
                        date_length = 2;
                        String startTime = Dates.getStringByFormat(currenttime - date_length * 24 * 60 * 60 * 1000, Dates.dateFormatYMD);
                        tvStartDate.setText(startTime);
                        startTime = startTime + " 00:00:00";
                        startMills = Dates.getMillisOfStr(startTime, Dates.dateFormatYMDHMS);
                        beginDate = startTime;
                        break;
                    case 2:
                        date_length = 6;//当前的也算是一天
                        String startDate = Dates.getStringByFormat(currenttime - date_length * 24 * 60 * 60 * 1000, Dates.dateFormatYMD);
                        tvStartDate.setText(startDate);
                        startDate = startDate + " 00:00:00";
                        startMills = Dates.getMillisOfStr(startDate, Dates.dateFormatYMDHMS);
                        beginDate = startDate;
                        break;
                }

            }
        });

        statePopup = new ListMenuPopupWindow(ActBetRecordSearch.this, AbDevice.SCREEN_WIDTH_PX, arrayStatus);
        statePopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {
                if (position == 0) {
                    status = null;
                } else {
                    status = CtxLottery.getUserBetsStatus(arrayStatus[position]);
                }
                tvStateName.setText(arrayStatus[position]);
            }
        });

        lotteryPopup = new ListMenuPopupWindow(ActBetRecordSearch.this, AbDevice.SCREEN_WIDTH_PX, arrayGameType);

        getGameType(THIRD_TYPE_AGIN);
        getGameType(THIRD_TYPE_KY);
        getGameType(THIRD_TYPE_IM);
        getGameType(THIRD_TYPE_DS);
        getGameType(THIRD_TYPE_PT);

        lotteryPopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {

                lotteryType = arrayGameTypeKeys[position];
                tvLotteryName.setText(arrayGameType[position]);


                if (lotteryType.equals(GAME_TYPE_CP)) {
                    setGamePopup(gameMap.get(lotteryType));
                    String lotteryString = arrayGames[0];
                    tvGameName.setText(lotteryString);
                    request_type = REQUEST_TYPE_CP;
                } else if (lotteryType.equals(THIRD_TYPE_AGIN)
                        || lotteryType.equals(THIRD_TYPE_KY)
                        || lotteryType.equals(THIRD_TYPE_IM)
                        || lotteryType.equals(THIRD_TYPE_DS)
                        || lotteryType.equals(THIRD_TYPE_PT)) {

                    setGamePopup(gameMap.get(lotteryType));
                    String lotteryString = arrayGames[0];
                    tvGameName.setText(lotteryString);
                    request_type = REQUEST_TYPE_THIRD;
                } else if (lotteryType.equals(THIRD_TYPE_VR)) {
                    setGamePopup(vrGameTypeList);
                    String lotteryString = arrayGames[0];
                    tvGameName.setText(lotteryString);
                    request_type = vrGameTypeList.get(0).getType();
                } else if (lotteryType.equals(GAME_TYPE_ALL)) {

                    List<ThirdGameType> allGameTypeList = new ArrayList<>();
                    allGameTypeList.addAll(thirdGameAGList);
                    allGameTypeList.addAll(thirdGameIMList);
                    allGameTypeList.addAll(thirdGameKYList);
                    allGameTypeList.addAll(thirdGameDSList);
                    allGameTypeList.addAll(thirdGamePTList);
                    allGameTypeList.addAll(lotteryGameTypeList);
                    allGameTypeList.addAll(vrGameTypeList);

                    setGamePopup(allGameTypeList);
                    String lotteryString = arrayGames[0];
                    tvGameName.setText(lotteryString);
                    request_type = allGameTypeList.get(0).getType();
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == vgGameType) {
            if (gamePopup != null) {
                gamePopup.showAsDropDown(vgGameType, 0, 0);//显示在rl_spinner的下方
            }
        } else if (v == vgAllState) {
            if (statePopup != null) {
                statePopup.showAsDropDown(vgAllState, 0, 0);
            }
        } else if (v == vgAllLottery) {
            if (lotteryPopup != null) {
                lotteryPopup.showAsDropDown(vgAllLottery, 0, 0);
            }
        } else if (v == tvStartDate) {
            Calendar calendar = Calendar.getInstance();
            showDateDialog(tvStartDate, Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
        } else if (v == tvEndDate) {
            Calendar calendar = Calendar.getInstance();
            showDateDialog(tvEndDate, Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
        } else if (v == btnFastChoose) {
            if (dateChoosePopup != null) {
                dateChoosePopup.showAsDropDown(btnFastChoose, 0, 0);
            }
        } else if (v == tv_search) {
            if (stopMills < startMills) {
                Toasts.show("结束时间不能小于起始时间", false);
                return;
            }

            if (Dates.getOffsetDay(startMills, stopMills) > 7) {
                Toasts.show("查询的时间跨度不能超过7天", false);
                return;
            }

            if (request_type.equals(REQUEST_TYPE_CP)) {
                String start = Dates.getStringByFormat(startMills, Dates.dateFormatYMD);
                start = start + " 00:00:00";
                String stop = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
                beginDate = start;
                overDate = stop;
                currPage = 0;
                searchBetRecord(start, stop);
            } else if (request_type.equals(REQUEST_TYPE_THIRD) || request_type.equals(REQUEST_TYPE_DS)|| request_type.equals(REQUEST_TYPE_PT)) {
                currPage = 0;
                doGetGameReport(startMills, stopMills);
            } else if (request_type.equals(REQUEST_TYPE_VR)) {
                currPage = 0;
                getVRRecord(startMills, stopMills);
            } else if (request_type.equals(REQUEST_TYPE_VR_TC)) {
                currPage = 0;
                getTianCaiVRRecord(startMills, stopMills);
            }
            if (mAdapter.getEmptyView() == null)
                mAdapter.setEmptyView(emptyView);
        }
    }

    private void onLoadData(String start, String stop) {
        switch (request_type) {
            case REQUEST_TYPE_CP:
                searchBetRecord(start, stop);
                break;
            case REQUEST_TYPE_THIRD:
                doGetGameReport(startMills, stopMills);
                break;
            case REQUEST_TYPE_VR:
                getVRRecord(startMills, stopMills);
                break;
            case REQUEST_TYPE_VR_TC:
                getTianCaiVRRecord(startMills, stopMills);
                break;
        }
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
                    List<ThirdGameType> thirdGameTypeList = getFieldObject(extra, "data", ArrayList.class);

                    if (thirdtype.equals(THIRD_TYPE_AGIN)) {
                        thirdGameAGList.addAll(thirdGameTypeList);
                    } else if (thirdtype.equals(THIRD_TYPE_IM)) {
                        thirdGameIMList.addAll(thirdGameTypeList);
                    } else if (thirdtype.equals(THIRD_TYPE_KY)) {
                        thirdGameKYList.addAll(thirdGameTypeList);
                    } else if (thirdtype.equals(THIRD_TYPE_DS)) {
                        thirdGameDSList.addAll(thirdGameTypeList);
                    } else if (thirdtype.equals(THIRD_TYPE_PT)) {
                        thirdGamePTList.addAll(thirdGameTypeList);
                    }

                    ThirdGameType gameTypeBean = new ThirdGameType();
                    gameTypeBean.setKey("");
                    gameTypeBean.setValue("全部游戏");
                    thirdGameTypeList.add(0, gameTypeBean);

                    if (thirdtype.equals(THIRD_TYPE_IM) && thirdGameTypeList.size() == 0) {
                        ThirdGameType gameType = new ThirdGameType();
                        gameType.setValue("IM体育");
                        gameType.setKey(THIRD_TYPE_IM);
                        thirdGameTypeList.add(gameType);
                        thirdGameIMList.add(gameType);
                    }

                    for (int i = 0; i < thirdGameTypeList.size(); i++) {
                        thirdGameTypeList.get(i).setType(REQUEST_TYPE_THIRD);
                    }

                    gameMap.put(thirdtype, thirdGameTypeList);

                    setGamePopup(thirdGameTypeList);
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

    private void setGamePopup(List<ThirdGameType> gametypelist) {
        if (gametypelist == null) {
            return;
        }
        arrayGames = new String[gametypelist.size()];

        for (int i = 0; i < arrayGames.length; i++) {
            arrayGames[i] = gametypelist.get(i).getValue();
        }

        gamePopup = new ListMenuPopupWindow(ActBetRecordSearch.this, AbDevice.SCREEN_WIDTH_PX, arrayGames);

        gamePopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {
                String lotteryString = arrayGames[position];
                request_type = gametypelist.get(position).getType();
                if (position == 0) {
                    gameCode = null;
                } else {
                    gameCode = gametypelist.get(position).getKey();
                }

                gameType = gametypelist.get(position).getKey();

                tvGameName.setText(lotteryString);
            }
        });
    }


    public void getOpenLotteries() {
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
                    List<ThirdGameType> lotteryTypeList = new ArrayList<>();

                    for (int i = 0; i < listAllLottery.size(); i++) {
                        ThirdGameType gameType = new ThirdGameType();
                        gameType.setKey(listAllLottery.get(i).getCode());
                        gameType.setValue(listAllLottery.get(i).getShowName());
                        gameType.setType(REQUEST_TYPE_CP);
                        lotteryTypeList.add(gameType);
                        lotteryGameTypeList.add(gameType);
                    }

                    ThirdGameType gameTypeBean = new ThirdGameType();
                    gameTypeBean.setKey("");
                    gameTypeBean.setValue("全部游戏");
                    gameTypeBean.setType(REQUEST_TYPE_CP);
                    lotteryTypeList.add(0, gameTypeBean);

                    gameMap.put("cp", lotteryTypeList);
                    lotteryType = arrayGameTypeKeys[0];
                    setGamePopup(gameMap.get(lotteryType));
                } else {
                    Toasts.show(ActBetRecordSearch.this, msg, false);
                }

                return true;
            }

            @Override
            public void onAfter(int id) {

            }
        });
    }

    public void searchBetRecord(String startTime, String stopTime) {
        HttpAction.searchOrderTouCai(ActBetRecordSearch.this, gameCode, status, lotteryType, startTime, stopTime, currPage, PAGE_SIZE, null, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                if (currPage == 0) {
                    Dialogs.showProgressDialog(ActBetRecordSearch.this, "搜索中...");
                    srlRefresh.setRefreshing(true);
                }
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespSearchOrder.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0) {
                    layout_search_view.setVisibility(View.GONE);
                    setToolbarButtonRightText("筛选");

                    HttpAction.RespSearchOrder respSearchOrder = getField(extra, "data", null);
                    if (respSearchOrder != null) {
                        if (currPage == 0) {
                            listOrderData.clear();
                        }

                        if (respSearchOrder.statistics != null) {
                            tvAwardAmount.setText(Nums.formatDecimal(respSearchOrder.statistics.awardAmount, 3));
                            tvbetAmount.setText(Nums.formatDecimal(respSearchOrder.statistics.betAmount, 3));
                            tvConfirmAmount.setText(Nums.formatDecimal(respSearchOrder.statistics.confirmAmount, 3));
                        }

                        listOrderData.addAll(respSearchOrder.list);

                        if (listOrderData.size() < respSearchOrder.totalCount) {
                            mAdapter.setEnableLoadMore(true);
                        } else {
                            mAdapter.setEnableLoadMore(false);
                            mAdapter.loadMoreEnd();
                        }

//                        if (listOrderData.size() == 0) {
//                            layout_amount.setVisibility(View.GONE);
//                        }

                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toasts.show(ActBetRecordSearch.this, msg, false);
                    }
                } else {
                    Toasts.show(ActBetRecordSearch.this, msg, false);
                }

                return true;
            }

            @Override
            public void onAfter(int id) {
                if (currPage == 0) {
                    Dialogs.hideProgressDialog(ActBetRecordSearch.this);
                }
                srlRefresh.setRefreshing(false);
                mAdapter.loadMoreComplete();
            }
        });
    }


    private void doGetGameReport(long sTime, long eTime) {
        String startTime = Dates.getStringByFormat(sTime, Dates.dateFormatYMD) + " 00:00:00";
        String stopTime = Dates.getStringByFormat(eTime, Dates.dateFormatYMDHMS);
        HttpActionTouCai.getThirdReport(null, gameType, startTime, stopTime, currPage, PAGE_SIZE, lotteryType, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.showProgressDialog(ActBetRecordSearch.this, "");
                    }
                });

            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", ThirdGameBean.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    ThirdGameBean thirdGameBean = getFieldObject(extra, "data", ThirdGameBean.class);
                    if (thirdGameBean != null) {
                        if (currPage == 0) {
                            listOrderData.clear();
                        }
                        List<ThirdGameBean.ListBean> list = thirdGameBean.getList();
                        for (int i = 0; i < thirdGameBean.getList().size(); i++) {
                            ThirdGameBean.ListBean bean = list.get(i);
                            LotteryOrderInfo info = new LotteryOrderInfo();
                            if (ActThirdGameRecord.THIRD_TYPE_KY.equals(lotteryType)) {
                                info.lottery = "KY棋牌";
                                //     info.issue = bean.getDate();
                                info.money = bean.getConfirmAmount();
                                //    info.winMoney = bean.getProfitAmount();
                                info.method = bean.getGameName();
                                info.statusRemark = bean.getProfitAmount() > 0 ? "赢" : "输";
                            } else if (ActThirdGameRecord.THIRD_TYPE_DS.equals(lotteryType)) {
                                info.lottery = "DS棋牌";
                                //     info.issue = bean.getDate();
                                info.money = bean.getConfirmAmount();
                                //    info.winMoney = bean.getProfitAmount();
                                info.method = bean.getGameName();
                                info.statusRemark = bean.getProfitAmount() > 0 ? "赢" : "输";
                            }else if (ActThirdGameRecord.THIRD_TYPE_PT.equals(lotteryType)) {
                                info.lottery = "PT电子";
                                //     info.issue = bean.getDate();
                                info.money = bean.getConfirmAmount();
                                //    info.winMoney = bean.getProfitAmount();
                                info.method = bean.getGameName();
                                info.statusRemark = bean.getProfitAmount() > 0 ? "赢" : "输";
                            }
                            listOrderData.add(info);
                        }

                        if (thirdGameBean.getStatistics() != null) {
                            tvAwardAmount.setText(Nums.formatDecimal(thirdGameBean.getStatistics().getAwardAmount(), 3));
                            tvbetAmount.setText(Nums.formatDecimal(thirdGameBean.getStatistics().getBetAmount(), 3));
                            tvConfirmAmount.setText(Nums.formatDecimal(thirdGameBean.getStatistics().getConfirmAmount(), 3));
                        }

                        layout_search_view.setVisibility(View.GONE);
                        //layout_amount.setVisibility(View.GONE);
                        mAdapter.setEnableLoadMore(listOrderData.size() < thirdGameBean.getTotalCount());
                        if (listOrderData.isEmpty()) {
                            clearData();
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toasts.show(ActBetRecordSearch.this, msg, false);
                    }
                }

                return true;
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                srlRefresh.setRefreshing(false);
                DialogsTouCai.hideProgressDialog(ActBetRecordSearch.this);
            }
        });
    }

    private void clearData() {
        layout_search_view.setVisibility(View.GONE);
        //layout_amount.setVisibility(View.GONE);
        listOrderData.clear();
        mAdapter.notifyDataSetChanged();
    }

    private void getVRRecord(long sTime, long eTime) {
        String startTime = Dates.getStringByFormat(sTime, Dates.dateFormatYMD) + " 00:00:00";
        String stopTime = Dates.getStringByFormat(eTime, Dates.dateFormatYMDHMS);
        HttpAction.getRecordVR(null, 1, "", startTime, stopTime, currPage, PAGE_SIZE, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.showProgressDialog(ActBetRecordSearch.this, "");
                    }
                });
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                clearData();
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(false);
                        DialogsTouCai.hideProgressDialog(ActBetRecordSearch.this);
                    }
                });

            }
        });
    }

    private void getTianCaiVRRecord(long sTime, long eTime) {

        String startTime = Dates.getStringByFormat(sTime, Dates.dateFormatYMD) + " 00:00:00";
        String stopTime = Dates.getStringByFormat(eTime, Dates.dateFormatYMDHMS);

        HttpAction.getTcVRReport(null, startTime, stopTime, currPage, PAGE_SIZE, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.showProgressDialog(ActBetRecordSearch.this, "");
                    }
                });
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                clearData();
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(false);
                        DialogsTouCai.hideProgressDialog(ActBetRecordSearch.this);
                    }
                });
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