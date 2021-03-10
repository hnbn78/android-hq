package com.desheng.base.view.trendchart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.desheng.base.R;

import java.util.ArrayList;
import java.util.List;

public class GameTrendDataLeftAdapter extends BaseAdapter {
    private List<String> arrayList = new ArrayList();
    private Context context;
    private ViewHolder holder;
    private LayoutInflater mInflater;
    public RequestManager requestManager;

    public GameTrendDataLeftAdapter(Context paramContext, List<String> paramList) {
        this.context = paramContext;
        this.arrayList = paramList;
        this.mInflater = LayoutInflater.from(this.context);
        this.requestManager = Glide.with(this.context);
    }

    private void init(int paramInt) {
        if (this.arrayList.get(paramInt) != null) {
            this.holder.tv_content.setText(this.arrayList.get(paramInt));
        }
    }

    public void DataSetChanged() {
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.arrayList.size();
    }

    public Object getItem(int paramInt) {
        return this.arrayList.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
        if (paramView == null) {
            paramView = this.mInflater.inflate(R.layout.layout_game_trend_left_item, paramViewGroup, false);
            this.holder = new ViewHolder(paramView);
//            ((TongjiActivityFrament) this.context).initDipView(paramView);
            paramView.setTag(this.holder);
        } else {
            this.holder = ((ViewHolder) paramView.getTag());
        }
        init(paramInt);
        return paramView;
    }

    static class ViewHolder {
        TextView tv_content;

        ViewHolder(View paramView) {
            tv_content = paramView.findViewById(R.id.tv_content);
        }
    }
}
