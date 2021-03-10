package com.desheng.app.toucai.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.context.LotteryKind;
import com.desheng.app.toucai.controller.BaseCtrlPlay;
import com.desheng.base.model.LotteryOrderInfo;
import com.desheng.base.panel.ActBettingRecordList;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * 玩法底部订单列表
 * Created by lee on 2018/3/13.
 */
public class PlayOrderListView extends FrameLayout implements View.OnClickListener {
    private TextView labBonus;
    private RecyclerView rvOrderList;
    private List<LotteryOrderInfo> listOrders = new ArrayList<>();
    private Context ctx;
    private int lotteryId;
    private OrderAdapter adapter;
    private OnOneKeyBuyListener listener;
    
    public PlayOrderListView(@NonNull Context context) {
        super(context);
        init(context);
    }
    
    public PlayOrderListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    public PlayOrderListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    
    private void init(Context context) {
        RelativeLayout inner = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.view_play_orider_list, this, false);
        labBonus = (TextView) inner.findViewById(R.id.labBonus);
        inner.findViewById(R.id.tvMore).setOnClickListener(this);
        rvOrderList = (RecyclerView) inner.findViewById(R.id.rvOrderList);
        rvOrderList.setLayoutManager(Views.genLinearLayoutManagerV(context));
        addView(inner);
    }
    
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvMore:
                ActBettingRecordList.launch((Activity) ctx);
                break;
        }
    }
    
    public void setConfig(Context ctx, int lotteryId) {
        this.ctx = ctx;
        this.lotteryId = lotteryId;
        adapter = new OrderAdapter(ctx);
        rvOrderList.setAdapter(adapter);
        //rvOrderList.addItemDecoration(new ItemDeviderDecoration(ctx, ItemDeviderDecoration.VERTICAL_LIST));
        adapter.bindToRecyclerView(rvOrderList);
        search();
    }
    
    public void setOnOneKeyBuyListener(OnOneKeyBuyListener listener) {
        this.listener = listener;
    }
    
    
    public void refresh() {
        search();
    }
    
    public void search() {
        String currDate = Dates.getStringByFormat(System.currentTimeMillis(), Dates.dateFormatYMD);
        long startMills = Dates.getMillisOfStr(currDate, Dates.dateFormatYMD);
        long stopMills = Dates.getMillisOfStr(currDate, Dates.dateFormatYMD) + 24 * 60 * 60 * 1000 - 1000;
        String startTime = Dates.getStringByFormat(startMills, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(stopMills, Dates.dateFormatYMDHMS);
        
        HttpActionTouCai.searchOrder(this.ctx, null, null, startTime, stopTime, 0, 5, null, new AbHttpResult() {
            
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
            
            }
            
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpActionTouCai.RespSearchOrder.class);
            }
            
            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                HttpActionTouCai.RespSearchOrder respSearchOrder = getField(extra, "data", null);
                if (respSearchOrder != null) {
                    listOrders.clear();
                    listOrders.addAll(respSearchOrder.list);
                    adapter.setNewData(listOrders);
                }
                return true;
            }
            
            @Override
            public void onAfter(int id) {
            
            }
        });
    }
    
    protected class OrderAdapter extends BaseQuickAdapter<LotteryOrderInfo, OrderAdapter.OrderVH> {
        
        private final Context ctx;
        
        public OrderAdapter(Context ctx) {
            super(R.layout.item_lottery_order, new ArrayList<LotteryOrderInfo>());
            this.ctx = ctx;
        }
        
        @Override
        protected void convert(OrderVH holder,  LotteryOrderInfo item) {
            holder.tvIssueNo.setText(item.issue + "期");
            holder.tvPlayName.setText(item.lottery + " - "  + item.method);
            holder.tvOrderMoney.setText("投注金额: " + item.money + "元");
            holder.tvOrderContent.setText("号码:" + item.content);
            holder.tvResult.setText(Strs.isEmpty(item.openCode) ? "未开奖" : "开奖号码:" + item.openCode);
            holder.tvBuyMore.setTag(item);
            holder.tvBuyMore.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        LotteryOrderInfo item = (LotteryOrderInfo) v.getTag();
                        String requestText = BaseCtrlPlay.convertRequestText(item.method, item.methodCode, item.content);
                        if(Strs.isEmpty(requestText)){
                            Toasts.show(ctx, "尚不支持再次投注!", false);
                            return;
                        }
                        listener.onBuyOneKey(LotteryKind.find(item.lotteryId).getCode(), null,
                                item.methodCode, requestText);
                    }
                }
            });
        }
        
        protected class OrderVH extends BaseViewHolder {
            public TextView tvIssueNo;
            public TextView tvPlayName;
            public TextView tvOrderMoney;
            public TextView tvBuyMore;
            public TextView tvResult;
            public TextView tvOrderContent;
            
            public OrderVH(View view) {
                super(view);
                tvIssueNo = (TextView) view.findViewById(R.id.tvIssueNo);
                tvPlayName = (TextView) view.findViewById(R.id.tvPlayName);
                tvOrderMoney = (TextView) view.findViewById(R.id.tvOrderMoney);
                tvBuyMore = (TextView) view.findViewById(R.id.tvBuyMore);
                tvResult = (TextView) view.findViewById(R.id.tvResult);
                tvOrderContent = (TextView) view.findViewById(R.id.tvOrderContent);
            }
        }
    }
    
    public interface OnOneKeyBuyListener {
        void onBuyOneKey(String lottery, String issue, String method, String requestText);
    }
}
