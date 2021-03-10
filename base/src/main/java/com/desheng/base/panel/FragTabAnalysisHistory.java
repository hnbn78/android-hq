package com.desheng.base.panel;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridView;
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
import com.desheng.base.adapter.AdapterNumGrid;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.context.ILotteryKind;
import com.desheng.base.model.AnalysisHistory;
import com.google.gson.Gson;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.act.util.UIHelper;
import com.pearl.view.SpaceTopDecoration;
import com.zhy.view.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 历史开奖
 * Created by lee on 2018/3/29.
 */
public class FragTabAnalysisHistory extends AbBaseFragment implements SwipeRefreshLayout.OnRefreshListener, ActAnalysisMain.OnTitleRightBtnClickListener {
    public static final int [] arrNums = new int[]{
            R.id.tbNum0,
            R.id.tbNum1, R.id.tbNum2, R.id.tbNum3, R.id.tbNum4, R.id.tbNum5,
            R.id.tbNum6, R.id.tbNum7, R.id.tbNum8, R.id.tbNum9, R.id.tbNum10,
    };
    
    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvOpen;
    private LinearLayout vgMenu;
    
    private ArrayList<HashMap<String, Object>> listMenuData ;
    private AdapterAnalysisHistory adapter;
    private ILotteryKind lotteryKind;
    
    private CheckBox[] arrNumButton;
    private AdapterNumGrid menuAdapter;
    private GridView gvCode;
    private TextView btnReset;
    private CompoundButton.OnCheckedChangeListener checkedChangeListener;
    
    public static FragTabAnalysisHistory newIns(int lotteryId) {
        FragTabAnalysisHistory fragment = new FragTabAnalysisHistory();
        Bundle bundle = new Bundle();
        bundle.putInt("lotteryId", lotteryId);
        fragment.setArguments(bundle);
        return fragment;
    }
    
    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_analysis_history;
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
        
        vgMenu = (LinearLayout) root.findViewById(R.id.vgMenu);
        vgMenu.setVisibility(View.GONE);
        gvCode = (GridView)root.findViewById(R.id.gvCode);
        
        arrNumButton = new CheckBox [arrNums.length];
        checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setTextColor(isChecked ? Color.WHITE : Color.GRAY);
                
