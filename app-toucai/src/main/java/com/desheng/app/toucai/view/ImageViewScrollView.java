package com.desheng.app.toucai.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.pearl.view.SimpleCollapse.SimpleNestedScrollView;

public class ImageViewScrollView extends SimpleNestedScrollView {

    public ImageViewScrollView(Context context) {
        super(context);
    }

    public ImageViewScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //用于记录下拉位置
    private float y = 0f;
    //zoomView原本的宽高
    private int zoomViewWidth = 0;
    private int zoomViewHeight = 0;
    //是否正在放大
    private boolean mScaling = false;
    //放大的view，默认为第一个子view
    private View zoomView;

    /**
     * onFinishInflate方法 View中所有的子控件均被映射成xml后触发
     */
//
//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//        // 不可过度滚动，否则上移后下拉会出现部分空白的情况
//        setOverScrollMode(OVER_SCROLL_NEVER);
//        //获得默认第一个view
//        if (getChildAt(0) != null && getChildAt(0) instanceof ViewGroup && zoomView == null) {
//            ViewGroup vg = (ViewGroup) getChildAt(0);
//            if (vg.getChildCount() > 0) {
//                zoomView = vg.getChildAt(0);
//            }
//        }
//    }

    float y1 = 0;
    float y2 = 0;


    /**
     * onTouchEvent方法
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //当手指按下的时候
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                //当手指离开的时候
                y2 = event.getY();
                if(y1 - y2 > 50) {
                    if (onScrollListener!=null) {
                        onScrollListener.onUpScroll();
                    }
                } else if(y2 - y1 > 50) {
                    if (onScrollListener!=null) {

                        onScrollListener.onDownScroll();
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * onScrollChanged方法
     */

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (null != onScrollListener) {
            onScrollListener.onScroll(l, t, oldl, oldt);
        }
    }

    /**
     * 滑动监听
     */

    private OnScrollListener onScrollListener;

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public interface OnScrollListener {
        void onScroll(int scrollX, int scrollY, int oldScrollX, int oldScrollY);

        void onDownScroll();

        void onUpScroll();
    }

}
