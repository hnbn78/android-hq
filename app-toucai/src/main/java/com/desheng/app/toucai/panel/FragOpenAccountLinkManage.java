package com.desheng.app.toucai.panel;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;

import com.ab.global.ENV;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.ClipboardUtils;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.ab.util.Toasts;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.app.toucai.fragment.BasePageFragment;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LinkMangeBean;
import com.desheng.base.panel.ActWebX5;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class FragOpenAccountLinkManage extends BasePageFragment {


    private RecyclerView recyclerView;
    private BaseQuickAdapter<LinkMangeBean.ListBean, BaseViewHolder> adapter;
    private LinkMangeBean linkMangeBean;
    private int page = 0;
    private static final int PAGESIZE = 20;
    private LinearLayout emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static FragOpenAccountLinkManage newInstance() {
        FragOpenAccountLinkManage fragment = new FragOpenAccountLinkManage();
        return fragment;
    }

    @Override
    protected int setContentView() {
        return R.layout.layout_frag_openaccount_linkmanage;
    }

    @Override
    protected void initView(View rootview) {
        recyclerView = ((RecyclerView) rootview.findViewById(R.id.recycleView));
        swipeRefreshLayout = ((SwipeRefreshLayout) rootview.findViewById(R.id.swipeRefreshLayout));
        emptyView = ((LinearLayout) rootview.findViewById(R.id.emptyView));
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        adapter = new BaseQuickAdapter<LinkMangeBean.ListBean, BaseViewHolder>(R.layout.item_openaccount_linkmanage) {
            @Override
            protected void convert(BaseViewHolder helper, LinkMangeBean.ListBean item) {

                String data = item.getCode();
                if (data.endsWith(".html")) {
                    data = data.replace(".html", "");
                }

                String urlok = "";
                List<String> urls = UserManager.getIns().getUrls();
                if (urls != null && urls.size() > 0) {
                    urlok = urls.get(0) + data;
                } else {
                    urlok = ENV.curr.host + data;
                }

                helper.setText(R.id.linkStr, Html.fromHtml("<u>" + urlok + "</u>"));
                helper.setText(R.id.fandian, "" + Nums.formatDecimal(item.getPoint(), 1));
                helper.setText(R.id.usertype, item.getType() == 1 ? "代理" : "会员");
                helper.setText(R.id.diedLine, Strs.isEmpty(item.getExpireTime()) ? "永不过期" : item.getExpireTime());

                String finalUrlok = urlok;
                helper.getView(R.id.copeBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardUtils.copyText(mActivity, finalUrlok);
                        Toasts.show(mActivity, "已复制到剪切板");
                    }
                });

                String finalUrlok1 = urlok;
                helper.getView(R.id.linkStr).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActWebX5.launchOutside(mActivity, finalUrlok1);
                    }
                });

                helper.getView(R.id.delBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteLink("" + item.getId());
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.setEnableLoadMore(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
            }
        });

        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getLinkList();
            }
        }, recyclerView);
    }

    @Override
    public void fetchData() {
        page = 0;
        adapter.setEnableLoadMore(true);
        getLinkList();
    }

    private List<LinkMangeBean.ListBean> totalDataList = new ArrayList<>();

    private void getLinkList() {
        HttpAction.linkManage(null, page, PAGESIZE, new AbHttpResult() {

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                //swipeRefreshLayout.setRefreshing(true);
                Dialogs.showProgressDialog(mActivity, "加载中...");
            }

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", LinkMangeBean.class);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0 && getField(extra, "data", null) != null) {
                    linkMangeBean = getField(extra, "data", null);
                    List<LinkMangeBean.ListBean> listBeanList = linkMangeBean.getList();

                    if (page == 0) {
                        totalDataList.clear();
                    }

                    if (listBeanList != null) {
                        totalDataList.addAll(listBeanList);
                    } else {
                        totalDataList.addAll(new ArrayList<LinkMangeBean.ListBean>());
                    }

                    adapter.setNewData(totalDataList);

                    int TOTAL_COUNTER = linkMangeBean.getTotalCount();

                    if (PAGESIZE * (page + 1) >= TOTAL_COUNTER) {
                        adapter.loadMoreEnd(true);
                        adapter.disableLoadMoreIfNotFullPage();
                        adapter.setEnableLoadMore(false);
                    } else {
                        page++;
                    }

                    if (linkMangeBean != null && totalDataList.size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                    }
                }

                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                return super.onError(status, content);
            }

            @Override
            public void onAfter(int id) {
                swipeRefreshLayout.setRefreshing(false);
                Dialogs.hideProgressDialog(mActivity);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void deleteLink(String id) {
        HttpAction.deleteLink(null, id, new AbHttpResult() {

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                Toasts.show(mActivity, msg);
                getLinkList();
                return true;

            }

            @Override
            public boolean onGetString(String str) {
                return super.onGetString(str);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(mActivity, content);
                return true;
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

}
