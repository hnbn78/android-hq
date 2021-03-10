package com.desheng.app.toucai.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BaseTextView extends LinearLayout {
    /**
     * 包含两个TextView,前缀部分和内容部分
     */
    private TextView mPrefixtTetxtView, mContentTetxtView;

    public BaseTextView(Context context) {
        super(context);
        initTextView(context);
    }

    public BaseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTextView(context);
    }

    /**
     * 初始化布局和子控件
     *
     * @param context
     */
    private void initTextView(Context context) {

        setChildViewGravity();

        mPrefixtTetxtView = new TextView(context);
        mPrefixtTetxtView.setGravity(Gravity.CENTER);
        mPrefixtTetxtView.setTextSize(14);
        mPrefixtTetxtView.setTextColor(Color.BLACK);
        mPrefixtTetxtView.setBackgroundColor(Color.TRANSPARENT);
        mPrefixtTetxtView.setId(mPrefixtTetxtView.getImeActionId());


        mContentTetxtView = new TextView(context);
        mContentTetxtView.setGravity(Gravity.LEFT);
        mContentTetxtView.setTextSize(14);

        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        float density = dm.density;

        LinearLayout.LayoutParams prefixtTetxtViewParams = new LayoutParams((int) (30 * density + 0.5f), ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mPrefixtTetxtView, prefixtTetxtViewParams);

        LinearLayout.LayoutParams contentTetxtViewParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentTetxtViewParams.setMargins(10, 0, 0, 0);
        addView(mContentTetxtView, contentTetxtViewParams);
    }

    /**
     * 设置布局的默认排列方式，为水平方向，并且竖直居中
     */
    private void setChildViewGravity() {
        setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        setLayoutParams(params);
    }

    /**
     * 设置带有前缀和主要内容的文本显示
     *
     * @param prefixtTetxt 前缀显示
     * @param contentText  主要内容显示
     */
    public void setText(String prefixtTetxt, String contentText) {
        //mPrefixtTetxtView.setText(prefixtTetxt);
        mPrefixtTetxtView.setVisibility(GONE);
        mContentTetxtView.setText(contentText);
        mContentTetxtView.setTextColor(Color.BLACK);

    }

    public void setContentTextColor(int contentTextColor) {
        mContentTetxtView.setTextColor(contentTextColor);
    }

    /**
     * 设置只带有主要内容文本显示
     *
     * @param contentText
     */
    public void setText(String contentText) {
        setText("", contentText);
    }

}
