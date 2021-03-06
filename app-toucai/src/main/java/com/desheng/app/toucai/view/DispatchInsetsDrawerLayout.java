package com.desheng.app.toucai.view;

import android.content.Context;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

/**
 * Created by user on 2018/3/9.
 */

public class DispatchInsetsDrawerLayout extends DrawerLayout {
    private DispatchInsetsHelper mInsetsHelper = new DispatchInsetsHelper(
            new DispatchInsetsHelper.Delegate() {

                @Override
                public int getGravityFromLayoutParams(ViewGroup.LayoutParams layoutParams) {
                    return ((LayoutParams) layoutParams).gravity;
                }

                @Override
                public ViewGroup getOwner() {
                    return DispatchInsetsDrawerLayout.this;
                }

                @Override
                public void superAddView(View child, int index, ViewGroup.LayoutParams params) {
                    DispatchInsetsDrawerLayout.super.addView(child, index, params);
                }

                @Override
                public boolean superAddViewInLayout(View child, int index,
                                                    ViewGroup.LayoutParams params,
                                                    boolean preventRequestLayout) {
                    return DispatchInsetsDrawerLayout.super.addViewInLayout(child, index, params,
                            preventRequestLayout);
                }
            });

    public DispatchInsetsDrawerLayout(Context context) {
        super(context);

        init();
    }

    public DispatchInsetsDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public DispatchInsetsDrawerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {

        setFitsSystemWindows(false);

        //实现全屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        return mInsetsHelper.dispatchApplyWindowInsets(insets);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        mInsetsHelper.addView(child, index, params);
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params,
                                      boolean preventRequestLayout) {
        return mInsetsHelper.addViewInLayout(child, index, params, preventRequestLayout);
    }
}
