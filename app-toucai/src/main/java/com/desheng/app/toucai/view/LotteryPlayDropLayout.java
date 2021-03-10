package com.desheng.app.toucai.view;

import android.animation.Animator;
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
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.NestedScrollingParentHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.ab.util.Strs;
import com.ab.util.Views;
import com.desheng.base.manager.UserManager;
import com.desheng.base.model.LotteryOpenHistory;
import com.shark.tc.R;

import java.util.ArrayList;
import java.util.List;

public class LotteryPlayDropLayout extends FrameLayout {
    private int mMaxFlintVelocity, mMinFlintVelocity;
    private Paint mPaint;
    private Paint mTextPaint;
    private ViewOutlineProvider mViewOutlineProvider;
    private Path mPath;
    float mHeaderHeight = 0;
    float mTriggerHeight = 0;
    float mExpandingHeight = 0;
    float mPullHeight = 0;
    float pullingScallFactor = 0.75f;
    float headerAlpha = 1.0f;
    private float mPointX;
    private Animator headerAninator;


    LotteryOpenHistoryAdapter listAdapter;
    private LinearLayout listContainer;


    private String hint = "继续下拉展开历史开奖记录";
    private String hint_noData = "没有历史开奖记录";
    private Scroller scroller;
    private Scroller listScroller;
    private VelocityTracker velocityTracker;
    private NestedScrollingParentHelper mParentHelper;

    private float touchDownY = 0;
    private float downScrollY = 0;

    private StateMachine stateMachine = new StateMachine();

    public LotteryPlayDropLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public LotteryPlayDropLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LotteryPlayDropLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mMaxFlintVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        mMinFlintVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        setHeaderHeight(Views.dp2px(48));
        setmTriggerHeight(Views.dp2px(120));
        setmExpandingHeight(Views.dp2px(150));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setClipToOutline(false);
        }
        setClipToPadding(false);
        setClipChildren(false);
        setWillNotDraw(false);
        velocityTracker = VelocityTracker.obtain();
        scroller = new Scroller(getContext());
        listScroller = new Scroller(getContext());
        mParentHelper = new NestedScrollingParentHelper(this);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(0xAAFFFFFF);
        mTextPaint.setTextSize(Views.sp2px(15));
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
        listContainer = new LinearLayout(getContext());
        listContainer.setOrientation(LinearLayout.VERTICAL);
//        listContainer.setLayoutTransition(new LayoutTransition());
        addView(listContainer, 0, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        listContainer.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        listContainer.layout(left, top, right, listContainer.getMeasuredHeight());
    }

    @Override
    protected void onDetachedFromWindow() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }

        super.onDetachedFromWindow();
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
//        Log.e("111", "onStartNestedScroll--" + ",nestedScrollAxes:" + nestedScrollAxes);
        mPullHeight = getScaleY();
        if (scroller.computeScrollOffset())
            scroller.forceFinished(true);
        return true;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (getScrollY() < 0 && dy > 0) {
            consumed[1] = Math.max(getScrollY(), dy);
            doDeltaScroll(consumed[1]);
        }
//        consumed[1] = dy;//完全消费y轴的滑动
//        Log.e("111", "onNestedPreScroll--getScrollY():" + getScrollY() + ",dx:" + dx + ",dy:" + dy + ",consumed:" + consumed);
    }


    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
