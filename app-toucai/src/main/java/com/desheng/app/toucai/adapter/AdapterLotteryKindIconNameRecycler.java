package com.desheng.app.toucai.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.util.Maps;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.desheng.base.model.LotteryInfo;
import com.shark.tc.R;

import java.util.List;

/**
 * 彩票 图片 + 图标
 * Created by lee on 2018/3/8.
 */
public class AdapterLotteryKindIconNameRecycler extends BaseQuickAdapter<LotteryInfo, AdapterLotteryKindIconNameRecycler.LotteryVH> {
    
    private Context context;
    private List<LotteryInfo> listData;
    
    public AdapterLotteryKindIconNameRecycler(Context context, List<LotteryInfo> list) {
        super(R.layout.item_lottery_kind_icon_name, list);
        this.context = context;
        this.listData = list;
    }
    
    @Override
    protected void convert(LotteryVH holder, LotteryInfo item) {
        //网络图片和本地图片
        String icon = null;
        if (Maps.value(item.getExtra(), "isOpened", false)){
            icon = item.getKind().getIconActive();
        }else{
            icon = item.getKind().getIconInactive();
        }
        if (icon.startsWith("http://") || icon.startsWith("https://")) {
            Glide.with(context).load(icon).into(holder.ivIcon);
        } else if (icon.startsWith("file:///")) {
            Glide.with(context).load(Uri.parse(icon)).into(holder.ivIcon);
        }
    
        holder.tvName.setText(item.getShowName());
        
        holder.itemView.setTag(item);
    }
    
    public static class LotteryVH extends BaseViewHolder{
        public ImageView ivIcon;
        public TextView tvName;
        public LotteryVH(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
        }
    }
}
