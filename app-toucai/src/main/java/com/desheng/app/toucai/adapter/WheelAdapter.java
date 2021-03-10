package com.desheng.app.toucai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.desheng.app.toucai.view.WheelItem;
import com.wx.wheelview.adapter.BaseWheelAdapter;

public class WheelAdapter extends BaseWheelAdapter<Integer> {

    private Context mContext;

    public WheelAdapter(Context context) {
        mContext = context;
    }

    @Override
    protected View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new WheelItem(mContext);
        }
        WheelItem item = (WheelItem) convertView;
        item.setImage(mList.get(position));
        return convertView;
    }
}
