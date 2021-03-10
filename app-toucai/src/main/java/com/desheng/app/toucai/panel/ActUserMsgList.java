package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.model.UserPushMsg;
import com.desheng.base.context.CtxLottery;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.SpaceTopDecoration;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * Created by user on 2018/3/13.
 */
// TODO: 2018/9/12/012 二期需要改 目前用不到 
public class ActUserMsgList extends AbAdvanceActivity implements SwipeRefreshLayout.OnRefreshListener  {
    
    public static void launch(Activity ctx) {
        simpleLaunch((Activity) ctx, ActUserMsgList.class);
    }
    
    private LinearLayout layout_nodata;
    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvMessage;
    
    int page = -1;
    int size = 10;
    
    private ArrayList<UserPushMsg> messageList;
    private ActUserMsgList.MessageAdapter messageAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.act_user_msg;
    }
    
    @Override
    protected void init() {
        messageList = new ArrayList<UserPushMsg>();
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "消息");
        setStatusBarTranslucentAndLightContentWithPadding();
    
    
        srlRefresh = findViewById(R.id.srlRefresh);
        srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);
    
        rvMessage = findViewById(R.id.rvMessageList);
        layout_nodata = findViewById(R.id.layout_nodata);
        rvMessage.setLayoutManager(Views.genLinearLayoutManagerV(ActUserMsgList.this));
        rvMessage.addItemDecoration(new SpaceTopDecoration(7));
        messageAdapter = new MessageAdapter(ActUserMsgList.this, messageList);
        rvMessage.setAdapter(messageAdapter);
    
        messageAdapter.setEnableLoadMore(true);
        messageAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                updateList();
            }
        }, rvMessage);
    
    }
    
    @Override
    public void onRefresh() {
        page = -1;
        updateList();
    }
    
    public void updateList() {
        page++;
        getMessage();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        onRefresh();
    }
    
    public void getMessage() {
        HttpActionTouCai.getUserPushMessage(ActUserMsgList.this, page, size, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(true);
                    }
                }, 100);
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<UserPushMsg>>(){}.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    List<UserPushMsg> resultList = getField(extra, "data", null);
                    if (page == 0) {
                        messageList.clear();
                    }
                    messageList.addAll(resultList);

                    if (messageList.size() < size) {
                        messageAdapter.setEnableLoadMore(false);
                        messageAdapter.loadMoreEnd();
                    } else {
                        messageAdapter.setEnableLoadMore(true);
                    }

                    layout_nodata.setVisibility(resultList.size()==0?View.VISIBLE:View.GONE);

                    messageAdapter.notifyDataSetChanged();
                } else {

                    layout_nodata.setVisibility(View.VISIBLE);

                    Toasts.show(ActUserMsgList.this, msg);
                }

                return true;
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                srlRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srlRefresh.setRefreshing(false);
                        messageAdapter.loadMoreComplete();
                    }
                }, 600);
            }

            @Override
            public boolean onError(int status, String content) {

                layout_nodata.setVisibility(View.VISIBLE);
                return super.onError(status, content);
            }
        });
    }

    protected class MessageAdapter extends BaseQuickAdapter<UserPushMsg, MessageAdapter.ViewHolder> {

        private Context ctx;

        public MessageAdapter(Context ctx, List<UserPushMsg> data) {
            super(R.layout.item_user_mesage, data);
            this.ctx = ctx;
        }

        @Override
        protected void convert(ViewHolder holder, final UserPushMsg item) {

            holder.tvTime.setText("发件时间: " + item.pushTime);
            holder.tvTitle.setText("" + item.title);
            holder.tvFrom.setText("发件人: " + "系统推送");
            holder.tvStatus.setVisibility(View.INVISIBLE);
            
            holder.layout_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String content = CtxLottery.transHtmlFrag(item.content);
                    ActUserMsgDetail.launch(ActUserMsgList.this, item);
                }
            });
        }

        class ViewHolder extends BaseViewHolder {
            TextView tvTitle;
            TextView tvTime;
            TextView tvFrom;
            TextView tvStatus;
            ImageView imArrow;
            RelativeLayout layout_message;

            public ViewHolder(View view) {
                super(view);
                layout_message = (RelativeLayout) view.findViewById(R.id.layout_message);
                tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                tvTime = (TextView) view.findViewById(R.id.tvTime);
                tvFrom = (TextView) view.findViewById(R.id.tvFrom);
                tvStatus = (TextView) view.findViewById(R.id.tvStatus);
                imArrow = (ImageView) view.findViewById(R.id.imArrow);
            }
        }
    }
    
}
