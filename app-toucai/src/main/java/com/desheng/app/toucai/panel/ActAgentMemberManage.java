package com.desheng.app.toucai.panel;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
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
import com.desheng.app.toucai.view.MyLinearLayoutManager;
import com.desheng.base.action.HttpAction;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.BonusBean;
import com.desheng.base.model.BonusSalaryBean;
import com.desheng.base.model.MemberManagementBean;
import com.desheng.base.panel.ActBonus;
import com.desheng.base.panel.ActTransfer;
import com.desheng.base.panel.PrepareTransferBean;
import com.desheng.base.panel.TransferTypeListBean;
import com.google.gson.reflect.TypeToken;
import com.noober.background.view.BLEditText;
import com.noober.background.view.BLTextView;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class ActAgentMemberManage extends AbAdvanceActivity implements View.OnClickListener {
    private static final String ACTION_LIFT_POINT = "升点";
    private static final String ACTION_TRANSFORM = "转账";
    private static final String ACTION_BONUS = "分红";
    private static final int PAGE_SIZE = 20;
    private TextView tv_title;
    private RecyclerView recyclerView;
    private BaseQuickAdapter<MemberManagementBean, BaseViewHolder> adapter;
    private BLEditText mUsername, mAgentXiaji;
    private BLTextView mSearch;
    private MaterialDialog dialog;
    private BonusBean bonusBean;
    private BonusSalaryBean bonusSalaryBean;
    private String userName, subName;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int currPage = 0;
    private HttpAction.RespSearchMember respSearchMember;
    private List<MemberManagementBean> memberManagementBeans = new ArrayList<>();
    private RecyclerView recycleView_Chain;
    private BaseQuickAdapter<String, BaseViewHolder> mAgentChainAdapter;
    private RelativeLayout emptyView;

    @Override
    protected int getLayoutId() {
        return R.layout.act_agent_member_manage;
    }

    @Override
    protected void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();
        tv_title = ((TextView) headRoot.findViewById(R.id.tv_title));
        tv_title.setText("会员管理");
        initView();
    }

    private void initView() {
        recyclerView = ((RecyclerView) findViewById(R.id.recycleView));
        recycleView_Chain = ((RecyclerView) findViewById(R.id.recycleView_Chain));
        swipeRefreshLayout = ((SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout));
        emptyView = ((RelativeLayout) findViewById(R.id.emptyView));
        mUsername = ((BLEditText) findViewById(R.id.username));
        mAgentXiaji = ((BLEditText) findViewById(R.id.agentXiaji));
        mSearch = ((BLTextView) findViewById(R.id.search));
        recyclerView.setLayoutManager(new MyLinearLayoutManager(this));
        mSearch.setOnClickListener(this);

        adapter = new BaseQuickAdapter<MemberManagementBean, BaseViewHolder>(R.layout.item_agent_member_manage) {
            @Override
            protected void convert(BaseViewHolder helper, MemberManagementBean item) {

                helper.setText(R.id.tv3, item.balance);
                helper.setText(R.id.teamLeft, item.teamBalance);
                helper.setText(R.id.feeBack, item.point);
                helper.setText(R.id.tvLastLoginTime, Strs.isNotEmpty(item.lastLoginTime) ? "最后登录时间：" + item.lastLoginTime : "最后登录时间： 未知 ");
                helper.setText(R.id.status, item.onlineStatus);
                ((TextView) helper.getView(R.id.status)).setTextColor(Strs.isEqual("离线", item.onlineStatus) ? Color.LTGRAY : Color.GREEN);
                helper.getView(R.id.usertype).setBackgroundColor(item.userType == 1 ? Color.RED : Color.BLUE);
                helper.setText(R.id.usertype, item.userType == 0 ? "玩家" : "代理");

                //是否显示分红
                helper.getView(R.id.fenhong).setVisibility((item.userType != 0 && bonusSalaryBean != null && bonusSalaryBean.getBonus() == 1) ? View.VISIBLE : View.GONE);
                helper.getView(R.id.fenhong).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //simpleLaunch(ActAgentMemberManage.this, ActAgentFenhong.class);
                        ActBonus.launch(ActAgentMemberManage.this, item);
                    }
                });
                helper.getView(R.id.zhuanzhang).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prepareTransfer(item.userName);
                    }
                });

                helper.setVisible(R.id.shengdian, UserManager.getIns().getMainUserLevel() >= 4);
                helper.getView(R.id.shengdian).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //prepareEditPoint(item.userName, item.point, ACTION_LIFT_POINT);
                        ActAgentShengdian.launch(ActAgentMemberManage.this, item.userName, item.point);
                    }
                });

                helper.getView(R.id.gongzigl).setVisibility((item.userType != 0 && bonusSalaryBean != null && bonusSalaryBean.getSalary() == 1) ? View.VISIBLE : View.GONE);
                helper.getView(R.id.gongzigl).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActSalary.launch(ActAgentMemberManage.this, item);
                    }
                });


                ((TextView) helper.getView(R.id.username)).setText(Html.fromHtml("<u>" + item.userName + "</u>"));
                ((TextView) helper.getView(R.id.username)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currPage = 0;
                        memberManagementBeans.clear();
                        mAgentXiaji.setText(item.userName);
                        userName = Views.getText(mUsername);
                        subName = item.userName;
                        getMemberManagementList();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);

        //代理线
        recycleView_Chain.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAgentChainAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_name_chain) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(com.desheng.base.R.id.tv_item_name, item + " > ");
            }
        };
        recycleView_Chain.setAdapter(mAgentChainAdapter);

        mAgentChainAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String name = (String) adapter.getData().get(position);
                if (position == 0) {
                    mAgentXiaji.setText("");
                    currPage = 0;
                    getQuerySalaryBonus();
                } else {
                    if (Strs.isNotEmpty(name)) {
                        mAgentXiaji.setText(name);
                        mAgentXiaji.setSelection(name.length());
                        currPage = 0;
                        getQuerySalaryBonus();
                    }
                }
            }
        });

        getQuerySalaryBonus();

        //刷新数据
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currPage = 0;
                userName = Views.getText(mUsername);
                subName = Views.getText(mAgentXiaji);
                getMemberManagementList();
            }
        });

        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getMemberManagementList();
            }
        }, recyclerView);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.search:
                currPage = 0;
                memberManagementBeans.clear();
                userName = Views.getText(mUsername);
                subName = Views.getText(mAgentXiaji);
                getMemberManagementList();
                break;
            default:
        }
    }


    /**
     * 获得会员管理
     */
    public void getMemberManagementList() {

        String subName = Views.getText(mAgentXiaji);
        if (Strs.isEmpty(subName)) {
            subName = null;
        }

        final String finalSubName = subName;

        HttpAction.getMemberManagement(userName, subName, currPage, PAGE_SIZE, new AbHttpResult() {

            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", HttpAction.RespSearchMember.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActAgentMemberManage.this, "搜索中...");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                respSearchMember = getField(extra, "data", null);
                if (respSearchMember != null) {
                    if (currPage == 0) {
                        memberManagementBeans.clear();
                    }

                    List<MemberManagementBean> templist = respSearchMember.list;

                    if (templist != null) {
                        memberManagementBeans.addAll(templist);
                    } else {
                        memberManagementBeans.addAll(new ArrayList<MemberManagementBean>(0));
                    }

                    adapter.setNewData(memberManagementBeans);

                    int TOTAL_COUNTER = respSearchMember.totalCount;

                    if (PAGE_SIZE * (currPage + 1) >= TOTAL_COUNTER) {
                        adapter.loadMoreEnd(true);
                        adapter.disableLoadMoreIfNotFullPage();
                        adapter.setEnableLoadMore(false);
                    } else {
                        currPage++;
                    }

                    //代理线
                    if (respSearchMember.statistics != null) {
                        String tempStr = "";
                        if (Strs.isEmpty(finalSubName)) {
                            tempStr = UserManager.getIns().getMainUserName();
                        } else {
                            tempStr = finalSubName;
                        }
                        respSearchMember.statistics.add(tempStr);
                        mAgentChainAdapter.setNewData(respSearchMember.statistics);
                    } else {

                        List<MemberManagementBean> data = respSearchMember.list;
                        if (data.size() == 1 && Strs.isEqual(data.get(0).getUserName(), userName)) {
                            List<String> agentLinkList = data.get(0).agentLinkList;
                            mAgentChainAdapter.setNewData(agentLinkList);
                        } else {
                            String tempStr = "";
                            tempStr = UserManager.getIns().getMainUserName();
                            List<String> list = new ArrayList<>();
                            list.add(tempStr);
                            mAgentChainAdapter.setNewData(list);
                        }
                    }

                    if (memberManagementBeans.size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                    }
                }
                return true;
            }

            @Override
            public boolean onError(int status, String content) {
                Dialogs.hideProgressDialog(ActAgentMemberManage.this);
                return super.onError(status, content);
            }

            @Override
            public void onAfter(int id) {
                Dialogs.hideProgressDialog(ActAgentMemberManage.this);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void prepareTransfer(final String name) {
        HttpAction.prepareTransfer(name, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<PrepareTransferBean>() {
                }.getType());
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                super.onBefore(request, id, host, funcName);
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                PrepareTransferBean transferBean = getField(extra, "data", null);
                if (code == 0 && error == 0 && transferBean != null) {
                    List<TransferTypeListBean> transferTypeList = transferBean.getTransferTypeList();
                    ArrayList<TransferTypeListBean> temp = new ArrayList<>();
                    temp.addAll(transferTypeList);
                    ActTransfer.launch(ActAgentMemberManage.this, name, temp);
                } else {
                    Toasts.show(ActAgentMemberManage.this, msg, false);
                }

                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onGetString(String str) {
                return super.onGetString(str);
            }

            @Override
            public boolean onError(int status, String content) {
                Toasts.show(ActAgentMemberManage.this, content);
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    private void getQuerySalaryBonus() {
        HttpAction.getQuerySalaryBonus(new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", BonusSalaryBean.class);
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(ActAgentMemberManage.this, "搜索中...");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {

                if (error == 0 && code == 0) {
                    bonusSalaryBean = getField(extra, "data", null);
                } else {
                    Toasts.show(ActAgentMemberManage.this, msg);
                }
                getMemberManagementList();
                return super.onSuccessGetObject(code, error, msg, extra);
            }

            @Override
            public boolean onError(int status, String content) {
                Dialogs.hideProgressDialog(ActAgentMemberManage.this);
                return super.onError(status, content);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Dialogs.hideProgressDialog(ActAgentMemberManage.this);
            }
        });
    }


}
