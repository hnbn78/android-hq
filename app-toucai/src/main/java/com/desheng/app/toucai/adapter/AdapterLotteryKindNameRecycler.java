package com.desheng.app.toucai.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.ab.util.Maps;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.model.LotteryInfo;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 彩票 文字
 * Created by lee on 2018/3/8.
 */
public class AdapterLotteryKindNameRecycler extends BaseQuickAdapter<LotteryInfo, AdapterLotteryKindNameRecycler.LotteryVH> {
    
    private Context context;
    private List<LotteryInfo> listData;
    
    public AdapterLotteryKindNameRecycler(Context context, List<LotteryInfo> list) {
        super(R.layout.item_lottery_kind_name, list);
        this.context = context;
        this.listData = list;
    }
    
    public AdapterLotteryKindNameRecycler(Context context, int layoutResId, ArrayList<LotteryInfo> list) {
        super(layoutResId, list);
        this.context = context;
        this.listData = list;
    }
    
    @Override
    protected void convert(LotteryVH holder, LotteryInfo item) {
        holder.tvName.setText(item.getShowName());
        holder.itemView.setTag(item);
        if (Maps.value(item.getExtra(), "isOpened", false)){
            holder.tvName.setTextColor(Color.BLACK);
        }else{
            holder.tvName.setTextColor(Color.GRAY);
        }
    }
    
    public static class LotteryVH extends BaseViewHolder{
        public TextView tvName;
        public LotteryVH(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
        }
    }
}
