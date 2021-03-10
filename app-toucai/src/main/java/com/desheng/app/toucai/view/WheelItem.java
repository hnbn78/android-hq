package com.desheng.app.toucai.view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shark.tc.R;
import com.wx.wheelview.common.WheelConstants;
import com.wx.wheelview.util.WheelUtils;

public class WheelItem extends FrameLayout{
    private ImageView image;

    public WheelItem(Context context) {
        super(context);
        init(context);
    }

    public WheelItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WheelItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_tiger_machin, this);
        image = ((ImageView) inflate.findViewById(R.id.ivlogo));
    }


    /**
     * 设置图片资源
     *
     * @param resId
     */
    public void setImage(int resId) {
        image.setVisibility(View.VISIBLE);
        image.setBackgroundResource(resId);
    }
}
