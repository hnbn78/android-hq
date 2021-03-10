package com.desheng.app.toucai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shark.tc.R;

public class PopupItemAdapter extends BaseAdapter {

    String[] args;
    private Context context;
    public PopupItemAdapter(Context context, String[] args) {
        this.args = args;
        this.context = context;
    }

   public void resetArgs(String[] args) {
        this.args = args;
    }

    @Override
    public int getCount() {
        return args.length;
    }

    @Override
    public Object getItem(int position) {
        return args[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_team_type, parent, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.textView.setText(args[position]);
        return convertView;
    }

    class Holder {
        TextView textView;

        Holder(View view) {
            textView = (TextView) view;
        }
    }
}
