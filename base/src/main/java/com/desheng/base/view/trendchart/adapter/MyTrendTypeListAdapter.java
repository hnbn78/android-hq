package com.desheng.base.view.trendchart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.desheng.base.R;
import com.desheng.base.manager.UserManager;
import com.desheng.base.view.trendchart.TrendTypeIndexBean;

import java.util.ArrayList;
import java.util.List;

public class MyTrendTypeListAdapter extends BaseAdapter {
    private List<TrendTypeIndexBean> arrayList = new ArrayList();
    private Context context;
    private ViewHolder holder;
    private int itemPos;
    private LayoutInflater mInflater;
    private int pPos;
    private int pos;
    private String lotteryType;

    public MyTrendTypeListAdapter(Context context,String lotteryType, List<TrendTypeIndexBean> dataList,int pos) {
        this.context = context;
        this.arrayList = dataList;
        this.lotteryType = lotteryType;
        this.mInflater = LayoutInflater.from(this.context);
        this.pos = pos;
        this.pPos = UserManager.getIns().getPposition(lotteryType);
        this.itemPos = UserManager.getIns().getItemPosition(lotteryType);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_trend_type_list_item, parent, false);
            holder = new ViewHolder();
            holder.tvTrendSmallType = convertView.findViewById(R.id.trend_type_list_item_name);
            holder.tvTrendSmallType.setText(arrayList.get(position).getName());
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(pos==pPos&&position==itemPos){
            holder.tvTrendSmallType.setTextColor(context.getResources().getColor(R.color.red));
        }


        return convertView;
    }


    static class ViewHolder {
        private TextView tvTrendSmallType;
    }


}
