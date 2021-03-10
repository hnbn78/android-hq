package com.desheng.app.toucai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.desheng.base.model.LotteryInfo;
import com.shark.tc.R;

import java.util.ArrayList;

/**
 * 彩票 图片 + 图标
 * Created by lee on 2018/3/8.
 */
public class AdapterLotteryKindGrid extends BaseAdapter {
    
    private Context context;
    private ArrayList<LotteryInfo> listData;
    LayoutInflater layoutInflater;
    
    public AdapterLotteryKindGrid(Context context, ArrayList<LotteryInfo> list) {
        this.context = context;
        this.listData = list;
        layoutInflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return listData.size() + 1;
        //注意此处
    }
    
    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.item_lottery_kind_icon_name, null);
    
        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        
        if (position < listData.size()) {
            //网络图片和本地图片
            String icon = listData.get(position).getKind().getIconActive();
            if (icon.startsWith("http://") || icon.startsWith("https://")) {
                Glide.with(context).load(icon).placeholder(R.drawable.ic_lottery_default).into(ivIcon);
            } else if (icon.startsWith("file:///")) {
                Glide.with(context).load(icon).placeholder(R.drawable.ic_lottery_default).into(ivIcon);
            }
            
            tvName.setText(listData.get(position).getShowName());
        } else {
            //最后一个显示加号图片
            Glide.with(context).load(R.mipmap.ic_home_add_more_btn).into(ivIcon);
            tvName.setText("更多");
        }
        return convertView;
    }
}
