package com.desheng.app.toucai.view;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.ab.util.Strs;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

public class ScrollingDigitalAnimation extends AppCompatTextView {
    private String numStart = "0";
    private String numEnd;
    private long duration = 2000L;
    private String prefixString = "";
    private String postfixString = "";
    private boolean isInt = false;

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public ScrollingDigitalAnimation(Context context) {
        super(context);
    }

    public ScrollingDigitalAnimation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollingDigitalAnimation(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setNumberString(String number) {
        this.setNumberString("0.00", number);
    }

    public void setNumberString(String numStart, String numberEnd) {
        this.numStart = numStart;
        this.numEnd = numberEnd;
        if (this.checkNumString(numStart, this.numEnd)) {
            this.start();
        } else {
            this.setText(this.prefixString + numberEnd + this.postfixString);
        }

    }

    public void setPrefixString(String prefixString) {
        this.prefixString = prefixString;
    }

    public void setPostfixString(String postfixString) {
        this.postfixString = postfixString;
    }

    private void start() {
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new BigDecimalEvaluator(), new Object[]{new BigDecimal(this.numStart), new BigDecimal(this.numEnd)});
        valueAnimator.setDuration(this.duration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                BigDecimal value = (BigDecimal) animation.getAnimatedValue();
                ScrollingDigitalAnimation.this.setText(ScrollingDigitalAnimation.this.prefixString + ScrollingDigitalAnimation.this.format(value) + ScrollingDigitalAnimation.this.postfixString);
            }
        });
        valueAnimator.start();
    }

    private String format(BigDecimal bd) {
        String pattern;
        if (this.isInt) {
            pattern = "#";
        } else {
            pattern = "0.00";
        }

        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        return decimalFormat.format(bd);
    }

    private boolean checkNumString(String numStart, String numEnd) {
        try {
            new BigInteger(numStart);
            new BigInteger(numEnd);
            this.isInt = true;
        } catch (Exception var6) {
            this.isInt = false;
            var6.printStackTrace();
        }

        try {
            BigDecimal start = new BigDecimal(numStart);
            BigDecimal end = new BigDecimal(numEnd);
            return end.compareTo(start) >= 0;
        } catch (Exception var5) {
            var5.printStackTrace();
            return false;
        }
    }

    static class BigDecimalEvaluator implements TypeEvaluator {
        BigDecimalEvaluator() {
        }

        public Object evaluate(float fraction, Object startValue, Object endValue) {
            BigDecimal start = (BigDecimal) startValue;
            BigDecimal end = (BigDecimal) endValue;
            BigDecimal result = end.subtract(start);
            return result.multiply(new BigDecimal("" + fraction)).add(start);
        }
    }
}
