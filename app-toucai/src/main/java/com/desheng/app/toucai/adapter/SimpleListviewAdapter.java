package com.desheng.app.toucai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shark.tc.R;

import java.util.List;

public class SimpleListviewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<String> mDatas;

    //MyAdapter需要一个Context，通过Context获得Layout.inflater，然后通过inflater加载item的布局
    public SimpleListviewAdapter(Context context, List<String> datas) {

        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    //返回数据集的长度
    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //这个方法才是重点，我们要为它编写一个ViewHolder
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_choujiangma_list, parent, false); //加载布局
            holder = new ViewHolder();

            holder.choujiangindex = (TextView) convertView.findViewById(R.id.choujiangindex);
            holder.choujiangma = (TextView) convertView.findViewById(R.id.choujiangma);
            convertView.setTag(holder);
        } else {   //else里面说明，convertView已经被复用了，说明convertView中已经设置过tag了，即holder
            holder = (ViewHolder) convertView.getTag();
        }

        String code = mDatas.get(position);
        holder.choujiangindex.setText(String.valueOf(mDatas.size() - position));
        holder.choujiangma.setText("抽奖码 : " + code);
        return convertView;
    }

    //这个ViewHolder只能服务于当前这个特定的adapter，因为ViewHolder里会指定item的控件，不同的ListView，item可能不同，所以ViewHolder写成一个私有的类
    private class ViewHolder {
        TextView choujiangindex;
        TextView choujiangma;
    }

}
