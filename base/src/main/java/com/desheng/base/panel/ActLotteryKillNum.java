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
import com.ab.util.ArraysAndLists;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.model.LotteryKillNum;
import com.google.gson.Gson;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.SpaceTopDecoration;
import com.zhy.view.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 稳赢杀号
 * Created by user on 2018/3/23.
 */
public class ActLotteryKillNum extends AbAdvanceActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String[] TITLE_NUMS_TEN = new String[]{"冠军", "亚军", "第三名", "第四名", "第五名", "第六名", "第七名", "第八名", "第九名", "第十名"};
    private static final String[] TITLE_NUMS_FIVE = new String[]{"万位杀号", "千位杀号", "百位杀号", "十位杀号", "个位杀号"};
    private static final Integer [] TITLE_IDS = new Integer[]{R.id.rbTitle1, R.id.rbTitle2, R.id.rbTitle3, R.id.rbTitle4, R.id.rbTitle5,
            R.id.rbTitle6, R.id.rbTitle7, R.id.rbTitle8, R.id.rbTitle9, R.id.rbTitle10};
    
    
    public static void launch(Activity act, int lotteryId) {
        Intent itt = new Intent(act, ActLotteryKillNum.class);
        itt.putExtra("lotteryId", lotteryId);
        act.startActivity(itt);
    }
   
    private RadioButton[] arrTitleBtn;
    private AdapterKillNum[] arrAdapter;
    
    private ILotteryKind lotteryKind;
    private SwipeRefreshLayout srlRefresh;
    
    private RadioGroup rgTitle;
    private RecyclerView rvKill;
    
    private int currTitle = 0;
    
    @Override
    protected int getLayoutId() {
        return R.layout.act_lottery_kill_num;
    }
    
    @Override
    protected void init() {
        int lotteryId = getIntent().getIntExtra("lotteryId", 0);
        lotteryKind = CtxLottery.getIns().findLotteryKind(lotteryId);
        
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "稳赢杀号");
        setStatusBarTranslucentAndLightContentWithPadding();
        
        srlRefresh = (SwipeRefreshLayout) findViewById(R.id.srlRefresh);
        srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);
    
        rgTitle = (RadioGroup) findViewById(R.id.rgTitle);
        arrTitleBtn = new RadioButton [TITLE_IDS.length];
        for (int i = 0; i < TITLE_IDS.length; i++) {
            arrTitleBtn[i] = (RadioButton) findViewById(TITLE_IDS[i]);
        }
        arrTitleBtn[0].setChecked(true);
    
        rgTitle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < arrTitleBtn.length; i++) {
                    arrTitleBtn[i].setTextColor(Views.fromColors(R.color.black));
                }
                currTitle = ArraysAndLists.findIndexWithEqualsOfArray(checkedId, TITLE_IDS);
                if(currTitle == -1){
                    return;
                }
                arrTitleBtn[currTitle].setTextColor(Views.fromColors(R.color.white));
                if (arrAdapter[currTitle] != null) {
                    rvKill.setAdapter(arrAdapter[currTitle]);
                }
            }
        });
        
        rvKill = (RecyclerView) findViewById(R.id.rvKill);
        rvKill.setLayoutManager(Views.genLinearLayoutManagerV(this));
        rvKill.addItemDecoration(new SpaceTopDecoration(Views.dp2px(6)));
        arrAdapter = new AdapterKillNum[10];
        
        getData();
    }
    
    @Override
    public void onRefresh() {
        getData();
    }
    
    private void getData() {
        HttpAction.getKillNum(ActLotteryKillNum.this, lotteryKind, new Callback<HttpAction.RespKillNum>() {
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
            public HttpAction.RespKillNum parseNetworkResponse(Response response, int id) throws
                    Exception {
                String string = response.body().string();
                if (Strs.isNotEmpty(string)) {
                    return new Gson().fromJson(string, HttpAction.RespKillNum.class);
                } else {
                    return null;
                }
            }
            
            @Override
            public void onError(Call call, Exception e, int id) {
                Toasts.show(ActLotteryKillNum.this, "获取杀号数据失败!", false);
            }
            
            @Override
            public void onResponse(HttpAction.RespKillNum response, int id) {
                if (response != null) {
                    if (response.errorCode == 0 && response.result != null && response.result.data != null) {
                        for (int j = 0; j < response.result.data.list.size(); j++) {
                            LotteryKillNum.DataBean.ListBean listBean = response.result.data.list.get(j);
                            listBean.arrNums = new List [] {listBean.firstNum, listBean.secondNum, listBean.thirdNum, listBean.fourthNum, listBean.fifthNum
                                    , listBean.sixthNum, listBean.sevenNum, listBean.eightNum, listBean.nineNum, listBean.tenNum};
                        }
                        if(response.result.data.list.get(0).sixthNum != null){
                            for (int i = 0; i < 10; i++) {
                                arrTitleBtn[i].setText(TITLE_NUMS_TEN[i]);
                                arrAdapter[i] = new AdapterKillNum(ActLotteryKillNum.this, i, response.result.data);
                                arrAdapter[i].setNewData(response.result.data.list);
                            }
                        }else{
                            for (int i = 0; i < 10; i++) {
                                if(i <= 4){
                                    arrTitleBtn[i].setText(TITLE_NUMS_FIVE[i]);
                                    arrAdapter[i] = new AdapterKillNum(ActLotteryKillNum.this, i, response.result.data);
                                    arrAdapter[i].setNewData(response.result.data.list);
                                }else{
                                    arrTitleBtn[i].setVisibility(View.GONE);
                                }
                            }
                        }
                        rvKill.setAdapter(arrAdapter[0]);
                        arrAdapter[0].bindToRecyclerView(rvKill);
                    } else {
                        Toasts.show(ActLotteryKillNum.this, response.message, false);
                    }
                } else {
                    Toasts.show(ActLotteryKillNum.this, "获取杀号数据失败!", false);
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
    
    
    protected static class AdapterKillNum extends BaseQuickAdapter<LotteryKillNum.DataBean.ListBean, AdapterKillNum.KillNumHolder> {
        
        private int titlePosion;
        private Context ctx;
        private LotteryKillNum.DataBean totoalData;
        
        public AdapterKillNum(Context ctx, int titlePosion, LotteryKillNum.DataBean totoalData) {
            super(R.layout.item_lottery_kill_num, new ArrayList<LotteryKillNum.DataBean.ListBean>());
            this.titlePosion = titlePosion;
            this.totoalData = totoalData;
            this.ctx = ctx;
        }
        
        @Override
        protected void convert(AdapterKillNum.KillNumHolder helper, LotteryKillNum.DataBean.ListBean item) {
            String issue = String.format("开奖期数:<font color=\"#ff0000\">%s</font>期", item.preDrawIssue);
            helper.tvTitleIssue.setText(Html.fromHtml(issue));
            
            String[] arrCode = null;
            int num = 0;
            TextView tvNum = null;
            helper.llWaittingOpen.setVisibility(View.INVISIBLE);
            helper.vgIssueBall.setVisibility(View.INVISIBLE);
            if (Strs.isEmpty(item.preDrawCode)) {
                helper.llWaittingOpen.setVisibility(View.VISIBLE);
            } else {
                //初始化开奖组
                helper.vgIssueBall.setVisibility(View.VISIBLE);
                helper.vgIssueBall.removeAllViews();
                arrCode = item.preDrawCode.split(",");
                for (int i = 0; i < arrCode.length; i++) {
                    num = Integer.parseInt(arrCode[i]);
                    tvNum = (TextView) LayoutInflater.from(ctx).inflate(R.layout.view_square_misc_round, helper.vgIssueBall, false);
                    tvNum.setText(Strs.of(num));
                    tvNum.setBackgroundDrawable(Views.fromDrawables(R.drawable.sh_bg_square_blue_solid_round));
                    GradientDrawable bg = (GradientDrawable) tvNum.getBackground();
                    bg.setColor(UIHelper.getNumBgColor(num));
                    bg.setStroke(1, UIHelper.getNumBgColor(num));
                    helper.vgIssueBall.addView(tvNum);
                }
                helper.vgIssueBall.requestLayout();
            }
            
            for (int i = 0; i < helper.arrTvBall.length; i++) {
                int color = 0;
                String strResult = "";
                int ballRes = 0;
        
                int numPositon = item.arrNums[titlePosion].get(i * 2 + 1);
                switch (numPositon){
                    case 0:
                        color = Color.parseColor("#c73e3e");
                        ballRes = R.drawable.sh_bg_ball_blue_solid_predict;
                        strResult = "预测";
                        break;
                    case 1:
                        color = Color.parseColor("#e2333e");
                        ballRes = R.drawable.sh_bg_ball_blue_solid_right;
                        strResult = "杀对";
                        break;
                    default:
                        color = Color.parseColor("#187ddc");
                        ballRes = R.drawable.sh_bg_ball_blue_solid_wrong;
                        strResult = "杀错";
                }
                helper.arrTvBall[i].setText(Strs.of(item.arrNums[titlePosion].get(i * 2)));
                helper.arrTvBall[i].setBackgroundResource(ballRes);
                helper.arrTvResult[i].setTextColor(color);
                helper.arrTvResult[i].setText(strResult);
            }
        }
        
        public static class KillNumHolder extends BaseViewHolder {
            private LinearLayout vgTitleGroup;
            private TextView tvTitleIssue;
            private FrameLayout vgNowIssue;
            private FlowLayout vgIssueBall;
            private LinearLayout llWaittingOpen;
            private LinearLayout llinfo;
            private TextView tvBall1;
            private TextView tvResult1;
            private TextView tvBall2;
            private TextView tvResult2;
            private TextView tvBall3;
            private TextView tvResult3;
            private TextView tvBall4;
            private TextView tvResult4;
            private TextView tvBall5;
            private TextView tvResult5;
            private TextView [] arrTvBall;
            private TextView [] arrTvResult;
            
            
            public KillNumHolder(View view) {
                super(view);
                vgTitleGroup = (LinearLayout) view.findViewById(R.id.vgTitleGroup);
                tvTitleIssue = (TextView) view.findViewById(R.id.tvTitleIssue);
                vgNowIssue = (FrameLayout) view.findViewById(R.id.vgNowIssue);
                vgIssueBall = (FlowLayout) view.findViewById(R.id.vgIssueBall);
                llWaittingOpen = (LinearLayout) view.findViewById(R.id.llWaittingOpen);
                llinfo = (LinearLayout) view.findViewById(R.id.llinfo);
                arrTvBall = new TextView[5];
                arrTvResult = new TextView[5];
                tvBall1 = (TextView) view.findViewById(R.id.tvBall1);
                tvResult1 = (TextView) view.findViewById(R.id.tvResult1);
                arrTvBall[0] = tvBall1;
                arrTvResult[0] = tvResult1;
                tvBall2 = (TextView) view.findViewById(R.id.tvBall2);
                tvResult2 = (TextView) view.findViewById(R.id.tvResult2);
                arrTvBall[1] = tvBall2;
                arrTvResult[1] = tvResult2;
                tvBall3 = (TextView) view.findViewById(R.id.tvBall3);
                tvResult3 = (TextView) view.findViewById(R.id.tvResult3);
                arrTvBall[2] = tvBall3;
                arrTvResult[2] = tvResult3;
                tvBall4 = (TextView) view.findViewById(R.id.tvBall4);
                tvResult4 = (TextView) view.findViewById(R.id.tvResult4);
                arrTvBall[3] = tvBall4;
                arrTvResult[3] = tvResult4;
                tvBall5 = (TextView) view.findViewById(R.id.tvBall5);
                tvResult5 = (TextView) view.findViewById(R.id.tvResult5);
                arrTvBall[4] = tvBall5;
                arrTvResult[4] = tvResult5;
            }
        }
    }
    
}