//        Log.e("111", "onNestedScroll--" + "target: " + ",dxConsumed" + dxConsumed + ",dyConsumed:" + dyConsumed
//                + ",dxUnconsumed:" + dxUnconsumed + ",dyUnconsumed:" + dyUnconsumed);
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        if (dyUnconsumed < 0) {
            doDeltaScroll(dyUnconsumed);
        }
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
//        Log.e("111", "onNestedScrollAccepted" + ",nestedScrollAxes:" + nestedScrollAxes);
        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(View target) {
//        Log.e("111", "onStopNestedScroll");
        mParentHelper.onStopNestedScroll(target);
        stateMachine.onTouchCancel();
    }

    @Override
    public void computeScroll() {
        if (stateMachine.getState() != stateMachine.expanded)
            listContainer.setTranslationY(getScrollY());

        if (scroller.computeScrollOffset()) {
            mPullHeight = -scroller.getCurrY();
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
//            updateHistoryListVisibility(true);
        }

        if (listScroller.computeScrollOffset()) {
            listContainer.setTranslationY(listScroller.getCurrY());
            listContainer.invalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDownY = ev.getRawY();
                mPointX = ev.getX();
                downScrollY = getScrollY();
                if (scroller.computeScrollOffset()) {
                    scroller.forceFinished(true);
                    invalidate();
                }
                break;
        }

        if (stateMachine.getState() == stateMachine.idle)
            return false;
        else if (stateMachine.getState() == stateMachine.expanded)
            return true;
        else
            return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPointX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                mPointX = event.getX();
                float y = event.getRawY();
                mPullHeight = -(y - touchDownY);
                float scrollTo = downScrollY - (y - touchDownY);

                if (-scrollTo > listContainer.getHeight())
                    scrollTo = -listContainer.getHeight();

                scrollTo(0, (int) scrollTo);
//                stateMachine.updateScroll(getScrollY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                stateMachine.onTouchCancel();
                break;
        }
        return true;
    }

    private void doDeltaScroll(float deltaY) {
        mPullHeight += (-deltaY);
        scrollBy(0, (int) deltaY);
        invalidate();
        stateMachine.updateScroll(getScrollY());
    }

    private void doFreeScroll() {
//        Log.e("111", "doFreeScroll: ");
        velocityTracker.computeCurrentVelocity(1000, mMaxFlintVelocity);
        scroller.fling(getScrollX(), getScrollY(), (int) -velocityTracker.getXVelocity(), (int) -velocityTracker.getYVelocity(), 0, 0, -listContainer.getHeight(), 0);

        if (-scroller.getFinalY() < mHeaderHeight) {
            stateMachine.getState().translateTo(stateMachine.idle);
            doFixedScroll();
        }
    }

    private void doFixedScroll() {
//        Log.e("111", "doFixedScroll: " + stateMachine.getState());
        if (stateMachine.getState() == stateMachine.expanded) {
            scroller.startScroll(getScrollX(), getScrollY(), 0, -(listContainer.getHeight() + getScrollY()));
            listScroller.startScroll(0, (int) listContainer.getTranslationY(), 0, (int) -(listContainer.getHeight() + listContainer.getTranslationY()));
            updateHistoryListVisibility(false);
            invalidate();
        } else if (stateMachine.getState() == stateMachine.idle) {
            scroller.startScroll(getScrollX(), getScrollY(), 0, -getScrollY());
            listScroller.startScroll(0, (int) listContainer.getTranslationY(), 0, 0);
            updateHistoryListVisibility(false);
            invalidate();
        }
    }

    private void updateHistoryListVisibility(boolean caculateScroll) {
        if (stateMachine.getState() == stateMachine.expanding
                || stateMachine.getState() == stateMachine.expanded) {
            int count = listContainer.getChildCount();
            int aniTimeOffset = 0;
            for (int i = 0; i < count; i++) {
                View child = listContainer.getChildAt(i);
                if ((!caculateScroll || child.getBottom() < -getScrollY()) && child.getVisibility() == View.INVISIBLE) {

                    if (child.getAnimation() != null && !child.getAnimation().hasEnded())
                        child.clearAnimation();

                    child.setVisibility(VISIBLE);
                    child.setTranslationX(listContainer.getMeasuredWidth());
                    Animation ani = new TranslateAnimation(0, -listContainer.getMeasuredWidth(), 0, 0);
                    Animation alphaAni = new AlphaAnimation(child.getAlpha(), 1);
                    AnimationSet animationSet = new AnimationSet(true);
                    animationSet.addAnimation(ani);
                    animationSet.addAnimation(alphaAni);
                    animationSet.setInterpolator(new LinearInterpolator());
                    animationSet.setDuration(150);
                    animationSet.setFillEnabled(true);
                    animationSet.setFillAfter(true);
                    animationSet.setFillBefore(false);
                    animationSet.setStartOffset(aniTimeOffset);
                    child.startAnimation(animationSet);
                    aniTimeOffset += 20;
                }

                if (stateMachine.getState() == stateMachine.expanding
                        && child.getVisibility() == VISIBLE && (!caculateScroll || child.getBottom() > -getScrollY())) {

                    if (child.getAnimation() != null && !child.getAnimation().hasEnded())
                        child.getAnimation().cancel();

                    Animation alpha = new AlphaAnimation(child.getAlpha(), 0);
                    alpha.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            child.setVisibility(INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    child.startAnimation(alpha);
                }
            }
        } else {
            int count = listContainer.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = listContainer.getChildAt(i);
                if (child.getVisibility() == VISIBLE) {
                    Animation alpha = new AlphaAnimation(child.getAlpha(), 0);
                    alpha.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            child.setVisibility(INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    child.startAnimation(alpha);
                }
            }
        }
    }

    public void setHistoryData(ArrayList<LotteryOpenHistory> listOpen) {
        if (listAdapter != null)
            return;

        if (listOpen.size() == 0)
            return;

        if (listAdapter == null) {
            listContainer.removeAllViews();
            listAdapter = new LotteryOpenHistoryAdapter(listOpen.subList(0, listOpen.size() > 12 ? 12 : listOpen.size()));
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View view = listAdapter.getView(i, null, listContainer);
                view.setVisibility(INVISIBLE);
//                view.setAlpha(0);
                listContainer.addView(view);
                listContainer.requestLayout();
            }
        } else {
            if (Strs.isEmpty(listOpen.get(0).getIssue()))
                return;

            LotteryOpenHistory theNewOne = listOpen.get(0);
            LotteryOpenHistory last = (LotteryOpenHistory) listAdapter.getItem(0);
            String newIssuno = theNewOne.getIssue().contains("-") ? theNewOne.getIssue().split("-")[1] : theNewOne.getIssue();
            String lastIssuno = "0";

            if (!Strs.isEmpty(last.getIssue()))
                lastIssuno = last.getIssue().contains("-") ? last.getIssue().split("-")[1] : last.getIssue();

            int n = Strs.parse(newIssuno, 0);
            int l = Strs.parse(lastIssuno, 0);

            if (n > l) {
                listAdapter.addNewData(theNewOne);
                View view = listAdapter.getView(0, null, listContainer);
                view.setVisibility(INVISIBLE);
                view.setAlpha(0);
                listContainer.addView(view);
                listContainer.requestLayout();
            }
        }
    }

    public float getHeaderAlpha() {
        return headerAlpha;
    }

    public void setHeaderAlpha(float headerAlpha) {
        this.headerAlpha = headerAlpha;
    }

    public void setmExpandingHeight(float mExpandingHeight) {
        this.mExpandingHeight = mExpandingHeight;
    }

    public void setmTriggerHeight(float mTriggerHeight) {
        this.mTriggerHeight = mTriggerHeight;
    }


    public void setHeaderHeight(float headerHeight) {
        mHeaderHeight = headerHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPulling(canvas);
    }

    private void drawPulling(Canvas canvas) {
        int save = canvas.save();
        mPaint.setAlpha((int) (headerAlpha * 255));
        mTextPaint.setAlpha((int) (headerAlpha * 255));
        canvas.translate(0, getScrollY());
        final int width = canvas.getWidth();
//        final float mDisplayX = (mPointX - width / 2f) * 0.5f + width / 2f;
        final float mDisplayX = width / 2f;
        Shader mShader = new LinearGradient(0, 0, width, 0, new int[]{Color.parseColor("#FDA254"), Color.parseColor("#F94F79")}, null, Shader.TileMode.REPEAT);
        mPaint.setShader(mShader);

        int headerHeight = (int) mHeaderHeight;
        int pullHeight = (int) (mPullHeight * pullingScallFactor + mHeaderHeight);

        if (pullHeight > mTriggerHeight)
            pullHeight = (int) mTriggerHeight;

        mPath.rewind();
        mPath.moveTo(0, 0);
        mPath.lineTo(0, headerHeight);
        mPath.quadTo(mDisplayX, pullHeight, width, headerHeight);
        mPath.lineTo(width, 0);
        mPath.close();
        canvas.drawPath(mPath, mPaint);

        if (pullHeight > mHeaderHeight) {
            String text =  (listAdapter != null && listAdapter.getCount() > 0) ? hint : hint_noData;
            float[] widths = new float[text.length()];
            mTextPaint.getTextWidths(text, widths);

            float totalWith = 0;
            for (float w : widths) {
                totalWith += w;
            }

            canvas.drawText(text, getMeasuredWidth() / 2 - totalWith / 2, mHeaderHeight, mTextPaint);
        }

        canvas.restoreToCount(save);
    }

    private class LotteryOpenHistoryAdapter extends BaseAdapter {
        private int[] ballBg = {
                R.drawable.ic_lhc_ball_red, // not used
                R.drawable.ic_lhc_ball_red,
                R.drawable.ic_lhc_ball_blue,
                R.drawable.ic_lhc_ball_green,
        };
        private List<LotteryOpenHistory> list;

        public LotteryOpenHistoryAdapter(List<LotteryOpenHistory> list) {
            this.list = list;
        }

        public void addNewData(LotteryOpenHistory n) {
            list.add(0, n);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lottery_play_open_record, parent, false);
                convertView.setTag(new LotteryOpenHistoryAdapter.ViewHolder(convertView));
            }

            LotteryOpenHistoryAdapter.ViewHolder vh = (LotteryOpenHistoryAdapter.ViewHolder) convertView.getTag();

            LotteryOpenHistory history = list.get(position);
            vh.issue.setText(String.format("第%s期", history.getIssue()));
