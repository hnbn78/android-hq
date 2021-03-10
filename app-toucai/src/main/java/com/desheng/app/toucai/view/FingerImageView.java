package com.desheng.app.toucai.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by user on 2018/3/20.
 */

@SuppressLint("AppCompatCustomView")
public class FingerImageView extends ImageView {

    public FingerImageView(Context context) {
        super(context);
    }

    public FingerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FingerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void startGif() {
        // 初始化
        setAlpha(0);
        setScaleX(1f);
        setScaleY(1f);

        // 设置帧动画
        //setBackgroundResource(R.drawable.);
        // 帧动画开始
        ((AnimationDrawable) getBackground()).start();

        // 开启渐变动画
        ObjectAnimator.ofFloat(this, "alpha", 0, 1).setDuration(200).start();
    }

    public void end(final boolean isSuccess){
        // 初始化
        setAlpha(0);
        setScaleX(1.54f);
        setScaleY(1.5f);

        // 设置帧动画
        //setBackgroundResource(isSuccess ? R.drawable.finger_suceed : R.drawable.finger_failed);
        // 帧动画开始
        ((AnimationDrawable) getBackground()).start();

        // 开启渐变动画
        ObjectAnimator.ofFloat(this, "alpha", 0, 1).setDuration(200).start();
    }

}

