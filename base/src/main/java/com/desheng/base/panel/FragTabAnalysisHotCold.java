package com.desheng.base.panel;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
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
import com.desheng.base.model.AnalysisHotCold;
import com.google.gson.Gson;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.SpaceTopDecoration;

import java.util.List;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 冷热分析
 * Created by lee on 2018/3/29.
 */
public class FragTabAnalysisHotCold extends AbBaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    
    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvOpen;
    
    private AdapterAnalysisHotCold adapter;
    private ILotteryKind lotteryKind;
    
    
    public static FragTabAnalysisHotCold newIns(int lotteryId) {
        FragTabAnalysisHotCold fragment = new FragTabAnalysisHotCold();
        Bundle bundle = new Bundle();
        bundle.putInt("lotteryId", lotteryId);
        fragment.setArguments(bundle);
        return fragment;
    }
    
    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_analysis_hotcold;
    }
    
    @Override
    public void init(View root) {
        int lotteryId = getArguments().getInt("lotteryId", 0);
        lotteryKind = CtxLottery.getIns().findLotteryKind(lotteryId);
        
        srlRefresh = (SwipeRefreshLayout) root.findViewById(R.id.srlRefresh);
        srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);
        
        rvOpen = (RecyclerView) root.findViewById(R.id.rvOpen);
        rvOpen.setLayoutManager(Views.genLinearLayoutManagerV(getActivity()));
        rvOpen.addItemDecoration(new SpaceTopDecoration(7));
        
    }
    
    @Override
    public void onShow() {
        super.onShow();
        getData();
    }
    
    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        getData();
    }
    
    public void getData() {
        HttpAction.getAnalysisHotCold(getActivity(), lotteryKind, new Callback<HttpAction.RespAnalysisHotCold>() {
            @Override
            public void onBefore(Request request, int id) {
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(true);
                    }
                }, 100);
            }
            
            @Override
            public HttpAction.RespAnalysisHotCold parseNetworkResponse(Response response, int id) throws
                    Exception {
                if (getActivity() == null) {
                    return null;
                }
                String string = response.body().string();
                if (Strs.isNotEmpty(string)) {
                    return new Gson().fromJson(string, HttpAction.RespAnalysisHotCold.class);
                } else {
                    return null;
                }
            }
            
            @Override
            public void onError(Call call, Exception e, int id) {
                
                if (getActivity() != null) {
                    Toasts.show(getActivity(), "获取冷热分析数据失败!", false);
                }
            }
            
            @Override
            public void onResponse(HttpAction.RespAnalysisHotCold response, int id) {
                if (getActivity() == null) {
                    return ;
                }
                if (response != null) {
                    if (response.errorCode == 0 && response.result != null && response.result.data != null) {
                        adapter = new AdapterAnalysisHotCold(getActivity(), response.result.data);
                        rvOpen.setAdapter(adapter);
                    } else {
                        Toasts.show(getActivity(), response.message, false);
                    }
                } else {
                    Toasts.show(getActivity(), "获取冷热分析数据失败!", false);
                }
            }
            
            @Override
            public void onAfter(int id) {
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(false);
                    }
                }, 600);
                
            }
        });
    }
    
    @Override
    public void onHide() {
        super.onHide();
    }
    
    
    protected class AdapterAnalysisHotCold extends BaseQuickAdapter<AnalysisHotCold.DataBean, AdapterAnalysisHotCold.AnalysisHotColdViewHolder> {
        
        private Context ctx;
        
        public AdapterAnalysisHotCold(Context ctx, List<AnalysisHotCold.DataBean> data) {
            super(R.layout.item_tab_analysis_hotcold, data);
            this.ctx = ctx;
            //setMultiTypeDelegate(new OpenLotteryTypeDelegate());
        }
        
        @Override
        protected void convert(AnalysisHotColdViewHolder helper, AnalysisHotCold.DataBean item) {
            String ball = "";
            switch (item.rank){
                case 1:
                    ball = "一";
                    break;
                case 2:
                    ball = "二";
                    break;
                case 3:
                    ball = "三";
                    break;
                case 4:
                    ball = "四";
                    break;
                case 5:
                    ball = "五";
                    break;
                case 6:
                    ball = "六";
                    break;
                case 7:
                    ball = "七";
                    break;
                case 8:
                    ball = "八";
                    break;
                case 9:
                    ball = "九";
                    break;
                case 10:
                    ball = "十";
                    break;
                case 11:
                    ball = "十一";
                    break;
                default:
            }
            helper.tvIssue.setText("第" + ball + "球");
            
            //热
            List<AnalysisHotCold.DataBean.ListBeanX.ListBean> balls = item.list.get(0).list;
            setBalls(helper.vgHot, balls);
            //温
            balls = item.list.get(1).list;
            setBalls(helper.vgWarm, balls);
            //冷
            balls = item.list.get(2).list;
            setBalls(helper.vgCold, balls);
            
        }
        
        private void setBalls(LinearLayout vg, List<AnalysisHotCold.DataBean.ListBeanX.ListBean> balls){
            for (int i = 0; i < vg.getChildCount(); i++) {
                if(i > 0){
                    vg.removeViewAt(i);
                }
            }
            for (int i = 0; i < balls.size(); i++) {
                TextView tvBall = new TextView(ctx);
                tvBall.setBackgroundResource(R.drawable.sh_bd_circle_blue);
                tvBall.setTextSize(12);
                tvBall.setTextColor(Color.BLACK);
                tvBall.setText(Strs.of(balls.get(i).drawCode));
                tvBall.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams param = Views.genLLParamsByDP(25, 25);
                param.leftMargin = 5;
                param.rightMargin = 5;
                vg.addView(tvBall, param);
            }
        }
    
        public class AnalysisHotColdViewHolder extends BaseViewHolder {
            private TextView tvIssue;
            private LinearLayout vgHot;
            private LinearLayout vgWarm;
            private LinearLayout vgCold;
        
        
            public AnalysisHotColdViewHolder(View view) {
                super(view);
                tvIssue = (TextView) view.findViewById(R.id.tvIssue);
                vgHot = (LinearLayout) view.findViewById(R.id.vgHot);
                vgWarm = (LinearLayout) view.findViewById(R.id.vgWarm);
                vgCold = (LinearLayout) view.findViewById(R.id.vgCold);
            }
        }
    }
    
   
}
