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

import okhttp3.Request;

/**
 * 投注记录
 */
public class ActThirdGameRecordNew extends AbAdvanceActivity {

    public static void launch(Activity ctx) {
        simpleLaunch(ctx, ActThirdGameRecordNew.class);
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
    private static final String REQUEST_TYPE_VR_TC = "vr_tc";
    private static final String REQUEST_TYPE_VR = "vr";

    public static final String GAME_TYPE_ALL = "all";
    public static final String THIRD_TYPE_KY = "ky";
    public static final String THIRD_TYPE_IM = "im";
    public static final String THIRD_TYPE_AGIN = "agin";
    public static final String THIRD_TYPE_OG = "og";
    public static final String THIRD_TYPE_PT = "pt";
    public static final String THIRD_TYPE_CQ = "cq";
    public static final String THIRD_TYPE_LEG = "leg";
    public static final String THIRD_TYPE_BBIN = "bbin";
    public static final String THIRD_TYPE_DT = "dt";
    private String arrayGameType[] = {"KY棋牌", "AGIN视讯", "IM体育", "OG真人", "CQ9电游", "LEG棋牌", "BBIN真人", "PT电子", "大唐棋牌"};
    private String arrayGameTypeKeys[] = {THIRD_TYPE_KY, THIRD_TYPE_AGIN, THIRD_TYPE_IM, THIRD_TYPE_OG,
            THIRD_TYPE_CQ, THIRD_TYPE_LEG, THIRD_TYPE_BBIN, THIRD_TYPE_PT, THIRD_TYPE_DT};

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
    private String lotteryType = THIRD_TYPE_KY;
    private String dateLength;

    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvDeposit;
    private BetRecordAdapter mAdapter;
    private List<LotteryOrderInfo> listOrderData = new ArrayList<>();
    private LinearLayout layout_search_view;
    private TextView tv_search;

    private int date_length = 0;
    private String request_type;
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
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "第三方游戏报表");
        setStatusBarTranslucentAndLightContentWithPadding();
        emptyView = LayoutInflater.from(this).inflate(R.layout.empty_view, null);
        layout_amount = findViewById(R.id.layout_amount);
        initView();
    }

    /**
     * 初始化所有控件
     */
    private void initView() {
        setToolbarButtonRightText("取消");
        setToolbarButtonRightTextColor(R.color.white);

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

        vgAllState.setVisibility(View.GONE);

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
        dateChoosePopup = new ListMenuPopupWindow(ActThirdGameRecordNew.this, AbDevice.SCREEN_WIDTH_PX / 2, arrayDateLength);
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

        statePopup = new ListMenuPopupWindow(ActThirdGameRecordNew.this, AbDevice.SCREEN_WIDTH_PX, arrayStatus);
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

        getGameType(THIRD_TYPE_KY);//初始化

        lotteryPopup = new ListMenuPopupWindow(ActThirdGameRecordNew.this, AbDevice.SCREEN_WIDTH_PX, arrayGameType);
        lotteryPopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {
                lotteryType = arrayGameTypeKeys[position];
                tvLotteryName.setText(arrayGameType[position]);
                getGameType(lotteryType);
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

            currPage = 0;
            doGetGameReport(startMills, stopMills);

            if (mAdapter.getEmptyView() == null)
                mAdapter.setEmptyView(emptyView);
        }
    }

    private void onLoadData(String start, String stop) {
        switch (request_type) {
            case REQUEST_TYPE_THIRD:
                doGetGameReport(startMills, stopMills);
                break;
        }
    }


    private void getGameType(final String thirdtype) {
        HttpAction.getGameType(null, thirdtype, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActThirdGameRecordNew.this, "");
            }

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
                    if (thirdGameTypeList != null) {
                        ThirdGameType element = new ThirdGameType();
                        element.setValue("全部");
                        thirdGameTypeList.add(0, element);
                        setGamePopup(thirdGameTypeList);
                    }
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                Dialogs.hideProgressDialog(ActThirdGameRecordNew.this);
            }
        });
    }

    private void setGamePopup(List<ThirdGameType> gametypelist) {
        if (gametypelist == null) {
            return;
        }
        ArrayList<String> temp = new ArrayList<>();
        for (ThirdGameType thirdGameType : gametypelist) {
            temp.add(thirdGameType.getValue());
        }
        arrayGames = temp.toArray(new String[temp.size()]);
        gamePopup = new ListMenuPopupWindow(ActThirdGameRecordNew.this, AbDevice.SCREEN_WIDTH_PX, arrayGames);

        gamePopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
            @Override
            public void click(int position, View view) {
                String lotteryString = arrayGames[position];
                request_type = gametypelist.get(position).getType();
                gameType = gametypelist.get(position).getKey();
                tvGameName.setText(lotteryString);
            }
        });
    }


    private void doGetGameReport(long sTime, long eTime) {
        String startTime = Dates.getStringByFormat(sTime, Dates.dateFormatYMD) + " 00:00:00";
        String stopTime = Dates.getStringByFormat(eTime, Dates.dateFormatYMDHMS);
        HttpActionTouCai.getThirdReportNew(null, gameType, startTime, stopTime, currPage, PAGE_SIZE, lotteryType, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        DialogsTouCai.showProgressDialog(ActThirdGameRecordNew.this, "");
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
                            if (THIRD_TYPE_KY.equals(lotteryType)) {
                                info.lottery = "KY棋牌";
                                //     info.issue = bean.getDate();
                                info.money = bean.getConfirmAmount();
                                //    info.winMoney = bean.getProfitAmount();
                                info.method = bean.getGameName();
                                info.statusRemark = bean.getProfitAmount() > 0 ? "赢" : "输";
                            } else if (THIRD_TYPE_AGIN.equals(lotteryType)) {
                                info.lottery = "AGIN视讯";
                                //     info.issue = bean.getDate();
                                info.money = bean.getConfirmAmount();
                                //    info.winMoney = bean.getProfitAmount();
                                info.method = bean.getGameName();
                                info.statusRemark = bean.getProfitAmount() > 0 ? "赢" : "输";
                            } else if (THIRD_TYPE_IM.equals(lotteryType)) {
                                info.lottery = "IM体育";
                                //     info.issue = bean.getDate();
                                info.money = bean.getConfirmAmount();
                                //    info.winMoney = bean.getProfitAmount();
                                info.method = bean.getGameName();
                                info.statusRemark = bean.getProfitAmount() > 0 ? "赢" : "输";
                            } else if (THIRD_TYPE_OG.equals(lotteryType)) {
                                info.lottery = "OG真人";
                                //     info.issue = bean.getDate();
                                info.money = bean.getConfirmAmount();
                                //    info.winMoney = bean.getProfitAmount();
                                info.method = bean.getGameName();
                                info.statusRemark = bean.getProfitAmount() > 0 ? "赢" : "输";
                            } else if (THIRD_TYPE_CQ.equals(lotteryType)) {
                                info.lottery = "CQ9电游";
                                //     info.issue = bean.getDate();
                                info.money = bean.getConfirmAmount();
                                //    info.winMoney = bean.getProfitAmount();
                                info.method = bean.getGameName();
                                info.statusRemark = bean.getProfitAmount() > 0 ? "赢" : "输";
                            } else if (THIRD_TYPE_LEG.equals(lotteryType)) {
                                info.lottery = "LEG棋牌";
                                //     info.issue = bean.getDate();
                                info.money = bean.getConfirmAmount();
                                //    info.winMoney = bean.getProfitAmount();
                                info.method = bean.getGameName();
                                info.statusRemark = bean.getProfitAmount() > 0 ? "赢" : "输";
                            } else if (THIRD_TYPE_BBIN.equals(lotteryType)) {
                                info.lottery = "BBIN真人";
                                //     info.issue = bean.getDate();
                                info.money = bean.getConfirmAmount();
                                //    info.winMoney = bean.getProfitAmount();
                                info.method = bean.getGameName();
                                info.statusRemark = bean.getProfitAmount() > 0 ? "赢" : "输";
                            } else if (THIRD_TYPE_PT.equals(lotteryType)) {
                                info.lottery = "PT电子";
                                //     info.issue = bean.getDate();
                                info.money = bean.getConfirmAmount();
                                //    info.winMoney = bean.getProfitAmount();
                                info.method = bean.getGameName();
                                info.statusRemark = bean.getProfitAmount() > 0 ? "赢" : "输";
                            }else if (THIRD_TYPE_DT.equals(lotteryType)) {
                                info.lottery = "大唐棋牌";
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
                        mAdapter.setEnableLoadMore(listOrderData.size() < thirdGameBean.getTotalCount());
                        if (listOrderData.isEmpty()) {
                            clearData();
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toasts.show(ActThirdGameRecordNew.this, msg, false);
                    }
                }

                return true;
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                srlRefresh.setRefreshing(false);
                DialogsTouCai.hideProgressDialog(ActThirdGameRecordNew.this);
            }
        });
    }

    private void clearData() {
        layout_search_view.setVisibility(View.GONE);
        listOrderData.clear();
        mAdapter.notifyDataSetChanged();
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