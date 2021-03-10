package com.desheng.base.view.trendchart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.desheng.base.R;
import com.desheng.base.util.ChangdiptopxUtil;
import com.desheng.base.view.trendchart.TrendView;

import java.util.List;

public class GameTrendDataAdapter extends BaseAdapter {
    private List<String> arrayList ;
    private Context context;
    private ViewHolder holder;
    private LayoutInflater mInflater;
    public RequestManager requestManager;
    private ChangdiptopxUtil changdiptopxUtil;
    private LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT);

    public GameTrendDataAdapter(Context context, List<String> stringList,int width) {
        this.context = context;
        this.arrayList = stringList;
        this.mInflater = LayoutInflater.from(this.context);
        this.requestManager = Glide.with(this.context);
        changdiptopxUtil = new ChangdiptopxUtil();
        lp.width = width;
        lp.height =changdiptopxUtil.dip2pxInt(TrendView.HEIGHT) ;
    }

    private void init(int position) {
        if ( this.arrayList.get(position) != null) {
            this.holder.tv_content.setText( this.arrayList.get(position));
        }
    }

    @Override
    public int getCount() {
        return this.arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.layout_game_trend_item, parent, false);

            this.holder = new ViewHolder(convertView, lp);
            convertView.setTag(this.holder);
        } else {
            this.holder = ((ViewHolder) convertView.getTag());
        }
        init(position);
        return convertView;
    }

    static class ViewHolder {
        TextView tv_content;

        ViewHolder(View view,LinearLayout.LayoutParams lp) {
            tv_content = view.findViewById(R.id.game_trend_item_tv_content);
            tv_content.setLayoutParams(lp);

        }
    }
}
