package com.desheng.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.hubert.guide.util.ScreenUtils;
import com.desheng.base.R;
import com.desheng.base.util.ResUtil;

import java.util.List;

/**
 * 走势图表 数据适配器(含期号,走势图顶部数据,走势图底部数据显示)....
 */
public class CellDataAdapter extends BaseAdapter {

    private Context context;
    private int mCellWidth;
    private List listData = null;

    public CellDataAdapter(Context context, List data,int codenums) {
        this.context = context;
        int temp = (ScreenUtils.getScreenWidth(context) - context.getResources().getDimensionPixelSize(R.dimen.trend_issuno_withd)) / codenums;
        mCellWidth = temp;
        this.listData = data;
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_trend_chart_cell, parent, false);
        }
        //item尺寸
        ViewGroup.LayoutParams convertParams = convertView.getLayoutParams();
        convertParams.width = mCellWidth;
        convertParams.height = mCellWidth;
        convertView.setLayoutParams(convertParams);
        final TextView tvContent = convertView.findViewById(R.id.tvContent);
        tvContent.setText(listData.get(position).toString());
        return convertView;
    }
}
