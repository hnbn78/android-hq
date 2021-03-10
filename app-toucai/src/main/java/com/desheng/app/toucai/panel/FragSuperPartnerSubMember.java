package com.desheng.app.toucai.panel;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ab.http.AbHttpRespEntity;
import com.ab.http.AbHttpResult;
import com.desheng.app.toucai.action.HttpActionTouCai;
import com.desheng.app.toucai.model.SubUserLastLogin;
import com.google.gson.reflect.TypeToken;
import com.pearl.act.base.AbBaseFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragSuperPartnerSubMember extends AbBaseFragment {
    int currentPage = 0;
    final int size = 20;
    private RecyclerView recyclerView;
    private View emptyView;
    private SmartRefreshLayout smartRefreshLayout;
    MemberAdapter memberAdapter = new MemberAdapter();

    @Override
    public int getLayoutId() {
        return R.layout.frag_super_partner_sub_member;
    }

    @Override
    public void init(View root) {
        emptyView = root.findViewById(R.id.layout_nodata);
        smartRefreshLayout = ((SmartRefreshLayout) root.findViewById(R.id.smartRefreshLayout));
        smartRefreshLayout.setEnableLoadMore(false);
        smartRefreshLayout.setEnableRefresh(false);
        smartRefreshLayout.setOnLoadMoreListener(l -> {
            initMember(currentPage + 1);
        });
        recyclerView = ((RecyclerView) root.findViewById(R.id.recycleView));
        recyclerView.setAdapter(memberAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        initMember(currentPage);
    }

    public void initMember(int page) {
        initMember(null, page);
    }

    public void initMember(String username, int page) {
        HttpActionTouCai.getSubUserLastLogin(
                this,
                null,
                null,
                username,
                page,
                size,
                new AbHttpResult() {
                    @Override
                    public void setupEntity(AbHttpRespEntity entity) {
                        entity.putField("data", new TypeToken<SubUserLastLogin>() {
                        }.getType());
                    }

                    @Override
                    public boolean onSuccessGetObject(int code, int error, String msg, HashMap<String, Object> extra) {
                        if (code == 0 && error == 0) {
                            if (page == 0) {
                                memberAdapter.data.clear();
                                memberAdapter.notifyDataSetChanged();
                            }
                            SubUserLastLogin list = getField(extra, "data", null);
                            if (list != null && list.getList() != null && list.getList().size() != 0) {
                                memberAdapter.data.addAll(list.getList());
                                memberAdapter.notifyDataSetChanged();
                                currentPage = page;
                            }
                        }

                        return true;
                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        smartRefreshLayout.finishLoadMore();
                        if (memberAdapter.getItemCount() == 0) {
                            smartRefreshLayout.setEnableLoadMore(false);
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            smartRefreshLayout.setEnableLoadMore(true);
                            emptyView.setVisibility(View.GONE);
                        }
                    }
                });

    }

    class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberHolder> {
        List<SubUserLastLogin.ListBean> data = new ArrayList<>();

        @Override
        public MemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemview;
            itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_super_partner_sub_member, parent, false);
            return new MemberHolder(itemview);
        }

        @Override
        public void onBindViewHolder(MemberHolder holder, int position) {
            if (data == null || position >= data.size()) {
                return;
            }
            SubUserLastLogin.ListBean listBean = data.get(position);
            holder.id.setText(String.valueOf(listBean.getIndex()));
            holder.name.setText(listBean.getCn());
            holder.lastLogin.setText(listBean.getLoginTime());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MemberHolder extends RecyclerView.ViewHolder {
            TextView id;
            TextView name;
            TextView lastLogin;

            public MemberHolder(View itemView) {
                super(itemView);
                id = ((TextView) itemView.findViewById(R.id.tv_id));
                name = ((TextView) itemView.findViewById(R.id.tv_name));
                lastLogin = ((TextView) itemView.findViewById(R.id.tv_last_login));
            }
        }
    }
}
