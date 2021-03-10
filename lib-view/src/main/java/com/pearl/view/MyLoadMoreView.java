package com.pearl.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2017/8/3 0003.
 */

public class MyLoadMoreView extends RelativeLayout {

    public static final int STATUS_DEFAULT = 0;
    public static final int STATUS_MORE = 1;
    public static final int STATUS_LOADING = 2;
    public static final int STATUS_FAIL = 3;
    public static final int STATUS_END = 4;

    private int mLoadMoreStatus = STATUS_DEFAULT;

    private boolean mLoadMoreEndGone = false;

    public MyLoadMoreView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(getLayoutId(), this);
    }

    public MyLoadMoreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(getLayoutId(), this);
    }

    public MyLoadMoreView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(getLayoutId(), this);
    }


    public void setLoadMoreStatus(int loadMoreStatus) {
        this.mLoadMoreStatus = loadMoreStatus;
        visibleLoadMore(false);
        visibleLoading(false);
        visibleLoadFail(false);
        visibleLoadEnd(false);
        switch (mLoadMoreStatus) {
            case STATUS_MORE:
                visibleLoadMore(true);
                break;
            case STATUS_LOADING:
                visibleLoading(true);
                break;
            case STATUS_FAIL:
                visibleLoadFail(true);
                break;
            case STATUS_END:
                visibleLoadEnd(true);
                break;
        }
    }

    public int getLoadMoreStatus() {
        return mLoadMoreStatus;
    }

    private void visibleLoadMore(boolean visible) {
        this.findViewById(getLoadMoreViewId()).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void visibleLoading(boolean visible) {
        this.findViewById(getLoadingViewId()).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void visibleLoadFail(boolean visible) {
        this.findViewById(getLoadFailViewId()).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void visibleLoadEnd(boolean visible) {
        this.findViewById(getLoadEndViewId()).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }


    public int getLayoutId() {
        return R.layout.my_load_more;
    }

    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }

    protected int getLoadMoreViewId() {
        return R.id.click_to_load_more_view;
    }

    ;


}
