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
import com.desheng.base.model.AnalysisSides;
import com.google.gson.Gson;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.SpaceTopDecoration;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 两面数据分析
 * Created by lee on 2018/3/29.
 */
public class FragTabAnalysisSides extends AbBaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    
    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvOpen;
    
    private AdapterAnalysisSides adapter;
    private ILotteryKind lotteryKind;
    
    
    public static FragTabAnalysisSides newIns(int lotteryId) {
        FragTabAnalysisSides fragment = new FragTabAnalysisSides();
        Bundle bundle = new Bundle();
        bundle.putInt("lotteryId", lotteryId);
        fragment.setArguments(bundle);
        return fragment;
    }
    
    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_analysis_sides;
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
        HttpAction.getAnalysisSides(getActivity(), lotteryKind, new Callback<HttpAction.RespAnalysisSides>() {
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
            public HttpAction.RespAnalysisSides parseNetworkResponse(Response response, int id) throws
                    Exception {
                if (getActivity() == null) {
                    return null;
                }
                String string = response.body().string();
                if (Strs.isNotEmpty(string)) {
                    return new Gson().fromJson(string, HttpAction.RespAnalysisSides.class);
                } else {
                    return null;
                }
            }
            
            @Override
            public void onError(Call call, Exception e, int id) {
                if (getActivity() == null) {
                    return ;
                }
                Toasts.show(getActivity(), "获取双面分析数据失败!", false);
            }
            
            @Override
            public void onResponse(HttpAction.RespAnalysisSides response, int id) {
                if (getActivity() == null) {
                    return ;
                }
                if (response != null) {
                    if (response.errorCode == 0 && response.result != null && response.result.data != null) {
                        List<AnalysisSides.DataBean> listTitle = new ArrayList<>();
                        List<AnalysisSides.DataBean.ListBean> listInfo = new ArrayList<>();
                        for (int i = 0; i < response.result.data.size(); i++) {
                            listTitle = response.result.data;
                            listInfo.add(response.result.data.get(i).list.get(0));
                        }
                        adapter = new AdapterAnalysisSides(getActivity(), listTitle, listInfo);
                        rvOpen.setAdapter(adapter);
                    } else {
                        Toasts.show(getActivity(), response.message, false);
                    }
                } else {
                    Toasts.show(getActivity(), "获取双面分析数据失败!", false);
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
    
    
    protected class AdapterAnalysisSides extends BaseQuickAdapter<AnalysisSides.DataBean.ListBean, AdapterAnalysisSides.AnalysisSidesViewHolder> {
        
        private Context ctx;
        List<AnalysisSides.DataBean> totalData;
        
        public AdapterAnalysisSides(Context ctx, List<AnalysisSides.DataBean> totalData,  List<AnalysisSides.DataBean.ListBean> data) {
            super(R.layout.item_tab_analysis_sides, data);
            this.ctx = ctx;
            this.totalData = totalData;
            //setMultiTypeDelegate(new OpenLotteryTypeDelegate());
        }
        
        @Override
        protected void convert(AnalysisSidesViewHolder helper, AnalysisSides.DataBean.ListBean item) {
            String ball = "一";
           /* switch (item.rank) {
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
            }*/
            helper.tvIssue.setText(totalData.get(helper.getAdapterPosition()).startIssue + " - " + totalData.get(helper.getAdapterPosition()).endIssue + " " + "第" + ball + "球");
            
            helper.tvOdd.setText(Strs.of(item.singleCount));
            helper.tvEven.setText(Strs.of(item.doubleCount));
            helper.tvBig.setText(Strs.of(item.bigCount));
            helper.tvSmall.setText(Strs.of(item.smallCount));
        }
        
        private void setBalls(LinearLayout vg, List<AnalysisHotCold.DataBean.ListBeanX.ListBean> balls) {
            for (int i = 0; i < vg.getChildCount(); i++) {
                if (i > 0) {
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
        
        public class AnalysisSidesViewHolder extends BaseViewHolder {
            private TextView tvIssue;
            private TextView tvOdd;
            private TextView tvEven;
            private TextView tvBig;
            private TextView tvSmall;
            
            public AnalysisSidesViewHolder(View view) {
                super(view);
                tvIssue = (TextView) view.findViewById(R.id.tvIssue);
                tvOdd = (TextView) view.findViewById(R.id.tvOdd);
                tvEven = (TextView) view.findViewById(R.id.tvEven);
                tvBig = (TextView) view.findViewById(R.id.tvBig);
                tvSmall = (TextView) view.findViewById(R.id.tvSmall);
            }
        }
    }
    
    
}
