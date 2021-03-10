package com.desheng.app.toucai.panel;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.ab.util.Views;
import com.desheng.app.toucai.adapter.ChaseIssueAdapter;
import com.desheng.app.toucai.model.ChaseDetailBean;
import com.pearl.act.base.AbAdvanceActivity;
import com.pearl.act.util.UIHelper;
import com.pearl.view.SpaceTopDecoration;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 投注记录
 */
public class ActChaseIssueList extends AbAdvanceActivity implements ChaseIssueAdapter.OnRefreshListListener {

    public static void launch(Activity ctx, ArrayList<ChaseDetailBean.ChaseListBean> chaseListBean) {
        Intent intent = new Intent(ctx, ActChaseIssueList.class);
        intent.putParcelableArrayListExtra("chase_list", chaseListBean);
        ctx.startActivity(intent);
    }

    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvIssueList;
    private List<ChaseDetailBean.ChaseListBean> listData = new ArrayList<>();
    private ChaseIssueAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.act_chase_issue;
    }

    @Override
    protected void init() {
        UIHelper.getIns().simpleToolbarWithBackBtn(getToolbar(), "期号列表");
        setStatusBarTranslucentAndLightContentWithPadding();
        initView();
    }

    /**
     * 初始化所有控件
     */
    private void initView() {

        srlRefresh = findViewById(com.shark.tc.R.id.srlRefresh);
        rvIssueList = findViewById(com.shark.tc.R.id.rv_chase_issue);

        srlRefresh.setEnabled(false);
        srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryInverse);
        rvIssueList.setLayoutManager(Views.genLinearLayoutManagerV(this));
        rvIssueList.addItemDecoration(new SpaceTopDecoration(7));

        listData = getIntent().getParcelableArrayListExtra("chase_list");
        Collections.reverse(listData);
        mAdapter = new ChaseIssueAdapter(this, listData , this);
        rvIssueList.setAdapter(mAdapter);

        mAdapter.setEnableLoadMore(false);

    }

    @Override
    public void onRefreshList(int position) {
        listData.get(position).allowCancel=false;
        listData.get(position).statusRemark="个人撤单";
        mAdapter.notifyDataSetChanged();
    }

}