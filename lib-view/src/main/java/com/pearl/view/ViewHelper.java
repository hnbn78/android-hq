package com.pearl.view;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class ViewHelper {
    
    public static int getQuickAdapterPosition(BaseQuickAdapter adapter, BaseViewHolder holder) {
          return holder.getLayoutPosition() - adapter.getHeaderLayoutCount();
    }
}
