package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dates;
import com.ab.util.Nums;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.context.ThirdGamePlatform;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.model.AccountChangeRecordBean;
import com.desheng.base.model.TransferRecordBean;
import com.desheng.base.panel.ActWeb;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.SpaceTopDecoration;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * 转账记录
 */
public class ActThirdTransferRecordList extends AbAdvanceActivity implements SwipeRefreshLayout.OnRefreshListener {
    
    public static void launch(Activity ctx) {
        simpleLaunch(ctx, ActThirdTransferRecordList.class);
    }
    
    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvRecordList;
    
    int page = -1;
    int size = 20;
    
    private List<TransferRecordBean> messageList;
    private MessageAdapter messageAdapter;
    
    @Override
    protected int getLayoutId() {
        return R.layout.act_third_transfer_record_list;
    }
    
    @Override
    protected void init() {
        messageList = new ArrayList<TransferRecordBean>();
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "转账记录");
        UIHelper.setToolbarRightButtonImg(getToolbar(), R.mipmap.ic_home_nav_customer, 43, 43, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActWeb.launchCustomService(ActThirdTransferRecordList.this);
            }
        });
        setStatusBarTranslucentAndLightContentWithPadding();
        
        srlRefresh = (SwipeRefreshLayout) findViewById(R.id.srlRefresh);
        srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);
        
        rvRecordList = (RecyclerView) findViewById(R.id.rvRecordList);
        rvRecordList.setLayoutManager(Views.genLinearLayoutManagerV(ActThirdTransferRecordList.this));
        rvRecordList.addItemDecoration(new SpaceTopDecoration(15));
        messageAdapter = new MessageAdapter(ActThirdTransferRecordList.this, messageList);
        rvRecordList.setAdapter(messageAdapter);
        
        messageAdapter.setEnableLoadMore(true);
        messageAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                updateList();
            }
        }, rvRecordList);
        
        updateList();
    }
    
    public void updateList() {
        page++;
        search(page, size);
    }
    
    @Override
    public void titleRightClick(View view) {
        super.titleRightClick(view);
        
    }
    
    @Override
    public void onRefresh() {
        page = -1;
        updateList();
    }
    
    /**
     * 账变记录
     */
    public void search(final int page, int size) {
        long currentTimeMillis = System.currentTimeMillis();
        String startTime = Dates.getStringByFormat(currentTimeMillis - Dates.DAY_MILLIS * 7, Dates.dateFormatYMDHMS);
        String stopTime = Dates.getStringByFormat(currentTimeMillis, Dates.dateFormatYMDHMS);
        
        HttpAction.getAccountChange(ActThirdTransferRecordList.this, "", "", "99", startTime, stopTime, page, size, new AbHttpResult() {
            
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(true);
                    }
                }, 100);
            }
            
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespAccountChange.class);
            }
            
            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    HttpAction.RespAccountChange respAccountChange = getField(extra, "data", null);
                    
                    if (page == 0) {
                        messageList.clear();
                    }
                    
                    for (int i = 0; i < respAccountChange.list.size(); i++) {
                        AccountChangeRecordBean bean = respAccountChange.list.get(i);
                        TransferRecordBean record = new TransferRecordBean();
                        String[] fromto = bean.changeTypeDetailStr.split("-");
                        ThirdGamePlatform platform = ThirdGamePlatform.valueOf(fromto[0]);
                        record.icon = platform.getPocketIcon();
                        record.from = CtxLottery.MAIN_POCKET_NAME;
                        record.to = platform.getTitle();
                        record.date = bean.createTime;
                        if ("转出".equals(fromto[1])) {
                             record.out0in1 = 1;
                        }else{
                            record.out0in1 = 0;
                            record.icon = R.mipmap.ic_pocket_main;
                        }
                        record.amount = Nums.formatDecimal(bean.amount, 1);
                        messageList.add(record);
                    }
                    
                    if (messageList.size() >= respAccountChange.totalCount) {
                        messageAdapter.loadMoreComplete();
                        messageAdapter.setEnableLoadMore(false);
                    }else {
                        messageAdapter.setEnableLoadMore(true);
                    }
                    
                    messageAdapter.notifyDataSetChanged();
                } else {
                    Toasts.show(ActThirdTransferRecordList.this, msg);
                }
                return true;
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
    
    protected class MessageAdapter extends BaseQuickAdapter<TransferRecordBean, MessageAdapter.ViewHolder> {
        
        private Context ctx;
        
        public MessageAdapter(Context ctx, List<TransferRecordBean> data) {
            super(R.layout.item_third_game_trans_record, data);
            this.ctx = ctx;
        }
        
        @Override
        protected void convert(ViewHolder holder, final TransferRecordBean item) {
            holder.ivIcon.setImageResource(item.icon);
            holder.tvFrom.setText(item.from);
            holder.tvTo.setText(item.to);
            if (item.out0in1 == 0) {
                holder.imArrow.setImageResource(R.mipmap.ic_out_arrow);
                holder.tvAmount.setText("+" + item.amount);
                holder.tvAmount.setTextColor(Color.parseColor("#ef534e"));
            } else if (item.out0in1 == 1) {
                holder.imArrow.setImageResource(R.mipmap.ic_in_arrow);
                holder.tvAmount.setText("-" + item.amount);
                holder.tvAmount.setTextColor(Color.parseColor("#3695E4"));
            }
            holder.tvTime.setText(item.date);
        }
        
        class ViewHolder extends BaseViewHolder {
            public RelativeLayout layoutMessage;
            public ImageView ivIcon;
            public TextView tvFrom;
            public ImageView imArrow;
            public TextView tvTo;
            public TextView tvTime;
            public TextView tvAmount;
            
            public ViewHolder(View view) {
                super(view);
                layoutMessage = (RelativeLayout) view.findViewById(R.id.layout_message);
                ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
                tvFrom = (TextView) view.findViewById(R.id.tvFrom);
                imArrow = (ImageView) view.findViewById(R.id.imArrow);
                tvTo = (TextView) view.findViewById(R.id.tvTo);
                tvTime = (TextView) view.findViewById(R.id.tvTime);
                tvAmount = (TextView) view.findViewById(R.id.tvAmount);
            }
        }
    }
}
