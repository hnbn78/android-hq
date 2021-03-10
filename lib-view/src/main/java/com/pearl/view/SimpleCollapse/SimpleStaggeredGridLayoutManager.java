package com.pearl.view.SimpleCollapse;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by lee on 2017/10/30.
 */

public class SimpleStaggeredGridLayoutManager extends StaggeredGridLayoutManager {
    public SimpleStaggeredGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    
    public SimpleStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }
    
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
