package com.desheng.app.toucai.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shark.tc.R;

import java.util.List;

public class ZhongjiangResultAapter extends RecyclerView.Adapter<ZhongjiangResultAapter.VH> {

    Context mContext;
    private List<String> mDatas;
    int levelstatus;

    public ZhongjiangResultAapter(Context context, List<String> data, int levelstatus) {
        this.mDatas = data;
        this.mContext = context;
        this.levelstatus = levelstatus;
    }

    public void setData(List<String> data) {
        mDatas = data;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (mDatas == null) {
            return;
        }
        holder.text1.setTextColor(levelstatus == 0 ? mContext.getResources().getColor(R.color.yellow_light) : Color.WHITE);
        holder.text1.setText(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_item_text, parent, false);
        return new VH(v);
    }

    class VH extends RecyclerView.ViewHolder {
        TextView text1;

        public VH(View v) {
            super(v);
            text1 = (TextView) v.findViewById(R.id.text1);
        }
    }

}

