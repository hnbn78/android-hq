package com.pearl.view.SimpleCollapse;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

/**
 * Created by lee on 2017/10/30.
 */

public class SimpleNestedScrollView extends NestedScrollView {
    private SimpleCoordinatorLayout parentCoordinator;
    
    public SimpleNestedScrollView(Context context) {
        super(context);
    }
    
    
    public SimpleNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public SimpleNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
  
    public void realSmoothScrollTo(int scrollToPosition, long duation, Animator.AnimatorListener listener){
        ValueAnimator realSmoothScrollAnimation =
                ValueAnimator.ofInt(getScrollY(), scrollToPosition);
        realSmoothScrollAnimation.setDuration(duation);
        realSmoothScrollAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int scrollTo = (Integer) animation.getAnimatedValue();
                scrollTo(0, scrollTo);
            }
        });
        if(listener != null){
            realSmoothScrollAnimation.addListener(listener);
        }
        realSmoothScrollAnimation.start();
    }
    
}
