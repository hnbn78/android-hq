package com.desheng.app.toucai.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ab.global.AbDevice;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.model.GudongTeamBetHistoryListBean;
import com.desheng.app.toucai.model.GudongTeamBetHistoryMode;
import com.desheng.app.toucai.model.GudongTeamDetailBean;
import com.desheng.app.toucai.model.GudongTeamDetailListBean;
import com.desheng.app.toucai.panel.ActTouzhuDetal;
import com.desheng.app.toucai.util.DateTool;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.app.toucai.util.Utils;
import com.desheng.app.toucai.view.CustomPopWindow;
import com.desheng.base.view.ListMenuPopupWindow;
import com.desheng.base.view.SpinnerListAdapter;
import com.google.gson.reflect.TypeToken;
import com.pearl.view.mypicker.DatePickerDialog;
import com.pearl.view.mypicker.DateUtil;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class FragGudongIncomesTeamLotterHisory extends BasePageFragment implements View.OnClickListener {

    private static final int PAGE_SIZE = 10;
    private RecyclerView rcTeamList;
    private BaseQuickAdapter quickAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CustomPopWindow mListPopWindow;
    private TextView kuanselect;
    private TextView betSum;
    private TextView paijiangSun;
    private TextView fandianSum;
    private TextView tvStartDate;
    private TextView tvStopTime;
    private TextView tvUsername;
    private String mUsernameStr;
    private String overDate;
    private TextView tvsearch;
    List<GudongTeamBetHistoryListBean> mDatalist = new ArrayList<>();
    private View emptyLayout;

    public static FragGudongIncomesTeamLotterHisory newInstance() {
        FragGudongIncomesTeamLotterHisory fragment = new FragGudongIncomesTeamLotterHisory();
        return fragment;
    }

    @Override
    public int setContentView() {
        return R.layout.frag_gudongincomes_team_lotterhisory;
    }

    @Override
    public void initView(View root) {
        rcTeamList = root.findViewById(R.id.rcTeamDetail);
        kuanselect = root.findViewById(R.id.kuanselect);
        tvStartDate = root.findViewById(R.id.tvStartDate);
        tvStopTime = root.findViewById(R.id.tvStopTime);
        tvUsername = root.findViewById(R.id.tvUsername);
        emptyLayout = root.findViewById(R.id.emptyLayout);
        tvsearch = root.findViewById(R.id.tvsearch);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        betSum = root.findViewById(R.id.betSum);
        paijiangSun = root.findViewById(R.id.paijiangSun);
        fandianSum = root.findViewById(R.id.fandianSum);

        kuanselect.setOnClickListener(this);
        tvStartDate.setOnClickListener(this);
        tvStopTime.setOnClickListener(this);
        tvsearch.setOnClickListener(this);

        String currDate = Dates.getStringByFormat(System.currentTimeMillis(), Dates.dateFormatYMD);
        tvStartDate.setText(currDate);
        startMills = Dates.getMillisOfStr(currDate, Dates.dateFormatYMD);

        tvStopTime.setText(currDate);
        stopMills = Dates.getMillisOfStr(currDate, Dates.dateFormatYMD) + 24 * 60 * 60 * 1000 - 1000;

        overDate = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
        tvqihaoPopup = new ListMenuPopupWindow(mActivity, AbDevice.SCREEN_WIDTH_PX, DATApoparray);

        rcTeamList.setLayoutManager(new LinearLayoutManager(mActivity));

        quickAdapter = new BaseQuickAdapter<GudongTeamBetHistoryListBean, BaseViewHolder>(R.layout.item_team_touzhu_history, mDatalist) {

            @Override
            protected void convert(BaseViewHolder helper, GudongTeamBetHistoryListBean item) {
                if (item == null) {
                    return;
                }

                helper.setText(R.id.username, item.getCn());
                helper.setText(R.id.tv_Lottery_name, item.getLottery_name());
                helper.setText(R.id.tv_lottery_num, "第" + item.getIssue_no() + "期");
                helper.setText(R.id.tv_play_method1, item.getMethod_name());
                helper.setText(R.id.tv_lottery_amount, Nums.formatDecimal(item.getAmount(), 2));
                helper.setText(R.id.tv_lottery_profit, "+" + Nums.formatDecimal(item.getBonus(), 2));
                helper.setText(R.id.tv_lottery_status, item.getLottery_status());
                helper.setText(R.id.fandian, Nums.formatDecimal(item.getRebound_amount(), 2));
            }
        };
        rcTeamList.setAdapter(quickAdapter);

        quickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

                if (quickAdapter.getData().size() < PAGE_SIZE) {
                    quickAdapter.loadMoreEnd(true);
                } else {
                    rcTeamList.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPageIndex++;
                            String startTime = Dates.getStringByFormat(startMills, Dates.dateFormatYMDHMS);
                            String stopTime = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
                            getData(mUsernameStr, startTime, stopTime, mPageIndex, 10);
                        }
                    }, 1000);
                }

            }
        }, rcTeamList);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPageIndex = 0;
                String startTime = Dates.getStringByFormat(startMills, Dates.dateFormatYMD);
                String stopTime = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
                startTime = startTime + " 00:00:00";
                getData(mUsernameStr, startTime, stopTime, mPageIndex, 10);
            }
        });

        quickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ActTouzhuDetal.launch(mActivity, ((GudongTeamBetHistoryListBean) adapter.getData().get(position)).getOrder_item_id());
            }
        });

        //showPopListView();
    }

    String[] DATApoparray = {"今天", "三天", "七天"};


    @Override
    public void fetchData() {
        mPageIndex = 0;
        String startTime = Dates.getStringByFormat(startMills, Dates.dateFormatYMD);
        String stopTime = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
        startTime = startTime + " 00:00:00";
        getData(null, startTime, stopTime, mPageIndex, 10);
    }

    int mPageIndex = 0;

    private void getData(String cn, String createTime, String updateTime, int page, int size) {

        HttpActionTouCai.getTeamBetList(mActivity, cn, createTime, updateTime, page, size, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                DialogsTouCai.showProgressDialog(mActivity, "");
                emptyLayout.setVisibility(View.GONE);
                rcTeamList.setVisibility(View.VISIBLE);
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<GudongTeamBetHistoryMode>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    GudongTeamBetHistoryMode bean = getField(extra, "data", null);
                    swipeRefreshLayout.setRefreshing(false);

                    if (bean != null) {

                        if (page == 0) {
                            mDatalist.clear();
                            if (bean.getList().size()==0) {
                                emptyLayout.setVisibility(View.VISIBLE);
                                rcTeamList.setVisibility(View.GONE);
                            }
                        }

                        mDatalist.addAll(bean.getList());
                        quickAdapter.setNewData(mDatalist);

                        if (bean.getList().size() < PAGE_SIZE) {
                            quickAdapter.loadMoreEnd(true);
                        }

                        if (bean.getTotalList() != null && bean.getTotalList().size() > 0) {
                            GudongTeamBetHistoryMode.TotalListBean totalListBean = bean.getTotalList().get(0);
                            if (totalListBean != null) {
                            }
                            betSum.setText(Nums.formatDecimal(totalListBean.getTotal_amount(), 2));
                            paijiangSun.setText(Nums.formatDecimal(totalListBean.getTotal_bonus(), 2));
                            fandianSum.setText(Nums.formatDecimal(totalListBean.getTotal_rebound_amount(), 2));
                        }

                        //tvUsername.setText("");
                    }
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(mActivity, content, false);
                swipeRefreshLayout.setRefreshing(false);
                return true;
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                DialogsTouCai.hideProgressDialog(mActivity);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private ListMenuPopupWindow tvqihaoPopup = null;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.kuanselect:
                if (tvqihaoPopup != null) {
                    tvqihaoPopup.showAsDropDown(kuanselect, 0, 0);//显示在vgType的下方
                    tvqihaoPopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
                        @Override
                        public void click(int position, View view) {
                            kuanselect.setText(DATApoparray[position]);
                            mPageIndex = 0;
                            String startTime = "";//快选时间
                            String stopTime = "";//快选时间
                            if (position == 0) {
                                long time1 = new Date().getTime();

                                startTime = Dates.getStringByFormat(time1, Dates.dateFormatYMD);
                                tvStartDate.setText(startTime);

                                tvStopTime.setText(Dates.getStringByFormat(new Date(), Dates.dateFormatYMD));
                                stopTime = Dates.getStringByFormat(new Date().getTime(), Dates.dateFormatYMDHMS);
                                startTime = startTime + " 00:00:00";

                            } else if (position == 1) {
                                long time3 = new Date().getTime() - 2 * 24 * 60 * 60 * 1000;
                                startTime = Dates.getStringByFormat(time3, Dates.dateFormatYMD);
                                tvStartDate.setText(startTime);
                                tvStopTime.setText(Dates.getStringByFormat(new Date(), Dates.dateFormatYMD));
                                stopTime = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
                                startTime = startTime + " 00:00:00";
                            } else if (position == 2) {
                                long time7 = new Date().getTime() - 6 * 24 * 60 * 60 * 1000;
                                startTime = Dates.getStringByFormat(time7, Dates.dateFormatYMD);
                                tvStartDate.setText(startTime);
                                tvStopTime.setText(Dates.getStringByFormat(new Date(), Dates.dateFormatYMD));
                                stopTime = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
                                startTime = startTime + " 00:00:00";
                            }

                            startMills = Dates.getMillisOfStr(startTime, Dates.dateFormatYMDHMS);
                            stopMills = Dates.getMillisOfStr(stopTime, Dates.dateFormatYMDHMS);

                            getData(mUsernameStr, startTime, stopTime, mPageIndex, 10);
                        }
                    });
                }
                break;
            case R.id.tvStartDate:
                Calendar calendar = Calendar.getInstance();
                showDateDialog(tvStartDate, Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
                break;
            case R.id.tvStopTime:
                Calendar calendar1 = Calendar.getInstance();
                showDateDialog(tvStopTime, Arrays.asList(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH) + 1, calendar1.get(Calendar.DAY_OF_MONTH)));
                break;
            case R.id.tvsearch:
                mUsernameStr = tvUsername.getText().toString().trim();

                if (stopMills < startMills) {
                    Toasts.show("结束时间不能小于起始时间", false);
                    return;
                }

                mPageIndex = 0;
                String startTime = Dates.getStringByFormat(startMills, Dates.dateFormatYMD);
                String stopTime = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
                startTime = startTime + " 00:00:00";
                getData(mUsernameStr, startTime, stopTime, mPageIndex, 10);
                break;
        }
    }

    private long startMills = 0;
    private long stopMills = 0;

    private void showDateDialog(final View view, List<Integer> date) {
        DatePickerDialog.Builder builder = new DatePickerDialog.Builder(mActivity);
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
                        } else if (view == tvStopTime) {
                            stopMills = Dates.getMillisOfStr(text, Dates.dateFormatYMD) + 24 * 60 * 60 * 1000 - 1000;
                            tvStopTime.setText(text);
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
