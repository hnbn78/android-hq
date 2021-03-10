package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.base.model.MessageFullBean;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Request;

public class ActHomeNotice extends AbAdvanceActivity {

    private SmartRefreshLayout mSmartRefreshLayout;
    private RecyclerView mRecyclerView;
    private BaseQuickAdapter<MessageFullBean, BaseViewHolder> adapter;
    private ArrayList<MessageFullBean> mListMessage;

    @Override
    protected int getLayoutId() {
        return R.layout.act_home_notice;
    }

    public static void launch(Activity act, ArrayList<MessageFullBean> listMessage) {
        Intent intent = new Intent(act, ActHomeNotice.class);
        intent.putParcelableArrayListExtra("listMessage", listMessage);
        act.startActivity(intent);
    }

    public static void launch(Activity act) {
        Intent intent = new Intent(act, ActHomeNotice.class);
        act.startActivity(intent);
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "公告");
        setStatusBarTranslucentAndLightContentWithPadding();
        mSmartRefreshLayout = ((SmartRefreshLayout) findViewById(R.id.smartRefreshLayout));
        mRecyclerView = ((RecyclerView) findViewById(R.id.recyclerView));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickAdapter<MessageFullBean, BaseViewHolder>(R.layout.item_notice_layout) {
            @Override
            protected void convert(BaseViewHolder helper, MessageFullBean item) {
                helper.setText(R.id.tvTitle, item.getTitle());
                helper.setText(R.id.tvDate, item.getTime());
            }
        };
        adapter.setEmptyView(R.layout.empty_view, mRecyclerView);
        mRecyclerView.setAdapter(adapter);
        initData();

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MessageFullBean messageFullBean = (MessageFullBean) adapter.getData().get(position);
                if (messageFullBean == null) {
                    return;
                }
                ActHomeNoticeDetail.launch(ActHomeNotice.this, messageFullBean);
            }
        });
    }

    private void initData() {
        mListMessage = getIntent().getParcelableArrayListExtra("listMessage");
        if (mListMessage != null) {
            adapter.setNewData(mListMessage);
        } else {
            getMarqueeInfo();
        }
    }

    private void getMarqueeInfo() {
        HttpActionTouCai.getMessageCenter(this, 0, 10, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActHomeNotice.this, "");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<MessageFullBean>>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    ArrayList<MessageFullBean> listMessage = getField(extra, "data", null);
                    adapter.setNewData(listMessage);
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                Dialogs.hideProgressDialog(ActHomeNotice.this);
            }
        });
    }
}
