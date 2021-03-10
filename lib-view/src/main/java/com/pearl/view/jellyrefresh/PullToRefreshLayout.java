package com.pearl.view.jellyrefresh;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.pearl.view.R;

/**
 * Created by yilun
 * on 09/07/15.
 */
public class PullToRefreshLayout extends FrameLayout {

    private static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(10);

    static final int STATE_IDLE = 0;
    static final int STATE_DRAGGING = 1;
    static final int STATE_RELEASING = 2;
    static final int STATE_SETTLING = 3;
    static final int STATE_REFRESHING = 4;
    static final int STATE_REFRESHING_SETTLING = 5;
    static final int STATE_EXTENDING = 6;
    static final int STATE_COLLAPSE = 7;

    @IntDef({STATE_IDLE,
            STATE_DRAGGING,
            STATE_REFRESHING,
            STATE_RELEASING,
            STATE_REFRESHING_SETTLING,
            STATE_SETTLING,
            STATE_EXTENDING,
            STATE_COLLAPSE})
    @interface State {
    }

    private float mTouchStartY;
    private float mCurrentY;
    private View mChildView;
    float mPullHeight;
    float mHeaderHeight;
    float mTriggerHeight;
    float mSecondaryTriggerHeight;
    @State
    private int mState = STATE_IDLE;
    private PullToRefreshListener mPullToRefreshListener;
    private PullToRefreshPullingListener mPullToRefreshPullingListener;
    private FrameLayout mHeader;
    private boolean secondaryExtanding = false;

    public PullToRefreshLayout(Context context) {
        this(context, null);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }

        if (getChildCount() > 1) {
            throw new RuntimeException("You can only attach one child");
        }


