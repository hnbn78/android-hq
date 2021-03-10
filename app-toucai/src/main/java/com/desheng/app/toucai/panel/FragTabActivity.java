package com.desheng.app.toucai.panel;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.global.ENV;
import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.ab.util.Dialogs;
import com.ab.util.Nums;
import com.ab.util.Strs;
import com.bumptech.glide.Glide;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.consitance.Consitances;
import com.desheng.app.toucai.manager.UserManagerTouCai;
import com.desheng.app.toucai.model.GetDesignHostMode;
import com.desheng.app.toucai.model.NewUserMission;
import com.desheng.app.toucai.model.eventmode.MissionindexMode;
import com.desheng.app.toucai.util.DialogsTouCai;
import com.desheng.base.context.CtxLottery;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbBaseFragment;
import com.pearl.view.expandablelayout.SimpleExpandableLayout;
import com.pearl.view.mypicker.DateUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shark.tc.R;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class FragTabActivity extends AbBaseFragment{
    RecyclerView recyclerView;
    LinearLayout emptyView;
    SmartRefreshLayout swipeRefreshLayout;
    private MyAdapter myAdapter;
    LinearLayoutManager layoutManager;

    @Override
    public int getLayoutId() {
        return R.layout.frag_tab_activity;
    }

    @Override
    public void init(View root) {
        recyclerView = root.findViewById(R.id.rv_list);
        emptyView = root.findViewById(R.id.ivEmpty);
        swipeRefreshLayout = root.findViewById(R.id.refresh);
        swipeRefreshLayout.setEnableRefresh(true);
        swipeRefreshLayout.setEnableLoadMore(false);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(new MyAdapter());
        //getActivityData();
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getActivityData();
            }
        });

        getActivityData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //默认方式, 在发送线程执行
    public void onUserEvent(MissionindexMode missionindexMode) {
        if (missionindexMode == null) {
            return;
        }
    }

    List<NewUserMission> listMission;


    public void getActivityData() {
        HttpActionTouCai.getActivityList(this, new AbHttpResult() {
            @Override
            public void setupEntity(AbHttpRespEntity entity) {
                entity.putField("data", new TypeToken<ArrayList<NewUserMission>>() {
                }.getType());
            }

            @Override
            public void onBefore(Request request, int id, String host, String funcName) {
                Dialogs.showProgressDialog(context,"");
            }

            @Override
            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                if (code == 0 && error == 0) {
                    listMission = getField(extra, "data", null);

                    if (listMission != null && listMission.size() > 0) {
                        myAdapter = new MyAdapter(listMission);
                        recyclerView.setAdapter(myAdapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    } else {
                        recyclerView.setAdapter(null);
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                } else {
                    recyclerView.setAdapter(null);
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }

                return true;
            }

            @Override
            public void onAfter(int id) {
                swipeRefreshLayout.finishRefresh();
                Dialogs.hideProgressDialog(context);
            }
        });
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.VH> {
        List<NewUserMission> list;
        boolean state[];

        public MyAdapter(List<NewUserMission> list) {
            this.list = list;
            state = new boolean[list.size()];
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = getLayoutInflater().inflate(R.layout.item_frag_tab_activity_list, parent, false);
            return new VH(root);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            NewUserMission mission = list.get(position);
            Glide.with(getContext()).load(ENV.curr.host + mission.imagePath).into(holder.bg);
//            holder.title.setText("「" + mission.title + "」");
            holder.description.setText(mission.title);
            holder.date.setText("活动截止日 " + mission.endTime);
            //holder.content.loadData(mission.content, "text/html; charset=UTF-8", null);
            String content = CtxLottery.transHtmlFrag(mission.content.replace("&nbsp;", "&#160;"));
            holder.content.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);

            holder.container.setExpanded(state[position], true);

            holder.itemView.setOnClickListener(v -> {
                boolean s = !state[position];
                for (int i = 0; i < state.length; i++)
                    state[i] = false;
                state[position] = s;
                holder.container.setExpanded(state[position]);
                notifyDataSetChanged();
            });


            if (mission.receiveType == 0) {

                if ("19".equals(mission.bizType)) {
                    holder.join.setVisibility(View.VISIBLE);
                    holder.join.setText("查看推荐链接");
                    holder.join.setOnClickListener(v -> {
                        HttpActionTouCai.checkJoinActivity(this, mission.id, new AbHttpResult() {
                            @Override
                            public void setupEntity(AbHttpRespEntity entity) {
                                entity.putField("data", new TypeToken<HashMap<String, Object>>() {
                                }.getType());
                            }

                            @Override
                            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                if (code == 0) {
                                    HashMap<String, Object> data = getField(extra, "data", null);
                                    if (data != null) {
                                        String inviteCode = (String) data.get("inviteCode");
                                        HttpActionTouCai.getDesignedHost(this, new AbHttpResult() {

                                            @Override
                                            public boolean onGetString(String str) {
                                                if (Strs.isNotEmpty(str)) {
                                                    try {
                                                        GetDesignHostMode designHostMode = new Gson().fromJson(str, GetDesignHostMode.class);
                                                        if (designHostMode != null) {

                                                            DialogsTouCai.showCreateRecommendLink(getContext(), "查看推荐链接",
                                                                    designHostMode.getUrls() + Consitances.RECOMMEND_LINK + inviteCode, inviteCode, true);
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                return super.onGetString(str);
                                            }
                                        });
                                    }
                                } else {
                                    switch (code) {
                                        case -4:
                                            DialogsTouCai.showDialog(getContext(), "温馨提醒", "参与该活动需要绑定真实姓名", "前往绑定", v -> {
                                                ActEditAccount.launcher(getActivity());
                                            });
                                            break;
                                        case -5:
                                            if (!UserManagerTouCai.getIns().getIsBindWithdrawPassword()) {
                                                DialogsTouCai.showDialog(getContext(), "温馨提醒", "参与该活动需要设置资金密码", "前往设置", v -> {
                                                    ActSettingFundsPwd.launcher(getActivity(), false);
                                                });
                                            } else {
                                                DialogsTouCai.showDialog(getContext(), "温馨提醒", "参与该活动需要绑定银行卡", "前往绑定", v -> {
                                                    ActBindBankCardToucai.launch(getActivity(), true);
                                                });
                                            }
                                            break;
                                        case -6:
                                            DialogsTouCai.showDialog(getContext(), "温馨提醒", "参与该活动需要绑定手机号", "前往绑定", v -> {
                                                ActBindPhone.launch(getActivity());
                                            });
                                            break;
                                        case -7:
                                            DialogsTouCai.showDialog(getContext(), "温馨提醒", "参与该活动需要绑定邮箱", "前往绑定", v -> {
                                                ActBindEmail.launcher(getActivity());
                                            });
                                            break;

                                        case -8:
                                            DialogsTouCai.showDialogOk(getContext(), "温馨提醒", "您已参与该活动,您可参与其他活动", v -> {
                                            });
                                            break;

                                        case -9:
                                            DialogsTouCai.showDialog(getContext(), "温馨提醒", "您已参与其他活动,请再次充值后再来领取奖金", "前往充值", v -> {
                                                ActDeposit.launch(getActivity());
                                            });
                                            break;

                                        case -10:
                                            DialogsTouCai.showDialog(getContext(), "温馨提醒", "您暂时未充值，请充值后再来领取奖金", "前往充值", v -> {
                                                ActDeposit.launch(getActivity());
                                            });
                                            break;

                                        case -11:
                                            DialogsTouCai.showDialog(getContext(), "温馨提醒", "参与该活动需要设置资金密码", "前往设置", v -> {
                                                ActSettingFundsPwd.launcher(getActivity(), false);
                                            });
                                            break;

                                        case -12:
                                            DialogsTouCai.showDialog(getContext(), "温馨提醒", "您充值金额未达标,请再次充值领取奖金", "前往充值", v -> {
                                                ActDeposit.launch(getActivity());
                                            });
                                            break;

                                        default:
                                            DialogsTouCai.showDialogOk(getContext(), "温馨提醒", msg, v -> {
                                            });
                                    }
                                }
                                return true;
                            }
                        });
                    });
                } else if ("21".equals(mission.bizType)) {
                    holder.join.setVisibility(View.GONE);
                } else if ("24".equals(mission.bizType)) {
                    holder.join.setVisibility(View.GONE);
                } else {
                    holder.join.setVisibility(View.VISIBLE);
                    holder.join.setText("立即申请");
                    holder.join.setOnClickListener(v -> {
                        HttpActionTouCai.checkJoinActivity(this, mission.id, new AbHttpResult() {
                            @Override
                            public void setupEntity(AbHttpRespEntity entity) {
                                entity.putField("data", new TypeToken<HashMap<String, Object>>() {
                                }.getType());
                            }

                            @Override
                            public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                                if (code == 0) {
                                    HashMap<String, Object> data = getField(extra, "data", null);
                                    Object o = "0";
                                    if (data != null) {
                                        o = data.get("prizeAmount");
                                        if (o == null)
                                            o = "0";
                                    }
                                    DialogsTouCai.showDialogOk(getContext(), "温馨提醒", String.format("您申请红利金额%s元,正在进入您的钱包", Nums.formatDecimal(o, 2)), v -> {

                                    });
                                    UserManagerTouCai.getIns().refreshUserData();
                                } else {
                                    switch (code) {
                                        case -4:
                                            DialogsTouCai.showDialog(getContext(), "温馨提醒", "参与该活动需要绑定真实姓名", "前往绑定", v -> {
                                                ActEditAccount.launcher(getActivity());
                                            });
                                            break;
                                        case -5:
                                            if (!UserManagerTouCai.getIns().getIsBindWithdrawPassword()) {
                                                DialogsTouCai.showDialog(getContext(), "温馨提醒", "参与该活动需要设置资金密码", "前往设置", v -> {
                                                    ActSettingFundsPwd.launcher(getActivity(), false);
                                                });
                                            } else {
                                                DialogsTouCai.showDialog(getContext(), "温馨提醒", "参与该活动需要绑定银行卡", "前往绑定", v -> {
                                                    ActBindBankCardToucai.launch(getActivity(), true);
                                                });
                                            }
                                            break;
                                        case -6:
                                            DialogsTouCai.showDialog(getContext(), "温馨提醒", "参与该活动需要绑定手机号", "前往绑定", v -> {
                                                ActBindPhone.launch(getActivity());
                                            });
                                            break;
                                        case -7:
                                            DialogsTouCai.showDialog(getContext(), "温馨提醒", "参与该活动需要绑定邮箱", "前往绑定", v -> {
                                                ActBindEmail.launcher(getActivity());
                                            });
                                            break;

                                        case -8:
                                            DialogsTouCai.showDialogOk(getContext(), "温馨提醒", "您已参与该活动,您可参与其他活动", v -> {
                                            });
                                            break;

                                        case -9:
                                            DialogsTouCai.showDialog(getContext(), "温馨提醒", "您已参与其他活动,请再次充值后再来领取奖金", "前往充值", v -> {
                                                ActDeposit.launch(getActivity());
                                            });
                                            break;

                                        case -10:
                                            DialogsTouCai.showDialog(getContext(), "温馨提醒", "您暂时未充值，请充值后再来领取奖金", "前往充值", v -> {
                                                ActDeposit.launch(getActivity());
                                            });
                                            break;

                                        case -11:
                                            DialogsTouCai.showDialog(getContext(), "温馨提醒", "参与该活动需要设置资金密码", "前往设置", v -> {
                                                ActSettingFundsPwd.launcher(getActivity(), false);
                                            });
                                            break;

                                        case -12:
                                            DialogsTouCai.showDialog(getContext(), "温馨提醒", "您充值金额未达标,请再次充值领取奖金", "前往充值", v -> {
                                                ActDeposit.launch(getActivity());
                                            });
                                            break;

                                        default:
                                            DialogsTouCai.showDialogOk(getContext(), "温馨提醒", msg, v -> {
                                            });
                                    }
                                }
                                return true;
                            }
                        });
                    });
                }

            } else if (!Strs.isEmpty(mission.outLink)) {
                holder.join.setVisibility(View.VISIBLE);
                holder.join.setText("立即加入");
                holder.join.setOnClickListener(v -> {
                    if (UserManagerTouCai.getIns().isLogined()) {
                        ActWebX5.launch(getContext(), mission.outLink, true);
                    } else {
                        UserManagerTouCai.getIns().redirectToLogin();
                    }
                });
            } else {
                holder.join.setVisibility(View.GONE);
                holder.join.setOnClickListener(null);
            }

            Date start = DateUtil.formatDateStr(mission.startTime, DateUtil.ymdhms);
            Date end = DateUtil.formatDateStr(mission.endTime, DateUtil.ymdhms);
            Date now = new Date();

            if (now.before(start)) {
                holder.status.setImageResource(R.mipmap.ic_activity_unstart);
            } else if (now.after(end)) {
                holder.status.setImageResource(R.mipmap.ic_activity_finished);
            } else {
                holder.status.setImageResource(R.mipmap.ic_activity_in_progress);

            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class VH extends RecyclerView.ViewHolder {
            ImageView bg;
            ImageView status;
            TextView title;
            TextView description;
            WebView content;
            TextView date;
            Button join;
            SimpleExpandableLayout container;

            public VH(View itemView) {
                super(itemView);
                bg = itemView.findViewById(R.id.iv_bg);
                status = itemView.findViewById(R.id.iv_status);
                title = itemView.findViewById(R.id.tv_title);
                content = itemView.findViewById(R.id.tv_content);
                description = itemView.findViewById(R.id.tv_description);
                date = itemView.findViewById(R.id.tv_date);
                join = itemView.findViewById(R.id.btn_join);
                container = itemView.findViewById(R.id.layout_container);
                content.getSettings().setTextZoom(70);
                content.getSettings().setJavaScriptEnabled(true);
                content.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                content.getSettings().setLoadWithOverviewMode(true);
            }
        }
    }
}