//            vh.category.setText(history.getLottery());

            String[] codes;
            if (history.getCode() == null) {
                codes = new String[]{"0", "0", "0", "0", "0", "0"};
            } else {
                codes = history.getCode().split(",");
            }

            for (int i = 0; i < 9; i++) {
                if (codes == null || i > codes.length - 1) {
                    vh.num[i].setVisibility(View.GONE);
                } else {
                    vh.num[i].setVisibility(View.VISIBLE);
                    vh.num[i].setText(codes[i]);
                    try {
                        vh.num[i].setBackgroundResource(ballBg[UserManager.getIns().getListLHCBallColor().get(Strs.parse(codes[i], 1))]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        vh.num[i].setBackgroundResource(ballBg[0]);
                    }
                }
            }

            return convertView;
        }

        class ViewHolder {
            TextView issue;
            TextView[] num = new TextView[9];
            TextView category;

            ViewHolder(View view) {
                issue = view.findViewById(R.id.tv_issue);
                num[0] = view.findViewById(R.id.tv_ball_1);
                num[1] = view.findViewById(R.id.tv_ball_2);
                num[2] = view.findViewById(R.id.tv_ball_3);
                num[3] = view.findViewById(R.id.tv_ball_4);
                num[4] = view.findViewById(R.id.tv_ball_5);
                num[5] = view.findViewById(R.id.tv_ball_6);
                num[6] = view.findViewById(R.id.tv_ball_7);
                num[7] = view.findViewById(R.id.tv_ball_8);
                num[8] = view.findViewById(R.id.tv_ball_9);
                category = view.findViewById(R.id.tv_category);
            }
        }
    }

    private class StateMachine {
        public State idle = new IdleState();
        public State pulling = new PullingState();
        public State expanding = new ExpandingState();
        public State expanded = new ExpandedState();

        private State state = new IdleState();

        public class IdleState extends State {
            @Override
            public void updateScroll(float scrollY) {
                if (scrollY < 0)
                    return;

                if (scrollY > 0 && scrollY < mHeaderHeight)
                    translateTo(pulling);
                else if (scrollY > mExpandingHeight)
                    translateTo(expanding);
            }

            @Override
            public void enter() {
                super.enter();
                if (headerAninator != null && headerAninator.isRunning())
                    headerAninator.cancel();
                headerAninator = ObjectAnimator.ofFloat(LotteryPlayDropLayout.this, "headerAlpha", getHeaderAlpha(), 1.0f);
                headerAninator.start();

            }
        }

        public class PullingState extends State {

            @Override
            public void updateScroll(float scrollY) {
//                if (scrollY > 0 && scrollY < mHeaderHeight)
//                    translateTo(pulling);
//                else
                if (scrollY > mExpandingHeight && listAdapter != null && listAdapter.getCount() > 0)
                    translateTo(expanding);
            }

            @Override
            public void onTouchCancel() {
                translateTo(idle);
                doFixedScroll();
            }

            @Override
            public void enter() {
                super.enter();
                if (headerAninator != null && headerAninator.isRunning())
                    headerAninator.cancel();
                headerAninator = ObjectAnimator.ofFloat(LotteryPlayDropLayout.this, "headerAlpha", getHeaderAlpha(), 1.0f);
                headerAninator.start();
            }
        }

        public class ExpandedState extends State {
            @Override
            public void enter() {
                super.enter();
//                listScrollFinished = false;
            }

            @Override
            public void updateScroll(float scrollY) {
                if (scrollY < mHeaderHeight)
                    translateTo(pulling);
//                else if (scrollY > mExpandingHeight)
//                    translateTo(expanding);
                updateHistoryListVisibility(true);
            }
        }

        public class ExpandingState extends State {
            @Override
            public void updateScroll(float scrollY) {
                if (scrollY > 0 && scrollY < mHeaderHeight)
                    translateTo(pulling);

                updateHistoryListVisibility(true);
            }

            @Override
            public void onTouchCancel() {
                translateTo(expanded);
                doFixedScroll();
            }

            @Override
            public void enter() {
                super.enter();
                if (headerAninator != null && headerAninator.isRunning())
                    headerAninator.cancel();
                headerAninator = ObjectAnimator.ofFloat(LotteryPlayDropLayout.this, "headerAlpha", getHeaderAlpha(), 0.0f);
                headerAninator.start();
            }
        }


        public void updateScroll(float scrollY) {
            state.updateScroll(-scrollY);
        }

        public void onTouchCancel() {
            state.onTouchCancel();
        }

        public State getState() {
            return state;
        }

        public class State {
            public void updateScroll(float scrollY) {
            }

            public void onTouchCancel() {
                doFreeScroll();
            }

            void translateTo(State state) {
                if (StateMachine.this.state == state)
                    return;

                StateMachine.this.state.exit();
                StateMachine.this.state = state;
                StateMachine.this.state.enter();
            }

            @CallSuper
            public void enter() {
            }

            @CallSuper
            public void exit() {
            }
        }
    }
}
