package com.pearl.view.SimpleCollapse;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by lee on 2017/10/12.
 */

public class SimpleCoordinatorLayout extends CoordinatorLayout{
    private boolean willFlitting = false;
    private boolean isTouching = false;
    public SimpleCoordinatorLayout(Context context) {
        super(context);
    }
    
    public SimpleCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public SimpleCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return willFlitting;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isTouching = true;
                break;
            case MotionEvent.ACTION_UP:
                isTouching = false;
                break;
        }
        return super.onTouchEvent(ev);
    }
    
    public boolean isFlitting() {
        return willFlitting;
    }
    
    public void setWillFlitting(boolean willFlitting) {
        this.willFlitting = willFlitting;
    }
    
    public boolean isTouching() {
        return isTouching;
    }
    
    public void setTouching(boolean touching) {
        isTouching = touching;
    }
}
