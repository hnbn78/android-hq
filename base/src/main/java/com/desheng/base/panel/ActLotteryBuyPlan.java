package com.desheng.base.panel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ab.http.Callback;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.model.LotteryBuyPlan;
import com.google.gson.Gson;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.SpaceTopDecoration;
import com.zhy.view.flowlayout.FlowLayout;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 购彩计划
 * Created by user on 2018/3/23.
 */
public class ActLotteryBuyPlan extends AbAdvanceActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static final String PLAN_A = "planA";
    public static final String PLAN_B = "planB";
    public static final String PLAN_C = "planC";
    private AdapterBuyPlan adapterA;
    private AdapterBuyPlan adapterB;
    private AdapterBuyPlan adapterC;
    private ILotteryKind lotteryKind;
    private SwipeRefreshLayout srlRefresh;
    
    public static void launch(Activity act, int lotteryId) {
        Intent itt = new Intent(act, ActLotteryBuyPlan.class);
        itt.putExtra("lotteryId", lotteryId);
        act.startActivity(itt);
    }
    
    private RadioGroup rgPlan;
    private RadioButton rbPlan1;
    private RadioButton rbPlan2;
    private RadioButton rbPlan3;
    private RecyclerView rvPlan;
    
    @Override
    protected int getLayoutId() {
        return R.layout.act_lottery_buy_plan;
    }
    
    @Override
    protected void init() {
        int lotteryId = getIntent().getIntExtra("lotteryId", 0);
        lotteryKind = CtxLottery.getIns().findLotteryKind(lotteryId);
        
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "购彩计划");
        setStatusBarTranslucentAndLightContentWithPadding();
        
        srlRefresh = (SwipeRefreshLayout) findViewById(R.id.srlRefresh);
        srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);
        
        rgPlan = (RadioGroup) findViewById(R.id.rgPlan);
        rbPlan1 = (RadioButton) findViewById(R.id.rbPlan1);
        rbPlan2 = (RadioButton) findViewById(R.id.rbPlan2);
        rbPlan3 = (RadioButton) findViewById(R.id.rbPlan3);
        
        rgPlan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rbPlan1.setTextColor(Views.fromColors(R.color.black));
                rbPlan2.setTextColor(Views.fromColors(R.color.black));
                rbPlan3.setTextColor(Views.fromColors(R.color.black));
                if (checkedId == R.id.rbPlan1) {
                    rbPlan1.setTextColor(Views.fromColors(R.color.white));
                    rvPlan.setAdapter(adapterA);
        
                } else if (checkedId == R.id.rbPlan2) {
                    rbPlan2.setTextColor(Views.fromColors(R.color.white));
                    rvPlan.setAdapter(adapterB);
        
                } else if (checkedId == R.id.rbPlan3) {
                    rbPlan3.setTextColor(Views.fromColors(R.color.white));
                    rvPlan.setAdapter(adapterC);
        
                }
            }
        });
        
        rvPlan = (RecyclerView) findViewById(R.id.rvPlan);
        rvPlan.setLayoutManager(Views.genLinearLayoutManagerV(this));
        rvPlan.addItemDecoration(new SpaceTopDecoration(Views.dp2px(6)));
        
        adapterA = new AdapterBuyPlan(this, PLAN_A);
        adapterB = new AdapterBuyPlan(this, PLAN_B);
        adapterC = new AdapterBuyPlan(this, PLAN_C);
        
        rvPlan.setAdapter(adapterA);
        adapterA.bindToRecyclerView(rvPlan);
        
        getData();
    }
    
    @Override
    public void onRefresh() {
        getData();
    }
    
    private void getData() {
        HttpAction.getBuyPlan(ActLotteryBuyPlan.this, lotteryKind, new Callback<HttpAction.RespBuyPlan>() {
            @Override
            public void onBefore(Request request, int id) {
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(true);
                    }
                },100);
            }
            
            @Override
            public HttpAction.RespBuyPlan parseNetworkResponse(Response response, int id) throws
                    Exception {
                String string = response.body().string();
                if (Strs.isNotEmpty(string)) {
                    return new Gson().fromJson(string, HttpAction.RespBuyPlan.class);
                } else {
                    return null;
                }
            }
            
            @Override
            public void onError(Call call, Exception e, int id) {
                Toasts.show(ActLotteryBuyPlan.this, "获取购奖计划失败!", false);
            }
            
            @Override
            public void onResponse(HttpAction.RespBuyPlan response, int id) {
                if (response != null) {
                    if (response.errorCode == 0 && response.result != null && response.result.data != null) {
                        adapterA.setNewData(response.result.data);
                        adapterB.setNewData(response.result.data);
                        adapterC.setNewData(response.result.data);
                    } else {
                        Toasts.show(ActLotteryBuyPlan.this, response.message, false);
                    }
                } else {
                    Toasts.show(ActLotteryBuyPlan.this, "获取购奖计划失败!", false);
                }
            }
            
            @Override
            public void onAfter(int id) {
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(false);
                    }
                },600);
            }
        });
    }
    
    
    protected static class AdapterBuyPlan extends BaseQuickAdapter<LotteryBuyPlan.DataBean, AdapterBuyPlan.BuyPlanHolder> {
        
        private String plan;
        private Context ctx;
        
        public AdapterBuyPlan(Context ctx, String plan) {
            super(R.layout.item_lottery_buy_plan, new ArrayList<LotteryBuyPlan.DataBean>());
            this.plan = plan;
            this.ctx = ctx;
        }
        
        @Override
        protected void convert(AdapterBuyPlan.BuyPlanHolder helper, LotteryBuyPlan.DataBean item) {
            String issue = String.format("开奖期数:<font color=\"#ff0000\">%s</font>期", item.preDrawIssue);
            helper.tvTitleIssue.setText(Html.fromHtml(issue));
            helper.tvIssueHour.setText(item.preDrawTime);
            
            int lotteryCost = 0;
            String profit = null;
            int lotteryCostAll = 0;
            String[] planCode = null;
            if (PLAN_A.equals(plan)) {
                lotteryCost = item.lotteryCostA;
                profit = item.profitA;
                lotteryCostAll = item.lotteryCostAllA;
                planCode = item.planA.split(",");
            } else if (PLAN_B.equals(plan)) {
                lotteryCost = item.lotteryCostB;
                profit = item.profitB;
                lotteryCostAll = item.lotteryCostAllB;
                planCode = item.planB.split(",");
            } else if (PLAN_C.equals(plan)) {
                lotteryCost = item.lotteryCostC;
                profit = item.profitC;
                lotteryCostAll = item.lotteryCostAllC;
                planCode = item.planC.split(",");
            }
            
            helper.tvCurrCost.setText(String.valueOf(lotteryCost));
            helper.tvCumulativeCost.setText(String.valueOf(lotteryCostAll));
            helper.tvBalance.setText(String.valueOf(profit));
            if (Strs.parse(profit, -1) <= 0) {
                helper.tvBalance.setTextColor(Color.parseColor("#0cb9f5"));
            } else {
                helper.tvBalance.setTextColor(Color.parseColor("#ff0000"));
            }
            
            String[] arrCode = null;
            int num = 0;
            TextView tvNum = null;
            String hitNum = "";
            TextView hitText = null;
            helper.llWaittingOpen.setVisibility(View.INVISIBLE);
            helper.vgIssueBall.setVisibility(View.INVISIBLE);
            if (Strs.isEmpty(item.preDrawCode)) {
                helper.llWaittingOpen.setVisibility(View.VISIBLE);
            } else {
                //初始化开奖组
                helper.vgIssueBall.setVisibility(View.VISIBLE);
                helper.vgIssueBall.removeAllViews();
                arrCode = item.preDrawCode.split(",");
                hitNum = arrCode[0];
                num = 0;
                tvNum = null;
                for (int i = 0; i < arrCode.length; i++) {
                    num = Integer.parseInt(arrCode[i]);
                    tvNum = (TextView) LayoutInflater.from(ctx).inflate(R.layout.view_square_misc_round, helper.vgIssueBall, false);
                    tvNum.setAlpha(0.3f);
                    tvNum.setText(arrCode[i]);
                    tvNum.setBackgroundDrawable(Views.fromDrawables(R.drawable.sh_bg_square_blue_solid_round));
                    GradientDrawable bg = (GradientDrawable) tvNum.getBackground();
                    bg.setColor(UIHelper.getNumBgColor(num));
                    bg.setStroke(1, UIHelper.getNumBgColor(num));
                    helper.vgIssueBall.addView(tvNum);
                    
                    if (i == 0) {
                        hitText = tvNum;
                    }
                }
                helper.vgIssueBall.requestLayout();
                
                //初始化预测组
                arrCode = planCode;
                helper.vgPredictBall.removeAllViews();
                for (int i = 0; i < arrCode.length; i++) {
                    num = Integer.parseInt(arrCode[i]);
                    tvNum = (TextView) LayoutInflater.from(ctx).inflate(R.layout.view_square_misc_round, helper.vgPredictBall, false);
                    tvNum.setText(arrCode[i]);
                    tvNum.setBackgroundDrawable(Views.fromDrawables(R.drawable.sh_bg_square_blue_solid_round));
                    tvNum.setAlpha(0.3f);
                    GradientDrawable bg = (GradientDrawable) tvNum.getBackground();
                    bg.setColor(UIHelper.getNumBgColor(num));
                    bg.setStroke(1, UIHelper.getNumBgColor(num));
                    helper.vgPredictBall.addView(tvNum);
                    
                    if (hitNum.equals(arrCode[i])) {
                        hitText.setAlpha(1);
                        tvNum.setAlpha(1);
                    }
                }
                helper.vgPredictBall.requestLayout();
            }
            
        }
        
        public static class BuyPlanHolder extends BaseViewHolder {
            public LinearLayout vgTitleGroup;
            public TextView tvTitleIssue;
            public TextView tvIssueHour;
            public FrameLayout vgNowIssue;
            public FlowLayout vgIssueBall;
            public LinearLayout llWaittingOpen;
            public FlowLayout vgPredictBall;
            public LinearLayout llinfo;
            public TextView tvCurrCost;
            public TextView tvCumulativeCost;
            public TextView tvBalance;
            
            public BuyPlanHolder(View view) {
                super(view);
                vgTitleGroup = (LinearLayout) view.findViewById(R.id.vgTitleGroup);
                tvTitleIssue = (TextView) view.findViewById(R.id.tvTitleIssue);
                tvIssueHour = (TextView) view.findViewById(R.id.tvIssueHour);
                vgNowIssue = (FrameLayout) view.findViewById(R.id.vgNowIssue);
                vgIssueBall = (FlowLayout) view.findViewById(R.id.vgIssueBall);
                llWaittingOpen = (LinearLayout) view.findViewById(R.id.llWaittingOpen);
                vgPredictBall = (FlowLayout) view.findViewById(R.id.vgPredictBall);
                llinfo = (LinearLayout) view.findViewById(R.id.llinfo);
                tvCurrCost = (TextView) view.findViewById(R.id.tvCurrCost);
                tvCumulativeCost = (TextView) view.findViewById(R.id.tvCumulativeCost);
                tvBalance = (TextView) view.findViewById(R.id.tvBalance);
            }
        }
    
       /* public static class OpenLotteryTypeDelegate extends MultiTypeDelegate<OpenLottery> {
            public OpenLotteryTypeDelegate() {
                registerItemType(OpenLottery.TYPE_LOTTERY_GROUP, R.layout.item_open_lottery);
            }
        
            @Override
            protected int getItemType(OpenLottery userAttentionItem) {
                return userAttentionItem.getItemType();
            }
        }*/
    }
    
}
