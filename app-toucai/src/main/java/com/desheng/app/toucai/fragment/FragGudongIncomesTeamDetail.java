package com.desheng.app.toucai.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.GudongTeamDetailBean;
import com.desheng.app.toucai.model.GudongTeamDetailListBean;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.google.gson.reflect.TypeToken;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class FragGudongIncomesTeamDetail extends BasePageFragment {

    private RecyclerView rcTeamList;
    private BaseQuickAdapter quickAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView incomeToday;
    private TextView incomeAll;
    private EditText etusername;
    private TextView search;
    List<GudongTeamDetailListBean> mDatalist = new ArrayList<>();
    private int mCurrentCounter;
    private static int PAGE_SIZE = 10;
    private View emptyLayout;
    Double[] agentType = {0.005, 0.01, 0.015, 0.02};
    String[] agentConfig = {"代理", "总代理", "股东", "大股东"};

    public static FragGudongIncomesTeamDetail newInstance() {
        FragGudongIncomesTeamDetail fragment = new FragGudongIncomesTeamDetail();
        return fragment;
    }

    @Override
    public int setContentView() {
        return R.layout.frag_gudongincomes_teamdetail;
    }

    @Override
    public void initView(View root) {
        rcTeamList = root.findViewById(R.id.rcTeamDetail);
        incomeToday = root.findViewById(R.id.incomeToday);
        incomeAll = root.findViewById(R.id.incomeAll);
        emptyLayout = root.findViewById(R.id.emptyLayout);
        etusername = root.findViewById(R.id.etusername);
        search = root.findViewById(R.id.search);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);

        rcTeamList.setLayoutManager(new LinearLayoutManager(mActivity));

        quickAdapter = new BaseQuickAdapter<GudongTeamDetailListBean, BaseViewHolder>(R.layout.item_team_detaol, mDatalist) {

            @Override
            protected void convert(BaseViewHolder helper, GudongTeamDetailListBean item) {
                if (item == null) {
                    return;
                }

                if (Strs.isEqual(item.getCn(), UserManagerTouCai.getIns().getMainUserName())) {
                    for (int i = 0; i < agentType.length; i++) {
                        if (UserManagerTouCai.getIns().getMainUserAgentType() == agentType[i]) {
                            helper.setText(R.id.name, item.getCn() + " (" + agentConfig[i] + ")");
                        }
                    }
                } else {
                    helper.setText(R.id.name, item.getCn());
                }
                helper.setText(R.id.touzhuTotal, Nums.formatDecimal(item.getConfirm_amount(), 2));
                helper.setText(R.id.paicaiTotal, Nums.formatDecimal(item.getAward_amount(), 2));
                helper.setText(R.id.incomeToday, Nums.formatDecimal(item.getRebound_amount(), 2));
                helper.setText(R.id.totalMoney, Nums.formatDecimal(item.getTotal_reboun_amount(), 2));
            }
        };
        rcTeamList.setAdapter(quickAdapter);
        quickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {

            @Override
            public void onLoadMoreRequested() {
                if (quickAdapter.getData().size() < PAGE_SIZE) {
                    quickAdapter.loadMoreEnd(true);
                } else {
                    rcTeamList.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPageIndex++;
                            getData(mUsername, mPageIndex, PAGE_SIZE);
                        }
                    }, 1000);
                }
            }
        }, rcTeamList);

        quickAdapter.setEmptyView(R.layout.rc_emptyview_layout, swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPageIndex = 0;
                        getData(mUsername, mPageIndex, PAGE_SIZE);
                    }
                }, 1000);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsername = etusername.getText().toString().trim();
                doSearch(mUsername);
            }
        });
    }

    String mUsername;

    private void doSearch(String username) {
        mPageIndex = 0;
        getData(username, mPageIndex, PAGE_SIZE);
    }


    @Override
    public void fetchData() {
        mPageIndex = 0;
        getData(null, mPageIndex, PAGE_SIZE);
    }

    int mPageIndex = 0;

    private void getData(String cn, int page, int size) {
        HttpActionTouCai.getTeamCentreDetailList(mActivity, cn, page, size, new AbHttpResult() {
            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                DialogsTouCai.showProgressDialog(mActivity, "");
                emptyLayout.setVisibility(View.GONE);
                rcTeamList.setVisibility(View.VISIBLE);
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<GudongTeamDetailBean>() {
                }.getType());
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    GudongTeamDetailBean bean = getField(extra, "data", null);
                    swipeRefreshLayout.setRefreshing(false);

                    if (bean != null) {
                        incomeAll.setText(Nums.formatDecimal(bean.getTotalAllReboundAmount(), 2));
                        incomeToday.setText(Nums.formatDecimal(bean.getTotalTodayReboundAmount(), 2));


                        List<GudongTeamDetailListBean> list = bean.getList();

                        if (page == 0) {
                            mDatalist.clear();
                            if (list.size() == 0) {
                                emptyLayout.setVisibility(View.VISIBLE);
                                rcTeamList.setVisibility(View.GONE);
                            }
                        }

                        mDatalist.addAll(list);
                        quickAdapter.setNewData(mDatalist);

                        if (bean.getList().size() < PAGE_SIZE) {
                            quickAdapter.loadMoreEnd(true);
                        }

                        //etusername.setText("");
                    }
                }
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(mActivity, content, false);
                swipeRefreshLayout.setRefreshing(false);
                return true;
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                DialogsTouCai.hideProgressDialog(mActivity);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
