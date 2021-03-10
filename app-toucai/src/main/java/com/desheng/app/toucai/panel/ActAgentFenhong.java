package com.desheng.app.toucai.panel;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pearl.act.base.AbAdvanceActivity;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.List;

public class ActAgentFenhong extends AbAdvanceActivity {

    private TextView tv_title;
    private RecyclerView recyclerView;
    private BaseQuickAdapter<String, BaseViewHolder> adapter;
    List<String> strings;

    @Override
    protected int getLayoutId() {
        return R.layout.act_agent_fenhong;
    }

    @Override
    protected void init() {
        hideToolbar();
        View headRoot = findViewById(R.id.vgToolbarGroup);
        paddingHeadOfStatusHeight(headRoot);
        setStatusBarTranslucentAndLightContentWithPadding();
        tv_title = ((TextView) headRoot.findViewById(R.id.tv_title));
        tv_title.setText("分红设置");
        initView();
    }

    private void initView() {
        recyclerView = ((RecyclerView) findViewById(R.id.recycleView));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_agent_fenhong) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                int layoutPosition = helper.getLayoutPosition();
                helper.getView(R.id.remove).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (adapter.getData().size() > 1) {
                            adapter.remove(layoutPosition);
                        }
                    }
                });

                if (adapter.getData().size() == 1) {
                    helper.getView(R.id.remove).setVisibility(View.GONE);
                } else {
                    helper.getView(R.id.remove).setVisibility(View.VISIBLE);
                }
            }
        };
        View footview = LayoutInflater.from(this).inflate(R.layout.agent_fenhong_footview, null, false);
        adapter.addFooterView(footview);
        footview.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (strings != null) {
                    int size = strings.size();
                    adapter.addData("" + size + 1);
                }
                recyclerView.scrollToPosition(adapter.getData().size()-1);
            }
        });

        recyclerView.setAdapter(adapter);


        strings = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            strings.add(String.valueOf(i));
        }

        adapter.setNewData(strings);
    }
}
