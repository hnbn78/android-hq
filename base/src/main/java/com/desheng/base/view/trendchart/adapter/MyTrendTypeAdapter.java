package com.desheng.base.view.trendchart.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.R;
import com.desheng.base.view.MyGridView;
import com.desheng.base.view.trendchart.TrendTypeBean;
import com.desheng.base.view.trendchart.TrendTypeIndexBean;

import java.util.List;


public class MyTrendTypeAdapter extends BaseQuickAdapter<TrendTypeBean, MyTrendTypeAdapter.ViewHolder> {

    private MyTrendTypeListAdapter trendTypeListAdapter;
    private Context context;
    private OnTypeItemClickListener onItemClickListener;
    private int currentPosition;
    private String lotteryType;

    public MyTrendTypeAdapter(@Nullable List<TrendTypeBean> data, Context context, String lotteryType, OnTypeItemClickListener onItemClickListener) {
        super(R.layout.layout_trend_type_item, data);
        this.context = context;
        this.lotteryType = lotteryType;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    protected void convert(final ViewHolder helper, final TrendTypeBean item) {

        helper.trendTvName.setText(item.getName());
        List<TrendTypeIndexBean> indexList = item.getIndex_list();
        currentPosition = helper.getAdapterPosition();
        helper.trendGV.setTag(currentPosition);
        trendTypeListAdapter = new MyTrendTypeListAdapter(context, lotteryType, indexList, currentPosition);
        helper.trendGV.setAdapter(trendTypeListAdapter);
        helper.trendGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                onItemClickListener.onItemClick(item.getIndex_list().get(position), (int) helper.trendGV.getTag(), position);
            }
        });

    }


    class ViewHolder extends BaseViewHolder {

        TextView trendTvName;
        MyGridView trendGV;

        public ViewHolder(View view) {
            super(view);
            trendTvName = view.findViewById(R.id.trend_type_item_name);
            trendGV = view.findViewById(R.id.trend_type_item_list);
        }
    }

    public interface OnTypeItemClickListener {
        void onItemClick(TrendTypeIndexBean indexBean, int pPosition, int itemPosition);
    }

}
