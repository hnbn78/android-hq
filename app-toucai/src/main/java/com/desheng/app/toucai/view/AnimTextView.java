package com.desheng.app.toucai.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import java.util.List;

public class AnimTextView extends RelativeLayout implements Runnable, View.OnClickListener {

    /**
     * Textview进入退出动画的执行速度
     */
    private long mAnimDuration = 1000;

    /**
     * TextView动画的停顿显示时间
     */
    private long mTextShowTime = 1500;


    /**
     * 有两个TextView 用来显示文本内容的进入和退出
     */
    private BaseTextView mTextViewMoveOut, mTextViewMoveIn;


    /**
     * 当前显示的进入和退出的文本内容
     */
    private String mMoveInText, mMoveOutText, mPrefixtInText, mPrefixtOutText;

    /**
     * 显示的文本集合
     */
    private List<AnimTextViewBean> mDataList;

    /**
     * 当前显示的文本下标和下一条文本的下标
     */
    private int mCurIndex = -1;

    /**
     * 点击事件
     */
    private OnClickListener mOnClickListener;


    public AnimTextView(Context context) {
        super(context);
    }

    public AnimTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //创建子视图
        mTextViewMoveOut = new BaseTextView(context);
        mTextViewMoveOut.setOnClickListener(this);

        mTextViewMoveIn = new BaseTextView(context);
        mTextViewMoveIn.setOnClickListener(this);

        addView(mTextViewMoveOut);
        addView(mTextViewMoveIn);
        setOnClickListener(this);
    }


    /**
     * 设置要显示在控件上的数据集合
     *
     * @param list
     */
    public void setDatas(List<AnimTextViewBean> list) {
        if (list == null) {
            throw new NullPointerException("请设置一个非空数据集合");
        }
        if (mDataList != null && mDataList.size() > 0) {
            return;
        }
        mDataList = list;
        new Thread(this).start();
    }

    /**
     * 点击事件
     *
     * @param onClickListener
     */
    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }


    /**
     * 开始动画
     */
    public void startAnim() {
        final TranslateAnimation moveOut = new TranslateAnimation(0, 0, 0, -mTextViewMoveOut.getHeight());
        moveOut.setFillAfter(true);
        moveOut.setDuration(mAnimDuration);

        final TranslateAnimation moveIn = new TranslateAnimation(0, 0, mTextViewMoveIn.getHeight(), 0);
        moveIn.setFillAfter(true);
        moveIn.setDuration(mAnimDuration);

        mTextViewMoveOut.startAnimation(moveOut);
        mTextViewMoveIn.startAnimation(moveIn);


    }

    @Override
    public void run() {
        while (true) {

            mCurIndex++;

            //显示玩后设置为退出部分的内容
            mMoveOutText = mMoveInText;
            mPrefixtOutText = mPrefixtInText;

            //进入是的内容
            mMoveInText = mDataList.get(mCurIndex % mDataList.size()).getContent();
            mPrefixtInText = mDataList.get(mCurIndex % mDataList.size()).getPrefixt();
            mHandler.sendEmptyMessage(0);
            try {
                Thread.sleep(mTextShowTime + mAnimDuration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mTextViewMoveIn.setText(mPrefixtInText, mMoveInText);
            mTextViewMoveOut.setText(mPrefixtOutText, mMoveOutText);
            startAnim();
        }
    };

    @Override
    public void onClick(View v) {
        if (mDataList == null) {
            return;
        }
        this.mOnClickListener.onClick(v, mCurIndex % mDataList.size());
    }


    public interface OnClickListener {
        void onClick(View v, int position);
    }

}
