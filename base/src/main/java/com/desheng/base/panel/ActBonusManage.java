package com.desheng.base.panel;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
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
import com.desheng.base.adapter.BonusManageAdapter;
import com.desheng.base.model.BonusPallBean;
import com.desheng.base.model.BonusRuleBean;
import com.desheng.base.model.UserPoints;
import com.desheng.base.model.XjBonusBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.mypicker.DatePickerDialog;
import com.pearl.view.mypicker.DateUtil;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ActBonusManage extends AbAdvanceActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private RecyclerView recycle_bonus_list;
    private SwipeRefreshLayout srlRefresh;
    private BonusAdapter mAdapter;
    private List<XjBonusBean.ListBean> listBeans = new ArrayList<>();
    private long startMills = 0;
    private long stopMills = 0;
    private int page = 1, size = 10;
    private int totalCount;
    private String username;

    //分红规则头
    private TextView tv_user_type, tv_bonus_rate, tv_consume_days, tv_lowest_amount;
    //分红细节头
    private TextView tv_team_profit, tv_should_get_bonus, tv_has_received_bonus, tv_bonus_date, tv_should_send_bonus, tv_has_send_bonus, tv_not_send_bonus;
    private TextView tvStartDate, tvEndDate;
    private EditText etUserName;
    private Button btnSearch, btn_distribute_bonus;

    private TextView tv_foot_team_profit_lost,tv_foot_bonus_amount;


    private BonusRuleBean mangeBean;

    public static void launch(Activity ctx) {
        simpleLaunch(ctx, ActBonusManage.class);
    }

    @Override
    protected void init() {

        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "分红管理");
        setStatusBarTranslucentAndLightContentWithPadding();

        recycle_bonus_list = findViewById(R.id.recycle_bonus_list);
        recycle_bonus_list.setLayoutManager(Views.genLinearLayoutManagerV(ActBonusManage.this));
        mAdapter = new BonusAdapter(ActBonusManage.this);
        recycle_bonus_list.setAdapter(mAdapter);

        srlRefresh = findViewById(R.id.srlRefresh);
        srlRefresh.setOnRefreshListener(this);

        View headView_bonus_rule = LayoutInflater.from(this).inflate(R.layout.headview_bonus_rule, null);
        tv_user_type = headView_bonus_rule.findViewById(R.id.tv_user_type);
        tv_bonus_rate = headView_bonus_rule.findViewById(R.id.tv_bonus_rate);
        tv_consume_days = headView_bonus_rule.findViewById(R.id.tv_consume_days);
        tv_lowest_amount = headView_bonus_rule.findViewById(R.id.tv_lowest_amount);
        View headView_bonus_detail = LayoutInflater.from(this).inflate(R.layout.headview_bonus_detail, null);
        tv_team_profit = headView_bonus_detail.findViewById(R.id.tv_team_profit);
        tv_should_get_bonus = headView_bonus_detail.findViewById(R.id.tv_should_get_bonus);
        tv_has_received_bonus = headView_bonus_detail.findViewById(R.id.tv_has_received_bonus);
        tv_bonus_date = headView_bonus_detail.findViewById(R.id.tv_bonus_date);
        tv_should_send_bonus = headView_bonus_detail.findViewById(R.id.tv_should_send_bonus);
        tv_has_send_bonus = headView_bonus_detail.findViewById(R.id.tv_has_send_bonus);
        tv_not_send_bonus = headView_bonus_detail.findViewById(R.id.tv_not_send_bonus);
        btn_distribute_bonus = headView_bonus_detail.findViewById(R.id.btn_distribute_bonus);
        View footView_bonus = LayoutInflater.from(this).inflate(R.layout.footview_bonus_manage,null);
        tv_foot_team_profit_lost = footView_bonus.findViewById(R.id.tv_foot_team_profit_lost);
        tv_foot_bonus_amount = footView_bonus.findViewById(R.id.tv_foot_bonus_amount);

        tvStartDate = headView_bonus_detail.findViewById(R.id.tvStartDate);
        tvEndDate = headView_bonus_detail.findViewById(R.id.tvEndDate);
        etUserName = headView_bonus_detail.findViewById(R.id.etUserName);
        btnSearch = headView_bonus_detail.findViewById(R.id.btnSearch);

        initDateView();

        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        tv_bonus_rate.setOnClickListener(this);
        btn_distribute_bonus.setOnClickListener(this);
        mAdapter.addHeaderView(headView_bonus_rule);
        mAdapter.addHeaderView(headView_bonus_detail);
        mAdapter.addFooterView(footView_bonus);

        mAdapter.setEnableLoadMore(true);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                getXjBonusBeanList();
            }
        }, recycle_bonus_list);

        getBonusRule();
        getXjBonusBeanList();
    }

    private void initDateView() {
        String currentDate = Dates.getStringByFormat(System.currentTimeMillis(), Dates.dateFormatYMD);
        startMills = Dates.getMillisOfStr(currentDate, Dates.dateFormatYMD);
        stopMills = Dates.getMillisOfStr(currentDate, Dates.dateFormatYMD) + 24 * 60 * 60 * 1000 - 1000;
        tvStartDate.setText(currentDate);
        tvEndDate.setText(currentDate);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_bonus_manage;
    }

    @Override
    public void onRefresh() {
        page = 1;
        getBonusRule();
        getXjBonusBeanList();

        srlRefresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                srlRefresh.setRefreshing(false);
            }
        }, 500);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSearch) {
            getXjBonusBeanList();
        } else if (v == tvStartDate) {
            Calendar calendar = Calendar.getInstance();
            showDateDialog(tvStartDate, Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));

        } else if (v == tvEndDate) {
            Calendar calendar = Calendar.getInstance();
            showDateDialog(tvEndDate, Arrays.asList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));

        } else if (v == btn_distribute_bonus) {
            final MaterialDialog dialog=new MaterialDialog(this);
            dialog.setTitle("确定现在派发？");
            dialog.setMessage("金额不足时只会进行部分派发！");
            dialog.setPositiveButton("确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pallBonus();
                    dialog.dismiss();
                }
            });

            dialog.setNegativeButton("取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 dialog.dismiss();
                }
            });
            dialog.show();

        } else if (v == tv_bonus_rate) {
            if (null != mangeBean && !Strs.isEmpty(mangeBean.getMixPercents())) {
                ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.dialog_bonus_show, null);
                RecyclerView lv_list = viewGroup.findViewById(R.id.lv_list);
                lv_list.setLayoutManager(Views.genLinearLayoutManagerV(ActBonusManage.this));
                Button btnClose = viewGroup.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialogs.dissmisCustomDialog();
                    }
                });

                Gson gson = new Gson();
                Type listType = new TypeToken<List<UserPoints>>() {
                }.getType();

                userPointList = gson.fromJson(mangeBean.getMixPercents(), listType);
                if(null!=userPointList){
                    BonusManageAdapter manageAdapter = new BonusManageAdapter(this, userPointList);
                    manageAdapter.setNewData(userPointList);
                    lv_list.setAdapter(manageAdapter);

                    Dialogs.showCustomDialog(this, viewGroup, true);
                }

            }else{
                Toasts.show(this,"没有分红数据",false);
            }
        }
    }

    private List<UserPoints> userPointList;

    private void getBonusRule() {
        HttpAction.Zj_Bonus(null, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", BonusRuleBean.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0) {
                    mangeBean = getField(extra, "data", null);
                    tv_team_profit.setText(Nums.formatDecimal(mangeBean.getProfitAmount(), 4));
                    tv_should_get_bonus.setText(Nums.formatDecimal(mangeBean.getDeservedBonus(), 4));
                    tv_has_received_bonus.setText(Nums.formatDecimal(mangeBean.getReceivedBonus(), 4));
                    String strStart,strEnd;
                    strStart=mangeBean.getStartDay();
                    strEnd=mangeBean.getEndDay();

                    if(Strs.isEmpty(strStart)){
                        strStart="--";
                    }
                    if(Strs.isEmpty(strEnd)){
                        strEnd="--";
                    }

                    tv_bonus_date.setText(strStart + "~" + strEnd);
                    tv_should_send_bonus.setText(Nums.formatDecimal(mangeBean.getShouleDistributBonus(), 4));
                    tv_has_send_bonus.setText(Nums.formatDecimal(mangeBean.getAlreadyDistributBonus(), 4));
                    tv_not_send_bonus.setText(Nums.formatDecimal(mangeBean.getYetDistributBonus(), 4));
                }

                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    /**
     * 获取分红列表
     */
    private void getXjBonusBeanList() {

        username = etUserName.getText().toString();

        String startTime = Dates.getStringByFormat(startMills, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);

        HttpAction.Xj_Bonus(null, username, startTime, stopTime, page, size, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", XjBonusBean.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0) {

                    XjBonusBean bonusBean = (XjBonusBean) extra.get("data");

                    if (page == 1) {
                        listBeans.clear();
                    }
                    totalCount = bonusBean.getTotalCount();
                    listBeans.addAll(bonusBean.getList());
                    mAdapter.setNewData(listBeans);

                    if (listBeans.size() >= totalCount) {
                        mAdapter.setEnableLoadMore(false);
                    }

                    if(listBeans.size()>0){
                        btn_distribute_bonus.setVisibility(View.VISIBLE);
                    }else{
                        btn_distribute_bonus.setVisibility(View.INVISIBLE);
                    }

                    if(listBeans.size()>0){
                        XjBonusBean.ListBean huizongbonus=listBeans.get(listBeans.size()-1);
                        listBeans.remove(huizongbonus);
                        tv_foot_team_profit_lost.setText(Nums.formatDecimal(huizongbonus.getProfitAmount(),4));
                        tv_foot_bonus_amount.setText(Nums.formatDecimal(huizongbonus.getBonusSum(),4));
                    }

                } else {
                    Toasts.show(ActBonusManage.this, msg);
                }

                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });

    }

    private void pallBonus(){
        HttpAction.pallBonus(null,new AbHttpResult(){
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                super.setupEntity(entity);

                entity.putField("data", BonusPallBean.class);

            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if(error==0&&code==0){
                    BonusPallBean pallBean=(BonusPallBean)extra.get("data");
                    Toasts.show(ActBonusManage.this,"派发成功",true);
                    getBonusRule();
                    page=1;
                    getXjBonusBeanList();
                }else{
                    Toasts.show(ActBonusManage.this,msg,false);
                }

                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onGetString(String str) {
                return super.onGetString(str);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    protected class BonusAdapter extends BaseQuickAdapter<XjBonusBean.ListBean, BonusAdapter.BonusMangeVH> {

        private final Context ctx;

        public BonusAdapter(Context ctx) {
            super(R.layout.item_bonus_manage_list, new ArrayList<XjBonusBean.ListBean>());
            this.ctx = ctx;
        }

        @Override
        protected void convert(final BonusAdapter.BonusMangeVH holder, final XjBonusBean.ListBean item) {

            if (null == item.getUserName()) {
                holder.tv_bonus_date_name.setText("汇总");
                holder.tv_username_name.setVisibility(View.GONE);
                holder.tv_bonus_rate_name.setVisibility(View.GONE);
                holder.tv_status_name.setVisibility(View.GONE);

                holder.tv_username.setVisibility(View.GONE);
                holder.tv_bonus_rate.setVisibility(View.GONE);
                holder.tv_status.setVisibility(View.GONE);

                if (null != listBeans) {
                    holder.tv_profit.setText(Nums.formatDecimal(item.getProfitAmount(), 4));
                    holder.tv_bonus_count.setText(Nums.formatDecimal(item.getBonusSum(), 4));
                }
            }else{
                String strStart=item.getStartDay();
                String strEnd=item.getEndDay();

                if(Strs.isEmpty(strStart)){
                    strStart="--";
                }
                if(Strs.isEmpty(strEnd)){
                    strEnd="--";
                }
                holder.tv_bonus_date.setText(strStart + " ~ " + strEnd);
                holder.tv_username.setText(item.getUserName());
                holder.tv_bonus_rate.setText((Nums.formatDecimal(item.getPercent() * 100, 4)) + "%");
                holder.tv_profit.setText("￥" + item.getRealProfitAmount());
                holder.tv_bonus_count.setText("￥" + item.getBonus());
                holder.tv_status.setText(item.getStatusStr());
            }
        }

        protected class BonusMangeVH extends BaseViewHolder {
            private TextView tv_bonus_date;
            private TextView tv_username;
            private TextView tv_bonus_rate;
            private TextView tv_profit;
            private TextView tv_bonus_count;
            private TextView tv_status;
            private TextView tv_bonus_date_name;
            private TextView tv_username_name;
            private TextView tv_bonus_rate_name;
            private TextView tv_profit_name;
            private TextView tv_bonus_count_name;
            private TextView tv_status_name;
            private View view_ling;

            public BonusMangeVH(View view) {
                super(view);
                tv_bonus_date = view.findViewById(R.id.tv_bonus_date);
                tv_username = view.findViewById(R.id.tv_username);
                tv_bonus_rate = view.findViewById(R.id.tv_bonus_rate);
                tv_profit = view.findViewById(R.id.tv_profit);
                tv_bonus_count = view.findViewById(R.id.tv_bonus_count);
                tv_status = view.findViewById(R.id.tv_status);
                view_ling = view.findViewById(R.id.view_ling);

                tv_bonus_date_name = view.findViewById(R.id.tv_bonus_date_name);
                tv_username_name = view.findViewById(R.id.tv_username_name);
                tv_bonus_rate_name = view.findViewById(R.id.tv_bonus_rate_name);
                tv_profit_name = view.findViewById(R.id.tv_profit_name);
                tv_bonus_count_name = view.findViewById(R.id.tv_bonus_count_name);
                tv_status_name = view.findViewById(R.id.tv_status_name);
            }
        }
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