        mPullHeight = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                150,
                getContext().getResources().getDisplayMetrics());

        mHeaderHeight = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                56,
                getContext().getResources().getDisplayMetrics());

        mTriggerHeight = mHeaderHeight;

        this.post(() -> {
            mChildView = getChildAt(0);
            addHeaderContainer();
        });

        if (attrs == null) return;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.JellyRefreshLayout);
        try {
            float height = a.getDimension(R.styleable.JellyRefreshLayout_headerHeight, mHeaderHeight);
            float pullHeight = a.getDimension(R.styleable.JellyRefreshLayout_pullMaxHeight, mPullHeight);
            float triggerHeight = a.getDimension(R.styleable.JellyRefreshLayout_triggerHeight, mTriggerHeight);
            float secondarytriggerHeight = a.getDimension(R.styleable.JellyRefreshLayout_secondarytriggerHeight, mSecondaryTriggerHeight);
            mHeaderHeight = height;
            mPullHeight = pullHeight;
            mTriggerHeight = triggerHeight;
            mSecondaryTriggerHeight = secondarytriggerHeight;
        } finally {
            a.recycle();
        }

    }

    public void setHeaderView(View headerView) {
        post(() -> mHeader.addView(headerView));
    }

    private void addHeaderContainer() {
        FrameLayout headerContainer = new FrameLayout(getContext());
        LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        headerContainer.setLayoutParams(layoutParams);
        mHeader = headerContainer;
        addViewInternal(headerContainer);
        setUpChildViewAnimator();
    }

    @SuppressLint("NewApi")
    private void setUpChildViewAnimator() {
        if (mChildView == null) {
            return;
        }
        mChildView.animate().setInterpolator(new DecelerateInterpolator());
        mChildView.animate().setUpdateListener(valueAnimator -> {
            if (mPullToRefreshPullingListener != null) {
                mPullToRefreshPullingListener.onTranslationYChanged(mChildView.getTranslationY());
            }
        });
    }

    private void addViewInternal(@NonNull View child) {
        super.addView(child);
    }

    @Override
    public void addView(@NonNull View child) {
        if (getChildCount() >= 1) {
            throw new RuntimeException("You can only attach one child");
        }
        mChildView = child;
        super.addView(child);
        setUpChildViewAnimator();
    }

    public boolean canChildScrollUp() {
        if (mChildView == null) {
            return false;
        }

        return ViewCompat.canScrollVertically(mChildView, -1);
    }

    @State
    public int getState() {
        return mState;
    }

    public void setState(@State int state) {
        Log.e("222", "" + state);
        if (mState != state) {
            mState = state;
            onStateChanged(mState);
        }
    }

    protected void onStateChanged(@State int newState) {
    }

    public boolean isRefreshing() {
        return getState() == STATE_REFRESHING;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartY = e.getY();
                secondaryExtanding = false;
                mCurrentY = mTouchStartY;
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = e.getY();
                float dy = currentY - mTouchStartY;
                if (dy > 0 && !canChildScrollUp()) {
                    return true;
                }
                break;
        }

        if (getState() == STATE_REFRESHING
                || getState() == STATE_SETTLING
                || getState() == STATE_REFRESHING_SETTLING
                || getState() == STATE_EXTENDING) {
            return true;
        }

        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent e) {
        if (getState() == STATE_REFRESHING
                || getState() == STATE_RELEASING
                || getState() == STATE_SETTLING
                || getState() == STATE_REFRESHING_SETTLING
                || getState() == STATE_EXTENDING) {
            if(mTouchStartY - e.getY() > 100){
                switch (e.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        setState(STATE_COLLAPSE);
                        break;
                    default:
                        setState(STATE_IDLE);
                }
                if (mPullToRefreshListener != null) {
                    mPullToRefreshListener.onCollasping(PullToRefreshLayout.this);
                    mTouchStartY=0;
                }
            }
            return true;
        }

        if (getState() == STATE_COLLAPSE) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    mCurrentY = e.getY();
                    float currentX = e.getX();
                    float dy = MathUtils.constrains(
                            0,
                            mPullHeight * 2,
                            mCurrentY - mTouchStartY);
                    if (mChildView != null) {
//                        mChildView.bringToFront();
//                        float offsetY = decelerateInterpolator.getInterpolation(dy / mPullHeight / 2) * dy / 2;
//                        mChildView.setTranslationY(offsetY);
                    }
                    return true;
            }
        }

        Log.e("111", "event " + e.getAction());
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                setState(STATE_DRAGGING);
                mCurrentY = e.getY();
                float currentX = e.getX();
                float dy = MathUtils.constrains(
                        0,
                        mPullHeight * 2,
                        mCurrentY - mTouchStartY);
                if (mChildView != null) {
                    mChildView.bringToFront();
                    float offsetY = decelerateInterpolator.getInterpolation(dy / mPullHeight / 2) * dy / 2;
                    mChildView.setTranslationY(offsetY);
                    if (mPullToRefreshPullingListener != null) {
                        mPullToRefreshPullingListener.onTranslationYChanged(offsetY);
                        mPullToRefreshPullingListener.onPulling(offsetY / mHeaderHeight, currentX);
                    }

                    if (mChildView.getTranslationY() >= mSecondaryTriggerHeight) {
                        if (mPullToRefreshListener != null && !secondaryExtanding) {
                            mPullToRefreshListener.onSecondaryExtending(PullToRefreshLayout.this);
                            secondaryExtanding = true;
                        }
                    }
                    }
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mChildView != null) {
                    if (mChildView.getTranslationY() >= mTriggerHeight && !secondaryExtanding) {
                        mChildView.animate().translationY(mHeaderHeight)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {
                                        setState(STATE_REFRESHING_SETTLING);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        setState(STATE_REFRESHING);
                                        if (mPullToRefreshListener != null) {
                                            mPullToRefreshListener.onPrepareRefresh(PullToRefreshLayout.this);
                                        }
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {
                                        setState(STATE_REFRESHING);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {

                                    }
                                })
                                .start();
                    } else {
                        mChildView.animate().translationY(0)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {
                                        setState(STATE_RELEASING);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        setState(STATE_IDLE);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {
                                        setState(STATE_IDLE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {

                                    }
                                })
                                .start();
                    }
                    if (mPullToRefreshPullingListener != null) {
                        mPullToRefreshPullingListener.onRelease();
                    }
                } else {
                    setState(STATE_IDLE);
                }
                return true;
            default:
                return super.onTouchEvent(e);
        }
    }

    public void setPullToRefreshListener(PullToRefreshListener pullToRefreshListener) {
        this.mPullToRefreshListener = pullToRefreshListener;
    }

    public void setPullingListener(PullToRefreshPullingListener pullingListener) {
        this.mPullToRefreshPullingListener = pullingListener;
    }

    public void setCollapsed() {
        setState(STATE_IDLE);

        if (mPullToRefreshListener != null) {
            mPullToRefreshListener.onCollasping(PullToRefreshLayout.this);
            mTouchStartY=0;
        }
    }

    public void setRefreshing(boolean refreshing) {
        setRefreshing(refreshing, true);
    }

    public void setRefreshing(boolean refreshing, boolean doAnimation) {
        Log.e("222", "set refresh " + refreshing);
        if (refreshing) {
            if (mChildView != null) {
                if (doAnimation) {
                    mChildView.animate().translationY(mHeaderHeight)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {
                                    setState(STATE_SETTLING);
                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    setState(STATE_REFRESHING);
                                    mPullToRefreshListener.onTriggerRefresh(PullToRefreshLayout.this);
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {
                                    setState(STATE_REFRESHING);
                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            })
                            .start();
                } else {
                    mChildView.setTranslationY(mHeaderHeight);
                    setState(STATE_REFRESHING);
                    mPullToRefreshListener.onTriggerRefresh(PullToRefreshLayout.this);
                }
            }
        } else {
            if (!isRefreshing()) return;
            if (mChildView != null) {
                if (doAnimation) {
                    mChildView.animate().translationY(0)
                            .setListener(null)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {
                                    setState(STATE_SETTLING);
                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    setState(STATE_EXTENDING);
                                    mPullToRefreshListener.onExtending(PullToRefreshLayout.this);
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {
                                    setState(STATE_IDLE);
                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            }).start();
                } else {
                    mChildView.setTranslationY(0);
                    setState(STATE_EXTENDING);
                    mPullToRefreshListener.onExtending(PullToRefreshLayout.this);
                }
            } else {
                setState(STATE_IDLE);
            }
        }
    }


    public interface PullToRefreshListener {

        void onPrepareRefresh(PullToRefreshLayout pullToRefreshLayout);

        void onTriggerRefresh(PullToRefreshLayout pullToRefreshLayout);

        void onExtending(PullToRefreshLayout pullToRefreshLayout);

        void onSecondaryExtending(PullToRefreshLayout pullToRefreshLayout);

        void onCollasping(PullToRefreshLayout pullToRefreshLayout);
    }

    public interface PullToRefreshPullingListener {

        void onPulling(float fraction, float pointXPosition);

        void onTranslationYChanged(float translationY);

        void onRelease();
    }
}
