package com.ab.adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ab.core.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 2017/10/2.
 */

public class AbConsoleAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<Spanned> mList;
    public View.OnClickListener itemClickListener;
    
    public AbConsoleAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        mList = new ArrayList<>();
    }
    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }
    
    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return 0;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.ab_item_console_list, parent, false);
        }
        ((TextView)convertView.findViewById(R.id.tvTxt)).setText(mList.get(position));
        return convertView;
    }
    
    public void appendInfo(String data){
        if(mList.size() > 1000){
            mList.clear();
        }
        String textStr = "<font color=\"#0000FF\">" + data + "</font>";
        mList.add(Html.fromHtml(textStr));
        notifyDataSetChanged();
    }
    
    public void appendError(String data){
        if(mList.size() > 1000){
            mList.clear();
        }
        String textStr = "<strong><font color=\"#FF0000\">" + data + "</font><strong>";
        mList.add(Html.fromHtml(textStr));
        notifyDataSetChanged();
    }
    
    public void clearData(){
        mList.clear();
        notifyDataSetChanged();
    }
}
