package com.desheng.base.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ab.util.Strs;
import com.desheng.base.R;
import com.pearl.act.util.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 彩票 图片 + 图标
 * Created by lee on 2018/3/8.
 */
public class AdapterNumGrid extends BaseAdapter {
    
    private Context context;
    private ArrayList<HashMap<String, Object>> listData;
    LayoutInflater layoutInflater;
    
    public AdapterNumGrid(Context context, ArrayList<HashMap<String, Object>> list) {
        this.context = context;
        this.listData = list;
        layoutInflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return listData.size();
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
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.item_square_misc_round_small, null);
        }
        TextView tvCode = (TextView) convertView.findViewById(R.id.tvCode);
        int num = (int) listData.get(position).get("code");
        double alpha = (double) listData.get(position).get("alpha");
        tvCode.setText(Strs.of(num));
        tvCode.setAlpha((float)alpha);
        GradientDrawable bg = (GradientDrawable) tvCode.getBackground();
        bg.setColor(UIHelper.getNumBgColor(num));
        bg.setStroke(1, UIHelper.getNumBgColor(num));
        
        return convertView;
    }
}
