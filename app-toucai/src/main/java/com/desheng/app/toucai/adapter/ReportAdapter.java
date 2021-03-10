package com.desheng.app.toucai.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.model.Params;
import com.shark.tc.R;

import java.util.List;

public class ReportAdapter extends BaseQuickAdapter<List<Params>, BaseViewHolder> {

    public com.desheng.base.adapter.OnItemClickListener<String> onItemClickListener;

    public ReportAdapter(@Nullable List<List<Params>> data) {
        super(R.layout.recycler_view, data);
    }

    public void setOnItemClickListener(com.desheng.base.adapter.OnItemClickListener<String> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, List<Params> item) {
        RecyclerView recyclerView = (RecyclerView) helper.itemView;
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 2));
        recyclerView.setNestedScrollingEnabled(false);
        ItemReportAdapter itemReportAdapter = new ItemReportAdapter(item);
        recyclerView.setAdapter(itemReportAdapter);
        final int index = helper.getAdapterPosition();
        itemReportAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (onItemClickListener != null) {
                String userName = String.valueOf(item.get(0).value);
                if (!TextUtils.isEmpty(userName) && index > 0)
                    onItemClickListener.onItemClick(userName);
            }
        });
    }
}