package com.desheng.base.panel;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
import com.desheng.base.model.AnalysisLuZhu;
import com.google.gson.Gson;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.SpaceTopDecoration;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 历史开奖
 * Created by lee on 2018/3/29.
 */
public class FragTabAnalysisLuZhu extends AbBaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String MODE_DAXIAO_2 = "daxiao_1";
    public static final String MODE_DANSHUANG_1 = "danshuang_1";
    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvOpen;
    
    private AdapterAnalysisLuZhu adapter;
    private ILotteryKind lotteryKind;
    
    
    public static FragTabAnalysisLuZhu newIns(int lotteryId) {
        FragTabAnalysisLuZhu fragment = new FragTabAnalysisLuZhu();
        Bundle bundle = new Bundle();
        bundle.putInt("lotteryId", lotteryId);
        fragment.setArguments(bundle);
        return fragment;
    }
    
    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_analysis_luzhu;
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
        HttpAction.getAnalysisLuZhu(getActivity(), lotteryKind, new Callback<HttpAction.RespAnalysisLuZhu>() {
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
            public HttpAction.RespAnalysisLuZhu parseNetworkResponse(Response response, int id) throws
                    Exception {
                if (getActivity() == null) {
                    return null;
                }
                String string = response.body().string();
                if (Strs.isNotEmpty(string)) {
                    return new Gson().fromJson(string, HttpAction.RespAnalysisLuZhu.class);
                } else {
                    return null;
                }
            }
            
            @Override
            public void onError(Call call, Exception e, int id) {
                if (getActivity() == null) {
                    return ;
                }
                Toasts.show(getActivity(), "获取路珠分析数据失败!", false);
            }
            
            @Override
            public void onResponse(HttpAction.RespAnalysisLuZhu response, int id) {
                if (getActivity() == null) {
                    return ;
                }
                if (response != null) {
                    if (response.errorCode == 0 && response.result != null && response.result.data != null) {
                        List<Object> delete = new ArrayList<Object>();
                        for (int i = 0; i < response.result.data.size(); i++) {
                            if (response.result.data.get(i).state == 3) {
                                delete.add(response.result.data.get(i));
                            }
                        }
                        for (Object object: delete) {
                            response.result.data.remove(object);
                        }
                        
                        adapter = new AdapterAnalysisLuZhu(getActivity(), response.result.data);
                        rvOpen.setAdapter(adapter);
                        
                    } else {
                        Toasts.show(getActivity(), response.message, false);
                    }
                } else {
                    Toasts.show(getActivity(), "获取路珠分析数据失败!", false);
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
    
    
    protected class AdapterAnalysisLuZhu extends BaseQuickAdapter<AnalysisLuZhu.DataBean, AdapterAnalysisLuZhu.AnalysisLuZhuViewHolder> {
        
        private Context ctx;
        private AdapterColumn[] arrAdapter;
        
        public AdapterAnalysisLuZhu(Context ctx, List<AnalysisLuZhu.DataBean> data) {
            super(R.layout.item_tab_analysis_luzhu, data);
            this.ctx = ctx;
            arrAdapter = new AdapterColumn[data.size()];
            //setMultiTypeDelegate(new OpenLotteryTypeDelegate());
        }
        
        @Override
        protected void convert(AnalysisLuZhuViewHolder helper, AnalysisLuZhu.DataBean item) {
            helper.itemView.setVisibility(View.VISIBLE);
            String mode = "";
            String state1 = "";
            String state2 = "";
            String rank = "";
            if (item.state == 1) {
                mode = MODE_DANSHUANG_1;
                state1 = "单";
                state2 = "双";
            } else if (item.state == 2) {
                mode = MODE_DAXIAO_2;
                state1 = "大";
                state2 = "小";
            } else if (item.state == 3) {
                helper.itemView.setVisibility(View.GONE);
                return;
            }
            if (item.rank <= 5) {
                rank = "第" + item.rank + "球";
            } else if (item.rank == 7) {
                rank = "和尾";
            } else {
                rank = "总和数";
            }
            
            String str = String.format("%s 累计:%s(<font color=\"#ff0000\">%s</font>) %s(<font color=\"#ff0000\">%s</font>) %s",
                    Strs.of(item.date), state1, item.totals.get(0), state2, item.totals.get(1), rank);
            helper.tvIssue.setText(Html.fromHtml(str));
            
            ArrayList<List<Integer>> listColmn = new ArrayList<>();
            listColmn.add(new ArrayList<Integer>());
            for (int i = 0; i < item.roadBeads.size(); i++) {
                Integer curr = item.roadBeads.get(i);
                List<Integer> span = listColmn.get(listColmn.size() - 1);
                if (i == 0) {
                    span.add(curr);
                } else if (span.size() > 0 && span.get(0).intValue() == curr.intValue()) {
                    span.add(curr);
                } else {
                    span = new ArrayList<>();
                    span.add(curr);
                    listColmn.add(span);
                }
            }
            if (arrAdapter[helper.getAdapterPosition()] != null) {
                helper.rvColumn.setAdapter(arrAdapter[helper.getAdapterPosition()]);
            } else {
                arrAdapter[helper.getAdapterPosition()] = new AdapterColumn(ctx, listColmn, mode);
                helper.rvColumn.setAdapter(arrAdapter[helper.getAdapterPosition()]);
            }
            
        }
        
        public class AnalysisLuZhuViewHolder extends BaseViewHolder {
            private TextView tvIssue;
            private RecyclerView rvColumn;
            
            public AnalysisLuZhuViewHolder(View view) {
                super(view);
                tvIssue = (TextView) view.findViewById(R.id.tvIssue);
                rvColumn = (RecyclerView) view.findViewById(R.id.rvColumn);
                rvColumn.setLayoutManager(Views.genLinearLayoutManagerH(getContext()));
            }
        }
    }
    
    
    public class AdapterColumn extends BaseQuickAdapter<List<Integer>, AdapterColumn.ColumnViewHolder> {
        private int maxCount;
        private String mode;
        private Context ctx;
        
        
        public AdapterColumn(Context ctx, ArrayList<List<Integer>> data, String mode) {
            super(R.layout.item_tab_analysis_luzhu_cell, data);
            this.ctx = ctx;
            this.mode = mode;
            maxCount = 0;
            
            for (int i = 0; i < data.size(); i++) {
                if (maxCount < data.get(i).size()) {
                    maxCount = data.get(i).size();
                }
            }
        }
        
        @Override
        protected void convert(ColumnViewHolder helper, List<Integer> item) {
            String txt = "";
            int bgColor = Color.WHITE;
            int txtColor = 0;
            
            ViewGroup vgLinear = (ViewGroup) helper.itemView;
            ViewGroup.LayoutParams param = vgLinear.getLayoutParams();
            param.height = maxCount * Views.sp2px(25) + Views.sp2px(10);
            vgLinear.setLayoutParams(param);
            
            for (int j = 0; j < vgLinear.getChildCount(); j++) {
                if (j > 0) {
                    vgLinear.removeViewAt(j);
                }
            }
            for (int i = 0; i < item.size(); i++) {
                TextView tvCell;
                if (i == 0) {
                    tvCell = helper.tvCell;
                } else {
                    tvCell = new TextView(ctx);
                    tvCell.setTextSize(Views.px2sp(helper.tvCell.getTextSize()));
                    tvCell.setLayoutParams(helper.tvCell.getLayoutParams());
                    tvCell.setGravity(Gravity.CENTER);
                    vgLinear.addView(tvCell);
                }
                
                if (lotteryKind.getOriginType() == CtxLottery.getIns().originLotteryType("T11S5")) {
                    if (MODE_DAXIAO_2.equals(mode)) {
                        if (item.get(i) == 1) {
                            txt = "大";
                            bgColor = Views.fromColors(R.color.colorPrimaryInverse);
                            txtColor = Color.RED;
                        } else if (item.get(i) == -1) {
                            txt = "小";
                            bgColor = Color.WHITE;
                            txtColor = Color.BLACK;
                        } else if (item.get(i) == 0) {
                            txt = "和";
                            bgColor = Views.fromColors(R.color.gray_light);
                            txtColor = Color.WHITE;
                        }
                    } else if (MODE_DANSHUANG_1.equals(mode)) {
                        if (item.get(i) == -1) {
                            txt = "单";
                            bgColor = Color.WHITE;
                            txtColor = Color.BLACK;
                        } else if (item.get(i) == 1) {
                            txt = "双";
                            bgColor = Views.fromColors(R.color.colorPrimaryInverse);
                            txtColor = Color.RED;
                        } else if (item.get(i) == 0) {
                            txt = "和";
                            bgColor = Views.fromColors(R.color.gray_light);
                            txtColor = Color.WHITE;
                        }
                    }
                    //case KUAI3:
                }else if(lotteryKind.getOriginType() == CtxLottery.getIns().originLotteryType("OTHER") ||
                        CtxLottery.getIns().originLotteryType("SSC") ==  lotteryKind.getOriginType()){
                        if (MODE_DAXIAO_2.equals(mode)) {
                            if (item.get(i) == 1) {
                                txt = "大";
                                bgColor = Views.fromColors(R.color.colorPrimaryInverse);
                                txtColor = Color.RED;
                            } else if (item.get(i) == 0) {
                                txt = "小";
                                bgColor = Color.WHITE;
                                txtColor = Color.BLACK;
                            }
                        } else if (MODE_DANSHUANG_1.equals(mode)) {
                            if (item.get(i) == 1) {
                                txt = "单";
                                bgColor = Color.WHITE;
                                txtColor = Color.BLACK;
                            } else if (item.get(i) == 0) {
                                txt = "双";
                                bgColor = Views.fromColors(R.color.colorPrimaryInverse);
                                txtColor = Color.RED;
                            }
                        }
                }
                
                tvCell.setText(txt);
                tvCell.setTextColor(txtColor);
                vgLinear.setBackgroundColor(bgColor);
            }
            
        }
        
        public class ColumnViewHolder extends BaseViewHolder {
            private TextView tvCell;
            
            public ColumnViewHolder(View view) {
                super(view);
                tvCell = (TextView) view.findViewById(R.id.tvCell);
            }
        }
    }
    
    
}
