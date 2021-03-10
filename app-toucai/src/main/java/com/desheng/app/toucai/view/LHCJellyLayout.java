package com.desheng.app.toucai.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import com.shark.tc.R;

public class LHCJellyLayout extends FrameLayout {

    private Paint mPaint;
    private Path mPath;
    @ColorInt
    private int mColor = Color.GRAY;
    private ViewOutlineProvider mViewOutlineProvider;
    private float mPointX;
    ObjectAnimator animator;

    private View releaseHint;
    private View loading;

    public void setPullHeight(float mPullHeight) {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
            animator = null;
        }

        if (mPullHeight >  mTriggerHeight) {
            releaseHint.setVisibility(VISIBLE);
        } else {
            releaseHint.setVisibility(INVISIBLE);
        }

        boolean needInvalidate = this.mPullHeight != mPullHeight;
        this.mPullHeight = mPullHeight;
        if (needInvalidate) invalidate();
    }

    float mHeaderHeight = 0;

    public void setmTriggerHeight(float mTriggerHeight) {
        this.mTriggerHeight = mTriggerHeight;
    }

    float mTriggerHeight = 0;
    float mPullHeight = 0;

    public LHCJellyLayout(Context context) {
        this(context, null);
    }

    public LHCJellyLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LHCJellyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);

        mPath = new Path();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mViewOutlineProvider = new ViewOutlineProvider() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void getOutline(View view, Outline outline) {
                    if (mPath.isConvex()) outline.setConvexPath(mPath);
                }
            };

        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        releaseHint = findViewById(R.id.tv_refresh);
        loading = findViewById(R.id.progress);
    }

    public void setColor(int color) {
        mColor = color;
    }

    public void setPointX(float pointX) {
        boolean needInvalidate = pointX != mPointX;
        mPointX = pointX;
        if (needInvalidate) invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawPulling(canvas);
    }

    private void drawPulling(Canvas canvas) {

        final int width = canvas.getWidth();
        final float mDisplayX = (mPointX - width / 2f) * 0.5f + width / 2f;
        Shader mShader = new LinearGradient(0,0,width,0,new int[] {Color.parseColor("#FDA254"), Color.parseColor("#F94F79")},null,Shader.TileMode.REPEAT);
        mPaint.setShader(mShader);
        mPaint.setColor(mColor);

        int headerHeight = (int) mHeaderHeight;
        int pullHeight = (int) Math.max(mPullHeight, mHeaderHeight);

        mPath.rewind();
        mPath.moveTo(0, 0);
        mPath.lineTo(0, headerHeight);
        mPath.quadTo(mDisplayX, pullHeight, width, headerHeight);
        mPath.lineTo(width, 0);
        mPath.close();

        canvas.drawPath(mPath, mPaint);
    }

    public void setHeaderHeight(float headerHeight) {
        mHeaderHeight = headerHeight;
    }

    public void onRelease() {
        animator = ObjectAnimator.ofFloat(this, "pullHeight", mPullHeight, 0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(1500);
        animator.start();
    }

    public void showLoading(boolean loading) {
        this.loading.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
    }
}
