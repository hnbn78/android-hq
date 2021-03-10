package com.desheng.app.toucai.panel;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.model.OnlineMemberInfo;
import com.desheng.base.action.HttpAction;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shark.tc.R;

import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ActOnlineMember extends AbAdvanceActivity implements OnRefreshListener, OnLoadMoreListener {

    private SmartRefreshLayout mSmartRefreshLayout;
    private RecyclerView mRecyclerView;
    private BaseQuickAdapter<OnlineMemberInfo.ListBean, BaseViewHolder> adapter;
    private int currentPage = 0;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, ActOnlineMember.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_online_member_layout;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "在线会员");
        setStatusBarTranslucentAndLightContentWithPadding();
        mSmartRefreshLayout = ((SmartRefreshLayout) findViewById(R.id.smartRefreshLayout));
        mSmartRefreshLayout.setOnRefreshListener(this::onRefresh);
        mSmartRefreshLayout.setOnLoadMoreListener(this::onLoadMore);
        mRecyclerView = ((RecyclerView) findViewById(R.id.recyclerView));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickAdapter<OnlineMemberInfo.ListBean, BaseViewHolder>(R.layout.item_agent_online_member) {
            @Override
            protected void convert(BaseViewHolder helper, OnlineMemberInfo.ListBean item) {
                helper.setText(R.id.username, item.getUsername());
                helper.setText(R.id.accountYue, item.getLotteryBalance());
                helper.setText(R.id.tvLastLoginTime, item.getLoginTime());

                helper.getView(R.id.dingdan).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActAgentTeamLotteryCenter.launcher(ActOnlineMember.this,item.getUsername());
                    }
                });

                helper.getView(R.id.zhangdan).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActAgentTeamAccountChangeRecord.launcher(ActOnlineMember.this,item.getUsername());
                    }
                });
            }
        };
        adapter.setEmptyView(R.layout.empty_view, mRecyclerView);
        mRecyclerView.setAdapter(adapter);
        initData();
    }

    private void initData() {
        HttpAction.getOnlineMember(this, currentPage, 30, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActOnlineMember.this, "");
            }


            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<OnlineMemberInfo>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                OnlineMemberInfo data = getField(extra, "data", null);
                if (code == 0 && data != null) {
                    List<OnlineMemberInfo.ListBean> list = data.getList();
                    if (currentPage == 0) {
                        adapter.setNewData(list);
                    } else {
                        adapter.addData(list);
                    }
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                Dialogs.hideProgressDialog(ActOnlineMember.this);
            }
        });
    }


    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        initData();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        currentPage++;
    }
}
