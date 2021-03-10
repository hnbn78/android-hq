package com.desheng.base.panel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.ab.util.Views;
import com.ab.view.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.R;
import com.desheng.base.action.HttpAction;
import com.desheng.base.context.CtxLottery;
import com.desheng.base.model.ListMessageBean;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.SpaceTopDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * 消息列表
 */
public class ActMessageList extends AbAdvanceActivity implements SwipeRefreshLayout.OnRefreshListener {

    private TextView tvReturn;
    private RelativeLayout rlEmptyLayout;

    public static void launch(Activity ctx) {
        simpleLaunch(ctx, ActMessageList.class);
    }

    public static void launch(Activity ctx, String title) {
        Intent intent = new Intent(ctx, ActMessageList.class);
        intent.putExtra("title_key", title);
        ctx.startActivity(intent);
    }

    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvMessage;

    int page = -1;
    int size = 10;

    private ListMessageBean messageBean;
    private List<ListMessageBean.ListBean> messageList;
    private MessageAdapter messageAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.act_message_list;
    }

    @Override
    protected void init() {
        String title = getIntent().getStringExtra("title_key");

        if (!TextUtils.isEmpty(title)) {
            UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), title);
            setStatusBarTranslucentAndLightContentWithPadding();
        } else {
            UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "消息");
            setStatusBarTranslucentAndLightContentWithPadding();
            setToolbarButtonRightText("发消息");
            setToolbarButtonRightTextColor(R.color.white);
            setToolbarRightButtonGroupClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActSendMessage.launch(ActMessageList.this);
                }
            });
        }
        messageList = new ArrayList<ListMessageBean.ListBean>();

        tvReturn = ((TextView) findViewById(R.id.tvReturn));
        rlEmptyLayout = ((RelativeLayout) findViewById(R.id.rlEmptyLayout));
        tvReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        srlRefresh = (SwipeRefreshLayout) findViewById(R.id.srlRefresh);
        srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        srlRefresh.setOnRefreshListener(this);

        rvMessage = (RecyclerView) findViewById(R.id.rvMessageList);
        rvMessage.setLayoutManager(Views.genLinearLayoutManagerV(ActMessageList.this));
        rvMessage.addItemDecoration(new SpaceTopDecoration(3));
        messageAdapter = new MessageAdapter(ActMessageList.this, messageList);
        rvMessage.setAdapter(messageAdapter);

        messageAdapter.setEnableLoadMore(true);
        messageAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                updateList();
            }
        }, rvMessage);

        updateList();
    }

    public void updateList() {
        page++;
        getMessage();
    }

    @Override
    public void titleRightClick(View view) {
        super.titleRightClick(view);
        Toasts.show(this, "发消息.....");
    }

    @Override
    public void onRefresh() {
        page = -1;
        updateList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        onRefresh();
    }

    public void getMessage() {
        HttpAction.listMessage(ActMessageList.this, page, size, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActMessageList.this, "");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", ListMessageBean.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    messageBean = getField(extra, "data", null);
                    if (page == 0) {
                        if (messageBean.getList().size() == 0) {
                            rlEmptyLayout.setVisibility(View.VISIBLE);
                        } else {
                            rlEmptyLayout.setVisibility(View.GONE);
                        }
                        messageList.clear();
                    }
                    messageList.addAll(messageBean.getList());

                    if (messageList.size() >= messageBean.getTotalCount()) {
                        messageAdapter.setEnableLoadMore(false);
                        messageAdapter.loadMoreEnd();
                    } else {
                        messageAdapter.setEnableLoadMore(true);
                    }

                    messageAdapter.notifyDataSetChanged();
                } else {
                    Toasts.show(ActMessageList.this, msg);
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
                Dialogs.hideProgressDialog(ActMessageList.this);
                srlRefresh.setRefreshing(false);
                messageAdapter.loadMoreComplete();
            }
        });
    }

    protected class MessageAdapter extends BaseQuickAdapter<ListMessageBean.ListBean, MessageAdapter.ViewHolder> {

        private Context ctx;

        public MessageAdapter(Context ctx, List<ListMessageBean.ListBean> data) {
            super(R.layout.item_mesage, data);
            this.ctx = ctx;
        }

        @Override
        protected void convert(ViewHolder holder, final ListMessageBean.ListBean item) {

            holder.tvTime.setText("发件时间：" + item.getLastUpdateTimeString());
            holder.tvTitle.setText("" + item.getTitle());
            holder.tvName.setText("发件人：" + item.getCreateUser());
            holder.tvStatus.setText("" + item.getReadStateStr());
            holder.tvStatus.setTextColor(Strs.isEqual("未读", item.getReadStateStr()) ? Color.RED : Color.LTGRAY);

            holder.layout_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String content = CtxLottery.transHtmlFrag(item.getContent());

                    ActMessageDetail.launch(ActMessageList.this, item);

//                    ActWebX5.launch(ActMessageList.this, item.getTitle(), content);
                }
            });

            holder.layout_message.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    final MaterialDialog dialog = new MaterialDialog(ctx);
                    dialog.setMessage("您确定要删除这条消息");

                    dialog.setTitle("消息提示");
                    dialog.setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            deleteMessage(item);

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

                    return false;
                }
            });
        }

        class ViewHolder extends BaseViewHolder {
            TextView tvTitle;
            TextView tvTime;
            TextView tvName;
            TextView tvStatus;
            ImageView imArrow;
            RelativeLayout layout_message;

            public ViewHolder(View view) {
                super(view);
                layout_message = (RelativeLayout) view.findViewById(R.id.layout_message);
                tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                tvTime = (TextView) view.findViewById(R.id.tvTime);
                tvName = (TextView) view.findViewById(R.id.tvName);
                tvStatus = (TextView) view.findViewById(R.id.tvStatus);
                imArrow = (ImageView) view.findViewById(R.id.imArrow);
            }
        }
    }


    private void deleteMessage(final ListMessageBean.ListBean item) {
        HttpAction.deleteMessage(null, item.getUserMessageId(), new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                super.setupEntity(entity);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (code == 0 && error == 0) {
                    messageList.remove(item);
                    messageAdapter.notifyDataSetChanged();
                }
                Toasts.show(ActMessageList.this, msg);

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
}