                int tagNum = ((Integer) buttonView.getTag());
                for (int i = 0; i < arrNumButton.length; i++) {
                    for (int j = 0; j < listMenuData.size(); j++) {
                        int code = ((Integer) listMenuData.get(j).get("code"));
                        if(lotteryKind.getOriginType() == CtxLottery.getIns().originLotteryType("T11S5")){
                            code = code - 1;
                        }
                        if (code == tagNum) { //当前号码的
                            if (isChecked) {
                                listMenuData.get(j).put("alpha", 1.0);
                            } else {
                                listMenuData.get(j).put("alpha", 0.3);
                            }
                        } else { //其他号码的
                            if (arrNumButton[code].isChecked()) {
                                listMenuData.get(j).put("alpha", 1.0);
                            } else {
                                listMenuData.get(j).put("alpha", 0.3);
                            }
                        }
                    }
                }
                menuAdapter.notifyDataSetChanged();
            }
        };
        
        for (int i = 0; i < arrNums.length; i++) {
            arrNumButton[i] = (CheckBox) root.findViewById(arrNums[i]);
            if(lotteryKind.getOriginType() == CtxLottery.getIns().originLotteryType("T11S5")){
                arrNumButton[i].setTag(i + 1);
                arrNumButton[i].setText(Strs.of(i + 1));
            }else{
                arrNumButton[i].setTag(i);
            }
            
            arrNumButton[i].setOnCheckedChangeListener(checkedChangeListener);
        }
        
        btnReset = (TextView) root.findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < arrNumButton.length; i++) {
                    arrNumButton[i].setOnCheckedChangeListener(null);
                    arrNumButton[i].setTextColor(Color.BLACK);
                    arrNumButton[i].setChecked(false);
                }
                for (int i = 0; i < listMenuData.size(); i++) {
                    listMenuData.get(i).put("alpha", 1.0);
                }
                menuAdapter.notifyDataSetChanged();
                vgMenu.setVisibility(View.GONE);
                srlRefresh.setVisibility(View.VISIBLE);
            }
        });
        
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
        HttpAction.getAnalysisHistory(getActivity(), lotteryKind, new Callback<HttpAction.RespAnalysisHistory>() {
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
            public HttpAction.RespAnalysisHistory parseNetworkResponse(Response response, int id) throws
                    Exception {
                if (getActivity() == null) {
                    return null;
                }
                String string = response.body().string();
                if (Strs.isNotEmpty(string)) {
                    return new Gson().fromJson(string, HttpAction.RespAnalysisHistory.class);
                } else {
                    return null;
                }
            }
            
            @Override
            public void onError(Call call, Exception e, int id) {
                if (getActivity() == null) {
                    return ;
                }
                Toasts.show(getActivity(), "获取开奖历史数据失败!", false);
            }
            
            @Override
            public void onResponse(HttpAction.RespAnalysisHistory response, int id) {
                if (getActivity() == null) {
                    return ;
                }
                if (response != null && getActivity() != null) {
                    if (response.errorCode == 0 && response.result != null && response.result.data != null) {
                        adapter = new AdapterAnalysisHistory(getActivity(), response.result.data);
                        rvOpen.setAdapter(adapter);
                        listMenuData = new ArrayList<HashMap<String, Object>>();
                        int columnSize = 0;
                        for (int i = 0; i < response.result.data.size(); i++) {
                            String [] arrCode = response.result.data.get(i).preDrawCode.split(",");
                            for (int j = 0; j < arrCode.length; j++) {
                                HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put("code", Strs.parse(arrCode[j], 0));
                                map.put("alpha", 1.0);
                                listMenuData.add(map);
                            }
                            columnSize = arrCode.length;
                            if(lotteryKind.getOriginType() == CtxLottery.getIns().originLotteryType("T11S5")){
                                arrNumButton[10].setVisibility(View.VISIBLE);
                            }else{
                                arrNumButton[10].setVisibility(View.GONE);
                            }
                        }
                        menuAdapter = new AdapterNumGrid(getActivity(), listMenuData);
                        gvCode.setNumColumns(columnSize);
                        gvCode.setAdapter(menuAdapter);
                    } else {
                        Toasts.show(getActivity(), response.message, false);
                    }
                } else {
                    Toasts.show(getActivity(), "获取开奖历史数据失败!", false);
                }
            }
            
            @Override
            public void onAfter(int id) {
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(getActivity() != null){
                            srlRefresh.setRefreshing(false);
                        }
                    }
                }, 600);
                
            }
        });
    }
    
    @Override
    public void onHide() {
        super.onHide();
    }
    
    
    @Override
    public void onRightBtnClick() {
        if(vgMenu.getVisibility() == View.VISIBLE){
            vgMenu.setVisibility(View.GONE);
            srlRefresh.setVisibility(View.VISIBLE);
        }else{
            vgMenu.setVisibility(View.VISIBLE);
            /*for (int i = 0; i < arrNumButton.length; i++) {
                arrNumButton[i].setChecked(false);
                arrNumButton[i].setTextColor(Color.BLACK);
                arrNumButton[i].setOnCheckedChangeListener(checkedChangeListener);
            }*/
            srlRefresh.setVisibility(View.GONE);
        }
    }
    
    
    protected static class AdapterAnalysisHistory extends BaseQuickAdapter<AnalysisHistory.DataBean, AnalysisHistoryHolder> {
        
        private Context ctx;
        
        public AdapterAnalysisHistory(Context ctx, List<AnalysisHistory.DataBean> data) {
            super(R.layout.item_tab_analysis_history, data);
            this.ctx = ctx;
            
            //setMultiTypeDelegate(new OpenLotteryTypeDelegate());
        }
        
        @Override
        protected void convert(AnalysisHistoryHolder helper, AnalysisHistory.DataBean item) {
            String str = String.format("开奖期数: <font color=\"#FF0000\">%s</font>期", Strs.of(item.preDrawIssue));
            helper.tvIssue.setText(Html.fromHtml(str));
            
            //开奖号球
            String[] arrCode = item.preDrawCode.split(",");
            int num = 0;
            TextView tvNum = null;
            
            helper.llSquareMisc.setVisibility(View.VISIBLE);
            helper.llSquareMisc.removeAllViews();
            for (int i = 0; i < arrCode.length; i++) {
                num = Integer.parseInt(arrCode[i]);
                tvNum = (TextView) LayoutInflater.from(ctx).inflate(R.layout.view_square_misc_round, helper.llSquareMisc, false);
                tvNum.setText(arrCode[i]);
                tvNum.setBackgroundDrawable(Views.fromDrawables(R.drawable.sh_bg_square_blue_solid_round));
                GradientDrawable bg = (GradientDrawable) tvNum.getBackground();
                bg.setColor(UIHelper.getNumBgColor(num));
                bg.setStroke(1, UIHelper.getNumBgColor(num));
                helper.llSquareMisc.addView(tvNum);
            }
            helper.llSquareMisc.requestLayout();
            
            if (item.sumNum != -1) {
                helper.tvNum.setText(Strs.of(item.sumNum));
            } else {
                helper.tvNum.setText(Strs.of(item.sumFS));
            }
            helper.tvNum.setTextColor(Color.BLUE);
            
            /**
             * 0 大 1 小
             0 双 1 单
             0 龙  1虎
             */
            String danShuangStr = "";
            int danShuangColor = 0;
            if (item.sumSingleDouble == 1) {
                danShuangStr = "双";
                danShuangColor = Color.RED;
            } else if (item.sumSingleDouble == 0) {
                danShuangStr = "单";
                danShuangColor = Color.BLUE;
            }
            helper.tvDanSuang.setText(danShuangStr);
            helper.tvDanSuang.setTextColor(danShuangColor);
            
            String daXiaoStr = "";
            int daXiaoColor = 0;
            if (item.sumBigSmall == 0) {
                daXiaoStr = "大";
                daXiaoColor = Color.RED;
            } else if (item.sumBigSmall == 1) {
                daXiaoStr = "小";
                daXiaoColor = Color.BLUE;
            }
            helper.tvDaXiao.setText(daXiaoStr);
            helper.tvDaXiao.setTextColor(daXiaoColor);
            
            if (item.dragonTiger != -1) {
                for (int i = 1; i < 5; i++) {
                    helper.arrLongHu[i].setVisibility(View.GONE);
                }
                String longHuStr = "";
                int longHuColor = 0;
                if (item.dragonTiger == 0) {
                    longHuStr = "龙";
                    longHuColor = Color.RED;
                } else if (item.dragonTiger == 1) {
                    longHuStr = "虎";
                    longHuColor = Color.BLUE;
                } else if(item.dragonTiger == 2){
                    longHuStr = "和";
                    longHuColor = Color.GREEN;
                }
                helper.arrLongHu[0].setText(longHuStr);
                helper.arrLongHu[0].setTextColor(longHuColor);
            } else {
                int[] arrLongHu = new int[]{item.firstDT, item.secondDT, item.thirdDT, item.fourthDT, item.fifthDT};
                for (int i = 0; i < 5; i++) {
                    String longHuStr = "";
                    int longHuColor = 0;
                    if (arrLongHu[i] == 0) {
                        longHuStr = "龙";
                        longHuColor = Color.RED;
                    } else if (arrLongHu[i] == 1) {
                        longHuStr = "虎";
                        longHuColor = Color.BLUE;
                    } else if(item.dragonTiger == 2){
                        longHuStr = "和";
                        longHuColor = Color.GREEN;
                    }
                    helper.arrLongHu[i].setVisibility(View.VISIBLE);
                    helper.arrLongHu[i].setText(longHuStr);
                    helper.arrLongHu[i].setTextColor(longHuColor);
                }
                
            }
        }
    }
    
    public static class AnalysisHistoryHolder extends BaseViewHolder {
        private TextView tvIssue;
        private FrameLayout vgBalls;
        private FlowLayout llSquareMisc;
        private LinearLayout llFuncs;
        private TextView tvChapian;
        private TextView tvNum;
        private TextView tvDaXiao;
        private TextView tvDanSuang;
        private TextView tvLongHuTitle;
        private TextView tvLongHu1;
        private TextView tvLongHu2;
        private TextView tvLongHu3;
        private TextView tvLongHu4;
        private TextView tvLongHu5;
        private TextView[] arrLongHu;
        
        public AnalysisHistoryHolder(View view) {
            super(view);
            tvIssue = (TextView) view.findViewById(R.id.tvIssue);
            vgBalls = (FrameLayout) view.findViewById(R.id.vgBalls);
            llSquareMisc = (FlowLayout) view.findViewById(R.id.llSquareMisc);
            llFuncs = (LinearLayout) view.findViewById(R.id.llFuncs);
            tvChapian = (TextView) view.findViewById(R.id.tvChapian);
            tvNum = (TextView) view.findViewById(R.id.tvNum);
            tvDaXiao = (TextView) view.findViewById(R.id.tvDaXiao);
            tvDanSuang = (TextView) view.findViewById(R.id.tvDanSuang);
            tvLongHuTitle = (TextView) view.findViewById(R.id.tvLongHuTitle);
            tvLongHu1 = (TextView) view.findViewById(R.id.tvLongHu1);
            tvLongHu2 = (TextView) view.findViewById(R.id.tvLongHu2);
            tvLongHu3 = (TextView) view.findViewById(R.id.tvLongHu3);
            tvLongHu4 = (TextView) view.findViewById(R.id.tvLongHu4);
            tvLongHu5 = (TextView) view.findViewById(R.id.tvLongHu5);
            arrLongHu = new TextView[]{tvLongHu1, tvLongHu2, tvLongHu3, tvLongHu4, tvLongHu5};
        }
    }
    
}
