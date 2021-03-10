package com.desheng.base.view.trendchart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.desheng.base.R;
import com.desheng.base.view.trendchart.TrendTypeIndexBean;

import java.util.ArrayList;
import java.util.List;

public class TrendTypeListAdapter extends BaseAdapter {
    private List<TrendTypeIndexBean> arrayList = new ArrayList();
    private Context context;
    private ViewHolder holder;
    private int itemPos;
    private LayoutInflater mInflater;
    private int pPos;
    private int pos;

    public TrendTypeListAdapter(Context paramContext, List<TrendTypeIndexBean> paramList, int paramInt1, int paramInt2) {
        this.context = paramContext;
        this.arrayList = paramList;
        this.mInflater = LayoutInflater.from(this.context);
        this.pPos = paramInt2;
        paramContext = this.context;
//        paramList = new StringBuilder();
//        paramList.append("trendSetting");
//        paramList.append(paramInt1);
//        paramContext = paramContext.getSharedPreferences(paramList.toString(), 0);
//        this.pos = paramContext.getInt("pos", 0);
//        this.itemPos = paramContext.getInt("itemPos", 0);
    }

    private void init(int paramInt) {
        TrendTypeIndexBean localTrendTypeIndexBean = this.arrayList.get(paramInt);
        if (localTrendTypeIndexBean != null) {
            this.holder.name.setText(localTrendTypeIndexBean.getName());
//            this.holder.name.setTextColor(ContextCompat.getColor(this.context, 2131492893));
//            this.holder.name.setBackgroundResource(2130837639);
            if ((this.pPos == this.pos) && (this.itemPos == paramInt)) {
//                this.holder.name.setTextColor(ContextCompat.getColor(this.context, 2131493111));
//                this.holder.name.setBackgroundResource(2130837781);
            }
        }
    }

    public void DataSetChanged(List<TrendTypeIndexBean> paramList) {
        this.arrayList = paramList;
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
            paramView = this.mInflater.inflate(R.layout.layout_trend_type_list_item, paramViewGroup, false);
            this.holder = new ViewHolder(paramView);
            paramView.setTag(this.holder);
        } else {
            this.holder = ((ViewHolder) paramView.getTag());
        }
        init(paramInt);
        return paramView;
    }

    class ViewHolder {
        private TextView name;
        public ViewHolder(View view){
            name = view.findViewById(R.id.trend_type_list_item_name);
        }
    }
}
