package com.desheng.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.desheng.base.R;

/**
 * Created by user on 2018/3/2.
 */

public class BackButton extends RelativeLayout {
    private Context mContext;
    private ImageView mImageView;

    public BackButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    public BackButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public BackButton(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        mImageView = new ImageView(mContext);
        mImageView.setBackgroundResource(R.mipmap.ic_back_normal);
        mImageView.setImageResource(R.mipmap.ic_back_normal);
        LayoutParams mParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mParams.addRule(RelativeLayout.CENTER_VERTICAL);
        setPadding(ViewUtils.dpToPxInt(10, mContext), 0, ViewUtils.dpToPxInt(16, mContext), 0);
        mImageView.setLayoutParams(mParams);
        addView(mImageView);
    }

    private LayoutParams createLayoutParams(int width, int height, int leftPadding) {
        int fixWidth = ViewUtils.dpToPxInt(width, mContext);
        int fixHeight = ViewUtils.dpToPxInt(height, mContext);
        LayoutParams mParams = new LayoutParams(fixWidth, fixHeight);
        mParams.addRule(RelativeLayout.CENTER_VERTICAL);
        setPadding(ViewUtils.dpToPxInt(leftPadding, mContext), 0, ViewUtils.dpToPxInt(16, mContext), 0);
        return mParams;
    }

    public void build(int width, int height, int leftPadding, int resId) {
        mImageView = new ImageView(mContext);
        mImageView.setLayoutParams(createLayoutParams(width, height, leftPadding));
        mImageView.setBackgroundResource(resId);
        removeAllViews();
        addView(mImageView);
    }



}
