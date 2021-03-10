package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.AbDevice;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.AbDateUtil;
import com.ab.util.Dates;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.model.BonusPoolChoujiangMode;
import com.desheng.app.toucai.model.ChoujiangCodeBean;
import com.desheng.app.toucai.util.UIHelperTouCai;
import com.desheng.base.view.ListMenuPopupWindow;
import com.desheng.base.view.SpinnerListAdapter;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.view.mypicker.DatePickerDialog;
import com.pearl.view.mypicker.DateUtil;
import com.shark.tc.R;
import com.zhy.view.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ActChoujiangListDetail extends AbAdvanceActivity implements SwipeRefreshLayout.OnRefreshListener {
    static final String[] statusArray = {"未开奖", "已中奖", "未中奖"};
    static final int[] statusColorArray = {R.color.gray, R.color.red, R.color.blue_mine};
    static final String[] popstatusArray = {"全部", "未开奖", "已中奖", "未中奖"};
    static final String[] qihaoArray = {"全部", "近一期", "近二期", "近三期"};
    private static final int PAGE_SIZE = 20;
    List<BonusPoolChoujiangMode.ListBean> mlistData = new ArrayList<>();
    private TextView tv_title;
    private SwipeRefreshLayout srlAwardCodeRefresh;
    private RecyclerView rvAwardCodelist;
    private AdapterOpenLottery adapter;
    private TextView tvqihao;
    private EditText tvCode;
    private TextView tvstatus;
    private TextView tvStartDate;
    private TextView tvEndDate;
    private Button tv_search;
    private LinearLayout vgType;
    private LinearLayout layout_search_view;
    private String beginDate, overDate;
    private String activityIssueNo;
    private ListMenuPopupWindow tvqihaoPopup = null;
    private ListMenuPopupWindow tvstatusPopup = null;
    private int currPage = 0;
    private String codeStatus = null;
    private String qihaoCode = null;
    private String codeTianxie = null;
    private long startMills = 0;
    private long stopMills = 0;
    private String activityId;
    private View emptyView;

    public static void launch(Activity act) {
        Intent intent = new Intent(act, ActChoujiangListDetail.class);
        act.startActivity(intent);
    }

    public static void launch(Activity act, String activityId, String activityIssueNo) {
        Intent intent = new Intent(act, ActChoujiangListDetail.class);
        intent.putExtra("activityIssueNo", activityIssueNo);
        intent.putExtra("activityId", activityId);
        act.startActivity(intent);
    }

    @Override
    public void init() {
        setStatusBarTranslucentAndLightContentWithPadding();
        UIHelperTouCai.getIns().simpleToolbarLeftBackAndCenterTitleAndeRightTitle(this, getToolbar(),
                "奖池号码列表", "搜索");
        initView();
        setToolbarRightButtonGroupClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_search_view != null && layout_search_view.getVisibility() == View.VISIBLE) {
                    layout_search_view.setVisibility(View.GONE);
                } else {
                    layout_search_view.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_awardpopl_list;
    }

    public void initView() {
        emptyView = LayoutInflater.from(this).inflate(R.layout.empty_view, null);
        activityIssueNo = getIntent().getStringExtra("activityIssueNo");
        activityId = getIntent().getStringExtra("activityId");
        //Log.e("ActChoujiangListDetail", activityIssueNo);
        srlAwardCodeRefresh = (SwipeRefreshLayout) findViewById(R.id.srlAwardCodeRefresh);
        tvqihao = ((TextView) findViewById(R.id.tvqihao));
        tvCode = ((EditText) findViewById(R.id.tvCode));
        tvstatus = ((TextView) findViewById(R.id.tvstatus));
        tvStartDate = ((TextView) findViewById(R.id.tvStartDate));
        tvEndDate = ((TextView) findViewById(R.id.tvEndDate));
        tv_search = ((Button) findViewById(R.id.tv_search));
        vgType = ((LinearLayout) findViewById(R.id.vgType));
        layout_search_view = ((LinearLayout) findViewById(R.id.layout_search_view));

        //时间选择器
        String currDate = Dates.getStringByFormat(System.currentTimeMillis(), Dates.dateFormatYMD);
        tvStartDate.setText(currDate);
        startMills = Dates.getMillisOfStr(currDate, Dates.dateFormatYMD);

        tvEndDate.setText(currDate);
        stopMills = Dates.getMillisOfStr(currDate, Dates.dateFormatYMD) + 24 * 60 * 60 * 1000 - 1000;

        overDate = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);

        initPopData();

        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        tvqihao.setOnClickListener(this);
        tvstatus.setOnClickListener(this);
        tv_search.setOnClickListener(this);

        //抽奖列表
        srlAwardCodeRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        srlAwardCodeRefresh.setOnRefreshListener(this);

        rvAwardCodelist = (RecyclerView) findViewById(R.id.rvAwardCodelist);
        rvAwardCodelist.setLayoutManager(Views.genLinearLayoutManagerV(this));

        adapter = new AdapterOpenLottery(this, mlistData);
        rvAwardCodelist.setAdapter(adapter);

        getChoujiangCodesData(null, null);

        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                currPage++;
                String startTime = Dates.getStringByFormat(startMills, Dates.dateFormatYMD);
                String stopTime = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
                startTime = startTime + " 00:00:00";
                getChoujiangCodesData(startTime, stopTime);
            }
        });
    }

    private void getChoujiangCodesData(String startTime, String stopTime) {
        HttpActionTouCai.getBonusPoolCodeList(this, qihaoCode, codeTianxie, activityId,
                codeStatus, startTime, stopTime, currPage, PAGE_SIZE, new AbHttpResult() {

                    @Override
                    public void setupEntity(AbHttpRespEntity entity) {
                        entity.putField("data", new TypeToken<BonusPoolChoujiangMode>() {
                        }.getType());
                    }

                    @Override
                    public void onBefore(Request request, int id, String host, String funcName) {
                        if (currPage == 0) {
                            Dialogs.showProgressDialog(ActChoujiangListDetail.this, "搜索中...");
                            srlAwardCodeRefresh.setRefreshing(true);
                        }
                    }

                    @Override
                    public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                        if (code == 0 && error == 0) {
                            BonusPoolChoujiangMode choujiangMode = getField(extra, "data", null);
                            List<BonusPoolChoujiangMode.ListBean> modeList = choujiangMode.getList();
                            if (currPage == 0) {
                                mlistData.clear();
                            }

                            mlistData.addAll(modeList);

                            if (mlistData.size() < choujiangMode.getTotalCount()) {
                                adapter.setEnableLoadMore(true);
                            } else {
                                adapter.setEnableLoadMore(false);
                                adapter.loadMoreEnd();
                                //Toast.makeText(Act, "全部加载完毕", Toast.LENGTH_SHORT).show();
                            }
                            adapter.notifyDataSetChanged();
                        }
                        return super.onSuccessGetObject(code, error, msg, extra);
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        if (currPage == 0) {
                            Dialogs.hideProgressDialog(ActChoujiangListDetail.this);
                        }
                        srlAwardCodeRefresh.setRefreshing(false);
                        adapter.loadMoreComplete();

                        if (adapter.getEmptyView() == null)
                            adapter.setEmptyView(emptyView);
                    }
                });
    }


    @Override
    public void onRefresh() {
        currPage = 0;
        getChoujiangCodesData(beginDate, overDate);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tvqihao:
                if (tvqihaoPopup != null) {
                    tvqihaoPopup.showAsDropDown(vgType, 0, 0);//显示在vgType的下方

                    tvqihaoPopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
                        @Override
                        public void click(int position, View view) {
                            tvqihao.setText(qihaoArray[position]);
                            int qihaoNum = 0;
                            if (Strs.isNotEmpty(activityIssueNo)) {
                                qihaoNum = Integer.parseInt(activityIssueNo);
                            }
                            if (position == 0) {
                                qihaoCode = null;
                            } else if (position == 1) {
                                qihaoCode = activityIssueNo;
                            } else if (position == 2) {
                                qihaoCode = activityIssueNo + "," + (qihaoNum - 1);
                            } else if (position == 3) {
                                qihaoCode = activityIssueNo + "," + (qihaoNum - 1) + "," + (qihaoNum - 2);
                            }
                        }
                    });
                }
                break;
            case R.id.tvstatus:
                if (tvstatusPopup != null) {
                    tvstatusPopup.showAsDropDown(vgType, 0, 0);//显示在vgType的下方
                    tvstatusPopup.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
                        @Override
                        public void click(int position, View view) {
                            tvstatus.setText(popstatusArray[position]);
                            if (position == 0) {
                                codeStatus = null;
                            } else {
                                codeStatus = String.valueOf(position - 1);
                            }
                        }
                    });
                }
                break;
            case R.id.tvStartDate:
                Calendar calendar = Calendar.getInstance();
                showDateDialog(tvStartDate, Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
                break;
            case R.id.tvEndDate:
                Calendar calendar1 = Calendar.getInstance();
                showDateDialog(tvEndDate, Arrays.asList(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH) + 1, calendar1.get(Calendar.DAY_OF_MONTH)));
                break;
            case R.id.tv_search:
                codeTianxie = tvCode.getText().toString().trim();
                if (TextUtils.isEmpty(codeTianxie)) {
                    codeTianxie = null;
                }

                if (stopMills < startMills) {
                    Toasts.show("结束时间不能小于起始时间", false);
                    return;
                }

                currPage = 0;
                String startTime = Dates.getStringByFormat(startMills, Dates.dateFormatYMD);
                String stopTime = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
                startTime = startTime + " 00:00:00";
                getChoujiangCodesData(startTime, stopTime);
                layout_search_view.setVisibility(View.GONE);
                break;
        }
    }

    private void initPopData() {
        tvqihaoPopup = new ListMenuPopupWindow(this, AbDevice.SCREEN_WIDTH_PX, qihaoArray);
        tvstatusPopup = new ListMenuPopupWindow(this, AbDevice.SCREEN_WIDTH_PX, popstatusArray);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    protected class AdapterOpenLottery extends BaseQuickAdapter<BonusPoolChoujiangMode.ListBean, AdapterOpenLottery.OpenLotteryHolder> {

        private Context ctx;

        public AdapterOpenLottery(Context ctx, List<BonusPoolChoujiangMode.ListBean> data) {
            super(R.layout.item_choujiang_code_list, data);
            this.ctx = ctx;
        }

        @Override
        protected void convert(AdapterOpenLottery.OpenLotteryHolder helper, BonusPoolChoujiangMode.ListBean item) {
            helper.choujiang_time.setText(AbDateUtil.getStringByFormat(item.getCreateTime(), AbDateUtil.dateFormatYMDHM));
            helper.choujiang_qihao.setText("期号 : " + item.getActivityIssueNo());
            helper.choujiang_status.setText(statusArray[item.getStatus()]);
            helper.choujiang_status.setTextColor(ctx.getResources().getColor(statusColorArray[item.getStatus()]));
            //开奖号球
            if (Strs.isEmpty(item.getActivityNumber())) {
                return;
            }
            char[] arrCode = item.getActivityNumber().toCharArray();
            TextView tvNum = null;

            helper.llBallsBlue.removeAllViews();
            for (int i = 0; i < arrCode.length; i++) {
                tvNum = (TextView) LayoutInflater.from(ctx).inflate(R.layout.view_ball_blue_small, helper.llBallsBlue, false);
                tvNum.setText(String.valueOf(arrCode[i]));
                tvNum.setBackgroundDrawable(Views.fromDrawables(R.drawable.sh_bg_ball_red_solid));
                tvNum.setTextColor(ctx.getResources().getColor(R.color.red));
                tvNum.setTextSize(18);
                tvNum.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                helper.llBallsBlue.addView(tvNum);
            }
            helper.llBallsBlue.requestLayout();

        }

        public class OpenLotteryHolder extends BaseViewHolder {
            public TextView choujiang_qihao;
            public TextView choujiang_time;
            public TextView choujiang_status;
            public FlowLayout llBallsBlue;

            public OpenLotteryHolder(View view) {
                super(view);
                choujiang_qihao = (TextView) view.findViewById(R.id.choujiang_qihao);
                choujiang_time = (TextView) view.findViewById(R.id.choujiang_time);
                choujiang_status = (TextView) view.findViewById(R.id.choujiang_status);
                llBallsBlue = (FlowLayout) view.findViewById(R.id.llBallsBlue);
            }
        }
    }
}
