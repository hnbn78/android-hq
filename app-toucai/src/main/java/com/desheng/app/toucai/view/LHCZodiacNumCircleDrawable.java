package com.desheng.app.toucai.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class LHCZodiacNumCircleDrawable extends Drawable {
    private Drawable drawable;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 圆心
     */
    private float cx, cy;
    /**
     * 半径
     */
    private float radius;

    private int size;

//    public CircleDrawable(int color, int width, int height) {
//        mPaint = new Paint();
//        mPaint.setAntiAlias(true);
//        mPaint.setColor(color);
//        size = Math.min(width, height);
//
//        cx = size / 2;
//        cy = size / 2;
//        radius = size / 2;
//    }

    public LHCZodiacNumCircleDrawable(Drawable drawable, int color) {
        this.drawable = drawable;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);


        size = Math.min(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        cx = size / 2;
        cy = size / 2;
        radius = size / 2;


    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(cx, cy, radius, mPaint);
    }

    @Override
    public int getIntrinsicHeight() {
        return size;
    }

    @Override
    public int getIntrinsicWidth() {
        return size;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
