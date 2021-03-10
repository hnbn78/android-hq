package com.desheng.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.desheng.base.R;
import com.desheng.base.model.MessageBean;

import java.util.List;

/**
 * Created by o on 2018/4/5.
 */

public class MessageAdapter extends BaseAdapter {
    public Context con;
    public LayoutInflater inflater;
    public List<MessageBean> list;

    public MessageAdapter(Context context, List<MessageBean> messageBeanList) {
        this.con = context;
        this.list = messageBeanList;
        inflater = LayoutInflater.from(con);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_mesage, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv2.setText("" + list.get(position).getContent());
        holder.tv2.setText("" + list.get(position).getTime());
        holder.tv1.setText("" + list.get(position).getTitle());
        return convertView;
    }

    class ViewHolder {
        TextView tv1;
        TextView tv2;
        ImageView img;

        public ViewHolder(View view) {
            tv1 = (TextView) view.findViewById(R.id.tvTitle);
            tv2 = (TextView) view.findViewById(R.id.tvTime);
            img = (ImageView) view.findViewById(R.id.imArrow);
        }
    }
}
